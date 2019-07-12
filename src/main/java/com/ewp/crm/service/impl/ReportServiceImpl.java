package com.ewp.crm.service.impl;

import com.ewp.crm.models.*;
import com.ewp.crm.models.dto.ClientDto;
import com.ewp.crm.models.dto.ReportDto;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.ewp.crm.service.interfaces.*;
import com.google.api.client.util.Strings;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@PropertySource(value = "file:./report.properties", encoding = "UTF-8")
public class ReportServiceImpl implements ReportService {

    private static Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final ClientRepository clientRepository;
    private final StatusService statusService;
    private final ProjectProperties projectProperties;
    private final ClientService clientService;
    private final String defaultTemplate;
    private final String allNewStudentsByDateTemplate;
    private final String repeatedClientTopic;
    private final ClientStatusChangingHistoryService clientStatusChangingHistoryService;
    private final UserService userService;

    private final Map<String, String> CLIENT_REPORT_FIELDS;

    @Autowired
    public ReportServiceImpl(ClientRepository clientRepository, StatusService statusService,
                             ProjectPropertiesService projectPropertiesService, ClientService clientService,
                             Environment env, UserService userService,
                             ClientStatusChangingHistoryService clientStatusChangingHistoryService) {
        this.clientRepository = clientRepository;
        this.statusService = statusService;
        this.projectProperties = projectPropertiesService.getOrCreate();
        this.clientService = clientService;
        this.clientStatusChangingHistoryService = clientStatusChangingHistoryService;
        this.userService = userService;
        this.defaultTemplate = env.getProperty("report.default.template");
        this.allNewStudentsByDateTemplate = env.getProperty("report.new.clients.by.date.template");
        this.repeatedClientTopic = env.getProperty("messaging.client.service.repeated");

        Map<String, String> clientReportMap = new LinkedHashMap<>();
        clientReportMap.put("name", "First Name");
        clientReportMap.put("lastName", "Last Name");
        clientReportMap.put("email", "E-mail");
        clientReportMap.put("phoneNumber", "Phone Number");
        clientReportMap.put("vk", "VK");
        clientReportMap.put("facebook", "Facebook");
        clientReportMap.put("unknown", "Unknown");
        clientReportMap.put("telegram", "Telegram");
        clientReportMap.put("whatsapp", "Whatsapp");
        clientReportMap.put("slack", "Slack");
        CLIENT_REPORT_FIELDS = Collections.unmodifiableMap(clientReportMap);
    }

    /**
     * Получает список клиентов, которые однажды были в заданном статусе и ни разу не были в статусах на исключение
     *
     * @param status искомый статус
     * @param excludeStatuses список статусов на исключение
     * @return список клиентов, подходящих под условие отбора
     */
    public List<ClientDto> getClientsEverBeenInStatusButExcludeStatuses(Status status, Status... excludeStatuses) {
        return clientStatusChangingHistoryService.getClientsEverBeenInStatusButExcludeStatuses(status, excludeStatuses);
    }

    /**
     *  Перевод всей истории смен статусов клиентов из ClientHistory в ClientStatusChangingHistory
     */
    @Override
    public void fillClientStatusChangingHistoryFromClientHistory() {
        logger.info("Start fillClientStatusChangingHistoryFromClientHistory()");
        List<Client> clients = clientRepository.findAll();
        List<ClientHistory.Type> types = Collections.singletonList(ClientHistory.Type.STATUS);
        Optional<Status> defaultInitStatus = statusService.getFirstStatusForClient();
        if (!defaultInitStatus.isPresent()) {
            logger.warn("Can't get default first status");
            return;
        }
        User defaultUser = userService.get(1L);
        for (Client client :clients) {
            logger.debug("Start filling history for client id = " + client.getId());
            if (client.getDateOfRegistration() == null) {
                clientService.setClientDateOfRegistrationByHistoryDate(client);
                clientService.updateClient(client);
            }
            boolean needToCreateInitHistory = false;
            List<ClientHistory> histories = clientRepository.getAllHistoriesByClientAndHistoryType(client, types);
            // у клиента нет истории перемещения между статусами - значит создадим
            // ему одну запись - присвоение первоначального статуса при создании
            if (histories.isEmpty()) {
                logger.debug("Found 0 histories.");
                ClientStatusChangingHistory history = new ClientStatusChangingHistory(client.getDateOfRegistration(), null, client.getStatus(), client, defaultUser);
                clientStatusChangingHistoryService.add(history);
            } else {
                logger.debug("Found " + histories.size() + " histories.");
                for (ClientHistory history :histories) {
                    Optional<String> destStatusName = parseStatusNameFromHistoryTitle(history.getTitle());
                    if (!destStatusName.isPresent()) {
                        logger.warn("Can't parse status from history " + history.getTitle());
                        continue;
                    }
                    Optional<Status> newStatus = statusService.get(destStatusName.get());
                    if (!newStatus.isPresent()) {
                        // Обработка переименованных статусов
                        if ("Проверено".equalsIgnoreCase(destStatusName.get())) {
                            newStatus = statusService.get(21L);
                        } else {
                            if ("Отказ".equalsIgnoreCase(destStatusName.get())) {
                                newStatus = statusService.get(26L);
                            }
                        }
                        if (!newStatus.isPresent()) {
                            logger.warn("Can't load status by name '" + destStatusName.get() + "'");
                            continue;
                        }
                    }

                    Optional<String> sourceStatusName = parseSourceStatusNameFromHistoryTitle(history.getTitle());
                    Status sourceStatus = null;
                    if (sourceStatusName.isPresent()) {
                        Optional<Status> status = statusService.get(sourceStatusName.get());
                        if (status.isPresent()) {
                            sourceStatus = status.get();
                        }
                    } else {
                        ClientHistory before = clientRepository.getNearestClientHistoryBeforeDate(client, history.getDate(), types);
                        if (before != null) {
                            Optional<String> beforeNewStatus = parseStatusNameFromHistoryTitle(before.getTitle());
                            if (beforeNewStatus.isPresent()) {
                                Optional<Status> status = statusService.get(beforeNewStatus.get());
                                if (status.isPresent()) {
                                    sourceStatus = status.get();
                                }
                            } else {
                                needToCreateInitHistory = true;
                            }
                        } else {
                            needToCreateInitHistory = true;
                        }
                    }

                    ZonedDateTime date = history.getDate();

                    User user = null;
                    String[] userName = parseUserNameFromHistoryTitle(history.getTitle());
                    if (userName != null) {
                        Optional<User> userOptional;
                        if (userName.length == 2) {
                            userOptional = userService.getUserByFirstNameAndLastName(userName[0], userName[1]);
                            if (!userOptional.isPresent()) {
                                userOptional = userService.getUserByFirstNameAndLastName(userName[1], userName[0]);
                            }
                        } else {
                            userOptional = userService.getUserByFirstNameAndLastName(userName[0], StringUtils.EMPTY);
                            if (!userOptional.isPresent()) {
                                userOptional = userService.getUserByFirstNameAndLastName(StringUtils.EMPTY, userName[0]);
                            }
                        }
                        user = userOptional.orElse(defaultUser);
                    }

                    ClientStatusChangingHistory newHistory = new ClientStatusChangingHistory(date, sourceStatus, newStatus.get(), client, user);
                    clientStatusChangingHistoryService.add(newHistory);

                }

                // Создаем пользователю первую историю получения статуса по-умолчанию, если необходимо
                if (needToCreateInitHistory) {
                    ClientStatusChangingHistory initHistory = new ClientStatusChangingHistory(client.getDateOfRegistration(), null, defaultInitStatus.get(), client, defaultUser);
                    clientStatusChangingHistoryService.add(initHistory);
                } else {
                    Optional<ClientStatusChangingHistory> firstHistory = clientStatusChangingHistoryService.getFirstClientStatusChangingHistoryByClient(client);
                    if (firstHistory.isPresent()) {
                        if (firstHistory.get().getSourceStatus() != null) {
                            ClientStatusChangingHistory initHistory = new ClientStatusChangingHistory(client.getDateOfRegistration(), null, firstHistory.get().getSourceStatus(), client, defaultUser);
                            clientStatusChangingHistoryService.add(initHistory);
                        } else {
                            firstHistory.get().setSourceStatus(defaultInitStatus.get());
                            clientStatusChangingHistoryService.update(firstHistory.get());
                            ClientStatusChangingHistory initHistory = new ClientStatusChangingHistory(client.getDateOfRegistration(), null, defaultInitStatus.get(), client, defaultUser);
                            clientStatusChangingHistoryService.add(initHistory);
                        }
                    }
                }
            }
        }
        logger.info("Finished fillClientStatusChangingHistoryFromClientHistory()");
    }

    /**
     *  Восстановление последовательности смены статусов клиентов в таблице ClientStatusChangingHistory
     */
    @Override
    public void processLinksInStatusChangingHistory() {
        logger.info("Start processLinksInStatusChangingHistory()");
        List<Client> clients = clientRepository.findAll();
        for (Client client :clients) {
            List<ClientStatusChangingHistory> clientStatusChangingHistories = clientStatusChangingHistoryService.getAllClientStatusChangingHistoryByClientByDate(client);
            Status previous = null;
            ClientStatusChangingHistory last = null;
            for (ClientStatusChangingHistory history : clientStatusChangingHistories) {
                if (last == null) {
                    last = history;
                }

                if (previous == null) {
                    previous = history.getNewStatus();
                    continue;
                }

                if (history.getSourceStatus() == null) {
                    history.setSourceStatus(previous);
                    clientStatusChangingHistoryService.update(history);
                    previous = history.getNewStatus();
                    continue;
                }

                if (!history.getSourceStatus().getId().equals(previous.getId())) {
                    if (history.getDate().minusMinutes(4L).isAfter(last.getDate().plusMinutes(4L))) {
                        ClientStatusChangingHistory clientStatusChangingHistory = new ClientStatusChangingHistory(
                                history.getDate().minusMinutes(4L),
                                previous,
                                history.getSourceStatus(),
                                history.getClient(),
                                history.getMover());
                        clientStatusChangingHistoryService.add(clientStatusChangingHistory);
                    } else {
                        history.setSourceStatus(last.getNewStatus());
                        clientStatusChangingHistoryService.update(history);
                    }
                }

                previous = history.getNewStatus();
                last = history;
            }
        }
        logger.info("Finished processLinksInStatusChangingHistory()");
    }

    /**
     *  Установить ключ is_creation для смен статусов, которые связаны с созданием клиента
     */
    @Override
    public void setCreationsInStatusChangingHistory() {
        logger.info("Start setCreationsInStatusChangingHistory()");
        List<ClientHistory.Type> historyTypes = Arrays.asList(ClientHistory.Type.ADD, ClientHistory.Type.SOCIAL_REQUEST);
        List<Client> clients = clientRepository.findAll();
        for (Client client :clients) {
            List<ClientHistory> histories = clientRepository.getAllHistoriesByClientAndHistoryType(client, historyTypes);
            for (ClientHistory history :histories) {
                clientStatusChangingHistoryService.setCreationInNearestStatusChangingHistoryForClient(client, history.getDate());
            }
        }
        logger.info("Finished setCreationsInStatusChangingHistory()");
    }

    /**
     * Получает имя (имя/фамилию) пользователя из истории смены статуса клиента
     * @param title заголовок истории клиента
     * @return массив с именем/фамилией клиента (в отдельных строках)
     */
    private String[] parseUserNameFromHistoryTitle(String title) {
        if (title.contains(ClientHistory.Type.STATUS.getInfo())) {
            String[] arr = title.split(ClientHistory.Type.STATUS.getInfo());
            if (arr.length > 1) {
                String[] name = arr[0].split("\\s");
                if (name.length == 2) {
                    return new String[]{name[0], name[1]};
                } else {
                    return new String[]{name[0]};
                }
            }
        }
        return null;
    }

    /**
     * Подсчитывает количество клиентов в статусе "новые" за период
     *
     * @param reportStartDate дата начала отчетного периода
     * @param reportEndDate   дата окончания отчетного периода
     * @param excludeStatusesIds список статусов, которые нужно игнорировать
     * @return отчет с количеством и списком найденных клиентов
     */
    @Override
    public ReportDto getAllNewClientsByDate(ZonedDateTime reportStartDate, ZonedDateTime reportEndDate, List<Long> excludeStatusesIds) {
        List<Status> excludes = getAllStatusesByIds(excludeStatusesIds);
        List<ClientDto> result = clientStatusChangingHistoryService.getNewClientsInPeriodButExcludeStatuses(reportStartDate, reportEndDate, excludes.toArray(new Status[0]));
        String message = MessageFormat.format(defaultTemplate, result.size());
        return new ReportDto(message, sortList(result));
    }

    /**
     *
     * @param reportStartDate дата начала отчетного периода
     * @param reportEndDate дата окончания отчетного периода
     * @param excludeStatusesIds список статусов, которые нужно игнорировать
     * @param firstStatusId статус, в котором искать появление нового клиента
     * @return отчет с количеством и списком найденных клиентов
     */
    @Override
    public ReportDto getAllNewClientsByDateAndFirstStatus(ZonedDateTime reportStartDate, ZonedDateTime reportEndDate, List<Long> excludeStatusesIds, Long firstStatusId) {
        List<ClientDto> result = new ArrayList<>();
        List<Status> excludes = getAllStatusesByIds(excludeStatusesIds);
        Optional<Status> firstStatus = statusService.get(firstStatusId);
        if (firstStatus.isPresent()) {
            result = clientStatusChangingHistoryService.getNewClientsInStatusAndPeriodButExcludeStatuses(firstStatus.get(), reportStartDate, reportEndDate, excludes.toArray(new Status[0]));
        }
        String message = MessageFormat.format(defaultTemplate, result.size());
        return new ReportDto(message, sortList(result));
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
        // После двоеточия и пробела начинается название статуса, в который перемещен клиент
        String[] strings1 = title.split(": ");
        if (strings1.length > 0 && strings1.length == 2) {
            // Бывает 2 варианта - когда просто указан статус, в который переместили клиента,
            // а бывает после указан статус, из которого клиент был перемещен
            String[] strings2 = strings1[1].split(" из ");
            if (strings2.length > 0) {
                if (strings2.length == 2) {
                    return Optional.of(strings2[0].trim());
                } else {
                    return Optional.of(strings1[1].trim());
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Получает название статуса, в котором клиент был ранее, из заголовка истории клиента
     *
     * @param title
     * @return
     */
    private Optional<String> parseSourceStatusNameFromHistoryTitle(String title) {
        // После двоеточия и пробела начинается название статуса, в который перемещен клиент
        String[] strings1 = title.split(": ");
        if (strings1.length > 0 && strings1.length == 2) {
            // Бывает 2 варианта - когда просто указан статус, в который переместили клиента,
            // а бывает после указан статус, из которого клиент был перемещен - этот вариант и проверяем
            String[] strings2 = strings1[1].split(" из ");
            if (strings2.length == 2) {
                return Optional.of(strings2[1].trim());
            }
        }
        return Optional.empty();
    }

    /**
     * Проверяет, есть ли у клиента история о смене статуса, предшествующая данной истории о смене статуса
     *
     * @param clientHistory
     * @return
     */
    private Optional<ClientHistory> historyBeforeThis(ClientHistory clientHistory) {
        // Получаем из истории клиента запись, предшествующую записи clientHistory, чтобы определить
        // исходный статус, из которого клиент перешел в искомый статус
        return Optional.ofNullable(clientRepository.getNearestClientHistoryBeforeDate(clientHistory.getClient(), clientHistory.getDate(), Collections.singletonList(ClientHistory.Type.STATUS)));
    }

    /**
     * Проверяет, если клиент сменил статус в течение 3-х минут после попадания в текущий статус, возвращает false,
     * иначе true
     *
     * @param clientHistory
     * @return
     */
    private boolean isFakeChangingStatusBy3minsRule(ClientHistory clientHistory) {
        // Получаем ближайший следующий переход клиента в другой статус, если такой переход случился
        // в течение 3 минут после предыдущей смены статуса, такой результат исключается из отчета
        ClientHistory afterHistory = clientRepository.getNearestClientHistoryAfterDate(clientHistory.getClient(), clientHistory.getDate(), Collections.singletonList(ClientHistory.Type.STATUS));
        if (afterHistory != null) {
            return clientHistory.getDate().plusMinutes(3L).isAfter(afterHistory.getDate());
        }
        return false;
    }

    /**
     * Проверяет, что клиент не вернулся в исходный статус в течение 24 часов с момента перемещения
     *
     * @param clientHistory
     * @return
     */
    private boolean isFakeChangingStatusBy24hrRule(ClientHistory clientHistory) {
        Optional<ClientHistory> beforeHistory = historyBeforeThis(clientHistory);
        // Получаем имя статуса, из которого клиент перешел в искомый статус
        return beforeHistory.filter(history -> isFakeChangingStatusBy24hrRule(clientHistory, history)).isPresent();
    }

    /**
     * Проверяет, что клиент не вернулся в исходный статус в течение 24 часов с момента перемещения
     *
     * @param clientHistory
     * @param beforeHistory
     * @return
     */
    private boolean isFakeChangingStatusBy24hrRule(ClientHistory clientHistory, ClientHistory beforeHistory) {
        // Получаем имя статуса, из которого клиент перешел в искомый статус
        Optional<String> beforeStatus = parseStatusNameFromHistoryTitle(beforeHistory.getTitle());
        return beforeStatus.map(s -> isFakeChangingStatusBy24hrRule(clientHistory, s)).orElse(true);
    }

    /**
     * Проверяет, что клиент не вернулся в исходный статус в течение 24 часов с момента перемещения
     *
     * @param clientHistory
     * @param beforeStatusName
     * @return
     */
    private boolean isFakeChangingStatusBy24hrRule(ClientHistory clientHistory, String beforeStatusName) {
        Client client = clientHistory.getClient();
        List<ClientHistory.Type> historyTypes = Collections.singletonList(ClientHistory.Type.STATUS);
        boolean isFake = false;
        // Получаем историю клиента, в которой он возвращается в исходный статус и проверяем,
        // если возврат произошел в течение 24 часов, то этот результат исключается из отчета
        ClientHistory returnHistory = clientRepository.getNearestClientHistoryAfterDateByHistoryType(client, clientHistory.getDate(), historyTypes, beforeStatusName);
        if (returnHistory != null) {
            Optional<String> returnStatus = parseStatusNameFromHistoryTitle(returnHistory.getTitle());
            if (returnStatus.isPresent() && beforeStatusName.equals(returnStatus.get()) && clientHistory.getDate().plusDays(1L).isAfter(returnHistory.getDate())) {
                isFake = true;
            }
        }
        return isFake;
    }

    /**
     * Был ли клиент когда-либо в одном из заданных статусов
     * При этом учитываем, являлись переходы в статус ошибочными
     *
     * @param statuses список статусов для проверки
     * @return был ли клиент в одном из статусов из списка
     */
    private boolean hasClientEverBeenInStatus(Client client, List<Status> statuses) {
        List<ClientHistory> allHistories = clientRepository.getAllHistoriesByClientStatusChanging(client, statuses, Collections.singletonList(ClientHistory.Type.STATUS));
        for (ClientHistory history :allHistories) {
            if (!isFakeChangingStatusBy3minsRule(history)) {
                Optional<ClientHistory> beforeHistory = historyBeforeThis(history);
                if (beforeHistory.isPresent()) {
                    if (!isFakeChangingStatusBy24hrRule(history, beforeHistory.get())) {
                        return true;
                    }
                } else {
                    Optional<String> beforeStatusName = parseSourceStatusNameFromHistoryTitle(history.getTitle());
                    if (beforeStatusName.isPresent()) {
                        if (!isFakeChangingStatusBy24hrRule(history, beforeStatusName.get())) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Подсчитывает количество клиентов, которые перешли в статус toStatus в заданный период reportStartDate - reportEndDate
     * из любого статуса и никогда не были в исключенных статусах excludeStatuses
     * Также проверяет, не являлся ли переход в статус ошибочным. В случае, если клиент вернулся в исходных статус в течение
     * 24 часов с момента смены статуса, то считается, что смена статуса была ошибочной
     *
     * @param reportStartDate дата начала отчетного периода
     * @param reportEndDate дата окончания отчетного периода
     * @param toStatusId конечный статус клиента
     * @param excludeStatusesIds список исключенных статусов
     * @return отчет с количеством подходящих под критерии клиентов и списком клиентов
     */
    @Override
    public ReportDto getAllChangedStatusClientsByDate(ZonedDateTime reportStartDate, ZonedDateTime reportEndDate, long toStatusId, List<Long> excludeStatusesIds) {
        List<ClientDto> result = new ArrayList<>();
        Optional<Status> toStatus = statusService.get(toStatusId);
        List<Status> excludeStatuses = getAllStatusesByIds(excludeStatusesIds);
        if (toStatus.isPresent()) {
            result = clientStatusChangingHistoryService.getClientsBeenInStatusAtPeriodButExcludeStatuses(toStatus.get(), reportStartDate, reportEndDate, excludeStatuses.toArray(new Status[0]));
        }
        String message = MessageFormat.format(defaultTemplate, result.size());
        return new ReportDto(message, sortList(result));
    }

    /**
     * Подсчитывает количество клиентов, которые перешли в статус toStatus в заданный период reportStartDate - reportEndDate
     * из статуса fromStatus и никогда не были в исключенных статусах excludeStatuses
     * Также проверяет, не являлся ли переход в статус ошибочным. В случае, если клиент вернулся в исходных статус в течение
     * 24 часов с момента смены статуса, то считается, что смена статуса была ошибочной
     *
     * @param reportStartDate    дата начала отчетного периода
     * @param reportEndDate      дата окончания отчетного периода
     * @param fromStatusId       исходный статус клиента
     * @param toStatusId         конечный статус клиента
     * @param excludeStatusesIds список исключенных статусов
     * @return отчет с количеством подходящих под критерии клиентов и списком клиентов
     */

    @Override
    public ReportDto getAllChangedStatusClientsByDate(ZonedDateTime reportStartDate, ZonedDateTime reportEndDate, long fromStatusId, long toStatusId, List<Long> excludeStatusesIds) {
        List<ClientDto> result = new ArrayList<>();
        Optional<Status> toStatus = statusService.get(toStatusId);
        Optional<Status> fromStatus = statusService.get(fromStatusId);
        List<Status> excludeStatuses = getAllStatusesByIds(excludeStatusesIds);
        if (toStatus.isPresent() && fromStatus.isPresent()) {
            result = clientStatusChangingHistoryService.getClientsWhoChangedStatusInPeriodButExcludeStatuses(fromStatus.get(), toStatus.get(), reportStartDate, reportEndDate, excludeStatuses.toArray(new Status[0]));
        }
        String message = MessageFormat.format(defaultTemplate, result.size());
        return new ReportDto(message, sortList(result));
    }


    /**
     * Подсчитывает количество студентов, которые впервые совершили оплату в заданный период
     *
     * @param reportStartDate дата начала отчетного периода
     * @param reportEndDate   дата окончания отчетного периода
     * @return отчет из количества и списка студентов, впервые совершивших оплату в заданный период
     */
    @Override
    public ReportDto getAllFirstPaymentClientsByDate(ZonedDateTime reportStartDate, ZonedDateTime reportEndDate, List<Long> excludeStatusesIds) {
        List<ClientDto> result = new ArrayList<>();
        // Получаем статус, в который переходит клиент после первой оплаты
        long defaultFirstPayStatusId = projectProperties.getClientFirstPayStatus();
        Optional<Status> inProgressStatus = statusService.get(defaultFirstPayStatusId);
        List<Status> excludeStatuses = getAllStatusesByIds(excludeStatusesIds);
        if (inProgressStatus.isPresent()) {
            result = clientStatusChangingHistoryService.getClientsBeenInStatusFirstTimeAtPeriodButExcludeStatuses(inProgressStatus.get(), reportStartDate, reportEndDate, excludeStatuses.toArray(new Status[0]));
        }
        String message = MessageFormat.format(defaultTemplate, result.size());
        return new ReportDto(message, sortList(result));
    }

    private List<ClientDto> sortList(List<ClientDto> list) {
        return list.stream().sorted(Comparator.comparing(ClientDto::getLastName).thenComparing(ClientDto::getName)).collect(Collectors.toList());
    }

    @Override
    public Optional<String> getFileName(List<String> selectedCheckboxes, String delimeter, String filetype, Status status) {
        StringBuilder fileName = new StringBuilder();
        if (status != null) {
            fileName.append(status.getName()).append("_");
        }
        for (String selectedCheckbox : selectedCheckboxes) {
            fileName.append(selectedCheckbox).append("_");
        }
        if (!Strings.isNullOrEmpty(delimeter) && !delimeter.startsWith("/") && !delimeter.startsWith("\\")) {
            fileName.append(delimeter).append(".").append(filetype);
        } else {
            fileName.append(".").append(filetype);
        }
        return Optional.of(fileName.toString());
    }

    @Override
    public void writeToFileWithFilteringConditions(FilteringCondition filteringCondition, String fileName) {
        fillTxtFile(clientRepository.filteringClientWithoutPaginator(filteringCondition),
                    filteringCondition.getSelectedCheckbox(),
                    filteringCondition.getDelimeter(),
                    createFilePath(fileName));
    }

    @Override
    public void writeToFileWithConditionToDownload(ConditionToDownload conditionToDownload, String fileName) {
        fillTxtFile(clientService.getAllClients(),
                    conditionToDownload.getSelected(),
                    conditionToDownload.getDelimeter(),
                    createFilePath(fileName));
    }

    @Override
    public void writeToExcelFileWithFilteringConditions(FilteringCondition filteringCondition, String fileName) {
        fillExcelFile(clientRepository.filteringClientWithoutPaginator(filteringCondition),
                        filteringCondition.getSelectedCheckbox(),
                        createFilePath(fileName));
    }

    @Override
    public void writeToExcelFileWithConditionToDownload(ConditionToDownload conditionToDownload, String fileName) {
        fillExcelFile(clientService.getAllClients(),
                        conditionToDownload.getSelected(),
                        createFilePath(fileName));
    }

    @Override
    public void writeToCSVFileWithFilteringConditions(FilteringCondition filteringCondition, String fileName) {
        fillCSVFile(clientRepository.filteringClientWithoutPaginator(filteringCondition),
                    filteringCondition.getSelectedCheckbox(),
                    createFilePath(fileName));
    }

    @Override
    public void writeToCSVFileWithConditionToDownload(ConditionToDownload conditionToDownload, String fileName) {
        fillCSVFile(clientService.getAllClients(),
                    conditionToDownload.getSelected(),
                    createFilePath(fileName));
    }

    private void fillTxtFile(List<Client> clients, List<String> checkedData, String delimeter,Path filePath) {
        if (Strings.isNullOrEmpty(delimeter)) {
            delimeter = "  ";
        }
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, Charset.forName("UTF-8"))) {
            for (Client client : clients) {
                if (checkedData.contains("name") && !Strings.isNullOrEmpty(client.getName())) {
                    writer.write(client.getName() + delimeter);
                }
                if (checkedData.contains("lastName") && !Strings.isNullOrEmpty(client.getLastName())) {
                    writer.write(client.getLastName() + delimeter);
                }
                if (checkedData.contains("email") && client.getEmail().isPresent()) {
                    List<String> clientEmails = client.getClientEmails();
                    for (String email : clientEmails) {
                        writer.write(email + delimeter);
                    }
                }
                if (checkedData.contains("phoneNumber") && client.getPhoneNumber().isPresent()) {
                    List<String> clientPhones = client.getClientPhones();
                    for (String phone : clientPhones) {
                        writer.write(phone + delimeter);
                    }
                }
                List<SocialProfile> clientsSocialProfiles = new ArrayList<>(client.getSocialProfiles());
                for (String checkedSocialNetwork : checkedData) {
                    for (SocialProfile clientSocialProfile : clientsSocialProfiles) {
                        if (checkedSocialNetwork.equals(clientSocialProfile.getSocialNetworkType().getName())) {
                            if (clientSocialProfile.getSocialNetworkType().getName().equals("vk")) {
                                writer.write(clientSocialProfile.getSocialNetworkType().getLink() + clientSocialProfile.getSocialId() + delimeter);
                                continue;
                            }
                            writer.write(clientSocialProfile.getSocialId() + delimeter);
                        }
                    }
                }
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            logger.error("Can't fill text file! ", e);
        }
    }

    private void fillExcelFile(List<Client> clients, List<String> checkedData, Path filePath) {
        Map<String,String> requestedFieldsMap = new LinkedHashMap<>(CLIENT_REPORT_FIELDS);
        requestedFieldsMap.entrySet().removeIf(x -> !checkedData.contains(x.getKey()));

        List<String> socialNetworkTypes = Arrays.asList(SocialProfile.SocialNetworkType.values())
                .stream()
                .map(x -> x.getName())
                .collect(Collectors.toList());

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Clients");

        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.BLACK.getIndex());

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        List<String> requestedFieldsHeader = new ArrayList<>(requestedFieldsMap.values());

        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < requestedFieldsHeader.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(requestedFieldsHeader.get(i));
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        int colNum;
        for (Client client : clients) {
            colNum = 0;
            Row row = sheet.createRow(rowNum++);
            for (String field : requestedFieldsMap.keySet()) {
                if (field.equals("name")) {
                        row.createCell(colNum++).setCellValue(client.getName());
                } else if (field.equals("lastName")) {
                        row.createCell(colNum++).setCellValue(client.getLastName());
                } else if (field.equals("email")) {
                        row.createCell(colNum++).setCellValue(client.getClientEmails()
                                .stream()
                                .collect(Collectors.joining(", ")));
                } else if (field.equals("phoneNumber")) {
                        row.createCell(colNum++).setCellValue(client.getClientPhones()
                                .stream()
                                .collect(Collectors.joining(", ")));
                } else if (socialNetworkTypes.contains(field)){
                    if (field.equals("vk")) {
                        row.createCell(colNum++).setCellValue(client.getSocialProfiles().stream()
                                .filter(x -> x.getSocialNetworkType().getName().equals("vk"))
                                .map(x -> x.getSocialNetworkType().getLink() + x.getSocialId())
                                .collect(Collectors.joining(", ")));
                    } else {
                        row.createCell(colNum++).setCellValue(client.getSocialProfiles().stream()
                                .filter(x -> x.getSocialNetworkType().getName().equals(field))
                                .map(x -> x.getSocialId())
                                .collect(Collectors.joining(", ")));
                    }
                }
            }
        }
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(filePath.toFile());
            workbook.write(fileOut);
            fileOut.close();
        } catch (FileNotFoundException e) {
            logger.error("Can't find file! ", e);
        } catch (IOException e) {
            logger.error("Can't fill excel file! ", e);
        }
    }

    private void fillCSVFile(List<Client> clients, List<String> checkedData, Path filePath) {
        Map<String,String> requestedFieldsMap = new LinkedHashMap<>(CLIENT_REPORT_FIELDS);
        requestedFieldsMap.entrySet().removeIf(x -> !checkedData.contains(x.getKey()));

        List<String> socialNetworkTypes = Arrays.asList(SocialProfile.SocialNetworkType.values())
                .stream()
                .map(x -> x.getName())
                .collect(Collectors.toList());

        try(BufferedWriter writer = Files.newBufferedWriter(filePath, Charset.forName("UTF-8"));
                CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            printer.printRecord(requestedFieldsMap.values());
            for (Client client : clients) {
                printer.println();
                for (String field : requestedFieldsMap.keySet()) {
                    try {
                        if (field.equals("name")) {
                            printer.print(client.getName());
                        } else if (field.equals("lastName")) {
                            printer.print(client.getLastName());
                        } else if (field.equals("email")) {
                            printer.print(client.getClientEmails()
                                    .stream()
                                    .collect(Collectors.joining(", ")));
                        } else if (field.equals("phoneNumber")) {
                            printer.print(client.getClientPhones()
                                    .stream()
                                    .collect(Collectors.joining(", ")));
                        } else if (socialNetworkTypes.contains(field)){
                            if (field.equals("vk")) {
                                printer.print(client.getSocialProfiles().stream()
                                        .filter(x -> x.getSocialNetworkType().getName().equals("vk"))
                                        .map(x -> x.getSocialNetworkType().getLink() + x.getSocialId())
                                        .collect(Collectors.joining(", ")));
                            } else {
                                printer.print(client.getSocialProfiles().stream()
                                        .filter(x -> x.getSocialNetworkType().getName().equals(field))
                                        .map(x -> x.getSocialId())
                                        .collect(Collectors.joining(", ")));
                            }
                        }
                    } catch (IOException e) {
                        logger.error("Can't fill field " + field + " for client with id " + client.getId(), e);
                    }
                }
            }
            printer.flush();
        } catch (IOException e) {
            logger.error("Can't fill csv file! ", e);
        }
    }

    private Path createFilePath(String fileName) {
        String path = "DownloadData";

        Path directory = Paths.get(path);
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                logger.error("Could not create folder for files", e);
            }
        }
        Path pathToFile = Paths.get(path, fileName);
        Path file = null;
        try {
            file = Files.createFile(pathToFile);
        } catch (IOException e) {
            try {
                Files.delete(pathToFile);
            } catch (IOException ex) {
                logger.error("Can not delete file", e);
            }
            try {
                file = Files.createFile(pathToFile);
            } catch (IOException ex) {
                logger.error("Can bot create file", e);
            }
        }
        return file;
    }
}