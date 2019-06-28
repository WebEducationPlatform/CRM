package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.service.interfaces.ClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "spring.profiles.active=test")
@Rollback
@Transactional
public class ClientServiceTest {

    @Autowired
    private ClientService clientService;

    @Test
    public void testAdd() {
        String expectedName = "Test_clientService_add";
        Client expectedClient = new Client.Builder(expectedName).build();
        Client actualClient = clientService.add(expectedClient);
        assertEquals(expectedClient, actualClient);
    }

    @Test
    public void testGet() {
        String expectedName = "Test_clientService_get";
        Client expectedClient = new Client.Builder(expectedName).build();
        clientService.add(expectedClient);
        Long id = expectedClient.getId();
        assertNotNull(id);
        Client actualClient = clientService.getClientByID(id).isPresent() ? clientService.getClientByID(id).get() : null;
        assertEquals(expectedClient, actualClient);
    }

    @Test
    public void testUpdate() {
        String oldName = "Test_clientService_update";
        Client expectedClient = new Client.Builder(oldName).build();
        clientService.add(expectedClient);
        String expectedName = "Test_clientService_updated";
        expectedClient.setName(expectedName);
        Long id = expectedClient.getId();
        assertNotNull(id);
        clientService.updateClient(expectedClient);
        Client actualClient = clientService.get(id);
        assertEquals(expectedClient, actualClient);
    }

    @Test
    public void testDelete() {
        Client client = new Client.Builder("Test_clientService_delete").build();
        clientService.add(client);
        Long id = client.getId();
        assertNotNull(id);
        clientService.delete(id);
        Client deleted = clientService.get(id);
        assertNull(deleted);
    }

}