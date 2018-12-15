package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.ListMailing;

public interface ListMailingService extends CommonService<ListMailing> {

    void addListMailing(ListMailing listMailing);

    ListMailing getByListName(String listName);

    void update(ListMailing listMailing);

    void delete(ListMailing listMailing);
}
