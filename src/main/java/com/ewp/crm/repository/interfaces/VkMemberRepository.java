package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.VkMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VkMemberRepository extends JpaRepository<VkMember, Long> {

    List<VkMember> getAllByGroupId(Long groupId);
}
