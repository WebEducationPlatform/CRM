package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.VkRequestForm;

import java.util.List;

public interface VkRequestFormService {
    List<VkRequestForm> getAll();

    VkRequestForm get(Long id);

    void add(VkRequestForm vkRequestForm);

    void update(VkRequestForm vkRequestForm);

    void delete(long id);
}
