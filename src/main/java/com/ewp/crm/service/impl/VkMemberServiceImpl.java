package com.ewp.crm.service.impl;

import com.ewp.crm.models.VkMember;
import com.ewp.crm.repository.interfaces.VkMemberRepository;
import com.ewp.crm.service.interfaces.VkMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VkMemberServiceImpl implements VkMemberService {

    private final VkMemberRepository vkMemberRepository;

    @Autowired
    public VkMemberServiceImpl(VkMemberRepository vkMemberRepository) {
        this.vkMemberRepository = vkMemberRepository;
    }

    @Override
    public List<VkMember> getAll() {
        return vkMemberRepository.findAll();
    }

    @Override
    public Optional<VkMember> get(Long id) {
        return Optional.of(vkMemberRepository.getOne(id));
    }

    @Override
    public void add(VkMember vkMember) {
        vkMemberRepository.save(vkMember);
    }

    @Override
    public void update(VkMember vkMember) {
        vkMemberRepository.saveAndFlush(vkMember);
    }

    @Override
    public void delete(Long id) {
        vkMemberRepository.deleteById(id);
    }

    @Override
    public void delete(VkMember vkMember) {
        vkMemberRepository.delete(vkMember);
    }


    public void addAllMembers(List<VkMember> vkMembers) {
        vkMemberRepository.saveAll(vkMembers);
    }

    @Override
    public List<VkMember> getAllMembersByGroupId(Long id) {
        return vkMemberRepository.getAllByGroupId(id);
    }

    @Override
    public Optional<VkMember> getVkMemberById(Long id) {
        return Optional.ofNullable(vkMemberRepository.getByVkId(id));
    }
}
