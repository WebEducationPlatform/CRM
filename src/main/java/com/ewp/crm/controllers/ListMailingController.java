package com.ewp.crm.controllers;

import com.ewp.crm.models.ListMailing;
import com.ewp.crm.service.interfaces.ListMailingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class ListMailingController {

    private ListMailingService listMailingService;

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

        ListMailing listMailing = new ListMailing(listName, recipientsEmail, recipientsSms, recipientsVk);
        listMailingService.addListMailing(listMailing);
        return "redirect:/client/mailing";

    }
}
