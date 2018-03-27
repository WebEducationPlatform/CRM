package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.SocialNetworkType;

import java.util.List;

public interface NetworkTypeService {

    void updateType(SocialNetworkType socialNetworkType);

    void deleteType(Long id);

    void deleteType(SocialNetworkType socialNetworkType);

    void addType(SocialNetworkType socialNetworkType);

    SocialNetworkType getByTypeName(String name);

    List<SocialNetworkType> getAll();

    SocialNetworkType getById(Long id);

}
