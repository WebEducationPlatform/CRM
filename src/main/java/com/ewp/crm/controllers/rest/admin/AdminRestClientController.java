package com.ewp.crm.controllers.rest.admin;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.AssignSkypeCallService;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.StatusService;
import com.ewp.crm.service.interfaces.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/rest/admin/client")
public class AdminRestClientController {

    private static Logger logger = LoggerFactory.getLogger(AdminRestClientController.class);

    private final ClientService clientService;
    private final ClientHistoryService clientHistoryService;
    private final StatusService statusService;
    private final StudentService studentService;
    private final AssignSkypeCallService assignSkypeCallService;

    @Autowired
    public AdminRestClientController(AssignSkypeCallService assignSkypeCallService,
                                     ClientService clientService,
                                     ClientHistoryService clientHistoryService,
                                     StatusService statusService, StudentService studentService) {
        this.assignSkypeCallService = assignSkypeCallService;
        this.clientService = clientService;
        this.clientHistoryService = clientHistoryService;
        this.statusService = statusService;
        this.studentService = studentService;
    }

    @PostMapping(value = "/add")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'HR')")
    public ResponseEntity addClient(@RequestBody Client client,
                                    @AuthenticationPrincipal User userFromSession) {
        Optional<Status> status = statusService.get(client.getStatus().getName());
        status.ifPresent(client::setStatus);
        clientHistoryService.createHistory(userFromSession, client, ClientHistory.Type.ADD).ifPresent(client::addHistory);
        clientService.addClient(client, userFromSession);
        studentService.addStudentForClient(client);
        logger.info("{} has added client: id {}, email {}", userFromSession.getFullName(), client.getId(), client.getEmail().orElse("not found"));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping(value = "/update")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'MENTOR', 'HR')")
    public ResponseEntity updateClient(@RequestBody Client currentClient,
                                       @AuthenticationPrincipal User userFromSession) {
        Client clientFromDB = clientService.get(currentClient.getId());
        currentClient.setWhatsappMessages(clientFromDB.getWhatsappMessages());
        currentClient.setHistory(clientFromDB.getHistory());
        currentClient.setComments(clientFromDB.getComments());
        currentClient.setOwnerUser(clientFromDB.getOwnerUser());
        currentClient.setStatus(clientFromDB.getStatus());
        currentClient.setStudent(clientFromDB.getStudent());
        if (clientFromDB.getDateOfRegistration() == null) {
            clientService.setClientDateOfRegistrationByHistoryDate(currentClient);
        } else {
            currentClient.setDateOfRegistration(ZonedDateTime.parse(clientFromDB.getDateOfRegistration().toString()));
        }
        currentClient.setSmsInfo(clientFromDB.getSmsInfo());
        currentClient.setCanCall(clientFromDB.isCanCall());
        currentClient.setCallRecords(clientFromDB.getCallRecords());
        currentClient.setClientDescriptionComment(clientFromDB.getClientDescriptionComment());
        currentClient.setLiveSkypeCall(clientFromDB.isLiveSkypeCall());
        currentClient.setState(clientFromDB.getState());
        if (currentClient.equals(clientFromDB)) {
            return ResponseEntity.noContent().build();
        }
        clientHistoryService.createHistory(userFromSession, clientFromDB, currentClient, ClientHistory.Type.UPDATE).ifPresent(currentClient::addHistory);

        // Код ниже необходим чтобы задедектить изменение сущностей которые смапленны аннотацией @ElementCollection
        // Относится к списку почты и телефона, ошибка заключается в том что когда пытаешься изменить порядок уже существующих данных
        // Происходит ошибка уникальности (неправильны мердж сущности) в остальном всё ок
        // Если произошёл такой случай то руками удаляем зависимости, сохраняем и записываем что пришло
        List<String> emails = currentClient.getClientEmails();
        List<String> phones = currentClient.getClientPhones();

        List<String> emailsFromDb = clientFromDB.getClientEmails();
        List<String> phonesFromDb = clientFromDB.getClientPhones();

        boolean needUpdateClient = false;

        // Если размеры равны начинаем проверку
        int count = Math.min(emails.size(), emailsFromDb.size());
        for (int i = 0; i < count; i++) {
            // Если почты не равны взводим флаг что нужн доп апдейт клиента
            if (!emails.get(i).equals(emailsFromDb.get(i))) {
                emailsFromDb.clear();
                needUpdateClient = true;
                break;
            }
        }

        // Если флаг взведён даже не проверям телефоны
        if (!needUpdateClient) {
            count = Math.min(phones.size(), phonesFromDb.size());
            for (int i = 0; i < count; i++) {
                // Если почты не равны взводим флаг что нужн доп апдейт клиента
                if (!phones.get(i).equals(phonesFromDb.get(i))) {
                    phonesFromDb.clear();
                    needUpdateClient = true;
                    break;
                }
            }
        }

        // Проверяем достаточные условия для удаления/записи
        if (needUpdateClient) {
            clientService.updateClient(clientFromDB);
        }

        clientService.updateClient(currentClient);
        logger.info("{} has updated client: id {}, email {}", userFromSession.getFullName(), currentClient.getId(), currentClient.getEmail().orElse("not found"));
        return ResponseEntity.ok(HttpStatus.OK);
    }
    
    @GetMapping(value = "/remove")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'HR')")
    public ResponseEntity removeClient(@RequestParam(name = "clientId") Long clientId,
                                       @AuthenticationPrincipal User userFromSession) {
        Client clientFromDB = clientService.get(clientId);
        if (Objects.isNull(clientFromDB)) {
            return ResponseEntity.notFound().build();
        }
        clientService.delete(clientId);
        
        logger.info("{} has delete client: id {}, email {}", userFromSession.getFullName(), clientFromDB.getId(), clientFromDB.getEmail().orElse("not found"));
        return ResponseEntity.ok(HttpStatus.OK);
    }
}