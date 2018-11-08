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
    public List<VkRequestForm> getAll() {
        return vkRequestFormRepository.findAll();
    }

    @Override
    public VkRequestForm get(Long id) {
        return vkRequestFormRepository.getOne(id);
    }

    @Override
    public void add(VkRequestForm vkRequestForm) {
        vkRequestFormRepository.save(vkRequestForm);
    }

    @Override
    public void update(VkRequestForm vkRequestForm) {
        vkRequestFormRepository.save(vkRequestForm);
    }

    @Override
    public void delete(long id) {
        vkRequestFormRepository.deleteById(id);
    }
}
