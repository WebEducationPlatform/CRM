package com.ewp.crm.controllers;

import com.ewp.crm.models.ListMailing;
import com.ewp.crm.service.interfaces.ListMailingService;
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

    @Autowired
    public ListMailingController(ListMailingService listMailingService) {
        this.listMailingService = listMailingService;
    }


    @RequestMapping(value = "/list-mailing", method = RequestMethod.POST)
    public String addListMailing(@RequestParam("listName") String listName,
                                @RequestParam("recipientsEmail") String recipientsEmail,
                                @RequestParam("recipientsSms") String recipientsSms,
                                @RequestParam("recipientsVk") String recipientsVk,
                                @RequestParam("recipientsSlack") String recipientsSlack) {
        List<String> recipientsEmailList = Arrays.asList(recipientsEmail.split("\n"));
        List<String> recipientsSmsList = Arrays.asList(recipientsSms.split("\n"));
        List<String> recipientsVkList = Arrays.asList(recipientsVk.split("\n"));
        List<String> recipientsSlackList = Arrays.asList(recipientsSlack.split("\n"));
        ListMailing listMailing = new ListMailing(listName, recipientsEmailList, recipientsSmsList, recipientsVkList, recipientsSlackList);
        listMailingService.addListMailing(listMailing);
        return "redirect:/client/mailing";

    }

    @RequestMapping(value = "/edit/list-mailing", method = RequestMethod.POST)
    public String editListMailing(
            @RequestParam("editListName") String editlistName,
            @RequestParam("listId") Long id,
            @RequestParam("editRecipientsEmail") String editRecipientsEmail,
            @RequestParam("editRecipientsSms") String editRecipientsSms,
            @RequestParam("editRecipientsVk") String editRecipientsVk,
            @RequestParam("editRecipientsSlack") String editRecipientsSlack) {
        Optional<ListMailing> listMailingOptional = listMailingService.getListMailingById(id);
        if (listMailingOptional.isPresent()) {
            ListMailing listMailing = listMailingOptional.get();
            List<String> editRecipientsEmailList = new ArrayList<>(Arrays.asList(editRecipientsEmail.split("\n")));
            List<String> editRecipientsSmsList = new ArrayList<>(Arrays.asList(editRecipientsSms.split("\n")));
            List<String> editRecipientsVkList = new ArrayList<>(Arrays.asList(editRecipientsVk.split("\n")));
            List<String> editRecipientsSlackList = new ArrayList<>(Arrays.asList(editRecipientsSlack.split("\n")));
            listMailing.setListName(editlistName);
            listMailing.setRecipientsEmail(editRecipientsEmailList);
            listMailing.setRecipientsSms(editRecipientsSmsList);
            listMailing.setRecipientsVk(editRecipientsVkList);
            listMailing.setRecipientsSlack(editRecipientsSlackList);
            listMailingService.update(listMailing);
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
