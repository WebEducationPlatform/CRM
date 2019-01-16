package com.ewp.crm.service.interfaces.vkcampaigns;

import com.ewp.crm.models.vkcampaigns.VkAddFriendsCampaign;
import com.ewp.crm.service.interfaces.CommonService;

import java.util.List;
import java.util.Map;

public interface VkCampaignService extends CommonService<VkAddFriendsCampaign> {

    void nextAttemptCycle();

    VkAddFriendsCampaign getByName(String name);

    Map<Long, Long> getRequestsStats();

    List<VkAddFriendsCampaign> getAllActiveCampaigns();

    void setActive(Long id, boolean b);

    Integer countProblems();
}
