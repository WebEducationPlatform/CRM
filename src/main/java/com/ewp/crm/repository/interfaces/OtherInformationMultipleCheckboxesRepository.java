package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.OtherInformationMultipleCheckboxes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface OtherInformationMultipleCheckboxesRepository extends JpaRepository<OtherInformationMultipleCheckboxes, Long> {

    @Transactional
    void deleteOtherInformationMultipleCheckboxesByNameField(String name);
}
