package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.ListMailing;

import java.util.List;

public interface ListMailingDAO extends CommonGenericRepository<ListMailing> {

    ListMailing getByListName(String listName);

    List<ListMailing> getByType_Name(String name);

}
