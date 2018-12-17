package com.ewp.crm.service.impl;

import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.ewp.crm.repository.interfaces.SocialProfileRepository;
import com.ewp.crm.repository.interfaces.StatusDAO;
import com.ewp.crm.service.conversation.ChatMessage;
import com.ewp.crm.service.conversation.ChatType;
import com.ewp.crm.service.conversation.Interlocutor;
import com.ewp.crm.service.conversation.JMConversation;
import com.ewp.crm.service.interfaces.*;
import org.apache.commons.io.FileUtils;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.Log;
import org.drinkless.tdlib.TdApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@PropertySource(value = "file:./telegram.properties", encoding = "Cp1251")
public class TelegramServiceImpl implements TelegramService, JMConversation {

    private static Client client = null;
    private static TdApi.AuthorizationState authorizationState = null;
    private static volatile boolean haveAuthorization = false;
    private static volatile boolean quiting = false;

    private static final Lock authorizationLock = new ReentrantLock();
    private static final Condition gotAuthorization = authorizationLock.newCondition();

    private static final int RETRY_COUNT = 15;
    private static final int GET_OBJECT_MAX_DELAY = 2;

    private static boolean tdlibInstalled = false;

    private static String phoneNumber = null;
    private static String code = null;
    private static String password = null;

    private static Environment env;
    private final boolean useMessageDatabase;
    private static Logger logger = LoggerFactory.getLogger(TelegramServiceImpl.class);

    private static final ConcurrentMap<Integer, TdApi.File> downloadingFiles = new ConcurrentHashMap<Integer, TdApi.File>();

    private final ClientRepository clientRepository;
    private final StatusDAO statusRepository;
    private final ClientHistoryService clientHistoryService;
    private final SendNotificationService sendNotificationService;
    private final SocialProfileRepository socialProfileRepository;
    private final SocialProfileTypeService socialProfileTypeService;
    private final ProjectPropertiesService projectPropertiesService;

    @Autowired
    public TelegramServiceImpl(Environment env, ClientRepository clientRepository, StatusDAO statusRepository,
                               ClientHistoryService clientHistoryService, SendNotificationService sendNotificationService,
                               SocialProfileRepository socialProfileRepository, SocialProfileTypeService socialProfileTypeService,
                               ProjectPropertiesService projectPropertiesService) {
        this.env = env;
        this.useMessageDatabase = Boolean.parseBoolean(env.getRequiredProperty("telegram.useMessageDatabase"));
        this.clientRepository = clientRepository;
        this.statusRepository = statusRepository;
        this.clientHistoryService = clientHistoryService;
        this.sendNotificationService = sendNotificationService;
        this.socialProfileRepository = socialProfileRepository;
        this.socialProfileTypeService = socialProfileTypeService;
        this.projectPropertiesService = projectPropertiesService;
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

    public TdApi.Chat createPrivateChat(int userId) {
        GetObjectHandler handler = new GetObjectHandler();
        client.send(new TdApi.CreatePrivateChat(userId, false), handler);
        handlerDelay(handler);
        return (TdApi.Chat) handler.getObject();
    }

    @Override
    public TdApi.Messages getChatMessages(long chatId, int limit) {
        if (!getChat(chatId).isPresent()) {
            createPrivateChat((int) chatId);
        }
        openChat(chatId);
        GetObjectHandler handler = new GetObjectHandler();
        TdApi.Messages messages;
        int counter = 0;
        do {
            client.send(new TdApi.GetChatHistory(chatId, 0, 0, limit, false), handler);
            handlerDelay(handler);
            messages = (TdApi.Messages) handler.getObject();
            handler.setObject(null);
            if(counter++ > RETRY_COUNT) {
                break;
            }
        } while (messages.totalCount <= 1);
        markMessagesAsRead(chatId, messages);
        return messages;
    }

    private void openChat(long chatId) {
        GetObjectHandler handler = new GetObjectHandler();
        client.send(new TdApi.OpenChat(chatId), handler);
        handlerDelay(handler);
    }

    @Override
    public TdApi.Messages getUnreadMessagesFromChat(long chatId, int limit) {
        TdApi.Messages messages = new TdApi.Messages(0, new TdApi.Message[]{});
        Optional<TdApi.Chat> chat = getChat(chatId);
        if (chat.isPresent() && chat.get().unreadCount != 0) {
            GetObjectHandler handler = new GetObjectHandler();
            do {
                client.send(new TdApi.GetChatHistory(chatId, 0, 0, chat.get().unreadCount, false), handler);
                handlerDelay(handler);
                messages = (TdApi.Messages) handler.getObject();
                handler.setObject(null);
            } while (messages.totalCount < chat.get().unreadCount);
            markMessagesAsRead(chatId, messages);
        }
        return messages;
    }

    private void markMessagesAsRead(long chatId, TdApi.Messages messages) {
        long[] messageIds = new long[messages.totalCount];
        for (int i = 0; i < messages.messages.length; i++) {
            messageIds[i] = messages.messages[i].id;
        }
        client.send(new TdApi.ViewMessages(chatId, messageIds, false), new DefaultHandler());
    }

    @Override
    public TdApi.Message sendChatMessage(long chatId, String text) {
        GetObjectHandler handler = new GetObjectHandler();
        TdApi.InputMessageContent content = new TdApi.InputMessageText(new TdApi.FormattedText(text, null), false, true);
        client.send(new TdApi.SendMessage(chatId, 0, false, false, null, content ), handler);
        handlerDelay(handler);
        return (TdApi.Message) handler.getObject();
    }

    @Override
    public Optional<TdApi.Chat> getChat(long chatId) {
        Optional<TdApi.Chat> result = Optional.empty();
        GetObjectHandler handler = new GetObjectHandler();
        client.send(new TdApi.GetChat(chatId), handler);
        if (handlerDelay(handler)) {
            result = Optional.of((TdApi.Chat) handler.getObject());
        }
        return result;
    }

    @Override
    public TdApi.User getMe() {
        GetObjectHandler handler = new GetObjectHandler();
        client.send(new TdApi.GetMe(), handler);
        handlerDelay(handler);
        return (TdApi.User) handler.getObject();
    }

    @Override
    public TdApi.User getUserById(int userId) {
        GetObjectHandler handler = new GetObjectHandler();
        client.send(new TdApi.GetUser(userId), handler);
        handlerDelay(handler);
        return (TdApi.User) handler.getObject();
    }

    @Override
    public TdApi.File getFileById(int fileId) {
        GetObjectHandler handler = new GetObjectHandler();
        client.send(new TdApi.GetFile(fileId), handler);
        handlerDelay(handler);
        return (TdApi.File) handler.getObject();
    }

    @Override
    public String downloadFile(TdApi.File file) throws IOException {
        byte[] fileContent = new byte[0];
        if (file.local.canBeDownloaded && !file.local.isDownloadingCompleted) {
            client.send(new TdApi.DownloadFile(file.id, 1), new DefaultHandler());
            downloadingFiles.putIfAbsent(file.id, file);
            while (downloadingFiles.containsKey(file.id) && !downloadingFiles.get(file.id).local.isDownloadingCompleted) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    logger.warn("File loading interrupted", e);
                    break;
                }
            }
            fileContent = FileUtils.readFileToByteArray(new File(downloadingFiles.get(file.id).local.path));
            downloadingFiles.remove(file.id);
        } else if (file.local.isDownloadingCompleted) {
            fileContent = FileUtils.readFileToByteArray(new File(file.local.path));
        }
        return Base64.getEncoder().encodeToString(fileContent);
    }

    @Override
    public int getClientIdByPhone(String phone) {
        GetObjectHandler handler = new GetObjectHandler();
        TdApi.Contact contact = new TdApi.Contact(phone, null, null, null, 0);
        client.send(new TdApi.ImportContacts(new TdApi.Contact[]{contact}), handler);
        handlerDelay(handler);
        TdApi.ImportedContacts contacts = (TdApi.ImportedContacts) handler.getObject();
        return contacts.userIds[0];
    }

    @Override
    public void closeChat(long chatId) {
        client.send(new TdApi.CloseChat(chatId), new DefaultHandler());
    }

    @Override
    public void logout() {
        client.send(new TdApi.LogOut(), new DefaultHandler());
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

    //JMConversation Implementation//
    @Override
    public ChatType getChatTypeOfConversation() {
        return ChatType.telegram;
    }

    @Override
    public void endChat(String chatId) {
        closeChat(Long.parseLong(chatId));
    }

    @Override
    public ChatMessage sendMessage(ChatMessage message) {
        TdApi.Message tgMessage = sendChatMessage(Long.parseLong(message.getChatId()), message.getText());
        return tdlibMessageToChatMessage(tgMessage);
    }

    @Override
    public List<ChatMessage> getNewMessages(String chatId, int count) {
        TdApi.Messages tgMessages = getUnreadMessagesFromChat(Long.parseLong(chatId), count);
        return tdlibMessagesToChatMessages(tgMessages);
    }

    @Override
    public List<ChatMessage> getMessages(String chatId, int count) {
        TdApi.Messages tgMessages = getChatMessages(Long.parseLong(chatId), count);
        return tdlibMessagesToChatMessages(tgMessages);
    }

    @Override
    public List<ChatMessage> getReadMessages(String chatId) {
        Optional<TdApi.Chat> chat = getChat(Long.parseLong(chatId));
        //TODO
        return null;
    }

    @Override
    public Interlocutor getInterlocutor(String recipientId) {
        TdApi.User user = getUserById(Integer.parseInt(recipientId));
        return tdlibUserToInterlocutor(user);
    }

    @Override
    public Interlocutor getMe(String recipientId) {
        TdApi.User user = getMe();
        return tdlibUserToInterlocutor(user);
    }

    private ChatMessage tdlibMessageToChatMessage(TdApi.Message message) {
        ZonedDateTime time = ZonedDateTime.ofInstant(Instant.ofEpochMilli(message.date), TimeZone.getDefault().toZoneId());
        return new ChatMessage(message.id, String.valueOf(message.chatId), ChatType.telegram, message.content.toString(), time, false, true);
    }

    private List<ChatMessage> tdlibMessagesToChatMessages(TdApi.Messages messages) {
        List<ChatMessage> result = new ArrayList<>();
        for (TdApi.Message message : messages.messages) {
            result.add(tdlibMessageToChatMessage(message));
        }
        return result;
    }

    private Interlocutor tdlibUserToInterlocutor(TdApi.User user) {
        TdApi.File file = getFileById(user.profilePhoto.small.id);
        String base64 = null;
        try {
            base64 = downloadFile(file);
        } catch (IOException e) {
            logger.error("File download failed!", e);
        }
        return new Interlocutor(String.valueOf(user.id), user.firstName, user.lastName, user.username, null, base64, ChatType.telegram);
    }

    //JMConversation Implementation//

    private class UpdatesHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {
            switch (object.getConstructor()) {
                case TdApi.UpdateAuthorizationState.CONSTRUCTOR: {
                    onAuthorizationStateUpdated(((TdApi.UpdateAuthorizationState) object).authorizationState);
                    break;
                }
                case TdApi.UpdateNewChat.CONSTRUCTOR: {
                    TdApi.Chat chat = ((TdApi.UpdateNewChat) object).chat;
                    if (chat.type instanceof TdApi.ChatTypePrivate) {
                        TdApi.ChatTypePrivate chatTypePrivate = (TdApi.ChatTypePrivate) chat.type;
                        client.send(new TdApi.GetUser(chatTypePrivate.userId), new NewUserHandler());
                    }
                    break;
                }
                case TdApi.UpdateFile.CONSTRUCTOR: {
                    TdApi.File file = ((TdApi.UpdateFile) object).file;
                    if (downloadingFiles.containsKey(file.id)) {
                        downloadingFiles.replace(file.id, file);
                        if (!useMessageDatabase && file.local.isDownloadingCompleted) {
                            cleanFileCopies(file.local.path);
                        }
                    }
                }
            }
        }
    }

    /**
     * Clean downloaded file copies. Retains last downloaded copy.
     * Api downloads copies of file with such names:
     * 255414630_9012.jpg
     * 255414630_9012_(0).jpg
     * @param path of downloaded file.
     */
    private void cleanFileCopies(String path) {
        String folderPath = path.substring(0, path.lastIndexOf(File.separator));
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();
        if (path.contains("_(")) {
            String fileName = path.substring(path.lastIndexOf(File.separator) + 1, path.lastIndexOf("_("));
            for (File savedFile : listOfFiles) {
                if (!path.equals(savedFile.getAbsolutePath()) && savedFile.getAbsolutePath().contains(fileName)) {
                    savedFile.delete();
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
                                    env.getRequiredProperty("telegram.proxy.password"))), new DefaultHandler());
                } else {
                    client.send(new TdApi.DisableProxy(), new DefaultHandler());
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
                    logger.error("Receive an error: {}", object);
                    onAuthorizationStateUpdated(null); // repeat last action
                    break;
                case TdApi.Ok.CONSTRUCTOR:
                    // result is already received through UpdateAuthorizationState, nothing to do
                    break;
                default:
                    logger.error("Receive wrong response from TDLib: {}", object);
            }
        }
    }

    private static class DefaultHandler implements Client.ResultHandler {

        private StackTraceElement[] trace = Thread.currentThread().getStackTrace();

        @Override
        public void onResult(TdApi.Object object) {
            if (!(object instanceof TdApi.Ok)) {
                if (object instanceof TdApi.Error) {
                    logger.error("Telegram method returned error object:\r\n {};\r\n {}", object.toString(), trace);
                } else {
                    logger.info("Method {} returned object:\r\n {}", trace[3], object.toString());
                }
            }
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
                newClient.setStatus(statusRepository.findById(projectPropertiesService.getOrCreate().getNewClientStatus()).get());
                newClient.addHistory(clientHistoryService.createHistory("Telegram"));
                clientRepository.saveAndFlush(newClient);
                sendNotificationService.sendNotificationsAllUsers(newClient);
                logger.info("Client with Telegram id {} added from telegram.", user.id);
            }
        }
    }

    /**
     * Get telegram object.
     */
    private interface GetObject {
        TdApi.Object getObject();
    }

    /**
     * Get telegram object handler.
     */
    private class GetObjectHandler implements Client.ResultHandler, GetObject {
        private StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        private TdApi.Object object;

        @Override
        public TdApi.Object getObject() {
            return object;
        }

        public StackTraceElement[] getTrace() {
            return trace;
        }

        public void setObject(TdApi.Object object) {
            this.object = object;
        }

        @Override
        public void onResult(TdApi.Object object) {
            this.object = object;
        }
    }


    private boolean handlerDelay(GetObjectHandler handler) {
        LocalTime delay = LocalTime.now().plusSeconds(GET_OBJECT_MAX_DELAY);
        boolean result = true;
        while (handler.getObject() == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                logger.warn("Object retrieval interrupted", e);
                break;
            }
            if (LocalTime.now().isAfter(delay)) {
                result = false;
            }
        }
        if (handler.getObject() instanceof TdApi.Error) {
            result = false;
            logger.error("Object retrieval error:\r\n{}\r\nin method {}", handler.getObject(), handler.getTrace()[3]);
        }
        return result;
    }
}
