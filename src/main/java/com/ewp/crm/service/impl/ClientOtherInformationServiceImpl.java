package com.ewp.crm.service.impl;

import com.ewp.crm.models.ClientOtherInformation;
import com.ewp.crm.repository.interfaces.ClientOtherInformationRepository;
import com.ewp.crm.service.interfaces.ClientOtherInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientOtherInformationServiceImpl implements ClientOtherInformationService {
    private final ClientOtherInformationRepository clientOtherInformationRepository;

    @Autowired
    public ClientOtherInformationServiceImpl(ClientOtherInformationRepository clientOtherInformationRepository) {
        this.clientOtherInformationRepository = clientOtherInformationRepository;
    }

    @Override
    public List<ClientOtherInformation> getAllClientOtherInformation() {
        return clientOtherInformationRepository.findAll();
    }

    @Override
    public Optional<ClientOtherInformation> getClientOtherInformationById(Long id) {
        return Optional.of(clientOtherInformationRepository.getOne(id));
    }

    @Override
    public void addClientOtherInformation(ClientOtherInformation clientOtherInformation) {
        clientOtherInformationRepository.save(clientOtherInformation);
    }

    @Override
    public void updateClientOtherInformation(ClientOtherInformation clientOtherInformation) {
        clientOtherInformationRepository.save(clientOtherInformation);
    }

    @Override
    public void deleteClientOtherInformationById(Long id) {
        clientOtherInformationRepository.deleteById(id);
    }

    @Override
    public List<ClientOtherInformation> getAllClientOtherInformaionById(Long clientId) {
        return clientOtherInformationRepository.getAllByClientId(clientId);
    }

    @Override
    public ClientOtherInformation getClientOtherInformationByNameAndClientId(String name, Long clientId) {
        return clientOtherInformationRepository.getByNameFieldAndClientId(name, clientId);
    }

    @Override
    public void save(ClientOtherInformation clientOtherInformation) {
        clientOtherInformationRepository.save(clientOtherInformation);
    }
}