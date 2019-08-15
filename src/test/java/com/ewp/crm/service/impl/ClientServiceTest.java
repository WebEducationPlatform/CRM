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

/**
 Тестирование CRUD операций ClientService на отдельной базе данных
 Перед запуском теста создать базу данных crmtest, на которой будет происходить тестирование,
 настройки хранятся в файле test\resources\application-test.properties,
 если убрать (properties = "spring.profiles.active=test") - тестирование произодет на реальной базе проекта
 */

@ExtendWith(SpringExtension.class)
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
		assertEquals(expectedClient, actualClient);
	}

	//проверяет, равен ли созданный клиент клиенту, прочитанному из базы методом getClientByID() клиентсервиса
	@Test
	public void testGet() {
		String expectedName = "Test_clientService_get";
		Client expectedClient = new Client.Builder(expectedName).build();
		clientService.add(expectedClient);
		Long id = expectedClient.getId();
		//проверяет, добавлен ли клиент в базу данных (id != null) перед чтением из базы
		assertNotNull(id);
		Client actualClient = clientService.getClientByID(id).isPresent() ? clientService.getClientByID(id).get() : null;
		assertEquals(expectedClient, actualClient);
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
		assertNotNull(id);
		clientService.updateClient(expectedClient);
		Client actualClient = clientService.get(id);
		assertEquals(expectedClient, actualClient);
	}

	//проверяет, равен ли удаленный клиент null после работы метода delete() клиентсервиса
	@Test
	public void testDelete() {
		Client client = new Client.Builder("Test_clientService_delete").build();
		clientService.add(client);
		Long id = client.getId();
		//проверяет, добавлен ли клиент в базу данных (id != null) перед удалением
		assertNotNull(id);
		clientService.delete(id);
		Client deleted = clientService.get(id);
		assertNull(deleted);
	}

}