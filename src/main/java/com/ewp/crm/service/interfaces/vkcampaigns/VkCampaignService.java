package com.ewp.crm.service.interfaces.vkcampaigns;

import com.ewp.crm.models.vkcampaigns.VkAddFriendsCampaign;
import com.ewp.crm.service.interfaces.CommonService;

import java.util.Map;

public interface VkCampaignService extends CommonService<VkAddFriendsCampaign> {

    void nextAttemptCycle();

    VkAddFriendsCampaign getByName(String name);

    Map<Long, Long> getRequestsStats();
}
