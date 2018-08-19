package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.ReportsStatus;
import com.ewp.crm.models.Status;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.ewp.crm.service.interfaces.ReportsStatusService;
import com.ewp.crm.service.interfaces.StatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class ReportService {

    private static Logger logger = LoggerFactory.getLogger(ReportService.class);
    
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ReportsStatusService reportsStatusService;

    @Autowired
    private StatusService statusService;

    public String buildReport(String date) {
        Date[] dates = parseTwoDate(date);
        return formationOfReportsText(dates);
    }

    public String buildReportOfLastMonth(){
        LocalDate today = LocalDate.now();
        LocalDate firstDayMonth = today.minusDays(1).withDayOfMonth(1);
        return formationOfReportsText(new Date[] {Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                                                  Date.from(firstDayMonth.atStartOfDay(ZoneId.systemDefault()).toInstant())});

    }

     private Date[] parseTwoDate(String date){
        String date1 = date.substring(0,10);
        String date2 = date.substring(13,23);
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
         try {
             long time = format.parse(date2).getTime();
             time += 86300000;
             return new Date[] {format.parse(date1), new Date(time)};
         } catch (ParseException e) {
             logger.error("String \"" + date + "\" hasn't parsed");
             return null;
         }
     }

     private String formationOfReportsText(Date[] dates) {
         ReportsStatus reportsStatus = reportsStatusService.getAll().get(0);
         String dropOutStatusName = statusService.get(reportsStatus.getDropOutStatus()).getName();
         String endLearningName = statusService.get(reportsStatus.getEndLearningStatus()).getName();
         Status inLearningStatus = statusService.get(reportsStatus.getInLearningStatus());
         Status pauseLearnStatus = statusService.get(reportsStatus.getPauseLearnStatus());
         Status trialLearnStatus = statusService.get(reportsStatus.getTrialLearnStatus());

         Date datePlusOneWeek = new Date(dates[0].getTime());
         datePlusOneWeek.setTime(datePlusOneWeek.getTime() - (86400000 * 7));
         List<Client> newClientPlusOneWeek = clientRepository.getClientByHistoryTimeIntervalAndHistoryType(datePlusOneWeek, dates[1],
                 new ClientHistory.Type[] {ClientHistory.Type.ADD,
                         ClientHistory.Type.SOCIAL_REQUEST});

         long countFirstPaymentClients = newClientPlusOneWeek.stream().filter(x -> x.getHistory().stream().anyMatch(y -> y.getTitle().contains(trialLearnStatus.getName())))
                                                                      .filter(x -> x.getHistory().stream().anyMatch(y -> y.getTitle().contains(inLearningStatus.getName()))).count();
         int countNewClient = clientRepository.getClientByHistoryTimeIntervalAndHistoryType(dates[0], dates[1],
                 new ClientHistory.Type[] {ClientHistory.Type.ADD, ClientHistory.Type.SOCIAL_REQUEST}).size();
         long countDropOutClients = clientRepository.getCountClientByHistoryTimeIntervalAndHistoryTypeAndTitle(dates[0], dates[1],
                 new ClientHistory.Type[] {ClientHistory.Type.STATUS}, dropOutStatusName);
         long countEndLearningClients = clientRepository.getCountClientByHistoryTimeIntervalAndHistoryTypeAndTitle(dates[0], dates[1],
                 new ClientHistory.Type[] {ClientHistory.Type.STATUS}, endLearningName);
         long countTakePauseClients = clientRepository.getCountClientByHistoryTimeIntervalAndHistoryTypeAndTitle(dates[0], dates[1],
                 new ClientHistory.Type[] {ClientHistory.Type.STATUS}, pauseLearnStatus.getName());
         int countInLearningClients = clientRepository.findAllByStatus(inLearningStatus).size();
         int countPauseLearnClients = clientRepository.findAllByStatus(pauseLearnStatus).size();
         int countTrialLearnClients = clientRepository.findAllByStatus(trialLearnStatus).size();

         String dateFrom = new SimpleDateFormat("d MMMM", new Locale("ru")).format(dates[0]);
         String dateTo = new SimpleDateFormat("d MMMM yyyy", new Locale("ru")).format(dates[1]);
         return  "Отчет за " + dateFrom + " - " + dateTo + " года. \n" +
                 "Начали учёбу " + countNewClient + " новых человек \n" +
                 "Произвели оплату " + countFirstPaymentClients + " новых человек\n" +
                 "Бросили учёбу " + countDropOutClients + " человек\n"+
                 "Окончили учёбу " + countEndLearningClients + " человек\n" +
                 "Взяли паузу " + countTakePauseClients + " человек\n" +
                 "В настоящее время на пробных " + countTrialLearnClients + " новых человек\n" +
                 "В настоящее время на паузе " + countPauseLearnClients + " человек\n" +
                 "В настоящее время обучается " + countInLearningClients + " человек.\n";
     }
}
