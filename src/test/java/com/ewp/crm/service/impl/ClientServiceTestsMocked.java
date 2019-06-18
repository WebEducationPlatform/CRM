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
import static org.mockito.BDDMockito.*;

/*
тестирование clientService с заглушенным репозиторием без использования базы данных
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class ClientServiceTestsMocked {

    @MockBean
    private ClientRepository clientRepository;

  /*  @MockBean
    private CommonService commonService; */

    @Autowired
    private ClientService clientService;

    @Test
    public void testGetByEmailAndPhone() {
        Client willReturnClient = new Client.Builder("Ivan").build();
        willReturnClient.setEmail("goblin@mail.ru");
        willReturnClient.setPhoneNumber("12345678999");
        when(this.clientRepository.getClientByEmail(any())).thenReturn(willReturnClient);
        when(this.clientRepository.getClientByPhoneNumber(eq("12345678999"))).thenReturn(willReturnClient);
        Client clientFromMail = clientService.getClientByEmail("goblin@mail.ru").get();
        Client clientFromPhone = clientService.getClientByPhoneNumber("12345678999").get();
        Assert.assertEquals(willReturnClient, clientFromMail);
        Assert.assertEquals(willReturnClient, clientFromPhone);
    }

    @Test
    public void testCreate() {
        String expectedName = "Test_clientService_add";
        Client expectedClient = new Client.Builder(expectedName).build();
        when(this.clientRepository.saveAndFlush(any())).thenReturn(expectedClient);
        Client actualClient = clientService.add(expectedClient);
        Assert.assertEquals(expectedClient, actualClient);
    }

 /*   @Test
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
*/
}