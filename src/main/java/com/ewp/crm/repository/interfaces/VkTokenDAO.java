package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.VkToken;

import java.util.List;

public interface VkTokenDAO extends CommonGenericRepository<VkToken> {

    List<VkToken> getVkTokenByIdSender(long id);
}
