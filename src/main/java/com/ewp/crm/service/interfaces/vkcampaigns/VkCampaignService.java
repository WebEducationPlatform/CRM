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

    void setActive(Long id, boolean isActive);

    void setProblem(Long id);

    Integer countProblems();

    Long countAddedFriends(Long id);

    Long countRequestsWithResponseCode(Long id, Integer responseCode);

    Long countVkIdsInList(Long id);
}
