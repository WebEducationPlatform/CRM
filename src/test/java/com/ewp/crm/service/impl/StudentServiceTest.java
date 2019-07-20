package com.ewp.crm.service.impl;

import com.ewp.crm.models.*;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.ewp.crm.repository.interfaces.StatusRepository;
import com.ewp.crm.repository.interfaces.StudentStatusRepository;
import com.ewp.crm.service.interfaces.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестирование методов StudentService на отдельной базе данных.
 * Перед запуском теста создать базу данных crmtest, на которой будет происходить тестирование,
 * настройки хранятся в файле test\resources\application-test.properties,
 * если убрать (properties = "spring.profiles.active=test"), то тестирование произодет на реальной базе проекта.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "spring.profiles.active=test")
@Rollback
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StudentServiceTest {

    private EntityManager entityManager;
    private StudentService studentService;
    private StatusService statusService;
    private StatusRepository statusRepository;
    private ClientService clientService;
    private ClientRepository clientRepository;
    private ProjectPropertiesService projectPropertiesService;
    private StudentStatusService studentStatusService;
    private StudentStatusRepository studentStatusRepository;

    @Autowired
    public StudentServiceTest(
            EntityManager entityManager,
            StudentService studentService,
            StatusService statusService,
            StatusRepository statusRepository,
            ClientService clientService,
            ClientRepository clientRepository,
            ProjectPropertiesService projectPropertiesService,
            StudentStatusService studentStatusService,
            StudentStatusRepository studentStatusRepository
            ) {
        this.entityManager = entityManager;
        this.studentService = studentService;
        this.statusService = statusService;
        this.statusRepository = statusRepository;
        this.clientService = clientService;
        this.clientRepository = clientRepository;
        this.projectPropertiesService = projectPropertiesService;
        this.studentStatusService = studentStatusService;
        this.studentStatusRepository = studentStatusRepository;
    }

    private Student createStudent(String clientEmail, String studentStatusName, String notes) {
        Client client = clientService.getClientByEmail(clientEmail).get();
        StudentStatus status = studentStatusService.getByName(studentStatusName).get();
        Student student = new Student(
                client,
                LocalDateTime.now().minusDays(2).truncatedTo(ChronoUnit.SECONDS),
                LocalDateTime.now().plusDays(10).truncatedTo(ChronoUnit.SECONDS),
                BigDecimal.valueOf(10011.00).setScale(2, RoundingMode.CEILING),
                BigDecimal.valueOf(10111.00).setScale(2, RoundingMode.CEILING),
                BigDecimal.valueOf(11111.00).setScale(2, RoundingMode.CEILING),
                status,
                notes);
        return student;
    }

    /**
     * Метод сохраняет студента.
     */
    @Test
    public void testSave() {
        Student expectedStudent = createStudent("test01@testStudentService.ru", "testStudentServiceStatus1", "notes for save");
        studentService.save(expectedStudent);

        entityManager.flush();
        entityManager.clear();

        Student actualStudent = studentService.get(expectedStudent.getId());
        assertEquals(expectedStudent, actualStudent);
    }

    /**
     * Метод достает студента с указанным клиенским id.
     */
    @Test
    public void testGetStudentByClientId() {
        Student expectedStudent = createStudent("test02@testStudentService.ru", "testStudentServiceStatus1", "notes for getStudentByClientId");
        studentService.save(expectedStudent);

        entityManager.flush();
        entityManager.clear();

        Student actualStudent = studentService.getStudentByClientId(expectedStudent.getClient().getId()).orElse(null);
        assertEquals(expectedStudent, actualStudent);
    }

    /**
     * Метод добавляет студента ддя клиента, используя данные из projectPropeties и клиентского статуса.
     */
    @Test
    public void testAddStudentForClient() {
        Client client = clientService.getClientByEmail("test03@testStudentService.ru").get();
        StudentStatus status = studentStatusService.getByName("testStudentServiceStatus1").get();

        ProjectProperties projectProperties = projectPropertiesService.getOrCreate();
        projectProperties.setDefaultStudentStatus(status);
        projectPropertiesService.update(projectProperties);

        Status clientStatus = client.getStatus();
        int trialOffset = clientStatus.getTrialOffset();
        int nextPaymentOffset = clientStatus.getNextPaymentOffset();

        Student expectedStudent = new Student(
                client,
                LocalDateTime.now().plusDays(trialOffset).truncatedTo(ChronoUnit.SECONDS),
                LocalDateTime.now().plusDays(nextPaymentOffset).truncatedTo(ChronoUnit.SECONDS),
                projectPropertiesService.getOrCreate().getDefaultPricePerMonth().setScale(2, RoundingMode.CEILING),
                projectPropertiesService.getOrCreate().getDefaultPayment().setScale(2, RoundingMode.CEILING),
                new BigDecimal(0.00),
                status,
                "");

        entityManager.flush();
        entityManager.clear();

        Student actualStudent = studentService.addStudentForClient(client).get();

        expectedStudent.setId(actualStudent.getId());
        expectedStudent.setTrialEndDate(actualStudent.getTrialEndDate());
        expectedStudent.setNextPaymentDate(actualStudent.getNextPaymentDate());

        assertEquals(expectedStudent, actualStudent);
    }

    /**
     * Метод достает студента с указанным e-mail.
     */
    @Test
    public void testGetStudentByEmail() {
        Student expectedStudent = createStudent("test04@testStudentService.ru", "testStudentServiceStatus2", "notes for getStudentByEmail");
        studentService.save(expectedStudent);

        entityManager.flush();
        entityManager.clear();

        Student actualStudent = studentService.getStudentByEmail(expectedStudent.getClient().getEmail().get());
        assertEquals(expectedStudent, actualStudent);
    }

    /**
     * Метод достает студентов, имеющих уведомления включенными уведомлениями и с сегодняшней датой об оплате.
     */
    @Test
    public void testGetStudentsWithTodayNotificationsEnabled() {
        Student expectedStudent1 = createStudent("test05@testStudentService.ru", "testStudentServiceStatus3", "nnotes for getStudentsWithTodayNotificationsEnabled");
        Student expectedStudent2 = createStudent("test06@testStudentService.ru", "testStudentServiceStatus3", "notes for getStudentsWithTodayNotificationsEnabled");
        List<Student> expectedStudents = Arrays.asList(expectedStudent1, expectedStudent2);
        for(Student student : expectedStudents) {
            student.setNextPaymentDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
            student.setNotifySlack(true);
            studentService.save(student);
        }

        entityManager.flush();
        entityManager.clear();

        List<Student> actualStudents = studentService.getStudentsWithTodayNotificationsEnabled();
        assertEquals(expectedStudents, actualStudents);
    }

    /**
     * Метод достает студентов, имеющих статус с указанным id.
     */
    @Test
    public void testGetStudentsByStatusId() {
        Student expectedStudent1 = createStudent("test07@testStudentService.ru", "testStudentServiceStatus4", "notes for getStudentsByStatusId");
        Student expectedStudent2 = createStudent("test08@testStudentService.ru", "testStudentServiceStatus4", "notes for getStudentsByStatusId");
        List<Student> expectedStudents = Arrays.asList(expectedStudent1, expectedStudent2);
        for(Student student : expectedStudents) {
            studentService.save(student);
        }

        entityManager.flush();
        entityManager.clear();

        StudentStatus status = studentStatusService.getByName("testStudentServiceStatus4").get();
        List<Student> actualStudents = studentService.getStudentsByStatusId(status.getId());
        assertEquals(expectedStudents, actualStudents);
    }

    /**
     * Метод достает всех студентов, имеющих не менее одного социального профиля, за исключением тех студентов,
     * которые имеют единственный социальный профиль указанного типа.
     */
    @Test
    public void testGetStudentsWithoutSocialProfileByType() {
        Student student1 = createStudent("test09@testStudentService.ru", "testStudentServiceStatus5", "notes for getStudentsWithoutSocialProfileByType");
        Student student2 = createStudent("test10@testStudentService.ru", "testStudentServiceStatus5", "notes for getStudentsWithoutSocialProfileByType");
        Student student3 = createStudent("test11@testStudentService.ru", "testStudentServiceStatus5", "notes for getStudentsWithoutSocialProfileByType");
        Student student4 = createStudent("test12@testStudentService.ru", "testStudentServiceStatus5", "notes for getStudentsWithoutSocialProfileByType");
        List<Student> students = Arrays.asList(student1, student2, student3, student4);
        for(Student student : students) {
            studentService.save(student);
        }

        List<Student> expectedStudents = studentService.getAll().stream()
                .filter(student -> {
                    boolean isReturned = true;
                    List<SocialProfile> socialProfiles = student.getClient().getSocialProfiles();
                    if (socialProfiles.isEmpty()) return false;
                    for (SocialProfile socialProfile : socialProfiles) {
                        if (socialProfiles.size() == 1 && SocialProfile.SocialNetworkType.VK.equals(socialProfile.getSocialNetworkType())) {
                            isReturned = false;
                        }
                    }
                    return isReturned;
                }).collect(Collectors.toList());

        entityManager.flush();
        entityManager.clear();

        List<Student> actualStudentsWithDublicates = studentService.getStudentsWithoutSocialProfileByType(Arrays.asList(SocialProfile.SocialNetworkType.VK));
        List<Student> actualStudents = actualStudentsWithDublicates.stream().distinct().collect(Collectors.toList());
        assertEquals(expectedStudents, actualStudents);
    }

    /**
     * Метод отделяет сущность от pesistence context.
     */
    @Test
    public void testDetach() {
        Student student = createStudent("test13@testStudentService.ru", "testStudentServiceStatus6", "notes for detach");
        studentService.save(student);

        assertTrue(entityManager.contains(student));
        studentService.detach(student);
        assertFalse(entityManager.contains(student));
    }

    /**
     * Метод сбрасывает цвет для всех студентов.
     */
    @Test
    public void testResetColor() {
        Student student = createStudent("test14@testStudentService.ru", "testStudentServiceStatus6", "notes for resetColor");
        student.setColor("#ff0009");
        studentService.save(student);
        studentService.resetColors();

        entityManager.flush();
        entityManager.clear();

        String color = studentService.getStudentByClientId(student.getClient().getId()).get().getColor();
        assertTrue(color == null);
    }

    /**
     * Метод для создания тестовых данных в базе.
     */
    @BeforeAll
    public void init() {
        Status firstStatus = new Status("status_stud_serv_test0", false, 1L, true, 5, 10);
        Status testStatus = new Status("status_stud_serv_test1", false, 2L, true, 7, 11);

        statusService.addInit(firstStatus);
        statusService.addInit(testStatus);

        Client client1 = new Client.Builder("Юрий", "79993332201", "test01@testStudentService.ru")
                .lastName("Долгоруков")
                .birthDate(LocalDate.parse("1995-09-24"))
                .sex(Client.Sex.MALE)
                .city("Тула")
                .country("Россия")
                .build();
        client1.setState(Client.State.NEW);

        Client client2 = new Client.Builder("Вадим", "79993332202", "test02@testStudentService.ru")
                .lastName("Бойко")
                .birthDate(LocalDate.parse("1989-08-04"))
                .sex(Client.Sex.MALE)
                .city("Тула")
                .country("Россия")
                .build();
        client2.setState(Client.State.NEW);

        Client client3 = new Client.Builder("Александра", "79993332203", "test03@testStudentService.ru")
                .lastName("Соловьева")
                .birthDate(LocalDate.parse("1975-03-10"))
                .sex(Client.Sex.FEMALE)
                .city("Тула")
                .country("Россия")
                .build();
        client3.setState(Client.State.NEW);
        client3.setStatus(testStatus);

        Client client4 = new Client.Builder("Бильбо", "79993332204", "test04@testStudentService.ru")
                .lastName("Бэггинс")
                .birthDate(LocalDate.parse("1979-09-08"))
                .sex(Client.Sex.MALE)
                .city("Сызрань")
                .country("Россия")
                .build();
        client4.setState(Client.State.NEW);

        Client client5 = new Client.Builder("Clay", "79993332205", "test05@testStudentService.ru")
                .lastName("Thompson")
                .birthDate(LocalDate.parse("1992-02-12"))
                .sex(Client.Sex.MALE)
                .city("Караганда")
                .country("Россиюшка")
                .build();
        client5.setState(Client.State.NEW);

        Client client6 = new Client.Builder("Stephen", "79993332206", "test06@testStudentService.ru")
                .lastName("Curry")
                .birthDate(LocalDate.parse("1990-01-15"))
                .sex(Client.Sex.MALE)
                .city("Киев")
                .country("Украина")
                .build();
        client6.setState(Client.State.NEW);

        Client client7 = new Client.Builder("Michael", "79993332207", "test07@testStudentService.ru")
                .lastName("Jordan")
                .birthDate(LocalDate.parse("1986-05-04"))
                .sex(Client.Sex.MALE)
                .city("Шарлотт")
                .country("США")
                .build();
        client7.setState(Client.State.NEW);

        Client client8 = new Client.Builder("Scottie", "79993332208", "test08@testStudentService.ru")
                .lastName("pippen")
                .birthDate(LocalDate.parse("1988-01-01"))
                .sex(Client.Sex.MALE)
                .city("Гаваи")
                .country("США")
                .build();
        client8.setState(Client.State.NEW);

        Client client9 = new Client.Builder("Steve", "79993332209", "test09@testStudentService.ru")
                .lastName("Kerr")
                .birthDate(LocalDate.parse("1987-02-02"))
                .sex(Client.Sex.MALE)
                .city("Висконсин")
                .country("США")
                .build();
        client9.setState(Client.State.NEW);

        Client client10 = new Client.Builder("Alexander", "79993332210", "test10@testStudentService.ru")
                .lastName("Berk")
                .birthDate(LocalDate.parse("1982-01-02"))
                .sex(Client.Sex.MALE)
                .city("Кливленд")
                .country("США")
                .build();
        client10.setState(Client.State.NEW);

        Client client11 = new Client.Builder("Victor", "79993332211", "test11@testStudentService.ru")
                .lastName("Scrum")
                .birthDate(LocalDate.parse("1985-01-02"))
                .sex(Client.Sex.MALE)
                .city("Каролина")
                .country("США")
                .build();
        client11.setState(Client.State.NEW);

        Client client12 = new Client.Builder("Harry", "79993332212", "test12@testStudentService.ru")
                .lastName("Potter")
                .birthDate(LocalDate.parse("1989-05-22"))
                .sex(Client.Sex.MALE)
                .city("Лондон")
                .country("Британия")
                .build();
        client12.setState(Client.State.NEW);

        Client client13 = new Client.Builder("Hermiona", "79993332213", "test13@testStudentService.ru")
                .lastName("Granger")
                .birthDate(LocalDate.parse("1998-02-07"))
                .sex(Client.Sex.FEMALE)
                .city("Манчестер")
                .country("Британия")
                .build();
        client13.setState(Client.State.NEW);

        Client client14 = new Client.Builder("Ron", "79993332214", "test14@testStudentService.ru")
                .lastName("Weasley")
                .birthDate(LocalDate.parse("1997-04-04"))
                .sex(Client.Sex.MALE)
                .city("Ливерпуль")
                .country("Британия")
                .build();
        client14.setState(Client.State.NEW);

        List<SocialProfile> spList1 = new ArrayList<>();
        spList1.add(new SocialProfile("https://vk.com/id999991", SocialProfile.SocialNetworkType.VK));
        spList1.add(new SocialProfile("https://fb.com/id-9999911", SocialProfile.SocialNetworkType.FACEBOOK));
        client9.setSocialProfiles(spList1);
        List<SocialProfile> spList2 = new ArrayList<>();
        spList2.add(new SocialProfile("https://vk.com/id9999555", SocialProfile.SocialNetworkType.VK));
        client10.setSocialProfiles(spList2);
        List<SocialProfile> spList3 = new ArrayList<>();
        spList3.add(new SocialProfile("https://fb.com/id-9994444", SocialProfile.SocialNetworkType.FACEBOOK));
        client11.setSocialProfiles(spList3);
        List<SocialProfile> spList4 = new ArrayList<>();
        spList4.add(new SocialProfile("https://vk.com/id99997777", SocialProfile.SocialNetworkType.VK));
        spList4.add(new SocialProfile("https://fb.com/id-99994747", SocialProfile.SocialNetworkType.FACEBOOK));
        client12.setSocialProfiles(spList4);

        List<Client> clients = Arrays.asList(
                client1,
                client2,
                client3,
                client4,
                client5,
                client6,
                client7,
                client8,
                client9,
                client10,
                client11,
                client12,
                client13,
                client14
        );
        clientRepository.saveAll(clients);

        StudentStatus status1 = new StudentStatus("testStudentServiceStatus1");
        StudentStatus status2 = new StudentStatus("testStudentServiceStatus2");
        StudentStatus status3 = new StudentStatus("testStudentServiceStatus3");
        StudentStatus status4 = new StudentStatus("testStudentServiceStatus4");
        StudentStatus status5 = new StudentStatus("testStudentServiceStatus5");
        StudentStatus status6 = new StudentStatus("testStudentServiceStatus6");
        StudentStatus status7 = new StudentStatus("testStudentServiceStatus7");

        List<StudentStatus> statuses = Arrays.asList(
                status1,
                status2,
                status3,
                status4,
                status5,
                status6,
                status7
        );
        studentStatusRepository.saveAll(statuses);
    }

    /**
     * Метод для удаления тестовых данных из базы.
     */
    @AfterAll
    public void clean() {
        clientService.delete(clientService.getClientByEmail("test01@testStudentService.ru").get());
        clientService.delete(clientService.getClientByEmail("test02@testStudentService.ru").get());
        clientService.delete(clientService.getClientByEmail("test03@testStudentService.ru").get());
        clientService.delete(clientService.getClientByEmail("test04@testStudentService.ru").get());
        clientService.delete(clientService.getClientByEmail("test05@testStudentService.ru").get());
        clientService.delete(clientService.getClientByEmail("test06@testStudentService.ru").get());
        clientService.delete(clientService.getClientByEmail("test07@testStudentService.ru").get());
        clientService.delete(clientService.getClientByEmail("test08@testStudentService.ru").get());
        clientService.delete(clientService.getClientByEmail("test09@testStudentService.ru").get());
        clientService.delete(clientService.getClientByEmail("test10@testStudentService.ru").get());
        clientService.delete(clientService.getClientByEmail("test11@testStudentService.ru").get());
        clientService.delete(clientService.getClientByEmail("test12@testStudentService.ru").get());
        clientService.delete(clientService.getClientByEmail("test13@testStudentService.ru").get());
        clientService.delete(clientService.getClientByEmail("test14@testStudentService.ru").get());

        studentStatusService.delete(studentStatusService.getByName("testStudentServiceStatus1").get());
        studentStatusService.delete(studentStatusService.getByName("testStudentServiceStatus2").get());
        studentStatusService.delete(studentStatusService.getByName("testStudentServiceStatus3").get());
        studentStatusService.delete(studentStatusService.getByName("testStudentServiceStatus4").get());
        studentStatusService.delete(studentStatusService.getByName("testStudentServiceStatus5").get());
        studentStatusService.delete(studentStatusService.getByName("testStudentServiceStatus6").get());

        statusRepository.delete(statusService.get("status_stud_serv_test0").get());
        statusRepository.delete(statusService.get("status_stud_serv_test1").get());
    }
}
