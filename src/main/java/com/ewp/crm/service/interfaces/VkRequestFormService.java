package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.VkRequestForm;

import java.util.List;
import java.util.Optional;

public interface VkRequestFormService {
    List<VkRequestForm> getAllVkRequestForm();

    Optional<VkRequestForm> getVkRequestFormById(Long id);

    void addVkRequestForm(VkRequestForm vkRequestForm);

    void updateVkRequestForm(VkRequestForm vkRequestForm);

    void deleteVkRequestFormById(long id);
}
