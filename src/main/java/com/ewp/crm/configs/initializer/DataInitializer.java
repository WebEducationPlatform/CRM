package com.ewp.crm.configs.initializer;

import com.ewp.crm.models.*;
import com.ewp.crm.service.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Date;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

public class DataInitializer {

	@Autowired
	private StatusService statusService;

	@Autowired
	private ClientService clientService;

	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private MessageTemplateService MessageTemplateService;

	@Autowired
	private SocialNetworkTypeService socialNetworkTypeService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private ClientHistoryService clientHistoryService;

	private void init() {

		Role roleAdmin = new Role("ADMIN");
		Role roleUser = new Role("USER");
		roleService.add(roleAdmin);
		roleService.add(roleUser);

		SocialNetworkType VK = new SocialNetworkType("vk");
		SocialNetworkType FACEBOOK = new SocialNetworkType("facebook");
		socialNetworkTypeService.addType(VK);
		socialNetworkTypeService.addType(FACEBOOK);

		User admin = new User("Stanislav", "Sorokin", "79331558899", "admin@mail.ru",
				"admin", null, Client.Sex.MALE.toString(), (byte) 22, "Moscow", "Russia", "Mentor",
				2000D, Arrays.asList(roleService.getByRoleName("USER"), roleService.getByRoleName("ADMIN")), true);
		userService.add(admin);

		User user1 = new User("Ivan", "Ivanov", "79123456789", "user1@mail.ru",
				"user", null, Client.Sex.MALE.toString(), (byte) 28, "Minsk", "Belarus", "Manager",
				1001D, Collections.singletonList(roleService.getByRoleName("USER")), true);
		userService.add(user1);

		User user2 = new User("Petr", "Petrov", "79129876543", "user2@mail.ru",
				"user", null, Client.Sex.MALE.toString(), (byte) 24, "Tver", "Russia", "Manager",
				500D, Collections.singletonList(roleService.getByRoleName("USER")), true);
		userService.add(user2);

		String templateText3 = "<!DOCTYPE html>\n" +
				"<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:th=\"http://www.thymeleaf.org\">\n" +
				"<head></head>\n" +
				"<body>\n" +
				"<p>Добрый день, %fullName%</p>\n" +
				"<p>Мы не смогли до Вас дозвониться</p>\n" +
				"<p>Пожалуйста, свяжитесь с нами</p>\n" +
				"<p>С наилучшими пожеланиями, команда JavaMentor</p>\n" +
				"<img src=\"https://sun9-9.userapi.com/c841334/v841334855/6acfb/_syiwM0RH0I.jpg\"/>\n" +
				"</body>\n" +
				"</html>";
		String templateText2 = "<!DOCTYPE html>\n" +
				"<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:th=\"http://www.thymeleaf.org\">\n" +
				"<head></head>\n" +
				"<body>\n" +
				"<p>Добрый день, %fullName%</p>\n" +
				"<p>Напоминаем, что необходимо опатить обучение за следующий  месяц</p>\n" +
				"<p>С наилучшими пожеланиями, команда JavaMentor</p>\n" +
				"<img src=\"https://sun9-9.userapi.com/c841334/v841334855/6acfb/_syiwM0RH0I.jpg\"/>\n" +
				"</body>\n" +
				"</html>";
		String templateText1 = "<!DOCTYPE html>\n" +
				"<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:th=\"http://www.thymeleaf.org\">\n" +
				"<head></head>\n" +
				"<body>\n" +
				"<p>%bodyText%</p>\n" +
				"</body>\n" +
				"</html>";

		String otherText3 = "Добрый день , %fullName%! \n Mы не смогли до Вас довзониться \n" +
				"Пожалуйста, свяжитесь с нами \n" + "С наилучшими пожеланиями, команда JavaMentor";
		String otherText2 = "Добрый день, %fullName%! \n  Напоминаем, что необходимо опатить обучение за следующий  месяц \n " +
				"С наилучшими пожеланиями, команда JavaMentor";
		String otherText1 = "%bodyText%";

		MessageTemplate MessageTemplate3 = new MessageTemplate("Не дозвонился", templateText3, otherText3);
		MessageTemplate MessageTemplate2 = new MessageTemplate("Оплата за обучение", templateText2, otherText2);
		MessageTemplate MessageTemplate1 = new MessageTemplate("После разговора", templateText1, otherText1);
		MessageTemplateService.add(MessageTemplate1);
		MessageTemplateService.add(MessageTemplate2);
		MessageTemplateService.add(MessageTemplate3);

		Status status0 = new Status("New clients");
		Status status1 = new Status("First status");
		Status status2 = new Status("Second status");
		Status status3 = new Status("Third status");
		Status defaultStatus = new Status("default", true);

		Client client1 = new Client("Юрий", "Долгоруков", "89891352481", "u.dolg@mail.ru", (byte) 21, Client.Sex.MALE, "Тула", "Россия", Client.State.FINISHED, new Date(Calendar.getInstance().getTimeInMillis() - 100000000));
		Client client2 = new Client("Вадим", "Бойко", "89687745632", "vboyko@mail.ru", (byte) 33, Client.Sex.MALE, "Тула", "Россия", Client.State.LEARNING, new Date(Calendar.getInstance().getTimeInMillis() - 200000000));
		Client client3 = new Client("Александра", "Соловьева", "89677345632", "a.solo@mail.ru", (byte) 53, Client.Sex.FEMALE, "Тула", "Россия", Client.State.LEARNING, new Date(Calendar.getInstance().getTimeInMillis() - 300000000));
		Client client4 = new Client("Иван", "Федоров", "89637745632", "i.fiod@mail.ru", (byte) 20, Client.Sex.MALE, "Тула", "Россия", Client.State.NEW, new Date(Calendar.getInstance().getTimeInMillis() - 400000000));
		client1.addSMSInfo(new SMSInfo(123456789, "SMS Message to client 1", admin));
		client2.addSMSInfo(new SMSInfo(12345678, "SMS Message to client 2", admin));
		client3.addSMSInfo(new SMSInfo(1234567, "SMS Message to client 3", admin));
		client4.addSMSInfo(new SMSInfo(123456, "SMS Message to client 4", admin));
		client1.addHistory(clientHistoryService.createHistory("инициализации crm"));
		client2.addHistory(clientHistoryService.createHistory("инициализации crm"));
		client3.addHistory(clientHistoryService.createHistory("инициализации crm"));
		client4.addHistory(clientHistoryService.createHistory("инициализации crm"));
		client1.setSocialNetworks(Arrays.asList(new SocialNetwork("https://vk.com/id", socialNetworkTypeService.getByTypeName("vk")),
				new SocialNetwork("https://fb", socialNetworkTypeService.getByTypeName("facebook"))));
		client2.setSocialNetworks(Arrays.asList(new SocialNetwork("https://vk.com/id", socialNetworkTypeService.getByTypeName("vk")),
				new SocialNetwork("https://fb", socialNetworkTypeService.getByTypeName("facebook"))));
		client3.setSocialNetworks(Arrays.asList(new SocialNetwork("https://vk.com/id", socialNetworkTypeService.getByTypeName("vk")),
				new SocialNetwork("https://fb", socialNetworkTypeService.getByTypeName("facebook"))));
		client4.setSocialNetworks(Arrays.asList(new SocialNetwork("https://vk.com/id", socialNetworkTypeService.getByTypeName("vk")),
				new SocialNetwork("https://fb", socialNetworkTypeService.getByTypeName("facebook"))));
		client1.setJobs(Arrays.asList(new Job("javaMentor", "developer"), new Job("Microsoft", "Junior developer")));
		clientService.addClient(client1);
		clientService.addClient(client2);
		clientService.addClient(client3);
		clientService.addClient(client4);
		status0.addClient(clientService.getClientByEmail("u.dolg@mail.ru"));
		status1.addClient(clientService.getClientByEmail("i.fiod@mail.ru"));
		status2.addClient(clientService.getClientByEmail("vboyko@mail.ru"));
		status3.addClient(clientService.getClientByEmail("a.solo@mail.ru"));
		statusService.add(status0);
		statusService.add(status1);
		statusService.add(status2);
		statusService.add(status3);
		statusService.add(defaultStatus);
		//TEST SMS ERROR NOTIFICATION
		Notification smsErrorNotificationExampleToClient1more = new Notification("Абонент вне зоны действия сети", clientService.getClientByID(1L), userService.get(1L), Notification.Type.SMS);
		Notification smsErrorNotificationExampleToClient2more = new Notification("Абонент вне зоны действия сети", clientService.getClientByID(2L), userService.get(1L), Notification.Type.SMS);
		Notification smsErrorNotificationExampleToClient3more = new Notification("Абонент вне зоны действия сети", clientService.getClientByID(3L), userService.get(1L), Notification.Type.SMS);
		Notification smsErrorNotificationExampleToClient4more = new Notification("Абонент вне зоны действия сети", clientService.getClientByID(4L), userService.get(1L), Notification.Type.SMS);
		notificationService.addNotification(smsErrorNotificationExampleToClient1more);
		notificationService.addNotification(smsErrorNotificationExampleToClient2more);
		notificationService.addNotification(smsErrorNotificationExampleToClient3more);
		notificationService.addNotification(smsErrorNotificationExampleToClient4more);
	}
}
