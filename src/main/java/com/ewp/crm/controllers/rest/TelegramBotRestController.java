package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.*;
import com.ewp.crm.repository.interfaces.StatusDAO;
import com.ewp.crm.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import java.util.Optional;

@RestController
@RequestMapping("/rest/telegrambot")
@PreAuthorize("hasAuthority('USER')")
public class TelegramBotRestController {
    private static Logger logger = LoggerFactory.getLogger(TelegramBotRestController.class);

    private TelegramClientReqService telegramClientReqService;
    private ClientHistoryService clientHistoryService;
    private ClientService clientService;
    private CommentService commentService;
    private StatusDAO statusRepository;
    private ProjectPropertiesService projectPropertiesService;

    @Autowired
    public TelegramBotRestController(TelegramClientReqService telegramClientReqService,
                                     ClientHistoryService clientHistoryService,
                                     ClientService clientService,
                                     CommentService commentService,
                                     StatusDAO statusRepository,
                                     ProjectPropertiesService projectPropertiesService
                                     ) {

        this.telegramClientReqService = telegramClientReqService;
        this.clientHistoryService = clientHistoryService;
        this.clientService = clientService;
        this.commentService = commentService;
        this.statusRepository = statusRepository;
        this.projectPropertiesService = projectPropertiesService;
    }

    /**
     * Метод получает данные клиента при начале диалога с ботом.
     * Сначала проверяет, были ли от клиента заявки раньше. Если заявки были, то обновляет основные данные,
     * если нет, то создает нового клиента.
     * @param telegramClientReq заявка, полученная от бота
     * @param userFromSession
     * @return
     */
    @PostMapping("/start")
    public HttpStatus startConversation(@RequestBody TelegramClientReq telegramClientReq,
                                @AuthenticationPrincipal User userFromSession) {
        Optional<TelegramClientReq> existClientReq = telegramClientReqService.getByUserId(telegramClientReq.getUserId());
        if (existClientReq.isPresent()) {
            // Обновление основных данных из заявки
            mergeClientReq(telegramClientReq, existClientReq.get());
            telegramClientReqService.update(existClientReq.get());
        } else {
            // Создание нового клиента и сохранение заявки
            addNewClientAndSaveClientReq(telegramClientReq, userFromSession);
            existClientReq = Optional.of(telegramClientReq);
        }
        logger.info("TelegramBot {} has started conversation with Client with id {}", userFromSession.getFullName(), existClientReq.get().getClient().getId());
        return HttpStatus.OK;
    }

    /**
     * Метод обновляет данные в процессе получения заявки.
     * Сначала проверяет, были ли от клиента заявки раньше, затем получает из заявки имя, e-mail, телефон и город.
     * После этого обновляет заявку от клиента и данные клиента, и создает клиентскую историю об обновлении.
     * @param telegramClientReq заявка, полученная от бота
     * @param userFromSession
     * @return
     */
    @PostMapping("/request")
    public HttpStatus handleRequest(@RequestBody TelegramClientReq telegramClientReq,
                                @AuthenticationPrincipal User userFromSession) {
        TelegramClientReq existClientReq = telegramClientReqService.getByUserId(telegramClientReq.getUserId()).orElseGet(() -> {
            logger.info("Can't find client for telegramBotClientReq with id: " + telegramClientReq.getUserId());
            addNewClientAndSaveClientReq(telegramClientReq, userFromSession);
            return telegramClientReq;
        });

        // Обновление основных данных из заявки
        mergeClientReq(telegramClientReq, existClientReq);

        Client client = existClientReq.getClient();

        // Обновление данных заявки и клиента
        if (telegramClientReq.getInputName() != null && !telegramClientReq.getInputName().equals(client.getName())) {
            existClientReq.setInputName(telegramClientReq.getInputName());
            client.setName(telegramClientReq.getInputName());
        }
        if (telegramClientReq.getPhone() != null && !client.getClientPhones().contains(telegramClientReq.getPhone())) {
            existClientReq.setPhone(telegramClientReq.getPhone());
            client.setPhoneNumber(telegramClientReq.getPhone());
        }
        if (telegramClientReq.getEmail() != null && !client.getClientEmails().contains(telegramClientReq.getEmail())) {
            existClientReq.setEmail(telegramClientReq.getEmail());
            client.setEmail(telegramClientReq.getEmail());
        }
        if (telegramClientReq.getCity() != null && !telegramClientReq.getCity().equals(client.getCity())) {
            existClientReq.setCity(telegramClientReq.getCity());
            client.setCity(telegramClientReq.getCity());
        }
        if (telegramClientReq.getQuestion() != null) {
            existClientReq.setQuestion(telegramClientReq.getQuestion());
            Comment comment = new Comment(userFromSession, client, "Клиент задал вопрос: " + telegramClientReq.getQuestion());
            commentService.add(comment);
        }

        telegramClientReqService.update(existClientReq);
        clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.UPDATE).ifPresent(client::addHistory);
        clientService.updateClient(client);
        logger.info("TelegramBot {} has updated client with id {}", userFromSession.getFullName(), client.getId());
        return HttpStatus.OK;
    }

    /**
     * Метод обрабатывает запросы о получении обратного звонка.
     * Сначала проверяет, были ли от клиента заявки раньше, затем обновляет основные данные из заявки и номер телефона.
     * После этого создает комментарий об обратном звонке.
     * @param telegramClientReq заявка, полученная от бота
     * @param userFromSession
     * @return
     */
    @PostMapping("/call")
    public HttpStatus askCall(@RequestBody TelegramClientReq telegramClientReq,
                              @AuthenticationPrincipal User userFromSession) {
        TelegramClientReq existClientReq = telegramClientReqService.getByUserId(telegramClientReq.getUserId()).orElseGet(() -> {
            logger.info("Can't find client for telegramBotClientReq with id: " + telegramClientReq.getUserId());
            addNewClientAndSaveClientReq(telegramClientReq, userFromSession);
            return telegramClientReq;
        });

        // Обновление основных данных из заявки и номера телефона
        mergeClientReq(telegramClientReq, existClientReq);
        existClientReq.setPhone(telegramClientReq.getPhone());
        telegramClientReqService.update(existClientReq);

        // Обновление номера у клиента
        Client client = existClientReq.getClient();
        client.setPhoneNumber(existClientReq.getPhone());

        // Создание комментария
        Comment comment = new Comment(userFromSession, client, "Клиент запросил обратный звонок.");
        commentService.add(comment);

        logger.info("TelegramBot {} has received request to call Client with id {}", userFromSession.getFullName(), client.getId());
        return HttpStatus.OK;
    }

    /**
     * Метод обновляет основные данные одной заявки данными из другой заявки.
     * @param from
     * @param to
     */
    private TelegramClientReq mergeClientReq(TelegramClientReq from, TelegramClientReq to) {
        to.setChatId(from.getChatId());
        to.setFirstName(from.getFirstName());
        to.setLastName(from.getLastName());
        to.setUsername(from.getUsername());
        return to;
    }

    /**
     * Метод создает нового клиента с историей и уведомлениями и сохраняет заявку.
     * Сначала создает нового клиента, устанавливает ему первоначальный статус и создает клиентскую историю.
     * Затем отправляет уведомления пользователям о новом клиенте и устанавливает клиента для заявки, после этого
     * сохраняет клиента и заявку в базу.
     * @param telegramClientReq
     * @param userFromSession
     */
    private Client addNewClientAndSaveClientReq(TelegramClientReq telegramClientReq, User userFromSession) {
        Client newClient = new Client.Builder(telegramClientReq.getFirstName()).build();
        newClient.setLastName(telegramClientReq.getLastName());
        newClient.setClientDescriptionComment("TelegramBot добавил клиента.");
        newClient.setStatus(statusRepository.findById(projectPropertiesService.getOrCreate().getNewClientStatus()).get());
        clientHistoryService.createHistory(userFromSession, newClient, ClientHistory.Type.SOCIAL_REQUEST).ifPresent(history -> {
            history.setTitle(history.getTitle() + " чата с TelegramBot");
            newClient.addHistory(history);
        });
        clientService.addClient(newClient);
        telegramClientReq.setClient(newClient);
        telegramClientReqService.add(telegramClientReq);
        logger.info("TelegramBot {} has added client with id {}", userFromSession.getFullName(), newClient.getId());
        return newClient;
    }
}
