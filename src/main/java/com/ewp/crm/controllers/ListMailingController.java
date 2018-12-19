package com.ewp.crm.controllers;

import com.ewp.crm.models.ListMailing;
import com.ewp.crm.service.interfaces.ListMailingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;


@Controller
public class ListMailingController {

    private final ListMailingService listMailingService;

    @Autowired
    public ListMailingController(ListMailingService listMailingService) {
        this.listMailingService = listMailingService;
    }


    @RequestMapping(value = "/list-mailing", method = RequestMethod.POST)
    public String addListMailing(
                                @RequestParam("listName") String listName,
                                @RequestParam("recipientsEmail") String recipientsEmail,
                                @RequestParam("recipientsSms") String recipientsSms,
                                @RequestParam("recipientsVk") String recipientsVk) {
        List<String> recipientsEmailList = Arrays.asList(recipientsEmail.split("\n"));
        List<String> recipientsSmsList = Arrays.asList(recipientsSms.split("\n"));
        List<String> recipientsVklList = Arrays.asList(recipientsVk.split("\n"));
        ListMailing listMailing = new ListMailing(listName, recipientsEmailList, recipientsSmsList, recipientsVklList);
        listMailingService.addListMailing(listMailing);
        return "redirect:/client/mailing";

    }

    @RequestMapping(value = "/edit/list-mailing", method = RequestMethod.POST)
    public String editListMailing(
            @RequestParam("editListName") String editlistName,
            @RequestParam("listName") String listName,
            @RequestParam("editRecipientsEmail") List<String> editRecipientsEmail,
            @RequestParam("editRecipientsSms") List<String> editRecipientsSms,
            @RequestParam("editRecipientsVk") List<String> editRecipientsVk) {
        ListMailing listMailing = listMailingService.getByListName(listName);
        listMailing.setListName(editlistName);
        listMailing.setRecipientsEmail(editRecipientsEmail);
        listMailing.setRecipientsSms(editRecipientsSms);
        listMailing.setRecipientsVk(editRecipientsVk);
        listMailingService.update(listMailing);
        return "redirect:/client/mailing";
    }

    @RequestMapping(value = "/remove/list-mailing", method = RequestMethod.POST)
    public String removeListMailing(@RequestParam("listName") String listName) {
        ListMailing listMailing = listMailingService.getByListName(listName);
        listMailingService.delete(listMailing);
        return "redirect:/client/mailing";
    }

}
