package com.ewp.crm.service.impl;

import com.ewp.crm.CrmApplication;
import com.ewp.crm.models.Client;
import com.ewp.crm.service.interfaces.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/*
Тестирование ClientService на embeded базе H2, настройки хранятся в файле test\resources\application.properties (даже если он пустой),
без него не запускается тест!
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CrmApplication.class)
@AutoConfigureTestDatabase
public class ClientServiceTestH2 {

    @Autowired
    private ClientService clientService;


    @Test
    public void testCreate() {
        String name = "Test_clientService_add";
        Client client = new Client.Builder(name).build();
        clientService.add(client);
        Long id = client.getId();
        Assert.assertNotNull(id);
    }

    @Test
    public void testUpdate() {
        Client client = new Client.Builder("Test_clientService_update_h2Base").build();
        clientService.add(client);
        Long id = client.getId();
        String expectedName = "Test_clientService_updated_H2Base";
        client.setName(expectedName);
        Assert.assertNotNull(id);
        clientService.updateClient(client);
        String actualName = clientService.getClientByID(id).isPresent() ? clientService.getClientByID(id).get().getName() : null;
        Assert.assertEquals(expectedName,actualName);
    }

    @Test
    public void testDelete() {
        Client client = new Client.Builder("Test_clientService_delete_H2Base").build();
        clientService.add(client);
        Long id = client.getId();
        Assert.assertNotNull(id);
        clientService.delete(id);
        Client deleted = clientService.get(id);
        Assert.assertNull(deleted);
    }




}