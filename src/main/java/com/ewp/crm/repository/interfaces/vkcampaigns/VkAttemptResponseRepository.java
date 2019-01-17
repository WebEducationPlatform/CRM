package com.ewp.crm.repository.interfaces.vkcampaigns;

import com.ewp.crm.models.vkcampaigns.VkAttemptResponse;
import com.ewp.crm.repository.interfaces.CommonGenericRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VkAttemptResponseRepository extends CommonGenericRepository<VkAttemptResponse> {
    void deleteByCampaignId(Long campaignId);

    Long countDistinctByCampaignIdAndResponseCodeAnd(Long vkCampaignService, Integer responseCode);
}
