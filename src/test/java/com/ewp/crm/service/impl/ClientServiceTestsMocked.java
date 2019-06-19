package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.ewp.crm.service.interfaces.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.mockito.BDDMockito.*;

/*
тестирование clientService с заглушенным репозиторием без использования базы данных
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class ClientServiceTestsMocked {

    @MockBean
    private ClientRepository clientRepository;


    @Autowired
    private ClientService clientService;

    static final long id = 123L;

    @Test
    public void add_delegateToRepository() {
        String expectedName = "Test_clientService_add_mock";
        Client expectedClient = new Client();
        expectedClient.setName(expectedName);
        when(this.clientRepository.saveAndFlush(any())).thenReturn(expectedClient);
        Client actualClient = clientService.add(expectedClient);
        clientService.add(expectedClient);
        Assert.assertEquals(expectedClient, actualClient);
    }

    @Test
    public void get_delegateToRepository() {
        String expectedName = "Test_clientService_get_mock";
        Client expectedClient = new Client();
        expectedClient.setName(expectedName);
        when(this.clientRepository.findById(any())).thenReturn(Optional.of(expectedClient));
        Client actualClient = clientService.getClientByID(id).isPresent() ? clientService.getClientByID(id).get() : null;
        Assert.assertEquals(expectedClient, actualClient);
    }

    @Test
    public void update_delegateToRepository() {
        String expectedName = "Test_clientService_update_mock";
        Client client = new Client();
        client.setName(expectedName);
        clientService.update(client);
        verify(clientRepository).saveAndFlush(client);
    }


    @Test
    public void detele_delegateToRepository() {
        String expectedName = "Test_clientService_delete_mock";
        Client client = new Client.Builder(expectedName).build();
        client.setName(expectedName);
        clientService.delete(client);
        verify(clientRepository).delete(client);
    }

}