package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.VkRequestForm;

import java.util.List;

public interface VkRequestFormService {
    List<VkRequestForm> getAllVkRequestForm();

    VkRequestForm getVkRequestFormById(Long id);

    void addVkRequestForm(VkRequestForm vkRequestForm);

    void updateVkRequestForm(VkRequestForm vkRequestForm);

    void deleteVkRequestFormById(long id);
}
