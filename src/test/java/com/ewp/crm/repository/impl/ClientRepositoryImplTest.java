package com.ewp.crm.repository.impl;

import com.ewp.crm.models.*;
import com.ewp.crm.repository.SlackInviteLinkRepository;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.ewp.crm.service.interfaces.ClientHistoryService;
import com.ewp.crm.service.interfaces.StatusService;
import com.ewp.crm.service.interfaces.StudentService;
import com.ewp.crm.service.interfaces.StudentStatusService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = "spring.profiles.active=test")
//    classes = CrmApplication.class
//    @TestPropertySource(locations="classpath:application-test.properties")
@Transactional
@Rollback
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClientRepositoryImplTest {
    @Resource
    ClientRepository clientRepository;
    @Resource
    private StudentService studentService;
    @Resource
    private StudentStatusService studentStatusService;
    @Resource
    private StatusService statusService;
    @Resource
    private ClientHistoryService clientHistoryService;
    @Resource
    private SlackInviteLinkRepository slackInviteLinkRepository;

    /*
    Init testdata
     */
    @BeforeAll
    public void before() {
        Status defaultStatus = new Status("deleted", true, 5L, false, 0, 0);
        Status status0 = new Status("New clients", false, 1L, false, 0, 0);
        Status status1 = new Status("trialLearnStatus", false, 2L, true, 3, 33);
        statusService.addInit(status0);
        statusService.addInit(status1);
        statusService.addInit(defaultStatus);
//            Add first client student
        Client.Builder clientBuilder1 = new Client.Builder("Test1", "79144830771", "test1@testmail.ru");
        Client client1 = clientBuilder1.lastName("Testing1")
                .birthDate(LocalDate.parse("1995-09-24"))
                .sex(Client.Sex.MALE)
                .city("Test1")
                .country("Test1")
                .build();
        client1.setState(Client.State.FINISHED);
        List<SocialProfile> spList1 = new ArrayList<>();
        spList1.add(new SocialProfile("https://vk.com/id1", SocialProfile.SocialNetworkType.VK));
        client1.setSocialProfiles(spList1);
        client1.setStatus(status1);
        clientHistoryService.createHistory("Test init crm - test1").ifPresent(client1::addHistory);
        clientRepository.save(client1);

//            Add two client -not student
        Client.Builder clientBuilder2 = new Client.Builder("Test2", "79144830772", "test2@testmail.ru");
        Client client2 = clientBuilder2.lastName("Testing2")
                .birthDate(LocalDate.parse("1995-09-24"))
                .sex(Client.Sex.MALE)
                .city("Test2")
                .country("Test2")
                .build();
        client2.setState(Client.State.FINISHED);
        List<SocialProfile> spList2 = new ArrayList<>();
        spList2.add(new SocialProfile("https://vk.com/id2", SocialProfile.SocialNetworkType.VK));
        spList2.add(new SocialProfile("https://fb.com/id-2", SocialProfile.SocialNetworkType.FACEBOOK));
        client2.setSocialProfiles(spList2);
        client2.setStatus(status0);
        clientHistoryService.createHistory("Test init crm - test2").ifPresent(client2::addHistory);

        clientRepository.save(client2);
        //        for SlackIvitelinks test
        SlackInviteLink newLink = new SlackInviteLink();
        newLink.setClient(client1);
        String newHash = UUID.randomUUID().toString();
        newLink.setHash(newHash);
        client1.setSlackInviteLink(newLink);
        slackInviteLinkRepository.saveAndFlush(newLink);

        clientRepository.saveAndFlush(client1);


        StudentStatus trialStatus = studentStatusService.add(new StudentStatus("Java CORE"));
        studentService.add(new Student(client1, LocalDateTime.now(), LocalDateTime.now().plusDays(30),
                new BigDecimal(12000.00),
                new BigDecimal(8000.00), new BigDecimal(4000.00),
                trialStatus, "Быстро учится"));


    }

    @Test
    void ShouldGetSocialIdsBySocialProfileTypeAndStudentExists() {
        assertEquals(2, clientRepository.findAll().size());
        assertEquals(1, clientRepository.getSocialIdsBySocialProfileTypeAndStudentExists(
                SocialProfile.SocialNetworkType.VK.getName()).size());
        assertEquals("https://vk.com/id1", clientRepository.getSocialIdsBySocialProfileTypeAndStudentExists(
                SocialProfile.SocialNetworkType.VK.getName()).get(0));
    }

    @Test
    void ShouldHasClientSocialProfileByType() {
        Client client = clientRepository.getClientByNameAndLastNameIgnoreCase("Test1", "Testing1");
        assertEquals(2, clientRepository.findAll().size());
        assertTrue(clientRepository.hasClientSocialProfileByType(client, SocialProfile.SocialNetworkType.VK.getName()));
        assertFalse(clientRepository.hasClientSocialProfileByType(client, SocialProfile.SocialNetworkType.FACEBOOK.getName()));
    }

    @Test
    void ShouldGetSocialIdsBySocialProfileTypeAndStatusAndStudentExists() {
        List<Status> statuses = statusService.getAll();
        assertEquals(2, clientRepository.findAll().size());
        assertEquals(1, clientRepository.getSocialIdsBySocialProfileTypeAndStatusAndStudentExists(
                statuses, SocialProfile.SocialNetworkType.VK.getName()).size());
        assertEquals("https://vk.com/id1", clientRepository.getSocialIdsBySocialProfileTypeAndStatusAndStudentExists(
                statuses, SocialProfile.SocialNetworkType.VK.getName()).get(0));
        assertTrue(clientRepository.getSocialIdsBySocialProfileTypeAndStatusAndStudentExists(
                statuses, SocialProfile.SocialNetworkType.FACEBOOK.getName()).isEmpty());
    }

    @Test
    void ShouldgetClientByTimeInterval() {
        assertEquals(2, clientRepository.findAll().size());
        assertEquals(2, clientRepository.getClientByTimeInterval(1).size());
        assertTrue(clientRepository.getClientByTimeInterval(1).get(0).getTitle().contains("test1"));
    }

    @Test
    void ShouldCountByDate() {
        Date dt = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(dt);
        assertEquals(2, clientRepository.countByDate(currentTime));
    }

    @Test
    void ShouldgetSlackLinkHashForClient() {
        Client client = clientRepository.getClientByNameAndLastNameIgnoreCase("Test1", "Testing1");
        Client clientNotSlack = clientRepository.getClientByNameAndLastNameIgnoreCase("Test2", "Testing2");
        assertTrue(clientRepository.getSlackLinkHashForClient(client) instanceof String);
        assertNull(clientRepository.getSlackLinkHashForClient(clientNotSlack));

    }

    @Test
    void ShouldGetNearestClientHistoryBeforeDate() {
        Client client = clientRepository.getClientByNameAndLastNameIgnoreCase("Test2", "Testing2");
        assertTrue(clientRepository.getNearestClientHistoryBeforeDate(client, ZonedDateTime.now().plusDays(1),
                Arrays.asList(ClientHistory.Type.values())).getTitle().contains("test2"));
        assertNull(clientRepository.getNearestClientHistoryBeforeDate(client, ZonedDateTime.now().minusDays(1),
                Arrays.asList(ClientHistory.Type.values())));
    }

    @Test
    void ShouldGetNearestClientHistoryAfterDate() {
        Client client = clientRepository.getClientByNameAndLastNameIgnoreCase("Test2", "Testing2");
        assertTrue(clientRepository.getNearestClientHistoryAfterDate(client, ZonedDateTime.now().minusDays(1),
                Arrays.asList(ClientHistory.Type.values())).getTitle().contains("test2"));
        assertNull(clientRepository.getNearestClientHistoryAfterDate(client, ZonedDateTime.now(),
                Arrays.asList(ClientHistory.Type.values())));
    }


    @Test
    void ShouldgetNearestClientHistoryAfterDateByHistoryType() {
        Client client = clientRepository.getClientByNameAndLastNameIgnoreCase("Test2", "Testing2");
        assertNotNull(clientRepository.getNearestClientHistoryAfterDateByHistoryType(client, ZonedDateTime.now().minusDays(1),
                Arrays.asList(ClientHistory.Type.SOCIAL_REQUEST), "test2"));
        assertNull(clientRepository.getNearestClientHistoryAfterDateByHistoryType(client, ZonedDateTime.now().plusDays(1),
                Arrays.asList(ClientHistory.Type.SOCIAL_REQUEST), "test2"));
    }

    @Test
    void ShouldgetHistoryByClientAndHistoryTimeIntervalAndHistoryType() {
        Client client = clientRepository.getClientByNameAndLastNameIgnoreCase("Test2", "Testing2");
        assertNotNull(clientRepository.getHistoryByClientAndHistoryTimeIntervalAndHistoryType(
                client,
                ZonedDateTime.now().minusDays(1),
                ZonedDateTime.now().plusDays(1),
                Arrays.asList(ClientHistory.Type.SOCIAL_REQUEST),
                "test2"));
        assertNull(clientRepository.getHistoryByClientAndHistoryTimeIntervalAndHistoryType(
                client,
                ZonedDateTime.now().minusDays(10),
                ZonedDateTime.now().minusDays(2),
                Arrays.asList(ClientHistory.Type.SOCIAL_REQUEST),
                "test2"));
    }
//
//    @Test
//    void getClientFirstStatusChangingHistory() {
//        Client client = clientRepository.getClientByNameAndLastNameIgnoreCase("Test2", "Testing2");
//
//
//        assertNotNull(clientRepository.getClientFirstStatusChangingHistory(client.getId()));
//    }
//
//    @Test
//    void hasClientStatusChangingHistory() {
//    }
//
//    @Test
//    void hasClientBeenInStatusBefore() {
//    }
//
//    @Test
//    void getClientByHistoryTimeIntervalAndHistoryType() {
//    }
//
//    @Test
//    void getAllHistoriesByClientStatusChanging() {
//    }
//
//    @Test
//    void hasClientChangedStatusFromThisToAnotherInPeriod() {
//    }
//
//    @Test
//    void getChangedStatusClientsInPeriod() {
//    }
//
//    @Test
//    void getCountClientByHistoryTimeIntervalAndHistoryTypeAndTitle() {
//    }
//
//    @Test
//    void filteringClient() {
//    }
//
//    @Test
//    void filteringClientWithoutPaginator() {
//    }
//
//    @Test
//    void getChangeActiveClients() {
//    }
//
//    @Test
//    void updateBatchClients() {
//    }
//
//    @Test
//    void addBatchClients() {
//    }
//
//    @Test
//    void getClientsEmail() {
//    }
//
//    @Test
//    void getClientsPhoneNumber() {
//    }
//
//    @Test
//    void getFilteredClientsEmail() {
//    }
//
//    @Test
//    void getFilteredClientsPhoneNumber() {
//    }
//
//    @Test
//    void getFilteredClientsSNLinks() {
//    }
//
//    @Test
//    void isTelegramClientPresent() {
//    }
//
//    @Test
//    void getClientBySocialProfile() {
//    }
//
//    @Test
//    void getClientsBySearchPhrase() {
//    }
//
//    @Test
//    void getClientsInStatusOrderedByRegistration() {
//    }
//
//    @Test
//    void getClientsInStatusOrderedByHistory() {
//    }
//
//    @Test
//    void transferClientsBetweenOwners() {
//    }
}
