package com.ewp.crm.service.impl;

import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.ewp.crm.repository.interfaces.SocialProfileRepository;
import com.ewp.crm.repository.interfaces.StatusDAO;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import com.ewp.crm.service.interfaces.SendNotificationService;
import com.ewp.crm.service.interfaces.SocialProfileTypeService;
import com.ewp.crm.service.interfaces.TelegramService;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.Log;
import org.drinkless.tdlib.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@PropertySource(value = "file:./telegram.properties", encoding = "Cp1251")
public class TelegramServiceImpl implements TelegramService {

    private static Client client = null;
    private static TdApi.AuthorizationState authorizationState = null;
    private static volatile boolean haveAuthorization = false;
    private static volatile boolean quiting = false;

    private static final Lock authorizationLock = new ReentrantLock();
    private static final Condition gotAuthorization = authorizationLock.newCondition();

    private static boolean tdlibInstalled = false;

    private static String phoneNumber = null;
    private static String code = null;
    private static String password = null;

    private static final Client.ResultHandler defaultHandler = new TelegramServiceImpl.DefaultHandler();

    private static Environment env;
    private static Logger logger = LoggerFactory.getLogger(TelegramServiceImpl.class);

    private final ClientRepository clientRepository;
    private final StatusDAO statusRepository;
    private final ClientHistoryService clientHistoryService;
    private final SendNotificationService sendNotificationService;
    private final SocialProfileRepository socialProfileRepository;
    private final SocialProfileTypeService socialProfileTypeService;

    @Autowired
    public TelegramServiceImpl(Environment env, ClientRepository clientRepository, StatusDAO statusRepository,
                               ClientHistoryService clientHistoryService, SendNotificationService sendNotificationService,
                               SocialProfileRepository socialProfileRepository, SocialProfileTypeService socialProfileTypeService) {
        this.env = env;
        this.clientRepository = clientRepository;
        this.statusRepository = statusRepository;
        this.clientHistoryService = clientHistoryService;
        this.sendNotificationService = sendNotificationService;
        this.socialProfileRepository = socialProfileRepository;
        this.socialProfileTypeService = socialProfileTypeService;
        try {
            System.loadLibrary("tdjni");
            Log.setVerbosityLevel(0);
            client = Client.create(new UpdatesHandler(), null, null);
            tdlibInstalled = true;
        } catch (UnsatisfiedLinkError e) {
            logger.error("Telegram database library not installed!", e);
        }
    }

    @Override
    public void sendAuthPhone(String phone) {
        phoneNumber = phone;
    }

    @Override
    public void sentAuthCode(String smsCode) {
        code = smsCode;
    }

    @Override
    public TdApi.Messages getChatMessages(long chatId, int limit) {
        GetChatMessagesHandler handler = new GetChatMessagesHandler();
        client.send(new TdApi.GetChatHistory(chatId, 0, 0, limit, false), handler);
        while (handler.isLoading()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                logger.warn("Message loading interrupted", e);
                break;
            }
        }
        return handler.getMessages();
    }

    @Override
    public void logout() {
        client.send(new TdApi.LogOut(), defaultHandler);
        haveAuthorization = false;
    }

    @Override
    public boolean isAuthenticated() {
        return haveAuthorization;
    }

    @Override
    public boolean isTdlibInstalled() {
        return tdlibInstalled;
    }

    private class UpdatesHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {
            switch (object.getConstructor()) {
                case TdApi.UpdateAuthorizationState.CONSTRUCTOR:
                    onAuthorizationStateUpdated(((TdApi.UpdateAuthorizationState) object).authorizationState);
                    break;
                case TdApi.UpdateNewChat.CONSTRUCTOR: {
                    TdApi.Chat chat = ((TdApi.UpdateNewChat) object).chat;
                    if (chat.type instanceof TdApi.ChatTypePrivate) {
                        TdApi.ChatTypePrivate chatTypePrivate = (TdApi.ChatTypePrivate) chat.type;
                        client.send(new TdApi.GetUser(chatTypePrivate.userId), new NewUserHandler());
                    }
                    break;
                }
            }
        }
    }

    /**
     * Handle telegram user authentication.
     * @param authorizationState authorization state type.
     */
    private void onAuthorizationStateUpdated(TdApi.AuthorizationState authorizationState) {
        if (authorizationState != null) {
            TelegramServiceImpl.authorizationState = authorizationState;
        }
        switch (TelegramServiceImpl.authorizationState.getConstructor()) {
            case TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR:
                if(Boolean.parseBoolean(env.getRequiredProperty("telegram.proxy.enable"))) {
                    client.send(new TdApi.AddProxy(env.getRequiredProperty("telegram.proxy.server"),
                            Integer.parseInt(env.getRequiredProperty("telegram.proxy.port")),true,
                            new TdApi.ProxyTypeSocks5(env.getRequiredProperty("telegram.proxy.username"),
                                    env.getRequiredProperty("telegram.proxy.password"))), defaultHandler);
                } else {
                    client.send(new TdApi.DisableProxy(), defaultHandler);
                }
                TdApi.TdlibParameters parameters = new TdApi.TdlibParameters();
                parameters.databaseDirectory = env.getRequiredProperty("telegram.databaseDirectory");
                parameters.useMessageDatabase = Boolean.parseBoolean(env.getRequiredProperty("telegram.useMessageDatabase"));
                parameters.useSecretChats = Boolean.parseBoolean(env.getRequiredProperty("telegram.useSecretChats"));
                parameters.apiId = Integer.parseInt(env.getRequiredProperty("telegram.apiId"));
                parameters.apiHash = env.getRequiredProperty("telegram.apiHash");
                parameters.systemLanguageCode = env.getRequiredProperty("telegram.systemLanguageCode");
                parameters.deviceModel = env.getRequiredProperty("telegram.deviceModel");
                parameters.systemVersion = env.getRequiredProperty("telegram.systemVersion");
                parameters.applicationVersion = env.getRequiredProperty("telegram.applicationVersion");
                parameters.enableStorageOptimizer = Boolean.parseBoolean(env.getRequiredProperty("telegram.enableStorageOptimizer"));

                client.send(new TdApi.SetTdlibParameters(parameters), new TelegramServiceImpl.AuthorizationRequestHandler());
                break;
            case TdApi.AuthorizationStateWaitEncryptionKey.CONSTRUCTOR:
                client.send(new TdApi.CheckDatabaseEncryptionKey(), new TelegramServiceImpl.AuthorizationRequestHandler());
                break;
            case TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR: {
                while (phoneNumber == null) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        logger.info("Thread interrupted!", e);
                    }
                }
                client.send(new TdApi.SetAuthenticationPhoneNumber(phoneNumber, false, false), new TelegramServiceImpl.AuthorizationRequestHandler());
                logger.info("SMS sent to phone number {}", phoneNumber);
                phoneNumber = null;
                break;
            }
            case TdApi.AuthorizationStateWaitCode.CONSTRUCTOR: {
                while (code == null) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        logger.info("Thread interrupted!", e);
                    }
                }
                client.send(new TdApi.CheckAuthenticationCode(code, "", ""), new TelegramServiceImpl.AuthorizationRequestHandler());
                logger.info("Confirmation code {} sent", code);
                code = null;
                break;
            }
            case TdApi.AuthorizationStateWaitPassword.CONSTRUCTOR: {
                while (code == null) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        logger.info("Thread interrupted!", e);
                    }
                }
                client.send(new TdApi.CheckAuthenticationPassword(password), new TelegramServiceImpl.AuthorizationRequestHandler());
                logger.info("Phone successfully has been authenticated with password {}", password);
                password = null;
                break;
            }
            case TdApi.AuthorizationStateReady.CONSTRUCTOR:
                haveAuthorization = true;
                authorizationLock.lock();
                try {
                    gotAuthorization.signal();
                } finally {
                    authorizationLock.unlock();
                }
                logger.info("Telegram authorization success");
                break;
            case TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR:
                haveAuthorization = false;
                logger.info("Telegram logging out");
                break;
            case TdApi.AuthorizationStateClosing.CONSTRUCTOR:
                haveAuthorization = false;
                logger.info("Telegram closing");
                break;
            case TdApi.AuthorizationStateClosed.CONSTRUCTOR:
                logger.info("Telegram closed");
                if (!quiting) {
                    client = Client.create(new UpdatesHandler(), null, null); // recreate client after previous has closed
                }
                break;
            default:
                System.err.println("Unsupported authorization state:" + System.lineSeparator() + TelegramServiceImpl.authorizationState);
        }
    }

    private class AuthorizationRequestHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {
            switch (object.getConstructor()) {
                case TdApi.Error.CONSTRUCTOR:
                    System.err.println("Receive an error:" + System.lineSeparator() + object);
                    onAuthorizationStateUpdated(null); // repeat last action
                    break;
                case TdApi.Ok.CONSTRUCTOR:
                    // result is already received through UpdateAuthorizationState, nothing to do
                    break;
                default:
                    System.err.println("Receive wrong response from TDLib:" + System.lineSeparator() + object);
            }
        }
    }

    private static class DefaultHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {
            System.out.println(object.toString());
        }
    }

    /**
     * Create new Client if not present.
     */
    private class NewUserHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {
            TdApi.User user = (TdApi.User) object;
            if (user.type.getConstructor() == TdApi.UserTypeRegular.CONSTRUCTOR && !clientRepository.isTelegramClientPresent(user.id)) {
                com.ewp.crm.models.Client newClient = new com.ewp.crm.models.Client();
                newClient.setName(user.firstName);
                newClient.setLastName(user.lastName);
                newClient.setPhoneNumber(user.phoneNumber);
                SocialProfile profile  = new SocialProfile(String.valueOf(user.id), socialProfileTypeService.getByTypeName("telegram"));
                newClient.setSocialProfiles(Collections.singletonList(profile));
                //TODO Хардкод. Вынести в меню?
                newClient.setStatus(statusRepository.findById(1L).get());
                newClient.addHistory(clientHistoryService.createHistory("Telegram"));
                com.ewp.crm.models.Client x = clientRepository.saveAndFlush(newClient);
                sendNotificationService.sendNotificationsAllUsers(newClient);
                logger.info("Client with Telegram id {} added from telegram.", user.id);
            }
        }
    }

    /**
     * Load Telegram messages by chat id.
     */
    private class GetChatMessagesHandler implements Client.ResultHandler {

        private TdApi.Messages messages;
        private boolean loading = true;

        public TdApi.Messages getMessages() {
            return messages;
        }

        public boolean isLoading() {
            return loading;
        }

        public void setLoading(boolean loading) {
            this.loading = loading;
        }

        @Override
        public void onResult(TdApi.Object object) {
            this.messages = (TdApi.Messages) object;
            this.loading = false;
        }
    }

}
