package com.ewp.crm.repository.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.models.FilteringCondition;
import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.SocialProfile.SocialNetworkType;
import com.ewp.crm.models.SortedStatuses.SortingType;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.User;
import com.ewp.crm.repository.interfaces.ClientRepositoryCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ClientRepositoryImpl implements ClientRepositoryCustom {

    private static Logger logger = LoggerFactory.getLogger(ClientRepositoryImpl.class);

    private final EntityManager entityManager;

    @Value("${project.jpa.batch-size}")
    private int batchSize;

    @Value("${project.pagination.page-size.clients}")
    private int pageSize;

    private final String queryPattern = " (s.socialId LIKE :search OR c.name LIKE :search OR c.lastName LIKE :search OR e LIKE :search OR p LIKE :search OR c.skype LIKE :search) ";

    @Autowired
    public ClientRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<String> getSocialIdsBySocialProfileTypeAndStudentExists(String socialProfileType) {

        return entityManager.createQuery("SELECT sp.socialId FROM Client c " +
                "LEFT JOIN c.socialProfiles AS sp " +
                "LEFT JOIN c.student AS s " +
                "WHERE s IS NOT NULL AND sp.socialNetworkType = :socialProfileType")
                .setParameter("socialProfileType", SocialNetworkType.valueOf(socialProfileType.toUpperCase()))
                .getResultList();
    }

    @Override
    public boolean hasClientSocialProfileByType(Client client, String socialProfileType) {
        return !entityManager.createQuery("SELECT sp.socialId FROM Client c " +
                "LEFT JOIN c.socialProfiles AS sp " +
                "WHERE c.id = :clientId AND sp.socialNetworkType = :socialProfileType")
                .setParameter("socialProfileType", SocialNetworkType.valueOf(socialProfileType.toUpperCase()))
                .setParameter("clientId", client.getId())
                .getResultList()
                .isEmpty();
    }

    @Override
    public List<String> getSocialIdsBySocialProfileTypeAndStatusAndStudentExists(List<Status> statuses, String socialProfileType) {
        return entityManager.createQuery("SELECT sp.socialId FROM Client c " +
                "LEFT JOIN c.socialProfiles AS sp " +
                "LEFT JOIN c.student AS s " +
                "WHERE s IS NOT NULL AND sp.socialNetworkType = :socialProfileType AND c.status IN (:statuses)")
                .setParameter("socialProfileType", SocialNetworkType.valueOf(socialProfileType.toUpperCase()))
                .setParameter("statuses", statuses)
                .getResultList();
    }

    @Override
    public List<ClientHistory> getClientByTimeInterval(int days) {
        return entityManager.createQuery("SELECT cl FROM ClientHistory cl where cl.date > (current_date() - (:days))")
                .setParameter("days", days)
                .getResultList();
    }

    @Override
    public Long countByDate(String date) {
        String val = date.substring(0, 10);
        String queryString0 = String.format("SELECT COUNT(*) FROM client WHERE date >= '%s' AND date < '%s 23:59:59'", val, val);
        Query query = entityManager.createNativeQuery(queryString0);
        BigInteger count = new BigInteger(query.getSingleResult().toString());
        return count.longValue();
    }

    @Override
    public String getSlackLinkHashForClient(Client client) {
        List<String> result = entityManager.createQuery("SELECT s.hash FROM Client c JOIN c.slackInviteLink AS s WHERE c.id = :clientId")
                .setParameter("clientId", client.getId())
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public ClientHistory getNearestClientHistoryBeforeDate(Client client, ZonedDateTime dateTime, List<ClientHistory.Type> types) {
        List<ClientHistory> result = entityManager.createQuery("SELECT h FROM Client c JOIN c.history AS h WHERE h.date < :dateTime AND c.id = :clientId AND h.type IN :types ORDER BY h.date DESC")
                .setParameter("dateTime", dateTime)
                .setParameter("clientId", client.getId())
                .setParameter("types", types)
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public ClientHistory getNearestClientHistoryAfterDate(Client client, ZonedDateTime dateTime, List<ClientHistory.Type> types) {
        List<ClientHistory> result = entityManager.createQuery("SELECT h FROM Client c JOIN c.history AS h WHERE h.date > :dateTime AND c.id = :clientId AND h.type IN :types ORDER BY h.date ASC")
                .setParameter("dateTime", dateTime)
                .setParameter("clientId", client.getId())
                .setParameter("types", types)
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public ClientHistory getNearestClientHistoryAfterDateByHistoryType(Client client, ZonedDateTime dateTime, List<ClientHistory.Type> types, String title) {
        List<ClientHistory> result = entityManager.createQuery("SELECT h FROM Client c JOIN c.history AS h WHERE h.date > :dateTime AND c.id = :clientId AND h.type IN :types AND h.title LIKE CONCAT('%: ',:title,'%') ORDER BY h.date ASC")
                .setParameter("dateTime", dateTime)
                .setParameter("clientId", client.getId())
                .setParameter("types", types)
                .setParameter("title", title)
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public ClientHistory getHistoryByClientAndHistoryTimeIntervalAndHistoryType(Client client, ZonedDateTime firstDay, ZonedDateTime lastDay, List<ClientHistory.Type> types, String title) {
        List<ClientHistory> result = entityManager.createQuery("SELECT DISTINCT h FROM Client c JOIN c.history AS h WHERE h.date >= :firstDay AND h.date <= :lastDay AND h.type IN :types AND c.id = :clientId AND h.title LIKE CONCAT('%: ',:title,'%') ORDER BY h.date DESC")
                .setParameter("firstDay", firstDay)
                .setParameter("lastDay", lastDay)
                .setParameter("types", types)
                .setParameter("clientId", client.getId())
                .setParameter("title", title)
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public ClientHistory getClientFirstStatusChangingHistory(long clientId) {
        List<ClientHistory> result = entityManager.createQuery("SELECT h FROM Client c JOIN c.history AS h WHERE c.id = :clientId AND h.title LIKE CONCAT('%',:status,'% из %') ORDER BY h.date ASC")
                .setParameter("clientId", clientId)
                .setParameter("status", ClientHistory.Type.STATUS.getInfo())
                .setFirstResult(0)
                .setMaxResults(1)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public boolean hasClientStatusChangingHistory(long clientId) {
        return getClientFirstStatusChangingHistory(clientId) != null;
    }

    @Override
    public boolean hasClientBeenInStatusBefore(long clientId, ZonedDateTime date, String statusName) {
        return !entityManager.createQuery("SELECT c FROM Client c JOIN c.history AS h WHERE c.id = :clientId AND h.date < :date AND h.title LIKE CONCAT('%: ',:title,'%')")
                .setParameter("clientId", clientId)
                .setParameter("date", date)
                .setParameter("title", statusName)
                .setMaxResults(1)
                .getResultList()
                .isEmpty();
    }

    @Override
    public List<Client> getClientByHistoryTimeIntervalAndHistoryType(ZonedDateTime firstDay, ZonedDateTime lastDay, List<ClientHistory.Type> types, List<Status> excludeStatuses) {
        String query = "SELECT DISTINCT c FROM Client c JOIN c.history p WHERE p.date >= :firstDay AND p.date <= :lastDay AND p.type IN :types";
        if (excludeStatuses != null && !excludeStatuses.isEmpty()) {
            query += " AND c.status NOT IN :excludes";
            return entityManager.createQuery(query)
                    .setParameter("firstDay", firstDay)
                    .setParameter("lastDay", lastDay)
                    .setParameter("types", types)
                    .setParameter("excludes", excludeStatuses)
                    .getResultList();
        }
        return entityManager.createQuery(query)
                .setParameter("firstDay", firstDay)
                .setParameter("lastDay", lastDay)
                .setParameter("types", types)
                .getResultList();
    }

    /*
    Был ли клиент когда-либо в заданных статусах
     */
    @Override
    public List<ClientHistory> getAllHistoriesByClientStatusChanging(Client client, List<Status> statuses, List<ClientHistory.Type> types) {
        List<ClientHistory> result = new ArrayList<>();
        if (!statuses.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < statuses.size(); i++) {
                Status status = statuses.get(i);
                sb.append("(p.title LIKE CONCAT('%: ").append(status.getName()).append("%'))");
                if (i < statuses.size() - 1) {
                    sb.append(" OR ");
                }
            }
            String query = "SELECT p FROM Client c JOIN c.history p WHERE (c.id = :id AND p.type IN :types AND (" + sb.toString() + "))";
            result = entityManager.createQuery(query)
                    .setParameter("types", types)
                    .setParameter("id", client.getId())
                    .getResultList();
        }
        return result;
    }

    /*
    Получить все истории клиента по типу
     */
    @Override
    public List<ClientHistory> getAllHistoriesByClientAndHistoryType(Client client, List<ClientHistory.Type> types) {
        List<ClientHistory> result = new ArrayList<>();
        String query = "SELECT p FROM Client c JOIN c.history p WHERE (c.id = :id AND p.type IN :types)";
        result = entityManager.createQuery(query)
                .setParameter("types", types)
                .setParameter("id", client.getId())
                .getResultList();
        return result;
    }

    /*
    Был ли клиент в определенном статусе в промежутке между двумя датами?
     */
    @Override
    public boolean hasClientChangedStatusFromThisToAnotherInPeriod(ZonedDateTime firstDate, ZonedDateTime lastDate, List<ClientHistory.Type> types, List<Status> excludeStatuses, String title) {
        List<ClientHistory> histories;
        String query = "SELECT p FROM Client c JOIN c.history p WHERE p.date >= :firstDate AND p.date <= :lastDate AND p.type IN :types AND p.title LIKE CONCAT('% из ',:title,'%')";
        if (excludeStatuses != null && !excludeStatuses.isEmpty()) {
            query += " AND c.status NOT IN :excludes";
            histories = entityManager.createQuery(query)
                    .setParameter("firstDate", firstDate)
                    .setParameter("lastDate", lastDate)
                    .setParameter("types", types)
                    .setParameter("title", title)
                    .setParameter("excludes", excludeStatuses)
                    .setMaxResults(1)
                    .getResultList();
        } else {
            histories = entityManager.createQuery(query)
                    .setParameter("firstDate", firstDate)
                    .setParameter("lastDate", lastDate)
                    .setParameter("types", types)
                    .setParameter("title", title)
                    .setMaxResults(1)
                    .getResultList();
        }
        return !histories.isEmpty();
    }

    /*
    Получаем все переходы клиента в определенный статус в заданном интервале дат
     */
    @Override
    public Map<Client, List<ClientHistory>> getChangedStatusClientsInPeriod(ZonedDateTime firstDate, ZonedDateTime lastDate, List<ClientHistory.Type> types, List<Status> excludeStatuses, String title) {
        List<ClientHistory> histories;
        String query = "SELECT p FROM Client c JOIN c.history p WHERE p.date >= :firstDate AND p.date <= :lastDate AND p.type IN :types AND p.title LIKE CONCAT('%: ',:title,'%')";
        if (excludeStatuses != null && !excludeStatuses.isEmpty()) {
            query += " AND c.status NOT IN :excludes";
            histories = entityManager.createQuery(query)
                    .setParameter("firstDate", firstDate)
                    .setParameter("lastDate", lastDate)
                    .setParameter("types", types)
                    .setParameter("title", title)
                    .setParameter("excludes", excludeStatuses)
                    .getResultList();
        } else {
            histories = entityManager.createQuery(query)
                    .setParameter("firstDate", firstDate)
                    .setParameter("lastDate", lastDate)
                    .setParameter("types", types)
                    .setParameter("title", title)
                    .getResultList();
        }
        Map<Client, List<ClientHistory>> result = new HashMap<>();
        for (ClientHistory history : histories) {
            if (!result.containsKey(history.getClient())) {
                result.put(history.getClient(), new ArrayList<>());
            }
            result.get(history.getClient()).add(history);
        }
        return result;
    }

    @Override
    public long getCountClientByHistoryTimeIntervalAndHistoryTypeAndTitle(ZonedDateTime firstDay, ZonedDateTime lastDay, List<ClientHistory.Type> types, String title) {
        return (Long) entityManager.createQuery("SELECT DISTINCT COUNT(c) FROM Client c JOIN c.history p WHERE p.date > :firstDay AND p.date < :lastDay AND p.type IN :types AND p.title LIKE CONCAT('%: ',:title,'%')")
                .setParameter("firstDay", firstDay)
                .setParameter("lastDay", lastDay)
                .setParameter("types", types)
                .setParameter("title", title)
                .getSingleResult();
    }

    @Override
    public List<Client> filteringClient(FilteringCondition filteringCondition) {
        Query query = entityManager.createQuery(createQuery(filteringCondition));
        int pageNumber = filteringCondition.getPageNumber();
        query.setFirstResult((pageNumber - 1) * pageSize);
        query.setMaxResults(pageSize);
        List<Client> fooList = query.getResultList();
        return fooList;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Client> filteringClientWithoutPaginator(FilteringCondition filteringCondition) {
        return entityManager.createQuery(createQuery(filteringCondition)).getResultList();
    }

    @Override
    public List<Client> getChangeActiveClients() {
        return entityManager.createQuery("SELECT cl FROM Client cl WHERE  cl.postponeDate IS NOT NULL AND cl.postponeDate<=now()").getResultList();
    }

    @Transactional
    @Override
    public void updateBatchClients(List<Client> clients) {
        for (int i = 0; i < clients.size(); i++) {
            entityManager.merge(clients.get(i));
            if (i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush();
        entityManager.clear();
    }

    @Transactional
    @Override
    public void addBatchClients(List<Client> clients) {
        for (int i = 0; i < clients.size(); i++) {
            entityManager.persist(clients.get(i));
            if (i % batchSize == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        entityManager.flush();
        entityManager.clear();
    }

    @Override
    public List<String> getClientsEmail() {
        return entityManager.createNativeQuery("SELECT client_email FROM client_emails").getResultList();
    }

    @Override
    public List<String> getClientsPhoneNumber() {
        return entityManager.createNativeQuery("SELECT client_phone FROM client_phones").getResultList();
    }

    @Override
    public List<String> getFilteredClientsEmail(FilteringCondition filteringCondition) {
        return entityManager.createNativeQuery(createQueryForGetEmails(filteringCondition)).getResultList();
    }

    @Override
    public List<String> getClientsEmailsByStatusesIds(List<Long> statusesIds) {
        return entityManager.createNativeQuery(
                "SELECT ce.client_email FROM client_emails ce " +
                "INNER JOIN status_clients sc ON ce.client_id = sc.user_id " +
                "WHERE sc.status_id IN (:statusesIds)")
                .setParameter("statusesIds", statusesIds)
                .getResultList();
    }

    @Override
    public List<String> getFilteredClientsPhoneNumber(FilteringCondition filteringCondition) {
        return entityManager.createNativeQuery(createQueryForGetPhoneNumbers(filteringCondition)).getResultList();
    }

    @Override
    public List<String> getClientsPhoneNumbersByStatusesIds(List<Long> statusesIds) {
        return entityManager.createNativeQuery(
                "SELECT cp.client_phone FROM client_phones cp " +
                        "INNER JOIN status_clients sc ON cp.client_id = sc.user_id " +
                        "WHERE sc.status_id IN (:statusesIds)")
                .setParameter("statusesIds", statusesIds)
                .getResultList();
    }

    @Override
    public List<String> getFilteredClientsSNLinks(FilteringCondition filteringCondition) {
        return entityManager.createNativeQuery(queryForGetSNLinksFromFilteredClients(filteringCondition)).getResultList();
    }

    @Override
    public boolean isTelegramClientPresent(Integer id) {
        List<SocialProfile> result = entityManager.createQuery("SELECT s FROM SocialProfile s WHERE s.socialId = :telegramId AND s.socialNetworkType = :socialType", SocialProfile.class)
                .setParameter("telegramId", id.toString())
                .setParameter("socialType", SocialNetworkType.valueOf("telegram".toUpperCase()))
                .getResultList();
        return !result.isEmpty();
    }

    @Override
    public Client getClientBySocialProfile(String id, String socialProfileType) {
        Client result = null;
        try {
            result = entityManager.createQuery("SELECT c FROM Client c " +
                    "LEFT JOIN c.socialProfiles s " +
                    "WHERE s.socialId = :sid AND s.socialNetworkType = :type", Client.class)
                    .setParameter("sid", id)
                    .setParameter("type", SocialNetworkType.valueOf(socialProfileType.toUpperCase()))
                    .getSingleResult();
        } catch (NoResultException e) {
            logger.info("Client with social id {} not found", id, e);
        }
        return result;
    }

    private String createQuery(FilteringCondition filteringCondition) {
        return "select cl from Client cl where 1 = 1" + filterQuery(filteringCondition);
    }

    private String createQueryForGetEmails(FilteringCondition filteringCondition) {
        return "SELECT client_email FROM client_emails ce JOIN client cl ON ce.client_id = cl.client_id" +
                " JOIN status_clients sc ON cl.client_id = sc.user_id" +
                " where 1 = 1" + filterQueryForPhonesAndEmails(filteringCondition);
    }

    private String createQueryForGetPhoneNumbers(FilteringCondition filteringCondition) {
        return "SELECT client_phone FROM client_phones cp JOIN client cl ON cp.client_id = cl.client_id" +
                " JOIN status_clients sc ON cl.client_id = sc.user_id" +
                " where 1 = 1" + filterQueryForPhonesAndEmails(filteringCondition);
    }

    private String filterQueryForPhonesAndEmails(FilteringCondition filteringCondition) {
        StringBuilder query = new StringBuilder();

        if (filteringCondition.getSex() != null) {
            query.append(" and cl.sex = '").append(filteringCondition.getSex()).append("'");
        }

        if (filteringCondition.getAgeFrom() != null) {
            LocalDate dateAgeTo = LocalDate.now().minusYears(filteringCondition.getAgeFrom());
            String dateTo = dateAgeTo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            query.append(" and cl.birthDate <= '").append(dateTo).append("'");
        }
        if (filteringCondition.getAgeTo() != null) {
            LocalDate dateAgeFrom = LocalDate.now().minusYears(filteringCondition.getAgeTo());
            String dateFrom = dateAgeFrom.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            query.append(" and cl.birthDate >= '").append(dateFrom).append("'");
        }

        if (filteringCondition.getCity() != null) {
            query.append(" and cl.city = '").append(filteringCondition.getCity()).append("'");
        }

        if (filteringCondition.getCountry() != null) {
            query.append(" and cl.country = '").append(filteringCondition.getCountry()).append("'");
        }

        if (filteringCondition.getDateFrom() != null) {
            query.append(" and cl.date >= '").append(filteringCondition.getDateFrom()).append("'");
        }

        if (filteringCondition.getDateTo() != null) {
            query.append(" and cl.date <= '").append(filteringCondition.getDateTo()).append("'");
        }

        if (filteringCondition.getStatus() != null) {
            query.append(" and cl.client_id in (select c2.client_id from client c2 join status_clients sc1 on sc1.user_id = c2.client_id where sc1.status_id in (select s1.status_id from status s1 where s1.status_name = '")
                    .append(filteringCondition.getStatus())
                    .append("'))");
        }

        return query.toString();
    }

    private String filterQuery(FilteringCondition filteringCondition) {
        StringBuilder query = new StringBuilder();

        if (filteringCondition.getSex() != null) {
            query.append(" and cl.sex = '").append(filteringCondition.getSex()).append("'");
        }

        if (filteringCondition.getAgeFrom() != null) {
            LocalDate dateAgeTo = LocalDate.now().minusYears(filteringCondition.getAgeFrom());
            String dateTo = dateAgeTo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            query.append(" and cl.birthDate <= '").append(dateTo).append("'");
        }
        if (filteringCondition.getAgeTo() != null) {
            LocalDate dateAgeFrom = LocalDate.now().minusYears(filteringCondition.getAgeTo());
            String dateFrom = dateAgeFrom.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            query.append(" and cl.birthDate >= '").append(dateFrom).append("'");
        }

        if (!filteringCondition.getCity().isEmpty()) {
            query.append(" and cl.city = '").append(filteringCondition.getCity()).append("'");
        }

        if (!filteringCondition.getCountry().isEmpty()) {
            query.append(" and cl.country = '").append(filteringCondition.getCountry()).append("'");
        }

        if (filteringCondition.getDateFrom() != null) {
            query.append(" and cl.dateOfRegistration >= '").append(filteringCondition.getDateFrom()).append("'");
        }

        if (filteringCondition.getDateTo() != null) {
            query.append(" and cl.dateOfRegistration <= '").append(filteringCondition.getDateTo().atTime(23, 59, 59)).append("'");
        }

        if (filteringCondition.getStatus() != null) {
            query.append(" and cl.status.name = '").append(filteringCondition.getStatus()).append("'");
        }

        if (filteringCondition.getOwnerUserId() != null) {
            query.append(" and cl.ownerUser.id = '").append(filteringCondition.getOwnerUserId()).append("'");
        }

        return query.toString();
    }

    private String queryForGetSNLinksFromFilteredClients(FilteringCondition filteringCondition) {

        StringBuilder query = new StringBuilder("SELECT social_network.social_id\n" +
                "FROM client_social_network\n" +
                "  INNER JOIN social_network ON client_social_network.social_network_id = social_network.id\n" +
                "  INNER JOIN client ON client_social_network.client_id = client.client_id\n" +
                "WHERE social_network.social_network_type = '" + filteringCondition.getChecked().toUpperCase() + "'");

        if (filteringCondition.getSex() != null) {
            query.append(" and client.sex = '").append(filteringCondition.getSex()).append("'");
        }

        if (filteringCondition.getAgeFrom() != null) {
            LocalDate dateAgeTo = LocalDate.now().minusYears(filteringCondition.getAgeFrom());
            String dateTo = dateAgeTo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            query.append(" and client.birth_date <= '").append(dateTo).append("'");
        }
        if (filteringCondition.getAgeTo() != null) {
            LocalDate dateAgeFrom = LocalDate.now().minusYears(filteringCondition.getAgeTo());
            String dateFrom = dateAgeFrom.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            query.append(" and client.birth_date >= '").append(dateFrom).append("'");
        }

        if (!filteringCondition.getCity().isEmpty()) {
            query.append(" and client.city = '").append(filteringCondition.getCity()).append("'");
        }

        if (!filteringCondition.getCountry().isEmpty()) {
            query.append(" and client.country = '").append(filteringCondition.getCountry()).append("'");
        }

        if (filteringCondition.getDateFrom() != null) {
            query.append(" and client.date >= '").append(filteringCondition.getDateFrom()).append("'");
        }

        if (filteringCondition.getDateTo() != null) {
            query.append(" and client.date <= '").append(filteringCondition.getDateTo()).append("'");
        }

        if (filteringCondition.getStatus() != null) {
            query.append(" and client.client_id in (select c2.client_id from client c2 join status_clients sc1 on sc1.user_id = c2.client_id where sc1.status_id in (select s1.status_id from status s1 where s1.status_name = '").append(filteringCondition.getStatus()).append("'))");
        }

        return query.toString();
    }

    @Override
    public List<Client> getClientsBySearchPhrase(String search) {
        StringBuilder searchString = new StringBuilder("SELECT distinct c FROM Client c LEFT JOIN c.socialProfiles AS s LEFT JOIN c.clientEmails AS e LEFT JOIN c.clientPhones AS p WHERE");
        String[] searchWords = search.split(" ");
        for (int i = 0; i < searchWords.length; i++) {
            searchString.append(queryPattern.replace("search", "search" + i));
            if (i != searchWords.length - 1) {
                searchString.append("AND");
            }
        }
        Query query = entityManager.createQuery(searchString.toString());
        for (int i = 0; i < searchWords.length; i++) {
            query.setParameter("search" + i, "%" + searchWords[i] + "%");
        }
        return query.getResultList();
    }

    @Override
    public List<Client> getClientsInStatusOrderedByRegistration(Status status, SortingType order) {
        String query = "SELECT c FROM Client c JOIN c.status s WHERE s.id=:status_id ORDER BY c.dateOfRegistration";
        if (SortingType.NEW_FIRST.equals(order)) {
            query += " DESC";
        }

        return (List<Client>) entityManager.createQuery(query).setParameter("status_id", status.getId()).getResultList();
    }

    @Override
    public List<Client> getClientsInStatusOrderedByHistory(Status status, SortingType order) {
        String query = "SELECT c FROM Client c JOIN c.status s JOIN c.history h WHERE s.id=:status_id GROUP BY c ORDER BY MAX(h.date)";
        if (SortingType.NEW_CHANGES_FIRST.equals(order)) {
            query += " DESC";
        }
        return (List<Client>) entityManager.createQuery(query).setParameter("status_id", status.getId()).getResultList();
    }

    @Transactional
    @Override
    public void transferClientsBetweenOwners(User sender, User receiver) {
        entityManager.createQuery("UPDATE Client c SET c.ownerUser = :receiver WHERE c.ownerUser = :sender")
                .setParameter("sender", sender)
                .setParameter("receiver", receiver)
                .executeUpdate();
    }
}

