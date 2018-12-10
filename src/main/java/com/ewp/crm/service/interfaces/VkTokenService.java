package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.VkToken;

import java.util.List;


public interface VkTokenService extends CommonService<VkToken> {
    List<VkToken> getTokenByIdSender(long id);
}
