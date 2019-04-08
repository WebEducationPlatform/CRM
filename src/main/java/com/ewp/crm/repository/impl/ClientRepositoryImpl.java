package com.ewp.crm.repository.impl;

import com.ewp.crm.models.*;
import com.ewp.crm.models.SortedStatuses.SortingType;
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
import java.util.Arrays;
import java.util.List;

@Repository
public class ClientRepositoryImpl implements ClientRepositoryCustom {

    private static Logger logger = LoggerFactory.getLogger(ClientRepositoryImpl.class);

    private final EntityManager entityManager;

    @Value("${project.jpa.batch-size}")
    private int batchSize;

    @Value("${project.pagination.page-size.clients}")
    private int pageSize;

    private final String queryPattern = " (s.socialId LIKE :search OR c.name LIKE :search OR c.lastName LIKE :search OR c.email LIKE :search OR c.phoneNumber LIKE :search OR c.skype LIKE :search) ";

    @Autowired
    public ClientRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<String> getSocialIdsBySocialProfileTypeAndStudentExists(String socialProfileType) {
        return entityManager.createQuery("SELECT sp.socialId FROM Client c LEFT JOIN c.socialProfiles AS sp LEFT JOIN sp.socialProfileType AS spt LEFT JOIN c.student AS s WHERE s IS NOT NULL AND spt.name = :socialProfileType")
                .setParameter("socialProfileType", socialProfileType)
                .getResultList();
    }

    @Override
    public List<String> getSocialIdsBySocialProfileTypeAndStatusAndStudentExists(List<Status> statuses, String socialProfileType) {
        return entityManager.createQuery("SELECT sp.socialId FROM Client c LEFT JOIN c.socialProfiles AS sp LEFT JOIN sp.socialProfileType AS spt LEFT JOIN c.student AS s WHERE s IS NOT NULL AND spt.name = :socialProfileType AND c.status IN (:statuses)")
                .setParameter("socialProfileType", socialProfileType)
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
    public List<Client> getClientByHistoryTimeIntervalAndHistoryType(ZonedDateTime firstDay, ZonedDateTime lastDay, ClientHistory.Type[] types) {
        return entityManager.createQuery("SELECT DISTINCT c FROM Client c JOIN c.history p WHERE p.date >= :firstDay AND p.date <= :lastDay AND p.type IN :types")
                .setParameter("firstDay", firstDay)
                .setParameter("lastDay", lastDay)
                .setParameter("types", Arrays.asList(types))
                .getResultList();
    }

    public List<Long> getChangedStatusClientIdsInPeriod(ZonedDateTime firstDate, ZonedDateTime lastDate, ClientHistory.Type[] types, String title) {
        return entityManager.createQuery("SELECT DISTINCT c.id FROM Client c JOIN c.history p WHERE p.date >= :firstDate AND p.date <= :lastDate AND p.type IN :types AND p.title LIKE CONCAT('%',:title,'%')")
                .setParameter("firstDate", firstDate)
                .setParameter("lastDate", lastDate)
                .setParameter("types", Arrays.asList(types))
                .setParameter("title", title)
                .getResultList();
    }

    public long getCountClientByHistoryTimeIntervalAndHistoryTypeAndTitle(ZonedDateTime firstDay, ZonedDateTime lastDay, ClientHistory.Type[] types, String title) {
        return (Long) entityManager.createQuery("SELECT DISTINCT COUNT(c) FROM Client c JOIN c.history p WHERE p.date > :firstDay AND p.date < :lastDay AND p.type IN :types AND p.title LIKE CONCAT('%',:title,'%')")
                .setParameter("firstDay", firstDay)
                .setParameter("lastDay", lastDay)
                .setParameter("types", Arrays.asList(types))
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
        return entityManager.createQuery("SELECT email FROM Client").getResultList();
    }

    @Override
    public List<String> getClientsPhoneNumber() {
        return entityManager.createQuery("SELECT phoneNumber FROM Client").getResultList();
    }

    @Override
    public List<String> getFilteredClientsEmail(FilteringCondition filteringCondition) {
        return entityManager.createQuery(createQueryForGetEmails(filteringCondition)).getResultList();
    }

    @Override
    public List<String> getFilteredClientsPhoneNumber(FilteringCondition filteringCondition) {
        return entityManager.createQuery(createQueryForGetPhoneNumbers(filteringCondition)).getResultList();
    }

    @Override
    public List<String> getFilteredClientsSNLinks(FilteringCondition filteringCondition) {
        return entityManager.createNativeQuery(queryForGetSNLinksFromFilteredClients(filteringCondition)).getResultList();
    }

    @Override
    public boolean isTelegramClientPresent(Integer id) {
        List<SocialProfile> result = entityManager.createQuery("SELECT s FROM SocialProfile s WHERE s.socialId = :telegramId AND s.socialProfileType.name = 'telegram'", SocialProfile.class)
                .setParameter("telegramId", id.toString())
                .getResultList();
        return !result.isEmpty();
    }

    @Override
    public Client getClientBySocialProfile(String id, String socialProfileType) {
        Client result = null;
        try {
            result = entityManager.createQuery("SELECT c FROM Client c LEFT JOIN c.socialProfiles s WHERE s.socialId = :sid AND s.socialProfileType.name = :type", Client.class)
                    .setParameter("sid", id)
                    .setParameter("type", socialProfileType)
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
        return "select email from Client cl where 1 = 1" + filterQuery(filteringCondition);
    }

    private String createQueryForGetPhoneNumbers(FilteringCondition filteringCondition) {
        return "select phoneNumber from Client cl where 1 = 1" + filterQuery(filteringCondition);
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
            query.append(" and cl.dateOfRegistration <= '").append(filteringCondition.getDateTo()).append("'");
        }

        if (filteringCondition.getStatus() != null) {
            query.append(" and cl.status.name = '").append(filteringCondition.getStatus()).append("'");
        }

        return query.toString();
    }

    private String queryForGetSNLinksFromFilteredClients(FilteringCondition filteringCondition) {

        StringBuilder query = new StringBuilder("SELECT social_network.social_id\n" +
                "FROM client_social_network\n" +
                "  INNER JOIN social_network ON client_social_network.social_network_id = social_network.id\n" +
                "  INNER JOIN client ON client_social_network.client_id = client.client_id\n" +
                "  INNER JOIN social_network_social_network_type ON social_network.id = social_network_social_network_type.social_network_id\n" +
                "  INNER JOIN social_network_type ON social_network_social_network_type.social_network_type_id = social_network_type.id\n" +
                "WHERE social_network_type.name = '" + filteringCondition.getSelected() + "'");

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
        StringBuilder searchString = new StringBuilder("SELECT distinct c FROM Client c LEFT JOIN c.socialProfiles s WHERE");
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
    public List<Client> getClientsInStatusOrderedByRegistration(Status status, SortingType order, boolean isAdmin, User user) {
        String query = isAdmin ? "SELECT c FROM Client c JOIN c.status s WHERE s.id=:status_id ORDER BY c.dateOfRegistration" :
                "SELECT c FROM Client c JOIN c.status s WHERE s.id=:status_id AND (c.ownerUser in (:ownerUser) or c.ownerUser is NULL) ORDER BY c.dateOfRegistration";
        if (SortingType.NEW_FIRST.equals(order)) {
            query += " DESC";
        }
        List<Client> orderedClients = isAdmin ?
                entityManager.createQuery(query)
                        .setParameter("status_id", status.getId())
                        .getResultList() :
                entityManager.createQuery(query)
                        .setParameter("status_id", status.getId())
                        .setParameter("ownerUser", user)
                        .getResultList();
        return orderedClients;
    }

    @Override
    public List<Client> getClientsInStatusOrderedByHistory(Status status, SortingType order, boolean isAdmin, User user) {
        String query = isAdmin ? "SELECT c FROM Client c JOIN c.status s JOIN c.history h WHERE s.id=:status_id GROUP BY c ORDER BY MAX(h.date)" :
                "SELECT c FROM Client c JOIN c.status s JOIN c.history h WHERE s.id=:status_id AND (c.ownerUser IN (:ownerUser) OR c.ownerUser IS NULL) GROUP BY c ORDER BY MAX(h.date)";
        if (SortingType.NEW_CHANGES_FIRST.equals(order)) {
            query += " DESC";
        }
        List<Client> orderedClients = isAdmin ?
                entityManager.createQuery(query)
                        .setParameter("status_id", status.getId())
                        .getResultList() :
                entityManager.createQuery(query)
                        .setParameter("status_id", status.getId())
                        .setParameter("ownerUser", user)
                        .getResultList();
        return orderedClients;
    }
}
