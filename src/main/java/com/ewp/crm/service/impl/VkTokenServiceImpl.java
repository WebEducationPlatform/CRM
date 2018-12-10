package com.ewp.crm.service.impl;


import com.ewp.crm.models.VkToken;
import com.ewp.crm.repository.interfaces.VkTokenDAO;
import com.ewp.crm.service.interfaces.VkTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VkTokenServiceImpl extends CommonServiceImpl<VkToken> implements VkTokenService {

    @Autowired
    private VkTokenDAO tokenDAO;


    @Override
    public List<VkToken> getTokenByIdSender(long id) {
        return tokenDAO.getVkTokenByIdSender(id);
    }
}
