package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.VkTrackedClub;

import java.util.List;

public interface VkTrackedClubService {

    List<VkTrackedClub> getAll();

    VkTrackedClub get(Long id);

    void add(VkTrackedClub vkTrackedClub);

    void update(VkTrackedClub vkTrackedClub);

    void delete(Long id);

    void delete(VkTrackedClub vkTrackedClub);

    void addAllClubs(List<VkTrackedClub> vkTrackedClubs);
}
