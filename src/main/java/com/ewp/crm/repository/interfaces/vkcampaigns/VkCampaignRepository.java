package com.ewp.crm.repository.interfaces.vkcampaigns;

import com.ewp.crm.models.vkcampaigns.VkAddFriendsCampaign;
import com.ewp.crm.repository.interfaces.CommonGenericRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VkCampaignRepository extends CommonGenericRepository<VkAddFriendsCampaign> {

    VkAddFriendsCampaign findFirstByCampaignName(String name);

    List<VkAddFriendsCampaign> findAllByActiveTrue();

}
