package com.ewp.crm.service.impl;

import com.ewp.crm.models.VkRequestForm;
import com.ewp.crm.repository.interfaces.VkRequestFormRepository;
import com.ewp.crm.service.interfaces.VkRequestFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VkRequestFormServiceImpl implements VkRequestFormService {
    private final VkRequestFormRepository vkRequestFormRepository;

    @Autowired
    public VkRequestFormServiceImpl(VkRequestFormRepository vkRequestFormRepository) {
        this.vkRequestFormRepository = vkRequestFormRepository;
    }


    @Override
    public List<VkRequestForm> getAllVkRequestForm() {
        return vkRequestFormRepository.findAll();
    }

    @Override
    public VkRequestForm getVkRequestFormById(Long id) {
        return vkRequestFormRepository.getOne(id);
    }

    @Override
    public void addVkRequestForm(VkRequestForm vkRequestForm) {
        vkRequestFormRepository.save(vkRequestForm);
    }

    @Override
    public void updateVkRequestForm(VkRequestForm vkRequestForm) {
        vkRequestFormRepository.save(vkRequestForm);
    }

    @Override
    public void deleteVkRequestFormById(long id) {
        vkRequestFormRepository.deleteById(id);
    }
}
