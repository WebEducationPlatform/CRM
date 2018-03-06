package com.ewp.crm.configs.initializer;

import com.ewp.crm.models.*;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.RoleService;
import com.ewp.crm.service.interfaces.StatusService;
import com.ewp.crm.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DataInitializer {

	@Autowired
	private StatusService statusService;
	@Autowired
	private ClientService clientService;
	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;

	private void init() {

		Role roleAdmin = new Role("ADMIN");
		Role roleUser = new Role("USER");
		roleService.add(roleAdmin);
		roleService.add(roleUser);

		User admin = new User("Stanislav", "Sorokin", "89331558899", "admin@mail.ru", "admin", 2000D, roleService.getByRoleName("ADMIN"));
		userService.add(admin);

		User user1 = new User("Ivan", "Ivanov", "89123456789", "user1@mail.ru", "user", 1000D, roleService.getByRoleName("USER"));
		userService.add(user1);

		User user2 = new User("Petr", "Petrov", "89129876543", "user2@mail.ru", "user", 1000D, roleService.getByRoleName("USER"));
		userService.add(user2);

		Status status0 = new Status("New clients");
		Status status1 = new Status("First status");
		Status status2 = new Status("Second status");
		Status status3 = new Status("Third status");
		Client client1 = new Client("Юрий", "Долгоруков", "89677745632", "u.dolg@mail.ru", (byte) 21, Client.Sex.MALE);
		Client client2 = new Client("Вадим", "Бойко", "89687745632", "vboyko@mail.ru", (byte) 33, Client.Sex.MALE);
		Client client3 = new Client("Александра", "Соловьева", "89677345632", "a.solo@mail.ru", (byte) 53, Client.Sex.FEMALE);
		Client client4 = new Client("Иван", "Федоров", "89637745632", "i.fiod@mail.ru", (byte) 20, Client.Sex.MALE);
		client1.addHistory(new ClientHistory("Клиент был добавлен при инициализации CRM"));
		client2.addHistory(new ClientHistory("Клиент был добавлен при инициализации CRM"));
		client3.addHistory(new ClientHistory("Клиент был добавлен при инициализации CRM"));
		client4.addHistory(new ClientHistory("Клиент был добавлен при инициализации CRM"));
		clientService.addClient(client1);
		clientService.addClient(client2);
		clientService.addClient(client3);
		clientService.addClient(client4);
		status0.setClients(clientService.getClientByEmail("u.dolg@mail.ru"));
		status1.setClients(clientService.getClientByEmail("i.fiod@mail.ru"));
		status2.setClients(clientService.getClientByEmail("vboyko@mail.ru"));
		status3.setClients(clientService.getClientByEmail("a.solo@mail.ru"));
		statusService.add(status0);
		statusService.add(status1);
		statusService.add(status2);
		statusService.add(status3);

	}
}
