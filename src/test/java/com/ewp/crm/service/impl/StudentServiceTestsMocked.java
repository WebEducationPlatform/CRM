package com.ewp.crm.service.impl;

import com.ewp.crm.models.*;
import com.ewp.crm.repository.interfaces.StudentRepository;
import com.ewp.crm.repository.interfaces.StudentRepositoryCustom;
import com.ewp.crm.service.interfaces.ProjectPropertiesService;
import com.ewp.crm.service.interfaces.StudentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Тестирование методов studentService с заглушенным репозиторием studentRepository при помощи mockito,
 * без использования базы данных.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class StudentServiceTestsMocked {

    @MockBean
    private StudentRepository studentRepository;

    @MockBean
    private StudentRepositoryCustom studentRepositoryCustom;

    private StudentService studentService;
    private ProjectPropertiesService projectPropertiesService;

    @Autowired
    public StudentServiceTestsMocked(
            StudentService studentService,
            ProjectPropertiesService projectPropertiesService
    ) {
        this.studentService = studentService;
        this.projectPropertiesService = projectPropertiesService;
    }

    private Student createStudent(String clientName, String clentPhone, String clientEmail, String notes, String studentStatus) {
        Client client = new Client.Builder(clientName, clentPhone, clientEmail)
                .lastName("Test")
                .birthDate(LocalDate.parse("1991-10-24"))
                .sex(Client.Sex.MALE)
                .city("Севастополь")
                .country("Россия")
                .build();
        client.setState(Client.State.NEW);
        StudentStatus status = new StudentStatus(studentStatus);
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

    @Test
    public void save_delegateToRepository() {
        Student student = createStudent("Юрий", "7999777553301", "mockTest01@studentServiceTests.ru", "save test", "mockTestStatus");
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        studentService.save(student);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    public void getStudentByClientId_delegateToRepository() {
        Student student = createStudent("Юрий", "7999777553302", "mockTest02@studentServiceTests.ru", "getStudentByClientId test", "mockTestStatus");
        student.getClient().setId(1L);
        when(studentRepository.getStudentByClientId(anyLong())).thenReturn(student);
        assertEquals(studentService.getStudentByClientId(1L).get(), student);
        verify(studentRepository, times(1)).getStudentByClientId(anyLong());
    }

    @Test
    public void addStudentForClient_delegateToRepository() {
        Status clientStatus = new Status("statustest", false, 1L, true, 7, 11);
        Client client = new Client.Builder("Жорес", "7999777553303", "mockTest03@studentServiceTests.ru")
                .lastName("Test")
                .birthDate(LocalDate.parse("1991-10-24"))
                .sex(Client.Sex.MALE)
                .city("Севастополь")
                .country("Россия")
                .build();
        client.setState(Client.State.NEW);
        client.setStatus(clientStatus);

        StudentStatus studentStatus = new StudentStatus("testStatus");
        ProjectProperties projectProperties = projectPropertiesService.getOrCreate();
        projectProperties.setDefaultStudentStatus(studentStatus);

        int trialOffset = client.getStatus().getTrialOffset();
        int nextPaymentOffset = client.getStatus().getNextPaymentOffset();

        Student student = new Student(
                client,
                LocalDateTime.now().plusDays(trialOffset),
                LocalDateTime.now().plusDays(nextPaymentOffset),
                projectProperties.getDefaultPricePerMonth(),
                projectProperties.getDefaultPayment(),
                new BigDecimal(0.00),
                projectProperties.getDefaultStudentStatus(),
                "");

        when(studentRepository.save(any(Student.class))).thenReturn(student);
        assertEquals(studentService.addStudentForClient(client).get(), student);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    public void getStudentByEmail_delegateToRepository() {
        Student student = createStudent("Юрий", "7999777553304", "mockTest04@studentServiceTests.ru", "getStudentByEmail test", "mockTestStatus");
        when(studentRepository.getStudentByEmail(anyString())).thenReturn(student);
        assertEquals(studentService.getStudentByEmail("mockTest04@studentServiceTests.ru"), student);
        verify(studentRepository, times(1)).getStudentByEmail(anyString());
    }

    @Test
    public void getStudentsWithTodayNotificationsEnabled_delegateToRepository() {
        Student student1 = createStudent("Виктор", "7999777553305", "mockTest05@studentServiceTests.ru", "getStudentsWithTodayNotificationsEnabled test", "mockTestStatus");
        Student student2 = createStudent("Владимир", "7999777553306", "mockTest06@studentServiceTests.ru", "getStudentsWithTodayNotificationsEnabled test", "mockTestStatus");
        List<Student> students =Arrays.asList(student1, student2);
        for (Student student : students) {
            student.setNextPaymentDate(LocalDateTime.now());
            student.setNotifySlack(true);
        }
        when(studentRepositoryCustom.getStudentsWithTodayNotificationsEnabled()).thenReturn(students);
        assertEquals(studentService.getStudentsWithTodayNotificationsEnabled(), students);
        verify(studentRepositoryCustom, times(1)).getStudentsWithTodayNotificationsEnabled();
    }

    @Test
    public void getStudentsByStatusId_delegateToRepository() {
        Student student1 = createStudent("Виктор", "7999777553307", "mockTest07@studentServiceTests.ru", "getStudentsByStatusId test", "mockTestStatus");
        Student student2 = createStudent("Владимир", "7999777553308", "mockTest08@studentServiceTests.ru", "getStudentsByStatusId test", "mockTestStatus");
        List<Student> students =Arrays.asList(student1, student2);
        for (Student student : students) {
            student.getStatus().setId(1L);
        }
        when(studentRepository.getStudentsByStatusId(anyLong())).thenReturn(students);
        assertEquals(studentService.getStudentsByStatusId(1L), students);
        verify(studentRepository, times(1)).getStudentsByStatusId(anyLong());
    }

    @Test
    public void getStudentsWithoutSocialProfileByType_delegateToRepository() {
        Student student1 = createStudent("Виктор", "7999777553309", "mockTest09@studentServiceTests.ru", "getStudentsWithoutSocialProfileByType test", "mockTestStatus");
        Student student2 = createStudent("Владимир", "7999777553310", "mockTest10@studentServiceTests.ru", "getStudentsWithoutSocialProfileByType test", "mockTestStatus");
        Student student3 = createStudent("Иннокентий", "7999777553311", "mockTest11@studentServiceTests.ru", "getStudentsWithoutSocialProfileByType test", "mockTestStatus");
        Student student4 = createStudent("Святослав", "7999777553312", "mockTest12@studentServiceTests.ru", "getStudentsWithoutSocialProfileByType test", "mockTestStatus");

        List<SocialProfile> spList1 = new ArrayList<>();
        spList1.add(new SocialProfile("https://vk.com/id999991", SocialProfile.SocialNetworkType.VK));
        spList1.add(new SocialProfile("https://fb.com/id-9999911", SocialProfile.SocialNetworkType.FACEBOOK));
        student1.getClient().setSocialProfiles(spList1);
        List<SocialProfile> spList2 = new ArrayList<>();
        spList2.add(new SocialProfile("https://vk.com/id9999555", SocialProfile.SocialNetworkType.VK));
        student2.getClient().setSocialProfiles(spList2);
        List<SocialProfile> spList3 = new ArrayList<>();
        spList3.add(new SocialProfile("https://fb.com/id-9994444", SocialProfile.SocialNetworkType.FACEBOOK));
        student3.getClient().setSocialProfiles(spList3);
        List<SocialProfile> spList4 = new ArrayList<>();
        spList4.add(new SocialProfile("https://vk.com/id99997777", SocialProfile.SocialNetworkType.VK));
        spList4.add(new SocialProfile("https://fb.com/id-99994747", SocialProfile.SocialNetworkType.FACEBOOK));
        student4.getClient().setSocialProfiles(spList4);

        List<Student> students = Arrays.asList(student1, student2, student3, student4).stream()
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

        when(studentRepositoryCustom.getStudentsWithoutSocialProfileByType(Collections.singletonList(SocialProfile.SocialNetworkType.VK))).thenReturn(students);
        assertEquals(studentService.getStudentsWithoutSocialProfileByType(Collections.singletonList(SocialProfile.SocialNetworkType.VK)), students);
        verify(studentRepositoryCustom, times(1)).getStudentsWithoutSocialProfileByType(anyList());
    }

    @Test
    public void detach_delegateToRepository() {
        Student student = createStudent("Изяслав", "7999777553313", "mockTest13@studentServiceTests.ru", "detach test", "mockTestStatus");
        studentService.detach(student);
        verify(studentRepositoryCustom, times(1)).detach(student);
    }

    @Test
    public void resetColor_delegateToRepository() {
        studentService.resetColors();
        verify(studentRepositoryCustom, times(1)).resetColors();
    }
}
