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

/**
 * тестирование CRUD операций clientService с заглушенным репозиторием clientRepository без использования базы данных
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class ClientServiceTestsMocked {

    @MockBean
    private ClientRepository clientRepository;

    @Autowired
    private ClientService clientService;

    private static long id = 123L;

    //Проверяет, происходит ли делегирование в методе add() клиент-сервиса методу saveAndFlush() клиент репозитория
    @Test
    public void add_delegateToRepository() {
        String expectedName = "Test_clientService_add_mock";
        Client expectedClient = new Client.Builder(expectedName).build();
        //метод saveAndFlush() заглущенного репозитория вернет клиента при вызове
        when(this.clientRepository.saveAndFlush(any())).thenReturn(expectedClient);
        Client actualClient = clientService.add(expectedClient);
        Assert.assertEquals(expectedClient, actualClient);
    }

    //Проверяет, происходит ли делегирование в методе getClientByID() клиент-сервиса методу findById() клиент репозитория
    @Test
    public void get_delegateToRepository() {
        String expectedName = "Test_clientService_get_mock";
        Client expectedClient = new Client.Builder(expectedName).build();
        //метод findById(any()) заглущенного репозитория вернет Optional клиента при вызове  с любым значением long id
        when(this.clientRepository.findById(any())).thenReturn(Optional.of(expectedClient));
        Client actualClient = clientService.getClientByID(id).isPresent() ? clientService.getClientByID(id).get() : null;
        Assert.assertEquals(expectedClient, actualClient);
    }

    //Проверяет, происходит ли делегирование в методе update() клиент-сервиса методу saveAndFlush() клиент репозитория
    @Test
    public void update_delegateToRepository() {
        String expectedName = "Test_clientService_update_mock";
        Client client = new Client.Builder(expectedName).build();
        clientService.update(client);
        //если при update() в клиентсервисе будет вызван метод saveAndFlush() в репозитории - тест пройден
        verify(clientRepository).saveAndFlush(client);
    }

    //Проверяет, происходит ли делегирование в методе delete() клиент-сервиса методу delete() клиент репозитория
    @Test
    public void detele_delegateToRepository() {
        String expectedName = "Test_clientService_delete_mock";
        Client client = new Client.Builder(expectedName).build();
        clientService.delete(client);
        //если при delete() в клиентсервисе будет вызван метод delete() в репозитории - тест пройден
        verify(clientRepository).delete(client);
    }
}