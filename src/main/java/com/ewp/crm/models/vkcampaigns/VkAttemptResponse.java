package com.ewp.crm.models.vkcampaigns;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "vk_add_friend_attempt_response")
public class VkAttemptResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attempt_id")
    private Long id;

    @ManyToOne
    private VkUser vkUser;

    @Column(name = "campaign_id")
    private Long campaignId;

    @Column(name = "attempt_date")
    private ZonedDateTime attemptDate;

    @Column(name = "response_code")
    private int responseCode;

    public VkAttemptResponse(VkUser vkUser, Long campaignId, ZonedDateTime attemptDate, int responseCode) {
        this.vkUser = vkUser;
        this.campaignId = campaignId;
        this.attemptDate = attemptDate;
        this.responseCode = responseCode;
    }

    public VkAttemptResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VkUser getVkUser() {
        return vkUser;
    }

    public void setVkUser(VkUser vkUser) {
        this.vkUser = vkUser;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public ZonedDateTime getAttemptDate() {
        return attemptDate;
    }

    public void setAttemptDate(ZonedDateTime attemptDate) {
        this.attemptDate = attemptDate;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
}
