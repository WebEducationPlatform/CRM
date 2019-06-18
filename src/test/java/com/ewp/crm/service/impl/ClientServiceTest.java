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

/*
Тестирование ClientService на отдельной базе данных
Перед запуском теста не забудьте создать базу данных crmtest, на которой будет происходить тестирование,
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

    @Test
    public void testCreate() {
        String expectedName = "Test_clientService_add";
        Client expectedClient = new Client.Builder(expectedName).build();
        clientService.add(expectedClient);
        Long id = expectedClient.getId();
        Assert.assertNotNull(id);
        Client actualClient = clientService.get(id);
        Assert.assertEquals(expectedClient, actualClient);
    }

    @Test
    public void testUpdate() {
        String oldName = "Test_clientService_update";
        Client expectedClient = new Client.Builder(oldName).build();
        clientService.add(expectedClient);
        String expectedName = "Test_clientService_updated";
        expectedClient.setName(expectedName);
        Long id = expectedClient.getId();
        Assert.assertNotNull(id);
        clientService.updateClient(expectedClient);
        Client actualClient = clientService.get(id);
        Assert.assertEquals(expectedClient, actualClient);
    }

    @Test
    public void testDelete() {
        Client client = new Client.Builder("Test_clientService_delete").build();
        clientService.add(client);
        Long id = client.getId();
        Assert.assertNotNull(id);
        clientService.delete(id);
        Client deleted = clientService.get(id);
        Assert.assertNull(deleted);
    }




}