package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.VkTrackedClub;

import java.util.List;
import java.util.Optional;

public interface VkTrackedClubService {

    List<VkTrackedClub> getAll();

    Optional<VkTrackedClub> get(Long id);

    void add(VkTrackedClub vkTrackedClub);

    void update(VkTrackedClub vkTrackedClub);

    void delete(Long id);

    void delete(VkTrackedClub vkTrackedClub);

    void addAllClubs(List<VkTrackedClub> vkTrackedClubs);
}
