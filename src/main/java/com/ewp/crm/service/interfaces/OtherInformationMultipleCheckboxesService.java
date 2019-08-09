package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.OtherInformationMultipleCheckboxes;

public interface OtherInformationMultipleCheckboxesService {

    void save(OtherInformationMultipleCheckboxes otherInformationMultipleCheckboxes);

    void deleteOtherInformationMultipleCheckboxesByNameField(String name);
}
