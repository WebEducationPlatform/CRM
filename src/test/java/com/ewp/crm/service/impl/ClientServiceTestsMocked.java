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

}