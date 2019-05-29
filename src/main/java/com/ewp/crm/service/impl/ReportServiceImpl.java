package com.ewp.crm.service.impl;

import com.ewp.crm.models.*;
import com.ewp.crm.repository.interfaces.ClientRepository;
import com.ewp.crm.service.interfaces.*;
import com.google.api.client.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {

    private static Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

    private final ClientRepository clientRepository;
    private final StatusService statusService;
    private final ProjectProperties projectProperties;
    private final ClientService clientService;

    @Autowired
    public ReportServiceImpl(ClientRepository clientRepository,
                             StatusService statusService,
                             ProjectPropertiesService projectPropertiesService,
                             ClientService clientService) {
        this.clientRepository = clientRepository;
        this.statusService = statusService;
        this.projectProperties = projectPropertiesService.getOrCreate();
        this.clientService = clientService;
    }

    /**
     * Подсчитывает количество клиентов в статусе "новые" за период
     *
     * @param reportStartDate дата начала отчетного периода
     * @param reportEndDate   дата окончания отчетного периода
     * @return количество найденных клиентов
     */
    @Override
    public String countNewClients(ZonedDateTime reportStartDate, ZonedDateTime reportEndDate, List<Long> excludeStatusesIds) {
        List<ClientHistory.Type> historyTypes = Arrays.asList(ClientHistory.Type.ADD, ClientHistory.Type.SOCIAL_REQUEST);
        reportStartDate = ZonedDateTime.of(reportStartDate.toLocalDate().atStartOfDay(), ZoneId.systemDefault());
        reportEndDate = ZonedDateTime.of(reportEndDate.toLocalDate().atTime(23, 59, 59), ZoneId.systemDefault());
        List<Status> excludeStatuses = getAllStatusesByIds(excludeStatusesIds);
        List<Client> clients = clientRepository.getClientByHistoryTimeIntervalAndHistoryType(reportStartDate, reportEndDate, historyTypes, excludeStatuses);
        int quantityAllNewClients = clients.size();
        clients.removeIf(client -> (
                client.getClientDescriptionComment() != null
                        && client.getClientDescriptionComment().equals("Клиент оставлил повторную заявку")));
        return "Новых клиентов " + quantityAllNewClients + ", из них " + (quantityAllNewClients - clients.size()) + " оставили посторную заявку.";
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
        if (strings1.length == 2) {
            // Бывает 2 варианта - когда просто указан статус, в который переместили клиента,
            // а бывает после указан статус, из которого клиент был перемещен
            String[] strings2 = strings1[1].split(" из ");
            if (strings2.length == 2) {
                return Optional.of(strings2[0].trim());
            } else {
                return Optional.of(strings1[1].trim());
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
     * @param reportStartDate    дата начала отчетного периода
     * @param reportEndDate      дата окончания отчетного периода
     * @param fromStatusId       исходный статус клиента
     * @param toStatusId         конечный статус клиента
     * @param excludeStatusesIds список исключенных статусов
     * @return количество подходящих под критерии клиентов
     */

    @Override
    public int countChangedStatusClients(ZonedDateTime reportStartDate, ZonedDateTime reportEndDate, long fromStatusId, long toStatusId, List<Long> excludeStatusesIds) {
        int result = 0;
        Optional<Status> fromStatus = statusService.get(fromStatusId);
        Optional<Status> toStatus = statusService.get(toStatusId);
        List<Status> excludeStatuses = getAllStatusesByIds(excludeStatusesIds);
        List<ClientHistory.Type> historyTypes = Collections.singletonList(ClientHistory.Type.STATUS);
        if (fromStatus.isPresent() && toStatus.isPresent() && !toStatus.equals(fromStatus) && reportStartDate != null && reportEndDate != null) {
            reportStartDate = ZonedDateTime.of(reportStartDate.toLocalDate().atStartOfDay(), ZoneId.systemDefault());
            reportEndDate = ZonedDateTime.of(reportEndDate.toLocalDate().atTime(23, 59, 59), ZoneId.systemDefault());
            // статус fromStatus для новых клиентов?
            long newClientStatus = projectProperties.getNewClientStatus();
            boolean isNewClient = newClientStatus == fromStatus.get().getId();
            List<Client> clients = clientRepository.getChangedStatusClientsInPeriod(reportStartDate, reportEndDate, historyTypes, excludeStatuses, toStatus.get().getName());
            for (Client client : clients) {
                // Получаем запись истории клиента по тем же параметрам, по которым выше был отобран клиент
                ClientHistory clientHistory = clientRepository.getHistoryByClientAndHistoryTimeIntervalAndHistoryType(client, reportStartDate, reportEndDate, historyTypes, toStatus.get().getName());
                if (clientHistory != null) {
                    boolean goodResult = true;
                    // Получаем из истории клиента запись, предшествующую записи выше, чтобы определить
                    // исходный статус, из которого клиент перешел в искомый статус
                    ClientHistory beforeHistory = clientRepository.getNearestClientHistoryBeforeDate(client, clientHistory.getDate(), historyTypes);
                    if (beforeHistory != null) {
                        Optional<String> beforeStatus = parseStatusNameFromHistoryTitle(beforeHistory.getTitle());
                        if (beforeStatus.isPresent() && beforeStatus.get().equals(fromStatus.get().getName())) {
                            // Получаем историю клиента, в которой он возвращается в исходный статус и проверяем,
                            // если возврат произошел в течение 24 часов, то этот результат исключается из отчета
                            ClientHistory returnHistory = clientRepository.getNearestClientHistoryAfterDateByHistoryType(client, clientHistory.getDate(), historyTypes, beforeStatus.get());
                            if (returnHistory != null) {
                                Optional<String> returnStatus = parseStatusNameFromHistoryTitle(returnHistory.getTitle());
                                if (returnStatus.isPresent() && beforeStatus.get().equals(returnStatus.get()) && clientHistory.getDate().plusDays(1L).isAfter(returnHistory.getDate())) {
                                    goodResult = false;
                                }
                            }
                        } else {
                            continue;
                        }
                    } else {
                        if (isNewClient) {
                            result++;
                            continue;
                        }
                    }
                    // Получаем ближайший следующий переход клиента в другой статус, если такой переход случился
                    // в течение 3 минут после предыдущей смены статуса, такой результат исключается из отчета
                    ClientHistory afterHistory = clientRepository.getNearestClientHistoryAfterDate(client, clientHistory.getDate(), historyTypes);
                    if (afterHistory != null) {
                        if (clientHistory.getDate().plusMinutes(3L).isAfter(afterHistory.getDate())) {
                            goodResult = false;
                        }
                    }
                    if (goodResult) {
                        result++;
                    }
                }
            }
        }
        return result;
    }


    /**
     * Подсчитывает количество студентов, которые впервые совершили оплату в заданный период
     *
     * @param reportStartDate дата начала отчетного периода
     * @param reportEndDate   дата окончания отчетного периода
     * @return количество студентов, впервые совершивших оплату в заданный период
     */
    @Override
    public long countFirstPaymentClients(ZonedDateTime reportStartDate, ZonedDateTime reportEndDate, List<Long> excludeStatusesIds) {
        long result = 0;
        // Получаем статус, в который переходит клиент после первой оплаты
        long defaultFirstPayStatusId = projectProperties.getClientFirstPayStatus();
        Optional<Status> inProgressStatus = statusService.get(defaultFirstPayStatusId);
        if (inProgressStatus.isPresent()) {
            List<ClientHistory.Type> historyTypes = Collections.singletonList(ClientHistory.Type.STATUS);
            List<Status> excludeStatuses = getAllStatusesByIds(excludeStatusesIds);
            reportStartDate = ZonedDateTime.of(reportStartDate.toLocalDate().atStartOfDay(), ZoneId.systemDefault());
            reportEndDate = ZonedDateTime.of(reportEndDate.toLocalDate().atTime(23, 59, 59), ZoneId.systemDefault());
            // Получение всех клиентов, которые перешли в статус в заданный период
            List<Client> clients = clientRepository.getChangedStatusClientsInPeriod(reportStartDate, reportEndDate, historyTypes, excludeStatuses, inProgressStatus.get().getName());
            // Для каждого клиента проверяем, впервые ли ему присвоен данный статус
            for (Client client : clients) {
                if (!clientRepository.hasClientBeenInStatusBefore(client.getId(), reportStartDate, inProgressStatus.get().getName())) {
                    result++;
                }
            }
        }
        return result;
    }

    @Override
    public Optional<String> getFileName(List<String> selectedCheckboxes, String delimeter, Status status) {
        StringBuilder fileName = new StringBuilder();
        if (status != null) {
            fileName.append(status.getName()).append("_");
        }
        String format = null;
        for (String selectedCheckbox : selectedCheckboxes) {
            if(selectedCheckbox.equals(".txt") || selectedCheckbox.equals(".csv") || selectedCheckbox.equals(".xlsx")){
                format = selectedCheckbox;
                break;
            }
            fileName.append(selectedCheckbox).append("_");
        }
        if (!Strings.isNullOrEmpty(delimeter) && !delimeter.startsWith("/") && !delimeter.startsWith("\\")) {
            fileName.append(delimeter).append(format);
        } else {
            fileName.append(format);
        }
        return Optional.of(fileName.toString());
    }

    @Override
    public void writeToFileWithFilteringConditions(FilteringCondition filteringCondition, String fileName) {
        Set<String> checkedData = new HashSet<>(filteringCondition.getSelectedCheckbox());
        String path = "DownloadData";
        String delimeter = filteringCondition.getDelimeter();

        if (Strings.isNullOrEmpty(filteringCondition.getDelimeter())) {
            delimeter = "  ";
        }

        Path directory = Paths.get(path);
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                logger.error("Could not create folder for text files", e);
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

        try (BufferedWriter writer = Files.newBufferedWriter(file, Charset.forName("UTF-8"))) {
            List<Client> filteredClients = clientRepository.filteringClientWithoutPaginator(filteringCondition);
            for (Client filteredClient : filteredClients) {
                if (checkedData.contains("name") && !Strings.isNullOrEmpty(filteredClient.getName())) {
                    writer.write(filteredClient.getName() + delimeter);
                }

                if (checkedData.contains("lastName") && !Strings.isNullOrEmpty(filteredClient.getLastName())) {
                    writer.write(filteredClient.getLastName() + delimeter);
                }

                if (checkedData.contains("email") && filteredClient.getEmail().isPresent()) {
                    List<String> clientEmails = filteredClient.getClientEmails();
                    for (String email : clientEmails) {
                        writer.write(email + delimeter);
                    }
                }

                if (checkedData.contains("phoneNumber") && filteredClient.getPhoneNumber().isPresent()) {
                    List<String> clientPhones = filteredClient.getClientPhones();
                    for (String phone : clientPhones) {
                        writer.write(phone + delimeter);
                    }
                }

                List<SocialProfile> clientsSocialProfiles = new ArrayList<>(filteredClient.getSocialProfiles());
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
            logger.error("File not created! ", e);
        }
    }

    @Override
    public void writeToFileWithConditionToDonwload(ConditionToDownload conditionToDownload, String fileName) {
        String path = "DownloadData";
        String delimeter = conditionToDownload.getDelimeter();

        if (Strings.isNullOrEmpty(conditionToDownload.getDelimeter())) {
            delimeter = "  ";
        }

        Path directory = Paths.get(path);
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                logger.error("Could not create folder for text files", e);
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

        Set<String> checkedData = new HashSet<>(conditionToDownload.getSelected());
        try (BufferedWriter writer = Files.newBufferedWriter(file, Charset.forName("UTF-8"))) {
            List<Client> filteredClients = clientService.getAllClients();
            for (Client filteredClient : filteredClients) {
                if (checkedData.contains("name") && !Strings.isNullOrEmpty(filteredClient.getName())) {
                    writer.write(filteredClient.getName() + delimeter);
                }

                if (checkedData.contains("lastName") && !Strings.isNullOrEmpty(filteredClient.getLastName())) {
                    writer.write(filteredClient.getLastName() + delimeter);
                }

                if (checkedData.contains("email") && filteredClient.getEmail().isPresent()) {
                    List<String> clientEmails = filteredClient.getClientEmails();
                    for (String email : clientEmails) {
                        writer.write(email + delimeter);
                    }
                }

                if (checkedData.contains("phoneNumber") && filteredClient.getPhoneNumber().isPresent()) {
                    List<String> clientPhones = filteredClient.getClientPhones();
                    for (String phone : clientPhones) {
                        writer.write(phone + delimeter);
                    }
                }

                List<SocialProfile> clientsSocialProfiles = new ArrayList<>(filteredClient.getSocialProfiles());
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
            logger.error("File not created! ", e);
        }
    }
}