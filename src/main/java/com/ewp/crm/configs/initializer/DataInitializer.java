package com.ewp.crm.configs.initializer;

import com.ewp.crm.configs.inteface.VKConfig;
import com.ewp.crm.exceptions.member.NotFoundMemberList;
import com.ewp.crm.models.VkMember;
import com.ewp.crm.models.VkTrackedClub;
import com.ewp.crm.service.impl.VKService;
import com.ewp.crm.service.interfaces.VkMemberService;
import com.ewp.crm.service.interfaces.VkTrackedClubService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DataInitializer {

    @Autowired
    private VkTrackedClubService vkTrackedClubService;

    @Autowired
    private VkMemberService vkMemberService;

    @Autowired
    private VKService vkService;

    @Autowired
    private VKConfig vkConfig;

    private void init() {

        vkTrackedClubService.add(new VkTrackedClub(Long.parseLong(vkConfig.getClubId()) * (-1),
                vkConfig.getCommunityToken(),
                "JavaMentorTest",
                Long.parseLong(vkConfig.getApplicationId())));
        List<VkTrackedClub> vkTrackedClubs = vkTrackedClubService.getAll();
        for (VkTrackedClub vkTrackedClub : vkTrackedClubs) {
            List<VkMember> memberList = vkService.getAllVKMembers(vkTrackedClub.getGroupId(), 0L)
                    .orElseThrow(NotFoundMemberList::new);
            vkMemberService.addAllMembers(memberList);
        }

    }
}