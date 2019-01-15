package com.ewp.crm.repository.interfaces.vkcampaigns;

import com.ewp.crm.models.vkcampaigns.VkUser;

import java.util.List;

public interface VkUserRepositoryCustom {

    VkUser getOneWithoutAttempt(Long campaignId);

    List<VkUser> getAllByCampaignIdWithResponseValue(Long campaignId, Integer responseValue);

    Long countSentRequestsByCampaignId(Long campaignId);

    void deleteAllWithoutCampaigns();

    VkUser getOneWithoutAttemptNoDuplicates(Long campaignId);
}
