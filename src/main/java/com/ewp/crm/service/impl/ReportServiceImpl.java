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

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

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
    public int countNewClients(ZonedDateTime firstReportDate, ZonedDateTime lastReportDate, List<Long> excludeStatusesIds) {
        List<ClientHistory.Type> historyTypes = Arrays.asList(ClientHistory.Type.ADD, ClientHistory.Type.SOCIAL_REQUEST);
        firstReportDate = ZonedDateTime.of(firstReportDate.toLocalDate().atStartOfDay(), ZoneId.systemDefault());
        lastReportDate = ZonedDateTime.of(lastReportDate.toLocalDate().atTime(23, 59, 59), ZoneId.systemDefault());
        List<Status> excludeStatuses = getAllStatusesByIds(excludeStatusesIds);
        List<Client> clients = clientRepository.getClientByHistoryTimeIntervalAndHistoryType(firstReportDate, lastReportDate, historyTypes, excludeStatuses);
        return clients.size();
    }

    private List<Status> getAllStatusesByIds(List<Long> ids) {
        List<Status> result = new ArrayList<>();
        if (ids != null) {
            for (Long id : ids) {
                statusService.get(id).ifPresent(result::add);
            }
        }
        return result;
    }

    /**
     * Получает название статуса, в который перевели клиента, из заголовка истории клиента
     *
     * @param title
     * @return название статуса
     */
    private Optional<String> parseStatusNameFromHistoryTitle(String title) {
        String[] parse1 = title.split(": ");
        if (parse1.length == 2) {
            String[] parse2 = parse1[1].split(" из ");
            if (parse2.length == 2) {
                return Optional.of(parse2[0].trim());
            } else {
                return Optional.of(parse1[1].trim());
            }
        }
        return Optional.empty();
    }

    /**
     * Подсчитывает количество клиентов, которые перешли в статус toStatus в заданный период reportStartDate - reportEndDate
     * из статуса fromStatus и в данный момент не находятся в исключенных статусах excludeStatuses
     * Также проверяет, не являлся ли переход в статус ошибочным. В случае, если клиент вернулся в исходных статус в течение
     * 24 часов с момента смены статуса, то считается, что смена статуса была ошибочной
     *
     * @param reportStartDate     дата начала отчетного периода
     * @param reportEndDate       дата окончания отчетного периода
     * @param fromStatusId        исходный статус клиента
     * @param toStatusId          конечный статус клиента
     * @param excludeStatusesIds  список исключенных статусов
     * @return количество подходящих под критерии клиентов
     */
    @Override
    public int countChangedStatusClients(ZonedDateTime reportStartDate, ZonedDateTime reportEndDate, long fromStatusId, long toStatusId, List<Long> excludeStatusesIds) {
        Optional<Status> fromStatus = statusService.get(fromStatusId);
        Optional<Status> toStatus = statusService.get(toStatusId);
        List<Status> excludeStatuses = getAllStatusesByIds(excludeStatusesIds);
        List<ClientHistory.Type> historyTypes = Collections.singletonList(ClientHistory.Type.STATUS);
        int result = 0;
        if (fromStatus.isPresent() && toStatus.isPresent() && !toStatus.equals(fromStatus) && reportStartDate != null && reportEndDate != null) {
            reportStartDate = ZonedDateTime.of(reportStartDate.toLocalDate().atStartOfDay(), ZoneId.systemDefault());
            reportEndDate = ZonedDateTime.of(reportEndDate.toLocalDate().atTime(23, 59, 59), ZoneId.systemDefault());
            // статус fromStatus для новых клиентов?
            ProjectProperties pp = projectPropertiesService.getOrCreate();
            long newClientStatus = pp.getNewClientStatus();
            boolean isNewClient = newClientStatus == fromStatus.get().getId();
            // Выбираем клиентов, которые изменили статус на заданный в выбранном периоде
            List<Client> clients = clientRepository.getChangedStatusClientsInPeriod(reportStartDate, reportEndDate, historyTypes, excludeStatuses, toStatus.get().getName());
            for (Client client : clients) {
                // Получаем запись истории клиента по тем же параметрам, по которым выше был отобран клиент
                ClientHistory clientHistory = clientRepository.getHistoryByClientAndHistoryTimeIntervalAndHistoryType(client, reportStartDate, reportEndDate, historyTypes, toStatus.get().getName());
                if (clientHistory != null) {
                    // Получаем записи из истории клиента, предшествующую и последующую записи выше
                    ClientHistory beforeHistory = clientRepository.getNearestClientHistoryBeforeDate(client, clientHistory.getDate(), historyTypes);
                    ClientHistory afterHistory = clientRepository.getNearestClientHistoryAfterDate(client, clientHistory.getDate(), historyTypes);
                    // Если записи с переходами в истории есть, получаем из них статусы -
                    // в каком статусе был клиент ранее и в какой статус перешел после.
                    // Если статусы совпадают, проверяем дату возврата в исходный статус.
                    // Если возврат в исходный статус произошел в течение 24 часов, то это считается ошибочным переносом
                    // и исключается из результатов отчета.
                    if (beforeHistory != null) {
                        Optional<String> beforeStatus = parseStatusNameFromHistoryTitle(beforeHistory.getTitle());
                        if (beforeStatus.isPresent() && beforeStatus.get().equals(fromStatus.get().getName())) {
                            result++;
                            if (afterHistory != null) {
                                Optional<String> afterStatus = parseStatusNameFromHistoryTitle(afterHistory.getTitle());
                                if (afterStatus.isPresent() && beforeStatus.get().equals(afterStatus.get()) && clientHistory.getDate().plusDays(1L).isAfter(afterHistory.getDate())) {
                                    result--;
                                }
                            }
                        }
                    } else {
                        // Если ищем переходы из статуса для Новых клиентов, то удостоверяемся, что ранее переходов в другие статусы не было
                        if (isNewClient) {
                            result++;
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Подсчитывает количество студентов, которые впервые совершили оплату в заданный период
     *
     * @param reportStartDate  дата начала отчетного периода
     * @param reportEndDate   дата окончания отчетного периода
     * @return количество студентов, впервые совершивших оплату в заданный период
     */
    @Override
    public long countFirstPaymentClients(ZonedDateTime reportStartDate, ZonedDateTime reportEndDate, List<Long> excludeStatusesIds) {
        long result = 0;
        // Получаем статус, в который переходит клиент после первой оплаты
        Optional<Status> inProgressStatus = statusService.get(4L);
        if (inProgressStatus.isPresent()) {
            List<ClientHistory.Type> historyTypes = Collections.singletonList(ClientHistory.Type.STATUS);
            List<Status> excludeStatuses = getAllStatusesByIds(excludeStatusesIds);
            reportStartDate = ZonedDateTime.of(reportStartDate.toLocalDate().atStartOfDay(), ZoneId.systemDefault());
            reportEndDate = ZonedDateTime.of(reportEndDate.toLocalDate().atTime(23, 59, 59), ZoneId.systemDefault());
            // Получение всех клиентов, которые перешли в статус в заданный период
            List<Client> clients = clientRepository.getChangedStatusClientsInPeriod(reportStartDate, reportEndDate, historyTypes, excludeStatuses, inProgressStatus.get().getName());
            // Для каждого клиента проверяем, впервые ли ему присвоен данный статус
            for (Client client :clients) {
                if (!clientRepository.hasClientBeenInStatusBefore(client.getId(), reportStartDate, inProgressStatus.get().getName())) {
                    result++;
                }
            }
        }
        return result;
    }

}