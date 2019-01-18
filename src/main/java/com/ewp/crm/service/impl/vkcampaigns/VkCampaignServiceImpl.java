package com.ewp.crm.service.impl.vkcampaigns;

import com.ewp.crm.models.vkcampaigns.VkAddFriendsCampaign;
import com.ewp.crm.models.vkcampaigns.VkAttemptResponse;
import com.ewp.crm.models.vkcampaigns.VkUser;
import com.ewp.crm.repository.interfaces.vkcampaigns.VkAttemptResponseRepository;
import com.ewp.crm.repository.interfaces.vkcampaigns.VkCampaignRepository;
import com.ewp.crm.repository.interfaces.vkcampaigns.VkUserRepository;
import com.ewp.crm.service.interfaces.CaptchaService;
import com.ewp.crm.service.interfaces.vkcampaigns.VkCampaignService;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiCaptchaException;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.friends.FriendStatus;
import com.vk.api.sdk.objects.friends.responses.AddResponse;
import com.vk.api.sdk.queries.friends.FriendsAddQuery;
import com.vk.api.sdk.queries.friends.FriendsAreFriendsQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VkCampaignServiceImpl implements VkCampaignService {
    private static final int STATUS_FRIENDS = 3;
    private static Logger logger = LoggerFactory.getLogger(VkCampaignServiceImpl.class);

    private final VkCampaignRepository vkCampaignRepository;
    private final VkAttemptResponseRepository vkAttemptResponseRepository;
    private final VkUserRepository vkUserRepository;
    private final CaptchaService captchaService;

    @Autowired
    public VkCampaignServiceImpl(VkCampaignRepository vkCampaignRepository,
                                 VkAttemptResponseRepository vkAttemptResponseRepository,
                                 VkUserRepository vkUserRepository, CaptchaService captchaService) {
        this.vkCampaignRepository = vkCampaignRepository;
        this.vkAttemptResponseRepository = vkAttemptResponseRepository;
        this.vkUserRepository = vkUserRepository;
        this.captchaService = captchaService;
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
        List<VkAddFriendsCampaign> listOfActiveCampaigns = this.getAllActiveCampaigns();

        for (VkAddFriendsCampaign campaign : listOfActiveCampaigns) {
            Long campaignId = campaign.getCampaignId();

            VkUser vkUserWithoutAttempt;
            if(campaign.getAllowDuplicates()) {
                vkUserWithoutAttempt = vkUserRepository.getOneWithoutAttempt(campaignId);
            } else {
                vkUserWithoutAttempt = vkUserRepository.getOneWithoutAttemptNoDuplicates(campaignId);
            }

            UserActor actor = new UserActor(Math.toIntExact(campaign.getVkUserId()),
                    campaign.getVkUserToken());
            VkApiClient vk = new VkApiClient(new HttpTransportClient());

            if (vkUserWithoutAttempt != null) {
                FriendsAddQuery friendsAddQuery = vk
                        .friends()
                        .add(actor, Math.toIntExact(vkUserWithoutAttempt.getVkId()))
                        .text(campaign.getRequestText());
                try {
                    AddResponse response = friendsAddQuery.execute();
                    logger.info("Was trying to add friend with ID: {} and got response code: {}",
                            Math.toIntExact(vkUserWithoutAttempt.getVkId()), response.getValue());
                    VkAttemptResponse vkAttemptResponse = new VkAttemptResponse(vkUserWithoutAttempt, campaignId,
                            ZonedDateTime.now(), response.getValue());
                    vkAttemptResponseRepository.saveAndFlush(vkAttemptResponse);
                } catch (ApiCaptchaException e) {
                    logger.error("Vk Captcha needed");
                    String captchaSid = e.getSid();
                    String captchaImgUrl = e.getImage();
                    String captchaKey = captchaService.captchaImgResolver(captchaImgUrl);
                    try {
                        AddResponse response = friendsAddQuery
                                .captchaSid(captchaSid)
                                .captchaKey(captchaKey)
                                .execute();
                        logger.info("Was re-trying to add friend with ID: {} with captcha solved as {} and got response code: {}",
                                Math.toIntExact(vkUserWithoutAttempt.getVkId()), captchaKey, response.getValue());
                    } catch (ApiCaptchaException ex) {
                        this.setProblem(campaignId);
                        logger.error("Unsuccessful captcha resolve - {}", e.getDescription());
                    } catch (ApiException ex) {
                        logger.error("Vk API exception - {}", e.getDescription());
                        VkAttemptResponse vkAttemptResponse = new VkAttemptResponse(vkUserWithoutAttempt, campaignId,
                                ZonedDateTime.now(), ex.getCode());
                        vkAttemptResponseRepository.saveAndFlush(vkAttemptResponse);
                    } catch (ClientException ex) {
                        this.setProblem(campaignId);
                        logger.error("Vk client exception - {}", ex.getMessage());
                    }

                } catch (ApiException e) {
                    logger.error("Vk API exception - {}", e.getDescription());
                    VkAttemptResponse vkAttemptResponse = new VkAttemptResponse(vkUserWithoutAttempt, campaignId,
                            ZonedDateTime.now(), e.getCode());
                    vkAttemptResponseRepository.saveAndFlush(vkAttemptResponse);
                } catch (ClientException e) {
                    this.setProblem(campaignId);
                    logger.error("Vk client exception - {}", e.getMessage());
                }
            }

            try {
                Thread.sleep(300_000);
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

    @Override
    public List<VkAddFriendsCampaign> getAllActiveCampaigns() {
        return vkCampaignRepository.findAllByActiveTrue();
    }

    @Override
    @Transactional
    public void setActive(Long id, boolean isActive) {
        VkAddFriendsCampaign vkAddFriendsCampaign = this.get(id);
        if (vkAddFriendsCampaign != null) {
            vkAddFriendsCampaign.setProblem(false); //reset problem flag on start
            vkAddFriendsCampaign.setActive(isActive);
            this.update(vkAddFriendsCampaign);
        }
    }

    @Override
    @Transactional
    public void setProblem(Long id) {
        VkAddFriendsCampaign vkAddFriendsCampaign = this.get(id);
        if (vkAddFriendsCampaign != null) {
            vkAddFriendsCampaign.setProblem(true); //set problem flag
            vkAddFriendsCampaign.setActive(false); //stop campaign
            this.update(vkAddFriendsCampaign);
        }
    }

    @Override
    public Integer countProblems() {
        return vkCampaignRepository.countDistinctByProblemIsTrue();
    }

    @Override
    public Long countAddedFriends(Long id) {
        VkAddFriendsCampaign campaign = this.get(id);
        if (campaign != null) {
            List<Integer> idsList = campaign.getVkUsersToAdd()
                    .stream()
                    .filter(user -> !user.getVkCampaignAttemptResponseMap().isEmpty())
                    .mapToInt(user -> Math.toIntExact(user.getVkId()))
                    .boxed().collect(Collectors.toList());

            UserActor actor = new UserActor(Math.toIntExact(campaign.getVkUserId()),
                    campaign.getVkUserToken());
            VkApiClient vk = new VkApiClient(new HttpTransportClient());

            FriendsAreFriendsQuery friendsAreFriendsQuery = vk
                    .friends()
                    .areFriends(actor, idsList);

            List<FriendStatus> friendStatusList = new ArrayList<>();
            try {
                friendStatusList = friendsAreFriendsQuery.execute();
            } catch (ApiException e) {
                logger.error("Vk API exception");
            } catch (ClientException e) {
                logger.error("Vk client exception");
            }
            return friendStatusList
                    .stream()
                    .filter(fs -> fs.getFriendStatus().getValue() == STATUS_FRIENDS)
                    .count();
        }
        return 0L;
    }

    @Override
    public Long countRequestsWithResponseCode(Long id, Integer responseCode) {
        return vkAttemptResponseRepository.countDistinctByCampaignIdAndResponseCode(id, responseCode);
    }

    @Override
    public Long countVkIdsInList(Long id) {
        return (long) this.get(id).getVkUsersToAdd().size();
    }
}
