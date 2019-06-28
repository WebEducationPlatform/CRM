package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.ListMailing;

import java.util.List;
import java.util.Optional;

public interface ListMailingService extends CommonService<ListMailing> {

    void addListMailing(ListMailing listMailing);

    Optional<ListMailing> getByListName(String listName);

    List<ListMailing> getByType(String typeName);

    @Override
    void update(ListMailing listMailing);

    @Override
    void delete(ListMailing listMailing);

    Optional<ListMailing> getListMailingById(Long id);

}
