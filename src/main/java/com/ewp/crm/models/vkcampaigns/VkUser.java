package com.ewp.crm.models.vkcampaigns;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Entity to use with long run add friends service
 */
@Entity
@Table(name = "vk_user")
public class VkUser {

    @Id
    @Column(name = "vk_id")
    private Long vkId;

    @OneToMany(mappedBy="vkUser")
    @MapKey(name = "campaignId")
    private Map<Long, VkAttemptResponse> vkCampaignAttemptResponseMap = new HashMap<>();

    public VkUser(Long vkId, Map<Long, VkAttemptResponse> vkCampaignAttemptResponseMap) {
        this.vkId = vkId;
        this.vkCampaignAttemptResponseMap = vkCampaignAttemptResponseMap;
    }

    public VkUser() {
    }

    public Long getVkId() {
        return vkId;
    }

    public void setVkId(Long vkId) {
        this.vkId = vkId;
    }

    public Map<Long, VkAttemptResponse> getVkCampaignAttemptResponseMap() {
        return vkCampaignAttemptResponseMap;
    }

    public void setVkCampaignAttemptResponseMap(Map<Long, VkAttemptResponse> vkCampaignAttemptResponseMap) {
        this.vkCampaignAttemptResponseMap = vkCampaignAttemptResponseMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VkUser vkUser = (VkUser) o;
        return Objects.equals(vkId, vkUser.vkId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vkId);
    }
}
