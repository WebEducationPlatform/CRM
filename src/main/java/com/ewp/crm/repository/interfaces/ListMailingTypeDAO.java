package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.ListMailingType;

public interface ListMailingTypeDAO extends CommonGenericRepository<ListMailingType> {

    ListMailingType getById(Long id);

    ListMailingType getByName(String name);

}