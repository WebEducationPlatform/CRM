package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.service.interfaces.ClientService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
Тестирование CRUD операций ClientService на отдельной базе данных
Перед запуском теста создать базу данных crmtest, на которой будет происходить тестирование,
настройки хранятся в файле test\resources\application-test.properties,
если убрать (properties = "spring.profiles.active=test") - тестирование произодет на реальной базе проекта
 */

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.profiles.active=test")
@Rollback
@Transactional
public class ClientServiceTest {

    @Autowired
    private ClientService clientService;

    //проверяет, равен ли созданный клиент клиенту, добавленному в базу методом add() клиентсервиса
    @Test
    public void testAdd() {
        String expectedName = "Test_clientService_add";
        Client expectedClient = new Client.Builder(expectedName).build();
        Client actualClient = clientService.add(expectedClient);
        Assert.assertEquals(expectedClient, actualClient);
    }

    //проверяет, равен ли созданный клиент клиенту, прочитанному из базы методом getClientByID() клиентсервиса
    @Test
    public void testGet() {
        String expectedName = "Test_clientService_get";
        Client expectedClient = new Client.Builder(expectedName).build();
        clientService.add(expectedClient);
        Long id = expectedClient.getId();
        //проверяет, добавлен ли клиент в базу данных (id != null) перед чтением из базы
        Assert.assertNotNull(id);
        Client actualClient = clientService.getClientByID(id).isPresent() ? clientService.getClientByID(id).get() : null;
        Assert.assertEquals(expectedClient, actualClient);
    }

    //проверяет, равен ли измененный клиент клиенту, прочитанному из базы после работы метода updateClient() клиентсервиса
    @Test
    public void testUpdate() {
        String oldName = "Test_clientService_update";
        Client expectedClient = new Client.Builder(oldName).build();
        clientService.add(expectedClient);
        String expectedName = "Test_clientService_updated";
        expectedClient.setName(expectedName);
        Long id = expectedClient.getId();
        //проверяет, добавлен ли клиент в базу данных (id != null) перед обновлением
        Assert.assertNotNull(id);
        clientService.updateClient(expectedClient);
        Client actualClient = clientService.get(id);
        Assert.assertEquals(expectedClient, actualClient);
    }

    //проверяет, равен ли удаленный клиент null после работы метода delete() клиентсервиса
    @Test
    public void testDelete() {
        Client client = new Client.Builder("Test_clientService_delete").build();
        clientService.add(client);
        Long id = client.getId();
        //проверяет, добавлен ли клиент в базу данных (id != null) перед удалением
        Assert.assertNotNull(id);
        clientService.delete(id);
        Client deleted = clientService.get(id);
        Assert.assertNull(deleted);
    }

}