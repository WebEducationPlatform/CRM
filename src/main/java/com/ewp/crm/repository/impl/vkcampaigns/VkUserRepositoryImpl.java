package com.ewp.crm.repository.impl.vkcampaigns;

import com.ewp.crm.models.vkcampaigns.VkUser;
import com.ewp.crm.repository.interfaces.vkcampaigns.VkUserRepositoryCustom;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@SuppressWarnings("ALL")
@Repository
public class VkUserRepositoryImpl implements VkUserRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public VkUser getOneWithoutAttempt(Long campaignId) {
        VkUser vkUser = (VkUser) em.createQuery(
                "SELECT u FROM VkAddFriendsCampaign c JOIN c.vkUsersToAdd u " +
                "WHERE c.campaignId = :campaignId and :campaignId not in indices(u.vkCampaignAttemptResponseMap)")
                .setParameter("campaignId", campaignId)
                .setMaxResults(1)
                .getResultList()
                .stream().findFirst().orElse(null);
        return vkUser;
    }

    @Override
    public VkUser getOneWithoutAttemptNoDuplicates(Long campaignId) {
        VkUser vkUser = (VkUser) em.createQuery(
                "SELECT u FROM VkAddFriendsCampaign c JOIN c.vkUsersToAdd u " +
                "WHERE (c.campaignId = :campaignId and u.vkId not in (" +
                                       "SELECT at.vkUser.vkId FROM VkAttemptResponse at" +
                                      " WHERE (at.campaignId in " +
                                           "(SELECT cmp.campaignId FROM VkAddFriendsCampaign cmp " +
                                            "WHERE cmp.vkUserId = c.vkUserId))))")
                .setParameter("campaignId", campaignId)
                .setMaxResults(1)
                .getResultList()
                .stream().findFirst().orElse(null);
        return vkUser;
    }

    @Override
    public List<VkUser> getAllByCampaignIdWithResponseValue(Long campaignId, Integer responseValue) {
        return em.createQuery("SELECT DISTINCT u FROM VkUser u JOIN u.vkCampaignAttemptResponseMap m " +
                "WHERE (KEY(m) = :campaignId and m.responseCode = :responseValue)")
                .setParameter("campaignId", campaignId)
                .setParameter("responseValue", responseValue)
                .getResultList();
    }

    @Override
    public Long countSentRequestsByCampaignId(Long campaignId) {
         return (Long) em.createQuery("SELECT COUNT(DISTINCT u) FROM VkUser u JOIN u.vkCampaignAttemptResponseMap m" +
                " WHERE (KEY(m) = :campaignId and m.responseCode in (1, 2, 4))")
                .setParameter("campaignId", campaignId)
                .getSingleResult();
    }

    @Override
    public void deleteAllWithoutCampaigns() {
        em.createNativeQuery("DELETE FROM vk_user WHERE vk_id not in " +
                "(SELECT vk_id FROM vk_add_friends_campaign_vk_user)")
                .executeUpdate();
    }
}
