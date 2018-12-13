package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.ListMailing;

public interface ListMailingDAO extends CommonGenericRepository<ListMailing> {

    ListMailing getByListName(String listName);
}
