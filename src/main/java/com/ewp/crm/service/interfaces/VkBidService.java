package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.VkBid;

import java.util.List;

public interface VkBidService {
    List<VkBid> getAll();

    VkBid get(Long id);

    void add(VkBid vkBid);

    void update(VkBid vkBid);

    void delete(long id);

}
