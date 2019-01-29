package com.ewp.crm.repository.interfaces.vkcampaigns;

import com.ewp.crm.models.vkcampaigns.VkUser;
import com.ewp.crm.repository.interfaces.CommonGenericRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VkUserRepository extends CommonGenericRepository<VkUser>, VkUserRepositoryCustom {
}
