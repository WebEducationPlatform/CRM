package com.ewp.crm.service.impl;

import com.ewp.crm.models.OtherInformationMultipleCheckboxes;
import com.ewp.crm.repository.interfaces.OtherInformationMultipleCheckboxesRepository;
import com.ewp.crm.service.interfaces.OtherInformationMultipleCheckboxesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OtherInformationMultipleCheckboxesServiceImpl implements OtherInformationMultipleCheckboxesService {

    private final OtherInformationMultipleCheckboxesRepository otherInformationMultipleCheckboxesRepository;

    @Autowired
    public OtherInformationMultipleCheckboxesServiceImpl(OtherInformationMultipleCheckboxesRepository otherInformationMultipleCheckboxesRepository) {
        this.otherInformationMultipleCheckboxesRepository = otherInformationMultipleCheckboxesRepository;
    }

    @Override
    public void save(OtherInformationMultipleCheckboxes otherInformationMultipleCheckboxes) {
        otherInformationMultipleCheckboxesRepository.save(otherInformationMultipleCheckboxes);
    }

    @Override
    public void deleteOtherInformationMultipleCheckboxesByNameField(String name) {
        otherInformationMultipleCheckboxesRepository.deleteOtherInformationMultipleCheckboxesByNameField(name);
    }
}
