package com.ewp.crm.service;

import com.ewp.crm.dao.StatusDAO;
import com.ewp.crm.models.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatusServiceImpl implements StatusService {

    @Autowired
    StatusDAO statusDAO;
    @Override
    public Status getStatusByName(String name) {
        return statusDAO.findStatusByName(name);
    }
}
