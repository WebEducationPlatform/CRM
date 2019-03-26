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

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

    public String buildReport(String period) {
        ZonedDateTime[] dates = parsePeriod(period);
        if (dates == null) return null;
        return generateReportText(dates[0], dates[1]);
    }

    public String buildReportOfLastMonth() {
        LocalDate today = LocalDate.now();
        LocalDate firstDayMonth = today.minusDays(1).withDayOfMonth(1);
        return generateReportText(
                ZonedDateTime.from(firstDayMonth.atStartOfDay(ZoneId.systemDefault())),
                ZonedDateTime.from(today.atStartOfDay(ZoneId.systemDefault()))
        );
    }

    private ZonedDateTime[] parsePeriod(String period) {
        String[] dates = period.split(" - ");
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        TemporalAccessor parse1 = format.parse(dates[0]);
        TemporalAccessor parse2 = format.parse(dates[1]);
        try {
            ZonedDateTime localDate1 = LocalDate.from(parse1).atStartOfDay(ZoneId.systemDefault());
            ZonedDateTime localDate2 = LocalDate.from(parse2).atStartOfDay(ZoneId.systemDefault()).plusHours(23).plusMinutes(59).plusSeconds(59);
            return new ZonedDateTime[]{localDate1, localDate2};
        } catch (DateTimeParseException e) {
            logger.error("String \"" + period + "\" hadn't parsed");
            return null;
        }
    }

    private String generateReportText(ZonedDateTime firstReportDate, ZonedDateTime lastReportDate) {

        ReportsStatus reportsStatus = reportsStatusService.getAll().get(0);

        Status dropOutStatus = statusService.get(reportsStatus.getDropOutStatus());
        Status endLearningStatus = statusService.get(reportsStatus.getEndLearningStatus());
        Status inLearningStatus = statusService.get(reportsStatus.getInLearningStatus());
        Status pauseLearnStatus = statusService.get(reportsStatus.getPauseLearnStatus());
        Status trialLearnStatus = statusService.get(reportsStatus.getTrialLearnStatus());

        int countNewClients = countNewClients(firstReportDate, lastReportDate);

        long countDropOutClients = countClientsByHistoryStatusAndInterval(firstReportDate, lastReportDate, dropOutStatus);
        long countEndLearningClients = countClientsByHistoryStatusAndInterval(firstReportDate, lastReportDate, endLearningStatus);
        long countTakePauseClients = countClientsByHistoryStatusAndInterval(firstReportDate, lastReportDate, pauseLearnStatus);

        int countInLearningClients = clientRepository.getAllByStatus(inLearningStatus).size();
        int countPauseLearnClients = clientRepository.getAllByStatus(pauseLearnStatus).size();
        int countTrialLearnClients = clientRepository.getAllByStatus(trialLearnStatus).size();

        String dateFrom = firstReportDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy").withLocale(new Locale("ru")));
        String dateTo = lastReportDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy").withLocale(new Locale("ru")));

        // ToDo: make mvc great again
        return "Отчет с " + dateFrom + " года \n " +
                "        по " + dateTo + " года. \n" +
                "Начали учёбу " + countNewClients + " новых человек \n" +
                "Произвели оплату " + countFirstPaymentClients(inLearningStatus.getName(), firstReportDate, lastReportDate) + " новых человек\n" +
                "Бросили учёбу " + countDropOutClients + " человек\n" +
                "Окончили учёбу " + countEndLearningClients + " человек\n" +
                "Взяли паузу " + countTakePauseClients + " человек\n" +
                "В настоящее время на пробных " + countTrialLearnClients + " новых человек\n" +
                "В настоящее время на паузе " + countPauseLearnClients + " человек\n" +
                "В настоящее время обучается " + countInLearningClients + " человек.\n";
    }

    private long countClientsByHistoryStatusAndInterval(ZonedDateTime firstReportDate, ZonedDateTime lastReportDate, Status status) {
        return clientRepository.getCountClientByHistoryTimeIntervalAndHistoryTypeAndTitle(firstReportDate, lastReportDate,
                    new ClientHistory.Type[]{ClientHistory.Type.STATUS}, status.getName());
    }

    private int countNewClients(ZonedDateTime firstReportDate, ZonedDateTime lastReportDate) {
        return clientRepository.getClientByHistoryTimeIntervalAndHistoryType(firstReportDate, lastReportDate,
                    new ClientHistory.Type[]{ClientHistory.Type.ADD, ClientHistory.Type.SOCIAL_REQUEST}).size();
    }

    private long countFirstPaymentClients(String inProgressStatus, ZonedDateTime firstReportDate, ZonedDateTime lastReportDate) {
        long result = 0;

        List<Client> allClients = clientRepository.findAll();

        for (Client client : allClients) {

            ClientHistory clientFirstPayment = client.getHistory().stream()
                    .filter(x -> x.getTitle().contains(inProgressStatus))
                    .findFirst()
                    .orElse(null);

            if (clientFirstPayment == null) break;

            ZonedDateTime firstPaymentDate = clientFirstPayment.getDate();
            if (firstPaymentDate.isAfter(firstReportDate) && firstPaymentDate.isBefore(lastReportDate)) {
                result++;
            }

        }

        return result;
    }

}
