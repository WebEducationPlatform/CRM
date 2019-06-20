package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.ClientOtherInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ClientOtherInformationRepository extends JpaRepository<ClientOtherInformation, Long> {

    List<ClientOtherInformation> getAllByClientId(Long clientId);

    ClientOtherInformation getByNameFieldAndClientId(String name, Long clientId);

    @Transactional
    void deleteAllByClientId(Long clientId);
}