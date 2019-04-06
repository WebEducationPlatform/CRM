package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.ProjectProperties;
import com.ewp.crm.models.Status;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.ewp.crm.service.interfaces.ProjectPropertiesService;
import com.ewp.crm.service.interfaces.ReportService;
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
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private static Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final ClientRepository clientRepository;
    private final StatusService statusService;
    private final ProjectPropertiesService projectPropertiesService;

    @Autowired
    public ReportServiceImpl(ClientRepository clientRepository, StatusService statusService, ProjectPropertiesService projectPropertiesService) {
        this.clientRepository = clientRepository;
        this.statusService = statusService;
        this.projectPropertiesService = projectPropertiesService;
    }

    /**
     * Подсчитывает количество клиентов в статусе "новые" за период
     *
     * @param firstReportDate дата начала отчетного периода
     * @param lastReportDate  дата окончания отчетного периода
     * @return количество найденных клиентов
     */
    @Override
    public long countNewClients(ZonedDateTime firstReportDate, ZonedDateTime lastReportDate, List<Long> excludeStatusesIds) {
        if (!checkReportPeriodCorrectness(firstReportDate, lastReportDate))
        {
            return -1;
        }
        List<Client> clients = clientRepository.getClientByHistoryTimeIntervalAndHistoryType(firstReportDate, lastReportDate,
                new ClientHistory.Type[]{ClientHistory.Type.ADD, ClientHistory.Type.SOCIAL_REQUEST});
        Set<Status> excludeStatuses = getExcludedStatuses(excludeStatusesIds);
        return clients.stream().filter(x-> !excludeStatuses.contains(x.getStatus())).count();
    }

    /**
     * Подсчитывает количество клиентов, которые перешли в статус to в заданный период firstReportDate - lastReportDate
     * из статуса from и в данный момент не находятся в исключенных статусах exclude
     *
     * @param firstReportDate       дата начала отчетного периода
     * @param lastReportDate        дата окончания отчетного периода
     * @param fromStatusId          исходный статус клиента
     * @param toStatusId            конечный статус клиента
     * @param excludeStatusesIds    список исключенных id статусов
     * @return количество подходящих под критерии клиентов
     */
    @Override
    public int countChangedStatusClients(ZonedDateTime firstReportDate, ZonedDateTime lastReportDate, Long fromStatusId, Long toStatusId, List<Long> excludeStatusesIds) {
        if (fromStatusId.equals(toStatusId))
        {
            return -1;
        }
        Optional<Status> from = statusService.get(fromStatusId);
        Optional<Status> to = statusService.get(toStatusId);
        if (!from.isPresent() || !to.isPresent() || !checkReportPeriodCorrectness(firstReportDate, lastReportDate)) {
            return -1;
        }
        // статус from для новых клиентов?
        boolean isNewClient = false;
        ProjectProperties pp = projectPropertiesService.getOrCreate();
        long newClientStatus = pp.getNewClientStatus();
        if (newClientStatus == from.get().getId()) {
            isNewClient = true;
        }
        int result = 0;
        // выбираем клиентов, которые на изменили стутус на заданный в выбранном периоде
        List<Long> clientIds = clientRepository.getChangedStatusClientIdsInPeriod(firstReportDate, lastReportDate,
                new ClientHistory.Type[]{ClientHistory.Type.STATUS}, to.get().getName());
        List<Client> clients = clientRepository.getAllByIdIn(clientIds);
        Set<Status> exclude = getExcludedStatuses(excludeStatusesIds);
        clients = clients.stream().filter(x -> !exclude.contains(x.getStatus())).collect(Collectors.toList());
        for (Client client : clients) {
            ListIterator li = client.getHistory().listIterator();
            // Показывает, найден ли в истории стутус to
            boolean needFindToStatus = true;
            while (li.hasNext()) {
                ClientHistory history = (ClientHistory) li.next();
                // ищем первое вхождение с конца в истории клиента (to)
                if (needFindToStatus && history.getType().equals(ClientHistory.Type.STATUS) && history.getTitle().contains(to.get().getName())) {
                    needFindToStatus = false;
                    continue;
                }
                // анализируем, является ли предыдущий статус - статусом нового клиента
                if (isNewClient && !needFindToStatus && (history.getType().equals(ClientHistory.Type.ADD) || history.getType().equals(ClientHistory.Type.SOCIAL_REQUEST))) {
                    result++;
                    break;
                }
                // анализируем, является ли предыдущий статус клиента заданным (для старых клиентов)
                if (!isNewClient && !needFindToStatus && history.getType().equals(ClientHistory.Type.STATUS)) {
                    if (history.getTitle().contains(from.get().getName())) {
                        result++;
                    }
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Подсчитывает количество студентов, которые впервые совершили оплату в заданный период
     *
     * @param inProgressStatus статус, который получает клиент при оплате
     * @param firstReportDate  дата начала отчетного периода
     * @param lastReportDate   дата окончания отчетного периода
     * @return количество студентов, впервые совершивших оплату в заданный период
     */
    @Override
    public long countFirstPaymentClients(Status inProgressStatus, ZonedDateTime firstReportDate, ZonedDateTime lastReportDate) {
        if (!checkReportPeriodCorrectness(firstReportDate, lastReportDate))
        {
            return -1;
        }
        long result = 0;
        List<Client> allClients = clientRepository.findAll();
        for (Client client : allClients) {
            // поиск записи о первой оплате клиента
            ClientHistory clientFirstPayment = client.getHistory().stream()
                    .filter(x -> x.getTitle().contains(inProgressStatus.getName()))
                    .findFirst()
                    .orElse(null);
            if (clientFirstPayment == null) {
                continue;
            }
            // проверка попадания оплаты в заданный период
            ZonedDateTime firstPaymentDate = clientFirstPayment.getDate();
            if (firstPaymentDate.isAfter(firstReportDate) && firstPaymentDate.isBefore(lastReportDate)) {
                result++;
            }
        }
        return result;
    }

    private boolean checkReportPeriodCorrectness(ZonedDateTime firstReportDate, ZonedDateTime lastReportDate) {
        if (firstReportDate == null || lastReportDate == null) {
            return false;
        }
        ZonedDateTime startOfDay = lastReportDate.toLocalDate().atStartOfDay(lastReportDate.getZone());
        if (lastReportDate.toInstant().compareTo(startOfDay.toInstant()) == 0) {
            lastReportDate = getEndOfDay(lastReportDate);
        }
        if (lastReportDate.isBefore(firstReportDate) || ChronoUnit.DAYS.between(lastReportDate, lastReportDate) != 0) {
            return false;
        }
        return true;
    }

    private Set<Status> getExcludedStatuses(List<Long> excludeStatusesIds) {
        Set<Status> excludeStatuses = new HashSet<>();
        if (excludeStatusesIds != null) {
            for (Long statusId : excludeStatusesIds) {
                statusService.get(statusId).ifPresent(excludeStatuses::add);
            }
        }
        return excludeStatuses;
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

    /**
     * Устанавливаем время окончаниедня дня в lastReportDate
     * @param reportDate исходная дата (начало дня)
     * @return дата с временем в конце дня
     */
    private ZonedDateTime getEndOfDay(ZonedDateTime reportDate) {
        return reportDate.plusDays(1).minusSeconds(1);
    }

}