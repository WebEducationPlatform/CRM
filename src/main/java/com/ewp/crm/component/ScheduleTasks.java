package com.ewp.crm.component;

import com.ewp.crm.configs.inteface.EwpConfig;
import com.ewp.crm.exceptions.member.NotFoundMemberList;
import com.ewp.crm.exceptions.parse.ParseClientException;
import com.ewp.crm.exceptions.util.FBAccessTokenException;
import com.ewp.crm.exceptions.util.VKAccessTokenException;
import com.ewp.crm.models.*;
import com.ewp.crm.models.dto.StudentProgressInfo;
import com.ewp.crm.repository.interfaces.MailingMessageRepository;
import com.ewp.crm.service.email.MailingService;
import com.ewp.crm.service.impl.StatusServiceImpl;
import com.ewp.crm.service.interfaces.VKService;
import com.ewp.crm.service.interfaces.*;
import com.ewp.crm.utils.patterns.ValidationPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@EnableScheduling
@PropertySource(value = "file:./skype-message.properties", encoding = "Cp1251")
public class ScheduleTasks {

	private final VKService vkService;

	private final PotentialClientService potentialClientService;

	private final YouTubeTrackingCardService youTubeTrackingCardService;

	private final ClientService clientService;

	private final StudentService studentService;

	private final StatusService statusService;

	private final SocialProfileService socialProfileService;

	private final SocialProfileTypeService socialProfileTypeService;

	private final SMSService smsService;

	private final SMSInfoService smsInfoService;

	private final MailSendService mailSendService;

	private final SendNotificationService sendNotificationService;

	private final ClientHistoryService clientHistoryService;

	private final FacebookService facebookService;

	private final VkTrackedClubService vkTrackedClubService;

	private final VkMemberService vkMemberService;

	private final YoutubeService youtubeService;

	private final YoutubeClientService youtubeClientService;

	private final AssignSkypeCallService assignSkypeCallService;

	private final ReportService reportService;

	private final MessageTemplateService messageTemplateService;

	private final ProjectPropertiesService projectPropertiesService;

	private Environment env;

	private final MailingMessageRepository mailingMessageRepository;

	private final MailingService mailingService;

	private final EwpInfoService ewpInfoService;

	private final StudentStatusService studentStatusService;

	private final EwpConfig ewpConfig;

	private static Logger logger = LoggerFactory.getLogger(ScheduleTasks.class);

	@Autowired
	public ScheduleTasks(VKService vkService, PotentialClientService potentialClientService,
						 YouTubeTrackingCardService youTubeTrackingCardService,
						 ClientService clientService, StudentService studentService,
						 StatusService statusService, MailingMessageRepository mailingMessageRepository,
						 MailingService mailingService, SocialProfileService socialProfileService,
						 SocialProfileTypeService socialProfileTypeService, SMSService smsService,
						 SMSInfoService smsInfoService, SendNotificationService sendNotificationService,
						 ClientHistoryService clientHistoryService, VkTrackedClubService vkTrackedClubService,
						 VkMemberService vkMemberService, FacebookService facebookService, YoutubeService youtubeService,
						 YoutubeClientService youtubeClientService, AssignSkypeCallService assignSkypeCallService,
						 MailSendService mailSendService, Environment env, ReportService reportService,
						 MessageTemplateService messageTemplateService, ProjectPropertiesService projectPropertiesService,
						 EwpInfoService ewpInfoService, StudentStatusService studentStatusService,
						 EwpConfig ewpConfig) {
		this.vkService = vkService;
		this.potentialClientService = potentialClientService;
		this.youTubeTrackingCardService = youTubeTrackingCardService;
		this.clientService = clientService;
		this.studentService = studentService;
		this.statusService = statusService;
		this.socialProfileService = socialProfileService;
		this.socialProfileTypeService = socialProfileTypeService;
		this.smsService = smsService;
		this.smsInfoService = smsInfoService;
		this.mailSendService = mailSendService;
		this.sendNotificationService = sendNotificationService;
		this.clientHistoryService = clientHistoryService;
		this.facebookService = facebookService;
		this.vkTrackedClubService = vkTrackedClubService;
		this.vkMemberService = vkMemberService;
		this.youtubeService = youtubeService;
		this.youtubeClientService = youtubeClientService;
		this.assignSkypeCallService = assignSkypeCallService;
		this.reportService = reportService;
		this.env = env;
		this.mailingMessageRepository = mailingMessageRepository;
		this.mailingService = mailingService;
		this.messageTemplateService = messageTemplateService;
		this.projectPropertiesService = projectPropertiesService;
		this.ewpInfoService = ewpInfoService;
		this.studentStatusService = studentStatusService;
		this.ewpConfig = ewpConfig;
	}

	private void addClient(Client newClient) {
		Status newClientsStatus = statusService.getFirstStatusForClient();
		newClient.setStatus(newClientsStatus);
		newClient.setState(Client.State.NEW);
		newClient.getSocialProfiles().get(0).setSocialProfileType(socialProfileTypeService.getByTypeName("vk"));
		newClient.addHistory(clientHistoryService.createHistory("vk"));
		String email = newClient.getEmail();
		if (email!=null&&!email.matches(ValidationPattern.EMAIL_PATTERN)){
			newClient.setClientDescriptionComment(newClient.getClientDescriptionComment()+System.lineSeparator()+"Возможно клиент допустил ошибку в поле Email: "+email);
			newClient.setEmail(null);
		}
		clientService.addClient(newClient);
		logger.info("New client with id{} has added from VK", newClient.getId());
	}

	@Scheduled(fixedRate = 15_000)
	private void checkCallInSkype() {
		for (AssignSkypeCall assignSkypeCall : assignSkypeCallService.getAssignSkypeCallIfCallDateHasAlreadyPassedButHasNotBeenClearedToTheClient()) {
			Client client = assignSkypeCall.getToAssignSkypeCall();
			client.setLiveSkypeCall(false);
			assignSkypeCall.setSkypeCallDateCompleted(true);
			clientService.updateClient(client);
			assignSkypeCallService.update(assignSkypeCall);
		}
	}

	@Scheduled(fixedRate = 30_000)
	private void checkCallInSkypeToSendTheNotification() {
		for (AssignSkypeCall assignSkypeCall : assignSkypeCallService.getAssignSkypeCallIfNotificationWasNoSent()) {
			Client client = assignSkypeCall.getToAssignSkypeCall();
			String skypeTemplate = env.getRequiredProperty("skype.template");
			User principal = assignSkypeCall.getFromAssignSkypeCall();
			String selectNetworks = assignSkypeCall.getSelectNetworkForNotifications();
			Long clientId = client.getId();
			String dateOfSkypeCall = ZonedDateTime.parse(assignSkypeCall.getNotificationBeforeOfSkypeCall().toString())
					.plusHours(1).format(DateTimeFormatter.ofPattern("dd MMMM в HH:mm по МСК"));
			sendNotificationService.sendNotificationType(dateOfSkypeCall, client, principal, Notification.Type.ASSIGN_SKYPE);
			if (selectNetworks.contains("vk")) {
				try {
					vkService.sendMessageToClient(clientId, skypeTemplate, dateOfSkypeCall, principal);
				} catch (Exception e) {
					logger.warn("VK message not sent", e);
				}
			}
			if (selectNetworks.contains("sms")) {
				try {
					smsService.sendSMS(clientId, skypeTemplate, dateOfSkypeCall, principal);
				} catch (Exception e) {
					logger.warn("SMS message not sent", e);
				}
			}
			if (selectNetworks.contains("email")) {
				try {
					mailSendService.prepareAndSend(clientId, skypeTemplate, dateOfSkypeCall, principal);
				} catch (Exception e) {
					logger.warn("E-mail message not sent");
				}
			}
			assignSkypeCall.setTheNotificationWasIsSent(true);
			assignSkypeCallService.update(assignSkypeCall);
		}
	}

	@Scheduled(fixedRate = 6_000)
	private void handleRequestsFromVk() {
		if (vkService.hasTechnicalAccountToken()) {
			try {
				Optional<List<String>> newMassages = vkService.getNewMassages();
				if (newMassages.isPresent()) {
					for (String message : newMassages.get()) {
						try {
							Client newClient = vkService.parseClientFromMessage(message);
							String s = newMassages.orElse(Collections.emptyList()).toString().replaceAll("<br><br>","<br>");
							ClientHistory clientHistory = new ClientHistory(s,ZonedDateTime.now(ZoneId.systemDefault()),ClientHistory.Type.SOCIAL_REQUEST);
							newClient.addHistory(clientHistory);
							addClient(newClient);
						} catch (ParseClientException e) {
							logger.error(e.getMessage());
						}
					}
				}
			} catch (VKAccessTokenException ex) {
				logger.error(ex.getMessage());
			}
		}
	}

	@Scheduled(fixedRate = 60_000)
	private void findNewMembersAndSendFirstMessage() {
		List<VkTrackedClub> vkTrackedClubList = vkTrackedClubService.getAll();
		List<VkMember> lastMemberList = vkMemberService.getAll();
		for (VkTrackedClub vkTrackedClub : vkTrackedClubList) {
			List<VkMember> freshMemberList = vkService.getAllVKMembers(vkTrackedClub.getGroupId(), 0L)
					.orElseThrow(NotFoundMemberList::new);
			int countNewMembers = 0;
			for (VkMember vkMember : freshMemberList) {
				if (!lastMemberList.contains(vkMember)) {
					vkService.sendMessageById(vkMember.getVkId(), vkService.getFirstContactMessage());
					vkMemberService.add(vkMember);
					countNewMembers++;
				}
			}
			if (countNewMembers > 0) {
				logger.info("{} new VK members has signed in {} club", countNewMembers, vkTrackedClub.getGroupName());
			}
		}
	}

	@Scheduled(fixedRate = 6_000)
	private void handleRequestsFromVkCommunityMessages() {
		Optional<List<Long>> newUsers = vkService.getUsersIdFromCommunityMessages();
		if (newUsers.isPresent()) {
			for (Long id : newUsers.get()) {
				Optional<Client> newClient = vkService.getClientFromVkId(id);
				if (newClient.isPresent()) {
					SocialProfile socialProfile = newClient.get().getSocialProfiles().get(0);
					if (!(Optional.ofNullable(socialProfileService.getSocialProfileByLink(socialProfile.getLink())).isPresent())) {
						addClient(newClient.get());
					}
				}
			}
		}
	}

	@Scheduled(fixedRate = 6_000)
	private void checkClientActivationDate() {
		for (Client client : clientService.getChangeActiveClients()) {
			client.setPostponeDate(null);
			client.setHideCard(false);
			sendNotificationService.sendNotificationType(client.getClientDescriptionComment(), client, client.getOwnerUser(), Notification.Type.POSTPONE);
			clientService.updateClient(client);
		}
	}

	@Scheduled(fixedRate = 6_000)
	private void sendMailing() {
		LocalDateTime currentTime = LocalDateTime.now();
		List<MailingMessage> messages = mailingMessageRepository.getAllByReadedMessageIsFalse();
		messages.forEach(x -> {
			if (x.getDate().compareTo(currentTime) < 0) {
				mailingService.sendMessage(x);
			}
		});
	}


	@Scheduled(fixedRate = 600_000)
	private void addFacebookMessageToDatabase() {
		try {
			facebookService.getFacebookMessages();
		} catch (FBAccessTokenException e) {
			logger.error("Facebook access token has not got", e);
		}
	}


	@Scheduled(fixedRate = 600_000)
	private void checkSMSMessages() {
		logger.info("start checking sms statuses");
		List<SMSInfo> queueSMS = smsInfoService.getSMSByIsChecked(false);
		for (SMSInfo sms : queueSMS) {
			String status = smsService.getStatusMessage(sms.getSmsId());
			if (!status.equals("queued")) {
				if (status.equals("delivered")) {
					sms.setDeliveryStatus("доставлено");
				} else if (sms.getClient() == null) {
					logger.error("Can not create notification with empty SMS client, SMS message: {}", sms);
					sms.setDeliveryStatus("Клиент не найден");
				} else {
					String deliveryStatus = determineStatusOfResponse(status);
					sendNotificationService.sendNotificationType(deliveryStatus, sms.getClient(), sms.getUser(), Notification.Type.SMS);
					sms.setDeliveryStatus(deliveryStatus);
				}
				sms.setChecked(true);
				smsInfoService.update(sms);
			}
		}
	}

	@Scheduled(cron = "0 0 10 01 * ?")
	private void buildAndSendReport() {
		mailSendService.sendNotificationMessageYourself(reportService.buildReportOfLastMonth());
	}

	private String determineStatusOfResponse(String status) {
		String info;
		switch (status) {
			case "delivery error":
				info = "Номер заблокирован или вне зоны";
				break;
			case "invalid mobile phone":
				info = "Неправильный формат номера";
				break;
			case "incorrect id":
				info = "Неверный id сообщения";
				break;
			default:
				info = "Неизвестная ошибка";
		}
		return info;
	}

	@Scheduled(fixedRate = 60_000)
	private void handleYoutubeLiveStreams() {
		for (YouTubeTrackingCard youTubeTrackingCard : youTubeTrackingCardService.getAllByHasLiveStream(false)) {
			youtubeService.handleYoutubeLiveChatMessages(youTubeTrackingCard);
		}
	}

	@Scheduled(fixedRate = 60_000)
	private void getPotentialClientsFromYoutubeClients() {
		for (YoutubeClient youtubeClient : youtubeClientService.getAllByChecked(false)) {
			Optional<PotentialClient> newPotentialClient = vkService.getPotentialClientFromYoutubeLiveStreamByYoutubeClient(youtubeClient);
			if (newPotentialClient.isPresent()) {
				SocialProfile socialProfile = newPotentialClient.get().getSocialProfiles().get(0);
				if (socialProfileService.getSocialProfileByLink(socialProfile.getLink()) == null) {
					potentialClientService.addPotentialClient(newPotentialClient.get());
				}
			}
		}
	}

	/**
	 * Sends payment notification to student's contacts.
	 */
	@Scheduled(fixedRate = 3600000)
	private void sendPaymentNotifications() {
		ProjectProperties properties = projectPropertiesService.getOrCreate();
		if (properties.isPaymentNotificationEnabled() && properties.getPaymentMessageTemplate() != null && properties.getPaymentNotificationTime() != null) {
			LocalTime time = properties.getPaymentNotificationTime().truncatedTo(ChronoUnit.HOURS);
			LocalTime now = LocalTime.now().truncatedTo(ChronoUnit.HOURS);
			if (properties.isPaymentNotificationEnabled() && now.equals(time)) {
				for (Student student : studentService.getStudentsWithTodayNotificationsEnabled()) {
					MessageTemplate template = properties.getPaymentMessageTemplate();
					Long clientId = student.getClient().getId();
					if (student.isNotifyEmail()) {
						mailSendService.sendSimpleNotification(clientId, template.getTemplateText());
					}
					if (student.isNotifySMS()) {
						smsService.sendSimpleSMS(clientId, template.getOtherText());
					}
					if (student.isNotifyVK()) {
						vkService.simpleVKNotification(clientId, template.getOtherText());
					}
				}
			}
		} else {
			logger.info("Payment notification properties not set!");
		}
	}

	@Value("50")
	private int REQUEST_PORTION;

	@Scheduled(cron = "0 1 0 * * *")
	private void updateStudentProgress() {
		if (ewpConfig.isUseEwpApi()) {
			List<Student> listStudent = studentService.getStudentsWithOldStatus();

			for (int i = 0; i <= listStudent.size() % REQUEST_PORTION; i++) {
				List<Student> portionListStudent = listStudent.subList(
						REQUEST_PORTION * i, Math.min(REQUEST_PORTION * (i + 1),listStudent.size()));

				List<StudentProgressInfo> listStudentProgressInfo = ewpInfoService.getStudentProgressInfo(portionListStudent);

				for (StudentProgressInfo info : listStudentProgressInfo) {
					String studentStatusNewName =
							info.getCourse()
									+ " - "
									+ info.getModule()
									+ " - "
									+ info.getChapter();

					StudentStatus studentStatusNew = null;

					Optional<StudentStatus> studentStatusOptional = Optional.ofNullable(studentStatusService.getStudentStatusByName(studentStatusNewName));
					studentStatusNew = studentStatusOptional.orElseGet(() -> studentStatusService.add(new StudentStatus(studentStatusNewName)));

					Client client = clientService.getClientByEmail(info.getEmail());

					Student student = null;
					Optional<Client> optionalClient = Optional.ofNullable(clientService.getClientByEmail(info.getEmail()));
					if (optionalClient.isPresent()) {
						Optional<Student> studentOptional = Optional.ofNullable(studentService.getStudentByClient(optionalClient.get()));
						if (studentOptional.isPresent()) {
							student = studentOptional.get();
						}
					}
					if (Optional.ofNullable(student).isPresent() && student.getStatus() != studentStatusNew) {
						student.setStatus(studentStatusNew);
						student.setStatusDate(LocalDateTime.now());
						studentService.update(student);
					}
				}
			}
		}
	}
}