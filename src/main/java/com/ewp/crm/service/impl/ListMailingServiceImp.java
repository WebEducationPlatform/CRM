package com.ewp.crm.service.impl;

import com.ewp.crm.models.ListMailing;
import com.ewp.crm.repository.interfaces.ListMailingDAO;
import com.ewp.crm.service.interfaces.ListMailingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ListMailingServiceImp extends CommonServiceImpl<ListMailing> implements ListMailingService {

    @Autowired
    private ListMailingDAO listMailingDAO;


    public void addListMailing(ListMailing listMailing) {
        listMailingDAO.save(listMailing);
    }

    @Override
    public ListMailing getByListName(String listName) {
        return listMailingDAO.getByListName(listName);
    }
}
