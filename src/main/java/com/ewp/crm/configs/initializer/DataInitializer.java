package com.ewp.crm.configs.initializer;

import com.ewp.crm.configs.inteface.VKConfig;
import com.ewp.crm.exceptions.member.NotFoundMemberList;
import com.ewp.crm.models.*;
import com.ewp.crm.models.SocialProfile.SocialNetworkType;
import com.ewp.crm.repository.interfaces.vkcampaigns.VkAttemptResponseRepository;
import com.ewp.crm.service.conversation.JMConversationHelper;
import com.ewp.crm.service.interfaces.*;
import com.ewp.crm.service.interfaces.vkcampaigns.VkCampaignService;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

public class DataInitializer {

    @Autowired
    private VkTrackedClubService vkTrackedClubService;

    @Autowired
    private VKConfig vkConfig;

    @Autowired
    private VkMemberService vkMemberService;

    @Autowired
    private VKService vkService;

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
    private ClientHistoryService clientHistoryService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentStatusService studentStatusService;

    @Autowired
    private VkRequestFormService vkRequestFormService;

    @Autowired
    private VkCampaignService vkCampaignService;

    @Autowired
    private VkAttemptResponseRepository vkAttemptResponseRepository;

    @Autowired
    private ListMailingTypeService listMailingTypeService;

    @Autowired
    private JMConversationHelper jmConversationHelper;

    private void init() {

        // DEFAULT STATUS AND FIRST STATUS FOR RELEASE
        Status defaultStatus = new Status("deleted", true, 5L, false, 0, 0);
        Status status0 = new Status("New clients", false, 1L, false, 0, 0);

        Role[] roles = {new Role("ADMIN"),
                        new Role("MENTOR"),
                        new Role("OWNER"),
                        new Role("USER"),
                        new Role("HR")};

        for (Role role: roles) {
            roleService.add(role);
        }

        ListMailingType vkList = new ListMailingType("vk");
        ListMailingType emailList = new ListMailingType("email");
        ListMailingType slackList = new ListMailingType("slack");
        ListMailingType smsList = new ListMailingType("sms");
        listMailingTypeService.add(vkList);
        listMailingTypeService.add(emailList);
        listMailingTypeService.add(slackList);
        listMailingTypeService.add(smsList);

        User admin = new User(
                "Stanislav",
                "Sorokin",
                LocalDate.of(1975, 12, 12),
                "88062334088",
                "admin@mail.ru",
                "admin",
                null, Client.Sex.MALE.toString(),
                "Moscow",
                "Russia",
                Arrays.asList(roleService.getRoleByName("USER"), roleService.getRoleByName("ADMIN"),
                        roleService.getRoleByName("OWNER")),
                true,
                true);
        userService.add(admin);

        User user1 = new User("Ivan", "Ivanov", LocalDate.of(1992, 9, 24), "79123456789", "user1@mail.ru",
                "user", null, Client.Sex.MALE.toString(), "Minsk", "Belarus", Collections.singletonList(roleService.getRoleByName("USER")), true, false);
        userService.add(user1);

        User user2 = new User("Petr", "Petrov", LocalDate.of(1984, 4, 22), "89118465234", "user2@mail.ru",
                "user", null, Client.Sex.MALE.toString(), "Tver", "Russia", Arrays.asList(roleService.getRoleByName("USER"), roleService.getRoleByName("MENTOR")), true, true);
        userService.add(user2);

        User user3 = new User("Vlad", "Mentor", LocalDate.of(1990, 11, 12), "89118465234", "photolife9112@gmail.com",
                "user", null, Client.Sex.MALE.toString(), "Tver", "Russia", Collections.singletonList(roleService.getRoleByName("MENTOR")), true, true);
        userService.add(user3);

        User user4 = new User("Nikita", "Mentor", LocalDate.of(1994, 2, 5), "89118465234", "ccfilcc@gmail.com",
                "user", null, Client.Sex.MALE.toString(), "Tver", "Russia", Collections.singletonList(roleService.getRoleByName("MENTOR")), true, true);
        userService.add(user4);

        User user5 = new User("Benedikt", "Manager", LocalDate.of(1988, 7, 19), "9999999999", "qqfilqq@gmail.com",
                "user", null, Client.Sex.MALE.toString(), "Tver", "Russia", Arrays.asList(roleService.getRoleByName("USER"), roleService.getRoleByName("ADMIN"),
                roleService.getRoleByName("OWNER")), true, true);
        userService.add(user5);

        String templateText5 = "<!DOCTYPE html>\n" +
                "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:th=\"http://www.thymeleaf.org\">\n" +
                "<head></head>\n" +
                "<body>\n" +
                "<p>Добрый день,</p>\n" +
                "<p>Спасибо за вашу заявку, скоро мы с вами свяжемся! </p>\n" +
                "<p>Когда вам было бы удобно провести первый созвон с ментором?</p>\n" +
                "<p>С наилучшими пожеланиями, команда JavaMentor.</p>\n" +
                "<img src=\"https://sun9-9.userapi.com/c841334/v841334855/6acfb/_syiwM0RH0I.jpg\"/>\n" +
                "</body>\n" +
                "</html>";

        String templateText4 = "<!DOCTYPE html>\n" +
                "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:th=\"http://www.thymeleaf.org\">\n" +
                "<head></head>\n" +
                "<body>\n" +
                "<p>Добрый день, %fullName%</p>\n" +
                "<p>Напоминаем, что сегодня %dateOfSkypeCall% с Вами состоится беседа по Skype.</p>\n" +
                "<p>С наилучшими пожеланиями, команда JavaMentor.</p>\n" +
                "<img src=\"https://sun9-9.userapi.com/c841334/v841334855/6acfb/_syiwM0RH0I.jpg\"/>\n" +
                "</body>\n" +
                "</html>";

        String templateText3 = "<!DOCTYPE html>\n" +
                "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:th=\"http://www.thymeleaf.org\">\n" +
                "<head></head>\n" +
                "<body>\n" +
                "<p>Добрый день, %fullName%</p>\n" +
                "<p>Мы не смогли до Вас дозвониться.</p>\n" +
                "<p>Пожалуйста, свяжитесь с нами</p>\n" +
                "<p>С наилучшими пожеланиями, команда JavaMentor.</p>\n" +
                "<img src=\"https://sun9-9.userapi.com/c841334/v841334855/6acfb/_syiwM0RH0I.jpg\"/>\n" +
                "</body>\n" +
                "</html>";
        String templateText2 = "<!DOCTYPE html>\n" +
                "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:th=\"http://www.thymeleaf.org\">\n" +
                "<head></head>\n" +
                "<body>\n" +
                "<p>Добрый день, %fullName%</p>\n" +
                "<p>Напоминаем, что вам необходимо оплатить обучение за следующий месяц.</p>\n" +
                "<p>С наилучшими пожеланиями, команда JavaMentor.</p>\n" +
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

        String newLine = System.getProperty("line.separator");
        String otherText5 = "Добрый день!\n" + newLine +
                "Спасибо за вашу заявку, скоро мы с вами свяжемся!" + newLine +
                "Когда вам было бы удобно провести первый созвон с ментором?" + newLine +
                "С наилучшими пожеланиями," + newLine +
                "команда JavaMentor.";

        String otherText4 = "Добрый день, %fullName%!\n Напоминаем, что сегодня %dateOfSkypeCall% с Вами состоится беседа по Skype.\n" +
                "С наилучшими пожеланиями, команда JavaMentor.";
        String otherText3 = "Добрый день, %fullName%!\n Mы не смогли до Вас дозвониться.\n" +
                "Пожалуйста, свяжитесь с нами.\n" + "С наилучшими пожеланиями, команда JavaMentor.";
        String otherText2 = "Добрый день, %fullName%!\nНапоминаем, что вам необходимо оплатить обучение за следующий месяц.\n" +
                "С наилучшими пожеланиями, команда JavaMentor.";
        String otherText1 = "%bodyText%";

        MessageTemplate MessageTemplate5 = new MessageTemplate("Автоответ из Java-Mentor", templateText5, otherText5);
        MessageTemplate MessageTemplate4 = new MessageTemplate("Беседа по Skype", templateText4, otherText4);
        MessageTemplate MessageTemplate3 = new MessageTemplate("Не дозвонился", templateText3, otherText3);
        MessageTemplate MessageTemplate2 = new MessageTemplate("Оплата за обучение", templateText2, otherText2);
        MessageTemplate MessageTemplate1 = new MessageTemplate("После разговора", templateText1, otherText1);
        MessageTemplateService.add(MessageTemplate1);
        MessageTemplateService.add(MessageTemplate2);
        MessageTemplateService.add(MessageTemplate3);
        MessageTemplateService.add(MessageTemplate4);
        MessageTemplateService.add(MessageTemplate5);

        Status status1 = new Status("trialLearnStatus", false, 2L, true, 3, 33);
        Status status2 = new Status("inLearningStatus", false, 3L, true, 0, 30);
        Status status3 = new Status("pauseLearnStatus", false, 4L, false, 0, 0);
        Status status4 = new Status("endLearningStatus", false, 5L, false, 0, 0);
        Status status5 = new Status("dropOut Status", false, 6L, false, 0, 0);

        Client.Builder clientBuilder1 = new Client.Builder("Юрий", "79999992288", "u.dolg@mail.ru");
        Client client1 = clientBuilder1.lastName("Долгоруков")
                                        .birthDate(LocalDate.parse("1995-09-24"))
                                        .sex(Client.Sex.MALE)
                                        .city("Тула")
                                        .country("Россия")
                                        .build();
        client1.setState(Client.State.FINISHED);
        Client.Builder clientBuilder2 = new Client.Builder("Вадим", "89687745632", "vboyko@mail.ru");
        Client client2 = clientBuilder2.lastName("Бойко")
                                        .birthDate(LocalDate.parse("1989-08-04"))
                                        .sex(Client.Sex.MALE)
                                        .city("Тула")
                                        .country("Россия")
                                        .build();
        client2.setState(Client.State.LEARNING);
        client2.setDateOfRegistration(ZonedDateTime.ofInstant(Instant.now().minusMillis(200000000), ZoneId.systemDefault()));
        Client.Builder clientBuilder3 = new Client.Builder("Александра", "78300029530", "a.solo@mail.ru");
        Client client3 = clientBuilder3.lastName("Соловьева")
                                        .birthDate(LocalDate.parse("1975-03-10"))
                                        .sex(Client.Sex.FEMALE)
                                        .city("Тула")
                                        .country("Россия")
                                        .build();
        client3.setState(Client.State.LEARNING);
        client3.setDateOfRegistration(ZonedDateTime.ofInstant(Instant.now().minusMillis(300000000), ZoneId.systemDefault()));
        Client.Builder clientBuilder4 = new Client.Builder("Иван", "78650824705", "i.fiod@mail.ru");
        Client client4 = clientBuilder4.lastName("Федоров")
                                        .birthDate(LocalDate.parse("1995-05-04"))
                                        .sex(Client.Sex.MALE)
                                        .city("Тула")
                                        .country("Россия")
                                        .build();
        client4.setState(Client.State.NEW);
        client4.setDateOfRegistration(ZonedDateTime.ofInstant(Instant.now().minusMillis(400000000), ZoneId.systemDefault()));

        client1.addSMSInfo(new SMSInfo(123456789L, "SMS Message to client 1", admin));
        client2.addSMSInfo(new SMSInfo(12345678L, "SMS Message to client 2", admin));
        client3.addSMSInfo(new SMSInfo(1234567L, "SMS Message to client 3", admin));
        client4.addSMSInfo(new SMSInfo(123456L, "SMS Message to client 4", admin));
        clientHistoryService.createHistory("инициализации crm").ifPresent(client1::addHistory);
        clientHistoryService.createHistory("инициализации crm").ifPresent(client2::addHistory);
        clientHistoryService.createHistory("инициализации crm").ifPresent(client3::addHistory);
        clientHistoryService.createHistory("инициализации crm").ifPresent(client4::addHistory);
        List<SocialProfile> spList1 = new ArrayList<>();
        spList1.add(new SocialProfile("https://vk.com/id1", SocialNetworkType.VK));
        spList1.add(  new SocialProfile("https://fb.com/id-1", SocialNetworkType.FACEBOOK));
        client1.setSocialProfiles(spList1);
        List<SocialProfile> spList2 = new ArrayList<>();
        spList2.add(new SocialProfile("https://vk.com/id6", SocialNetworkType.VK));
        spList2.add(new SocialProfile("https://fb.com/id-6", SocialNetworkType.FACEBOOK));
        client2.setSocialProfiles(spList2);
        List<SocialProfile> spList3 = new ArrayList<>();
        spList3.add(new SocialProfile("https://vk.com/id7", SocialNetworkType.VK));
        spList3.add(new SocialProfile("https://fb.com/id-3", SocialNetworkType.FACEBOOK));
        client3.setSocialProfiles(spList3);
        List<SocialProfile> spList4 = new ArrayList<>();
        spList4.add(new SocialProfile("https://vk.com/id8", SocialNetworkType.VK));
        spList4.add(new SocialProfile("https://fb.com/id-4", SocialNetworkType.FACEBOOK));
        client4.setSocialProfiles(spList4);
        client1.setJobs(Arrays.asList(new Job("javaMentor", "developer"), new Job("Microsoft", "Junior developer")));

//        vkTrackedClubService.add(new VkTrackedClub(Long.parseLong(vkConfig.getClubId()),
//                vkConfig.getCommunityToken(),
//                "JavaMentorTest",
//                Long.parseLong(vkConfig.getApplicationId())));
//        List<VkTrackedClub> vkTrackedClubs = vkTrackedClubService.getAll();
//        for (VkTrackedClub vkTrackedClub : vkTrackedClubs) {
//            List<VkMember> memberList = vkService.getAllVKMembers(vkTrackedClub.getGroupId(), 0L)
//                    .orElseThrow(() -> new NotFoundMemberList("Лист подписчиков сообщества не был получен"));
//            vkMemberService.addAllMembers(memberList);
//        }



        clientService.addClient(client1);
        clientService.addClient(client2);
        clientService.addClient(client3);
        clientService.addClient(client4);
        clientService.getClientByEmail("u.dolg@mail.ru").ifPresent(status0::addClient);
        clientService.getClientByEmail("i.fiod@mail.ru").ifPresent(status1::addClient);
        clientService.getClientByEmail("vboyko@mail.ru").ifPresent(status2::addClient);
        clientService.getClientByEmail("a.solo@mail.ru").ifPresent(status3::addClient);
        statusService.addInit(status0);
        statusService.addInit(status1);
        statusService.addInit(status2);
        statusService.addInit(status3);
        statusService.addInit(status4);
        statusService.addInit(status5);
        statusService.addInit(defaultStatus);

        StudentStatus trialStatus = studentStatusService.add(new StudentStatus("Java CORE"));
        StudentStatus learningStatus = studentStatusService.add(new StudentStatus("Java web"));
        StudentStatus pauseStatus = studentStatusService.add(new StudentStatus("Spring MVC"));

        clientService.getClientByEmail("i.fiod@mail.ru").ifPresent(c -> studentService.add(
                new Student(c, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(3), new BigDecimal(12000.00),
                        new BigDecimal(8000.00), new BigDecimal(4000.00), trialStatus, "На пробных")));
        clientService.getClientByEmail("vboyko@mail.ru").ifPresent(c -> studentService.add(
                new Student(c, LocalDateTime.now(), LocalDateTime.now().plusDays(30), new BigDecimal(12000.00),
                        new BigDecimal(8000.00), new BigDecimal(4000.00), learningStatus, "Быстро учится")));
        clientService.getClientByEmail("a.solo@mail.ru").ifPresent(c -> studentService.add(
                new Student(c, LocalDateTime.now(), LocalDateTime.now().plusDays(14), new BigDecimal(12000.00),
                        new BigDecimal(12000.00), new BigDecimal(0.00), pauseStatus, "Уехал в отпуск на 2 недели")));


        //TODO удалить после теста

        Faker faker = new Faker();
        List<Client> list = new LinkedList<>();
        for (int i = 0; i < 20; i++) {
            if (statusService.get("trialLearnStatus").isPresent()) {
                Client.Builder clientBuilder = new Client.Builder(faker.name().firstName(), faker.phoneNumber().phoneNumber(), "teststatususer" + i + "@gmail.com");
                Client client = clientBuilder.lastName(faker.name().lastName())
                                                .birthDate(LocalDate.parse("1990-01-01"))
                                                .sex(Client.Sex.MALE)
                                                .build();
                client.setStatus(statusService.get("trialLearnStatus").get());
                clientHistoryService.createHistory("инициализация crm").ifPresent(client::addHistory);
                list.add(client);
            }
        }
        clientService.addBatchClients(list);
        list.clear();

        for (int i = 0; i < 50; i++) {
            if (statusService.get("endLearningStatus").isPresent()) {
                Client.Builder clientBuilder = new Client.Builder(faker.name().firstName(), faker.phoneNumber().phoneNumber(), "testclient" + i + "@gmail.com");
                Client client = clientBuilder.lastName(faker.name().lastName())
                                                .birthDate(LocalDate.parse("1990-01-01"))
                                                .sex(Client.Sex.MALE)
                                                .build();
                client.setStatus(statusService.get("endLearningStatus").get());
                clientHistoryService.createHistory("инициализация crm").ifPresent(client::addHistory);
                list.add(client);
            }
        }
        clientService.addBatchClients(list);


        VkRequestForm vkRequestForm1 = new VkRequestForm(1, "Имя", "Поле сопоставленное с данными");
        VkRequestForm vkRequestForm2 = new VkRequestForm(2, "Фамилия", "Поле сопоставленное с данными");
        VkRequestForm vkRequestForm3 = new VkRequestForm(3, "Номер телефона", "Поле сопоставленное с данными");
        VkRequestForm vkRequestForm4 = new VkRequestForm(4, "Ваш skype для первого созвона с ментором (необязательно)", "Дополнительная информация");
        VkRequestForm vkRequestForm5 = new VkRequestForm(5, "Когда удобно начать первый пробный день?", "Дополнительная информация");

        vkRequestFormService.addVkRequestForm(vkRequestForm1);
        vkRequestFormService.addVkRequestForm(vkRequestForm2);
        vkRequestFormService.addVkRequestForm(vkRequestForm3);
        vkRequestFormService.addVkRequestForm(vkRequestForm4);
        vkRequestFormService.addVkRequestForm(vkRequestForm5);



        Client clientN2 = clientService.get(2L);
        List<String> emails = new ArrayList<>();
        emails.add("yabloko@mail.ru");
        emails.add("apricot@mail.ru");
        emails.add("gribok@mail.ru");
        emails.add("zibra@mail.ru");
        emails.add("cemel@mail.ru");
        List<String> phones = new ArrayList<>();
        phones.add("7-123123123");
        phones.add("7-345345345");
        phones.add("7-567567567");
        phones.add("7-789789789");
        clientN2.setClientEmails(emails);
        clientN2.setClientPhones(phones);
        clientService.update(clientN2);
        clientN2.setEmail("miqolay@gmail.com");
        clientN2.setPhoneNumber("79080584002");
        clientService.update(clientN2);
        Client nulli = new Client.Builder("Nulli", null, null ).lastName("Nullov").build();
        System.out.println(nulli.getEmail().orElse("no Email"));
        System.out.println(clientN2.getEmail().orElse("not found"));

    }
}
