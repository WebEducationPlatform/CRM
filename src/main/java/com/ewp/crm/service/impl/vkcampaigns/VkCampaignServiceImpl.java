package com.ewp.crm.service.impl.vkcampaigns;

import com.ewp.crm.models.vkcampaigns.VkAddFriendsCampaign;
import com.ewp.crm.models.vkcampaigns.VkAttemptResponse;
import com.ewp.crm.models.vkcampaigns.VkUser;
import com.ewp.crm.repository.interfaces.vkcampaigns.VkAttemptResponseRepository;
import com.ewp.crm.repository.interfaces.vkcampaigns.VkCampaignRepository;
import com.ewp.crm.repository.interfaces.vkcampaigns.VkUserRepository;
import com.ewp.crm.service.interfaces.vkcampaigns.VkCampaignService;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.queries.friends.FriendsAddQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;

@Service
public class VkCampaignServiceImpl implements VkCampaignService {
    private static Logger logger = LoggerFactory.getLogger(VkCampaignServiceImpl.class);

    private final VkCampaignRepository vkCampaignRepository;
    private final VkAttemptResponseRepository vkAttemptResponseRepository;
    private final VkUserRepository vkUserRepository;

    @Autowired
    public VkCampaignServiceImpl(VkCampaignRepository vkCampaignRepository, VkAttemptResponseRepository vkAttemptResponseRepository, VkUserRepository vkUserRepository) {
        this.vkCampaignRepository = vkCampaignRepository;
        this.vkAttemptResponseRepository = vkAttemptResponseRepository;
        this.vkUserRepository = vkUserRepository;
    }

    @Override
    public VkAddFriendsCampaign get(Long id) {
        VkAddFriendsCampaign vkAddFriendsCampaign;
        try {
            vkAddFriendsCampaign = vkCampaignRepository.getOne(id);
        } catch (javax.persistence.EntityNotFoundException e) {
            vkAddFriendsCampaign = null;
        }
        return vkAddFriendsCampaign;
    }

    @Override
    @Transactional
    public VkAddFriendsCampaign add(VkAddFriendsCampaign entity) {
        if(!entity.getVkUsersToAdd().isEmpty()) {
            Set<VkUser> savedVkUsersSet = new HashSet<>();
            entity.getVkUsersToAdd().forEach(u -> savedVkUsersSet.add(vkUserRepository.save(u)));
            entity.setVkUsersToAdd(savedVkUsersSet);
        }
        return vkCampaignRepository.saveAndFlush(entity);
    }

    @Override
    public List<VkAddFriendsCampaign> getAll() {
        return vkCampaignRepository.findAll();
    }

    @Override
    public void update(VkAddFriendsCampaign entity) {
        vkCampaignRepository.saveAndFlush(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        vkCampaignRepository.deleteById(id);
        vkAttemptResponseRepository.deleteByCampaignId(id);
        vkUserRepository.deleteAllWithoutCampaigns();
    }

    @Override
    public void delete(VkAddFriendsCampaign entity) {
        vkCampaignRepository.delete(entity);
    }

    @Override
    @Transactional
    public void nextAttemptCycle() {
        List<VkAddFriendsCampaign> listOfCampaigns = this.getAll();

        for (VkAddFriendsCampaign campaign : listOfCampaigns) {
            Long campaignId = campaign.getCampaignId();

            VkUser vkUserWithoutAttempt = vkUserRepository.getOneWithoutAttempt(campaignId);
            List<VkUser> vkUserListWithResponseValue =
                    vkUserRepository.getAllByCampaignIdWithResponseValue(campaignId, 1);

            /*Hibernate.initialize(campaign.getVkUsersToAdd());
            Set<VkUser> vkUserSet = campaign.getVkUsersToAdd();*/

            UserActor actor = new UserActor(Math.toIntExact(campaign.getVkUserId()),
                    campaign.getVkUserToken());
            VkApiClient vk = new VkApiClient(new HttpTransportClient());

            if (vkUserWithoutAttempt != null) {
                FriendsAddQuery friendsAddQuery = vk
                        .friends()
                        .add(actor, Math.toIntExact(vkUserWithoutAttempt.getVkId()))
                        .text(campaign.getRequestText());
                //try {
                //AddResponse response = friendsAddQuery.execute();
                logger.info("Was trying to add friend with ID: {} and got response code: {}",
                        Math.toIntExact(vkUserWithoutAttempt.getVkId()), 1);
                VkAttemptResponse vkAttemptResponse = new VkAttemptResponse(vkUserWithoutAttempt, campaignId,
                        ZonedDateTime.now(), 1);
                vkAttemptResponseRepository.saveAndFlush(vkAttemptResponse);
                    /*} catch (ApiCaptchaException e) {
                        logger.error("Vk Captcha needed");
                    /*String captchaSid = e.getSid();
                    String captchaImgUrl = e.getImage();

                    //TODO something to solve captcha

                    String captchaKey = ""; //captchaResolver(captchaImgUrl);

                    try {
                        AddResponse response = friendsAddQuery
                                .captchaSid(captchaSid)
                                .captchaKey(captchaKey)
                                .execute();
                        logger.info("Was re-trying to add friend with ID: {} with captcha solved as {} and got response code: {}",
                                Math.toIntExact(vkUser.getVkId()), "", response.getValue());
                    } catch (ApiException ex) {
                        logger.error("Vk API exception");
                    } catch (ClientException ex) {
                        logger.error("Vk client exception");
                    }*/

                    /*} catch (ApiFriendsAddYourselfException e) {
                        logger.error("Vk API exception - trying to add yourself");
                        vkUserSet.remove(vkUser);
                        this.update(campaign);
                    } catch (ApiException e) {
                        logger.error("Vk API exception");
                    } catch (ClientException e) {
                        logger.error("Vk client exception");
                    }*/
            }
            try {
                Thread.sleep(5_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public VkAddFriendsCampaign getByName(String name) {
        return vkCampaignRepository.findFirstByCampaignName(name.toLowerCase().trim());
    }

    @Override
    public Map<Long, Long> getRequestsStats() {
        List<VkAddFriendsCampaign> listOfCampaigns = this.getAll();
        Map<Long, Long> statsMap = new HashMap<>();

        for (VkAddFriendsCampaign campaign : listOfCampaigns) {
            Long campaignId = campaign.getCampaignId();

            statsMap.put(campaignId, vkUserRepository.countSentRequestsByCampaignId(campaignId));
        }
        return statsMap;
    }
}
