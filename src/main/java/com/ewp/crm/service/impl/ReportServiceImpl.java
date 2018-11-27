package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.ReportsStatus;
import com.ewp.crm.models.Status;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.ewp.crm.service.interfaces.ReportService;
import com.ewp.crm.service.interfaces.ReportsStatusService;
import com.ewp.crm.service.interfaces.StatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Locale;

@Service
public class ReportServiceImpl implements ReportService {

	private static Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

	@Autowired
	private ClientRepository clientRepository;

	@Autowired
	private ReportsStatusService reportsStatusService;

	@Autowired
	private StatusService statusService;

	public String buildReport(String date) {
		ZonedDateTime[] dates = parseTwoDate(date);
		return formationOfReportsText(dates);
	}

	public String buildReportOfLastMonth() {
		LocalDate today = LocalDate.now();
		LocalDate firstDayMonth = today.minusDays(1).withDayOfMonth(1);
		return formationOfReportsText(new ZonedDateTime[]{ZonedDateTime.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()),
				ZonedDateTime.from(firstDayMonth.atStartOfDay(ZoneId.systemDefault()).toInstant())});
	}

	private ZonedDateTime[] parseTwoDate(String date) {
		String date1 = date.substring(0, 10);
		String date2 = date.substring(13, 23);
		DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		TemporalAccessor parse1 = format.parse(date1);
		TemporalAccessor parse2 = format.parse(date2);
		try {
			ZonedDateTime localDate1 = LocalDate.from(parse1).atStartOfDay(ZoneId.systemDefault());
			ZonedDateTime localDate2 = LocalDate.from(parse2).atStartOfDay(ZoneId.systemDefault()).plusHours(23).plusMinutes(59).plusSeconds(59);
			return new ZonedDateTime[]{localDate1, localDate2};
		} catch (DateTimeParseException e) {
			logger.error("String \"" + date + "\" hasn't parsed");
			return null;
		}
	}

	private String formationOfReportsText(ZonedDateTime[] dates) {
		ReportsStatus reportsStatus = reportsStatusService.getAll().get(0);
		String dropOutStatusName = statusService.get(reportsStatus.getDropOutStatus()).getName();
		String endLearningName = statusService.get(reportsStatus.getEndLearningStatus()).getName();
		Status inLearningStatus = statusService.get(reportsStatus.getInLearningStatus());
		Status pauseLearnStatus = statusService.get(reportsStatus.getPauseLearnStatus());
		Status trialLearnStatus = statusService.get(reportsStatus.getTrialLearnStatus());

//		ZonedDateTime datePlusOneWeek = ZonedDateTime.of(dates[0].toLocalDate(), LocalTime.MIN, ZoneId.systemDefault()).minusWeeks(1); todo зачем нужна была?
		List<Client> newClientPlusOneWeek = clientRepository.getClientByHistoryTimeIntervalAndHistoryType(dates[0], dates[1],
				new ClientHistory.Type[]{ClientHistory.Type.ADD, ClientHistory.Type.SOCIAL_REQUEST});

		long countFirstPaymentClients = newClientPlusOneWeek.stream()
				.filter(x -> x.getHistory().stream().anyMatch(y -> y.getTitle().contains(trialLearnStatus.getName())))
				.filter(x -> x.getHistory().stream().anyMatch(y -> y.getTitle().contains(inLearningStatus.getName()))).count();
		int countNewClient = clientRepository.getClientByHistoryTimeIntervalAndHistoryType(dates[0], dates[1],
				new ClientHistory.Type[]{ClientHistory.Type.ADD, ClientHistory.Type.SOCIAL_REQUEST}).size();
		long countDropOutClients = clientRepository.getCountClientByHistoryTimeIntervalAndHistoryTypeAndTitle(dates[0], dates[1],
				new ClientHistory.Type[]{ClientHistory.Type.STATUS}, dropOutStatusName);
		long countEndLearningClients = clientRepository.getCountClientByHistoryTimeIntervalAndHistoryTypeAndTitle(dates[0], dates[1],
				new ClientHistory.Type[]{ClientHistory.Type.STATUS}, endLearningName);
		long countTakePauseClients = clientRepository.getCountClientByHistoryTimeIntervalAndHistoryTypeAndTitle(dates[0], dates[1],
				new ClientHistory.Type[]{ClientHistory.Type.STATUS}, pauseLearnStatus.getName());
		int countInLearningClients = clientRepository.getAllByStatus(inLearningStatus).size();
		int countPauseLearnClients = clientRepository.getAllByStatus(pauseLearnStatus).size();
		int countTrialLearnClients = clientRepository.getAllByStatus(trialLearnStatus).size();

		String dateFrom = dates[0].format(DateTimeFormatter.ofPattern("d MMMM yyyy").withLocale(new Locale("ru")));
		String dateTo = dates[1].format(DateTimeFormatter.ofPattern("d MMMM yyyy").withLocale(new Locale("ru")));
		return "Отчет с " + dateFrom + " года \n " +
				"        по " + dateTo + " года. \n" +
				"Начали учёбу " + countNewClient + " новых человек \n" +
				"Произвели оплату " + countFirstPaymentClients + " новых человек\n" +
				"Бросили учёбу " + countDropOutClients + " человек\n" +
				"Окончили учёбу " + countEndLearningClients + " человек\n" +
				"Взяли паузу " + countTakePauseClients + " человек\n" +
				"В настоящее время на пробных " + countTrialLearnClients + " новых человек\n" +
				"В настоящее время на паузе " + countPauseLearnClients + " человек\n" +
				"В настоящее время обучается " + countInLearningClients + " человек.\n";
	}
}
