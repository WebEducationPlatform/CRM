package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.MessageSubject;
import com.ewp.crm.models.MessageTemplate;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.StudentStatus;
import com.ewp.crm.repository.interfaces.StudentStatusRepository;
import com.ewp.crm.service.interfaces.MessageSubjectService;
import com.ewp.crm.service.interfaces.MessageTemplateService;
import com.ewp.crm.service.interfaces.StatusService;
import com.google.api.client.http.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/rest/subject-template")
public class MessageSubjectRestController {
    @Autowired
    private MessageSubjectService messageSubjectService;
    @Autowired
    private MessageTemplateService messageTemplateService;
    @Autowired
    private StatusService statusService;

    @PostMapping
    public void saveOrUpdate(HttpServletRequest request) {
        MessageSubject messageSubject = parseMessageObjectFromRequest(request);
        Optional<MessageSubject> byTitle = messageSubjectService.getByTitle(messageSubject.getTitle());



        if (byTitle.isPresent()) {
            Long id = byTitle.get().getSubject_id();
            messageSubject.setSubject_id(id);

            messageSubjectService.update(messageSubject);
        }

        if (!byTitle.isPresent()) {
            messageSubjectService.add(messageSubject);
        }

    }

    @RequestMapping(value = "/delete")
    public void delete(HttpServletRequest request) {
        MessageSubject messageSubject = parseMessageObjectFromRequest(request);
        Optional<MessageSubject> messageSubjectServiceByTitle = messageSubjectService.getByTitle(messageSubject.getTitle());
        if (messageSubjectServiceByTitle.isPresent()) {
            messageSubjectService.delete(messageSubjectServiceByTitle.get());
        }
    }

    @GetMapping
    public String getTable() {
        StringBuilder trBuilder = new StringBuilder();
        List<MessageSubject> messageSubjectList = messageSubjectService.getAll();

        for (int i = 0; i < messageSubjectList.size(); i++) {
            MessageSubject messageSubject = messageSubjectList.get(i);
            trBuilder.append("<tr>");
            createBodyOfTable(messageSubject, trBuilder);
            trBuilder.append("</tr>");
        }
        return trBuilder.toString();
    }


    private void createBodyOfTable(MessageSubject messageSubject, StringBuilder trBuilder){

        StringBuilder statusBuilder = new StringBuilder();
        StringBuilder templateBuilder = new StringBuilder();

        List<Status> statuses = statusService.getAll();
        List<MessageTemplate> allMessageTemplates = messageTemplateService.getAll();

        String title = messageSubject.getTitle();
        String template = messageSubject.getMessageTemplate().getName();
        String statusName;

        if(messageSubject.getStatus() == null){
            statusName = "Не добавлять в статус";
        }else{
            statusName = messageSubject.getStatus().getName();
        }



        // создаем поле для ввода темы сообщения и заполняем значением из базы данных
        trBuilder.append("<td><input type='text' value='"+messageSubject.getTitle()+"' required/></td>");

        // создаем выпадающий список шаблонов
        templateBuilder.append("<td>");
        templateBuilder.append("<select>");
        for (int i = 0; i < allMessageTemplates.size(); i++) {
            String item = allMessageTemplates.get(i).getName();

            if(template.equals(item)){
                templateBuilder.append("<option value='" + item + "' selected='selected'>" + item + "</option>");
            }else{
                templateBuilder.append("<option value='" + item + "'>" + item + "</option>");
            }

        }
        templateBuilder.append("</select>");
        templateBuilder.append("</td>");

        trBuilder.append(templateBuilder.toString());

        // создаем выпадающий список статусов
        statusBuilder.append("<td>");
        statusBuilder.append("<select>");
        statusBuilder.append("<option value='Не добавлять в статус'>Не добавлять в статус</option>");
        for (int i = 0; i < statuses.size(); i++) {

            String item = statuses.get(i).getName();

            if(statusName.equals(item)){
                statusBuilder.append("<option value='" + item + "' selected='selected'>" + item + "</option>");
            }else{
                statusBuilder.append("<option value='" + item + "'>" + item + "</option>");
            }

        }
        statusBuilder.append("</select>");
        statusBuilder.append("</td>");

        trBuilder.append(statusBuilder.toString());

        // add buttons
        if(title.equalsIgnoreCase("Не известный")){
            trBuilder.append("<td>");
            trBuilder.append("</td>");
            trBuilder.append("<td>");
            trBuilder.append("<button class=\"save-row-button\"><i class=\"far fa-save\"></i></button>");
            trBuilder.append("</td>");
        }else{
            trBuilder.append("<td>");
            trBuilder.append("<button class=\"remove-row-button\"><i class=\"far fa-trash-alt\"></i></button>");
            trBuilder.append("</td>");
            trBuilder.append("<td>");
            trBuilder.append("<button class=\"save-row-button\"><i class=\"far fa-save\"></i></button>");
            trBuilder.append("</td>");
        }

    }


    private MessageSubject parseMessageObjectFromRequest(HttpServletRequest request) {
        String title = "";
        String template = "";
        String status = "";
        String line = "";

        Optional<MessageTemplate> templateByName = Optional.empty();
        Optional<Status> statusByName = Optional.empty();

        Map<String, String> subjectMap = new HashMap<>();

        try {
            line = request.getReader()
                    .readLine()
                    .replace("\\", "")
                    .replace("\"{", "")
                    .replace("}\"", "");

            Scanner scanner = new Scanner(line).useDelimiter(",");
            while (scanner.hasNext()) {
                String next = scanner.next();
                int index = next.indexOf(":");

                String key = next.substring(1, index - 1);
                String value = next.substring(index + 2, next.length() - 1);
                subjectMap.put(key, value);
            }
            title = subjectMap.get("title");
            template = subjectMap.get("template");
            status = subjectMap.get("status");

            templateByName = messageTemplateService.getByName(template);

            if(!status.equals("Не добавлять в статус")){
                statusByName = statusService.getStatusByName(status);
            }else{
                return new MessageSubject(title, templateByName.get(), null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new MessageSubject(title, templateByName.get(), statusByName.get());
    }


}
