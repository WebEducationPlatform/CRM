package com.ewp.crm.service.impl;

import com.ewp.crm.models.VkTrackedClub;
import com.ewp.crm.repository.interfaces.VkTrackedClubRepository;
import com.ewp.crm.service.interfaces.VkTrackedClubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VkTrackedClubServiceImpl implements VkTrackedClubService {

    @Autowired
    private VkTrackedClubRepository vkTrackedClubRepository;

    @Override
    public List<VkTrackedClub> getAll() {
        return vkTrackedClubRepository.findAll();
    }

    @Override
    public Optional<VkTrackedClub> get(Long id) {
        return vkTrackedClubRepository.findById(id);
    }

    @Override
    public void add(VkTrackedClub vkTrackedClub) {
        vkTrackedClubRepository.saveAndFlush(vkTrackedClub);
    }

    @Override
    public void update(VkTrackedClub vkTrackedClub) {
        vkTrackedClubRepository.saveAndFlush(vkTrackedClub);
    }

    @Override
    public void delete(Long id) {
        vkTrackedClubRepository.deleteById(id);
    }

    @Override
    public void delete(VkTrackedClub vkTrackedClub) {
        vkTrackedClubRepository.delete(vkTrackedClub);
    }

    @Override
    public void addAllClubs(List<VkTrackedClub> vkTrackedClubs) {
        vkTrackedClubRepository.saveAll(vkTrackedClubs);
    }
}
