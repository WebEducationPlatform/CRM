package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.ClientOtherInformation;

import java.util.List;
import java.util.Optional;

public interface ClientOtherInformationService {
    List<ClientOtherInformation> getAllClientOtherInformation();

    Optional<ClientOtherInformation> getClientOtherInformationById(Long id);

    void addClientOtherInformation(ClientOtherInformation clientOtherInformation);

    void updateClientOtherInformation(ClientOtherInformation clientOtherInformation);

    void deleteClientOtherInformationById(Long id);

    List<ClientOtherInformation> getAllClientOtherInformaionById(Long clientId);

    ClientOtherInformation getClientOtherInformationByNameAndClientId(String name, Long clientId);

    void save(ClientOtherInformation clientOtherInformation);
}