package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.VkMember;

import java.util.List;
import java.util.Optional;

public interface VkMemberService {

    List<VkMember> getAll();

    Optional<VkMember> get(Long id);

    void add(VkMember vkMember);

    void update(VkMember vkMember);

    void delete(Long id);

    void delete(VkMember vkMember);

    void addAllMembers(List<VkMember> vkMembers);

    List<VkMember> getAllMembersByGroupId(Long id);

    Optional<VkMember> getVkMemberById(Long id);
}
