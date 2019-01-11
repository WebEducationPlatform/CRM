package com.ewp.crm.repository.interfaces.vkcampaigns;

import com.ewp.crm.models.vkcampaigns.VkAddFriendsCampaign;
import com.ewp.crm.repository.interfaces.CommonGenericRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VkCampaignRepository extends CommonGenericRepository<VkAddFriendsCampaign> {

    VkAddFriendsCampaign findFirstByCampaignName(String name);

}
