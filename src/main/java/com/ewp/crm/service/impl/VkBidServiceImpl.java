package com.ewp.crm.service.impl;

import com.ewp.crm.models.VkBid;
import com.ewp.crm.repository.interfaces.VkBidDao;
import com.ewp.crm.service.interfaces.VkBidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VkBidServiceImpl implements VkBidService {
    private final VkBidDao vkBidDao;

    @Autowired
    public VkBidServiceImpl(VkBidDao vkBidDao) {
        this.vkBidDao = vkBidDao;
    }

    @Override
    public List<VkBid> getAll() {
        return vkBidDao.findAll();
    }

    @Override
    public VkBid get(Long id) {
        return vkBidDao.getOne(id);
    }

    @Override
    public void add(VkBid vkBid) {
        vkBidDao.save(vkBid);
    }

    @Override
    public void update(VkBid vkBid) {
        vkBidDao.save(vkBid);
    }

    @Override
    public void delete(long id) {
        vkBidDao.deleteById(id);
    }
}