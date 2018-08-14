package com.ewp.crm.report;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.ReportsStatus;
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
             time += 86400000;
             return new Date[] {format.parse(date1),
                         new Date(time)};
         } catch (ParseException e) {
             logger.error("String \"" + date + "\" hasn't parsed");
         }
         return null;
     }

     private String formationOfReportsText(Date[] dates){
         ReportsStatus reportsStatus = reportsStatusService.getAll().get(0);
         String dropOutStatusName = statusService.get(reportsStatus.getDropOutStatus()).getName();
         String endLearningName = statusService.get(reportsStatus.getEndLearningStatus()).getName();
         String inLearningName = statusService.get(reportsStatus.getInLearningStatus()).getName();
         ClientHistory.Type[] types = {ClientHistory.Type.ADD, ClientHistory.Type.SOCIAL_REQUEST, ClientHistory.Type.STATUS};
         List<Client> students = clientRepository.getClientByHistoryTimeIntervalAndHistoryType(dates[0], dates[1], types);
         long countNewClient = students.stream()
                 .filter(x -> x.getHistory().stream()
                         .anyMatch(y -> y.getType().equals(ClientHistory.Type.ADD) ||
                                 y.getType().equals(ClientHistory.Type.SOCIAL_REQUEST)))
                 .count();
         long countDropOutClients = students.stream().filter(x -> x.getHistory().stream()
                 .anyMatch(y -> y.getType().equals(ClientHistory.Type.STATUS) &&
                         y.getTitle().contains(dropOutStatusName))).count();
         long countEndLearningClients = students.stream().filter(x -> x.getHistory().stream()
                 .anyMatch(y -> y.getType().equals(ClientHistory.Type.STATUS) &&
                         y.getTitle().contains(endLearningName))).count();
         long countInLearningClients = students.stream().filter(x -> x.getStatus().getName().equals(inLearningName)).count();

         long percentage;
         if (countDropOutClients + countEndLearningClients != 0) {
             percentage = (long) (((double) countEndLearningClients / (double) (countDropOutClients + countEndLearningClients)) * 100);
         } else {
             percentage = 0;
         }
         
         return  "Отчет за " + dates[0] + "-" + dates[1] + "\n\r" +
                 "На проекте " + countNewClient + " новых клиентов. \n\r" +
                 "Покинули " + (countDropOutClients + countEndLearningClients) +" человек из которых " +
                 percentage + "% (" +
                 countEndLearningClients +" человек) закончили обучение. \n\r" +
                 "В настоящее время обучается " + countInLearningClients + "человек.";
     }
}
