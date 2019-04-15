package com.ewp.crm.controllers;

import com.ewp.crm.models.ListMailing;
import com.ewp.crm.models.ListMailingType;
import com.ewp.crm.service.interfaces.ListMailingService;
import com.ewp.crm.service.interfaces.ListMailingTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Controller
public class ListMailingController {

    private final ListMailingService listMailingService;
    private final ListMailingTypeService listMailingTypeService;

    @Autowired
    public ListMailingController(ListMailingService listMailingService, ListMailingTypeService listMailingTypeService) {
        this.listMailingService = listMailingService;
        this.listMailingTypeService = listMailingTypeService;
    }


    @RequestMapping(value = "/list-mailing", method = RequestMethod.POST)
    public String addListMailing(@RequestParam("listName") String listName,
                                 @RequestParam("recipients") String recipients,
                                 @RequestParam("typeId") Long typeId) {
        ListMailingType type = listMailingTypeService.get(typeId);
        if (type != null) {
            List<String> recipientsList = Arrays.asList(recipients.split("\n"));
            ListMailing listMailing = new ListMailing(listName, recipientsList, type);
            listMailingService.addListMailing(listMailing);
        }
        return "redirect:/client/mailing";

    }

    @RequestMapping(value = "/edit/list-mailing", method = RequestMethod.POST)
    public String editListMailing(@RequestParam("editListName") String editlistName,
                                  @RequestParam("listId") Long id,
                                  @RequestParam("editRecipients") String editRecipients,
                                  @RequestParam("typeId") Long typeId) {
        ListMailingType type = listMailingTypeService.get(typeId);
        if (type != null) {
            Optional<ListMailing> listMailingOptional = listMailingService.getListMailingById(id);
            if (listMailingOptional.isPresent()) {
                ListMailing listMailing = listMailingOptional.get();
                List<String> editRecipientsList = new ArrayList<>(Arrays.asList(editRecipients.split("\n")));
                listMailing.setListName(editlistName);
                listMailing.setRecipients(editRecipientsList);
                listMailing.setType(type);
                listMailingService.update(listMailing);
            }
        }
        return "redirect:/client/mailing";
    }

    @RequestMapping(value = "/remove/list-mailing", method = RequestMethod.POST)
    public String removeListMailing(@RequestParam("listId") long id) {
        Optional<ListMailing> listMailing = listMailingService.getListMailingById(id);
        listMailing.ifPresent(listMailingService::delete);
        return "redirect:/client/mailing";
    }

}
