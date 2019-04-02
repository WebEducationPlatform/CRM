package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.ListMailingType;

public interface ListMailingTypeService extends CommonService<ListMailingType> {

    ListMailingType get(String name);

}
