package com.ewp.crm.repository.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Status;
import com.ewp.crm.models.dto.ClientDto;
import com.ewp.crm.repository.interfaces.ClientStatusChangingHistoryRepositoryCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ClientStatusChangingHistoryRepositoryImpl implements ClientStatusChangingHistoryRepositoryCustom {

    private static Logger logger = LoggerFactory.getLogger(PersistentLoginRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Возвращает клиентов, созданных (как впервые, так и повторно) в период времени beginDate - endDate
     * и которые ни разу не были в статусах excludeStatuses
     *
     * @param beginDate начало периода
     * @param endDate конец периода
     * @param excludeStatuses статусы на исключение
     * @return список клиентов
     */
    @Override
    public List<ClientDto> getNewClientsInPeriodButExcludeStatuses(ZonedDateTime beginDate, ZonedDateTime endDate, Status... excludeStatuses) {
        List<ClientDto> clients = new ArrayList<>();
        List<Tuple> tuples = entityManager.createNativeQuery(
                "SELECT DISTINCT c.client_id AS id, c.first_name AS name, c.last_name AS lastName, cp.client_phone AS phone, ce.client_email AS email FROM client c " +
                        "   LEFT JOIN client_emails ce ON ce.client_id = c.client_id AND ce.number_in_list = 0 " +
                        "   LEFT JOIN client_phones cp ON cp.client_id = c.client_id AND cp.number_in_list = 0 " +
                        "   WHERE " +
                        "       c.client_id IN ( " +
                        "           SELECT csch.client_id FROM client_status_changing_history csch " +
                        "               WHERE " +
                        "                   csch.is_client_creation IS TRUE AND " +
                        "                   csch.client_id NOT IN ( " +
                        "                       SELECT client_id FROM client_status_changing_history exclude " +
                        "                           WHERE " +
                        "                               exclude.client_id = csch.client_id AND " +
                        "                               exclude.new_status_id IN (-1" + createStringOfExcludeStatusesIds(excludeStatuses) + ") " +
                        "                   ) AND " +
                        "                   csch.date >= :beginDate AND " +
                        "                   csch.date <= :endDate AND " +
                        "                   csch.is_fake IS FALSE " +
                        "       )", Tuple.class)
                .setParameter("beginDate", beginDate)
                .setParameter("endDate", endDate)
                .getResultList();
        fillClientsFromTuples(tuples, clients);
        return clients;
    }

    /**
     * Возвращает клиентов, созданных (как впервые, так и повторно) в статусе status в период времени beginDate - endDate
     * и которые ни разу не были в статусах excludeStatuses
     *
     * @param status искомый статус
     * @param beginDate начало периода
     * @param endDate конец периода
     * @param excludeStatuses статусы на исключение
     * @return список клиентов
     */
    @Override
    public List<ClientDto> getNewClientsInStatusAndPeriodButExcludeStatuses(Status status, ZonedDateTime beginDate, ZonedDateTime endDate, Status... excludeStatuses) {
        List<ClientDto> clients = new ArrayList<>();
        List<Tuple> tuples = entityManager.createNativeQuery(
                "SELECT DISTINCT c.client_id AS id, c.first_name AS name, c.last_name AS lastName, cp.client_phone AS phone, ce.client_email AS email FROM client c " +
                        "   LEFT JOIN client_emails ce ON ce.client_id = c.client_id AND ce.number_in_list = 0 " +
                        "   LEFT JOIN client_phones cp ON cp.client_id = c.client_id AND cp.number_in_list = 0 " +
                        "   WHERE " +
                        "       c.client_id IN ( " +
                        "           SELECT csch.client_id FROM client_status_changing_history csch " +
                        "               WHERE " +
                        "                   csch.is_client_creation IS TRUE AND " +
                        "                   csch.client_id NOT IN ( " +
                        "                       SELECT client_id FROM client_status_changing_history exclude " +
                        "                           WHERE " +
                        "                               exclude.client_id = csch.client_id AND " +
                        "                               exclude.new_status_id IN (-1" + createStringOfExcludeStatusesIds(excludeStatuses) + ") " +
                        "                   ) AND " +
                        "                   csch.date >= :beginDate AND " +
                        "                   csch.date <= :endDate AND " +
                        "                   csch.is_fake IS FALSE AND " +
                        "                   csch.new_status_id = :statusId " +
                        "       )", Tuple.class)
                .setParameter("beginDate", beginDate)
                .setParameter("endDate", endDate)
                .setParameter("statusId", status.getId())
                .getResultList();
        fillClientsFromTuples(tuples, clients);
        return clients;
    }

    /**
     * Возвращает список клиентов, которые были в статусе status и при этом
     * ни разу не были одном из статусов excludeStatuses
     *
     * @param status искомый статус
     * @param excludeStatuses статусы на исключение
     * @return список клиентов
     */
    @Override
    public List<ClientDto> getClientsEverBeenInStatusButExcludeStatuses(Status status, Status... excludeStatuses) {
        List<ClientDto> clients = new ArrayList<>();
        List<Tuple> tuples = entityManager.createNativeQuery(
                "SELECT DISTINCT c.client_id AS id, c.first_name AS name, c.last_name AS lastName, cp.client_phone AS phone, ce.client_email AS email FROM client_status_changing_history csch " +
                        "   RIGHT JOIN client c ON c.client_id = csch.client_id " +
                        "   LEFT JOIN client_emails ce ON ce.client_id = c.client_id AND ce.number_in_list = 0 " +
                        "   LEFT JOIN client_phones cp ON cp.client_id = c.client_id AND cp.number_in_list = 0 " +
                        "   WHERE " +
                        "       csch.is_fake = false AND " +
                        "       csch.new_status_id = :statusId AND " +
                        "       csch.client_id NOT IN ( " +
                        "           SELECT client_id FROM client_status_changing_history " +
                        "               WHERE " +
                        "                   new_status_id IN (-1" + createStringOfExcludeStatusesIds(excludeStatuses) + ") AND " +
                        "                   is_fake = false " +
                        ");", Tuple.class)
                .setParameter("statusId", status.getId())
                .getResultList();
        fillClientsFromTuples(tuples, clients);
        return clients;
    }

    /**
     * Возвращает список клиентов, которые были в статусе status в период времени beginDate - endDate и при этом
     * ни разу не были одном из статусов excludeStatuses
     *
     * @param status искомый статус
     * @param beginDate начало периода
     * @param endDate конец периода
     * @param excludeStatuses статусы на исключение
     * @return список клиентов
     */
    @Override
    public List<ClientDto> getClientsBeenInStatusAtPeriodButExcludeStatuses(Status status, ZonedDateTime beginDate, ZonedDateTime endDate, Status... excludeStatuses) {
        List<ClientDto> clients = new ArrayList<>();
        List<Tuple> tuples = entityManager.createNativeQuery(
                "SELECT DISTINCT c.client_id AS id, c.first_name AS name, c.last_name AS lastName, cp.client_phone AS phone, ce.client_email AS email FROM client_status_changing_history csch " +
                        "   RIGHT JOIN client c ON c.client_id = csch.client_id " +
                        "   LEFT JOIN client_emails ce ON ce.client_id = c.client_id AND ce.number_in_list = 0 " +
                        "   LEFT JOIN client_phones cp ON cp.client_id = c.client_id AND cp.number_in_list = 0 " +
                        "   WHERE " +
                        "       csch.date > :beginDate AND" +
                        "       csch.date < :endDate AND" +
                        "       csch.is_fake = false AND " +
                        "       csch.new_status_id = :statusId AND " +
                        "       csch.client_id NOT IN ( " +
                        "           SELECT client_id FROM client_status_changing_history " +
                        "               WHERE " +
                        "                   new_status_id IN (-1" + createStringOfExcludeStatusesIds(excludeStatuses) + ") AND " +
                        "                   is_fake = false " +
                        ");", Tuple.class)
                .setParameter("statusId", status.getId())
                .setParameter("beginDate", beginDate)
                .setParameter("endDate", endDate)
                .getResultList();
        fillClientsFromTuples(tuples, clients);
        return clients;
    }

    /**
     * Возвращает список клиентов, которые в период времени beginDate - endDate получили заданный статус status впервые,
     * при этом ни разу не были в статусах excludeStatuses
     *
     * @param status искомый статус
     * @param beginDate начало периода
     * @param endDate конец периода
     * @param excludeStatuses статусы на исключение
     * @return список клиентов
     */
    @Override
    public List<ClientDto> getClientsBeenInStatusFirstTimeAtPeriodButExcludeStatuses(Status status, ZonedDateTime beginDate, ZonedDateTime endDate, Status... excludeStatuses) {
        List<ClientDto> clients = new ArrayList<>();
        List<Tuple> tuples = entityManager.createNativeQuery(
                "SELECT DISTINCT c.client_id AS id, c.first_name AS name, c.last_name AS lastName, cp.client_phone AS phone, ce.client_email AS email FROM client_status_changing_history csch " +
                        "   RIGHT JOIN client c ON c.client_id = csch.client_id " +
                        "   LEFT JOIN client_emails ce ON ce.client_id = c.client_id AND ce.number_in_list = 0 " +
                        "   LEFT JOIN client_phones cp ON cp.client_id = c.client_id AND cp.number_in_list = 0 " +
                        "   WHERE " +
                        "       csch.date > :beginDate AND " +
                        "       csch.date < :endDate AND " +
                        "       csch.is_fake IS FALSE AND " +
                        "       csch.new_status_id = :statusId AND " +
                        "       csch.client_id NOT IN ( " +
                        "           SELECT client_id FROM client_status_changing_history " +
                        "               WHERE " +
                        "                   new_status_id = :statusId AND " +
                        "                   is_fake IS FALSE AND " +
                        "                   date < :beginDate AND " +
                        "                   client_id = csch.client_id " +
                        "       ) AND " +
                        "       csch.client_id NOT IN ( " +
                        "           SELECT client_id FROM client_status_changing_history " +
                        "               WHERE " +
                        "                   new_status_id IN (-1" + createStringOfExcludeStatusesIds(excludeStatuses) + ") AND " +
                        "                   is_fake IS FALSE AND " +
                        "                   client_id = csch.client_id " +
                        ");", Tuple.class)
                .setParameter("statusId", status.getId())
                .setParameter("beginDate", beginDate)
                .setParameter("endDate", endDate)
                .getResultList();
        fillClientsFromTuples(tuples, clients);
        return clients;
    }

    /**
     * Возвращает список клиентов, которые перешли из статуса sourceStatus в статус destinationStatus
     * в период времени beginDate - endDate и при этом ни разу не были в статусе из excludeStatuses
     *
     * @param sourceStatus исходный статус
     * @param destinationStatus статус назначения
     * @param beginDate начало периода
     * @param endDate конец периода
     * @param excludeStatuses статусы на исключение
     * @return список клиентов
     */
    @Override
    public List<ClientDto> getClientsWhoChangedStatusInPeriodButExcludeStatuses(Status sourceStatus, Status destinationStatus, ZonedDateTime beginDate, ZonedDateTime endDate, Status... excludeStatuses) {
        List<ClientDto> clients = new ArrayList<>();
        List<Tuple> tuples = entityManager.createNativeQuery(
                "SELECT c.client_id AS id, c.first_name AS name, c.last_name AS lastName, cp.client_phone AS phone, ce.client_email AS email FROM client c " +
                        "   LEFT JOIN client_emails ce ON ce.client_id = c.client_id AND ce.number_in_list = 0 " +
                        "   LEFT JOIN client_phones cp ON cp.client_id = c.client_id AND cp.number_in_list = 0 " +
                        "   WHERE c.client_id IN ( " +
                        "       SELECT DISTINCT c1.client_id FROM client_status_changing_history c1 " +
                        "          RIGHT JOIN client_status_changing_history c2 " +
                        "              ON c1.client_id = c2.client_id " +
                        "              AND c1.date > c2.date " +
                        "              AND c2.date >= :beginDate " +
                        "              AND c2.new_status_id = :sourceStatus " +
                        "              AND c2.is_fake IS FALSE " +
                        "          WHERE c1.is_fake IS FALSE " +
                        "          AND c1.date <= :endDate " +
                        "          AND c1.new_status_id = :destinationStatus " +
                        "          AND c1.client_id = c2.client_id " +
                        "          AND c1.client_id NOT IN ( " +
                        "              SELECT c3.client_id FROM client_status_changing_history c3 " +
                        "                  WHERE new_status_id IN (-1" + createStringOfExcludeStatusesIds(excludeStatuses) + ") " +
                        "                  AND c3.client_id = c1.client_id " +
                        "           )" +
                        "   );", Tuple.class)
                .setParameter("sourceStatus", sourceStatus)
                .setParameter("destinationStatus", destinationStatus)
                .setParameter("beginDate", beginDate)
                .setParameter("endDate", endDate)
                .getResultList();
        fillClientsFromTuples(tuples, clients);
        return clients;
    }

    /**
     * Устанавливает флаг is_fake всем переходам, для которых выполняется условие,
     * что клиент был перемещен в другой статус в течение minutes минут после перехода в данный
     *
     * @param minutes количество минут
     */
    @Override
    @Transactional
    public void markAllFakeStatusesByChangingInIntervalRule(int minutes) {
        try {
            int result = entityManager.createNativeQuery(
                    "UPDATE client_status_changing_history SET is_fake = TRUE WHERE id IN ( " +
                            "   SELECT cheat.id AS id FROM ( " +
                            "       SELECT DISTINCT c1.id FROM client_status_changing_history c1 " +
                            "           RIGHT JOIN client_status_changing_history c2 " +
                            "               ON c2.date > c1.date " +
                            "               AND c2.date < DATE_ADD(c1.date, INTERVAL :minutes MINUTE) " +
                            "               AND c1.client_id = c2.client_id " +
                            "               AND c2.is_fake IS FALSE " +
                            "           WHERE c1.client_id = c2.client_id " +
                            "           AND c1.is_fake IS FALSE " +
                            "   ) AS cheat " +
                            ");").setParameter("minutes", minutes)
                    .executeUpdate();
            logger.debug("markAllFakeStatusesByChangingInIntervalRule({}) marks {} fields as fake", minutes, result);
        } catch (Exception e) {
            logger.error("Exception while executing markAllFakeStatusesByChangingInIntervalRule()", e);
        }
    }

    /**
     * Устанавливает флаг is_fake всем переходам, для которых выполняется условие,
     * что клиент был возвращен в исходный статус в течение hours часов после перемещения из него
     *
     * @param hours количество часов
     */
    @Override
    @Transactional
    public void markAllFakeStatusesByReturningInIntervalRule(int hours) {
        try {
            int result = entityManager.createNativeQuery(
                    "UPDATE client_status_changing_history SET is_fake = TRUE WHERE id IN ( " +
                            "   SELECT cheat.id AS id FROM ( " +
                            "       SELECT DISTINCT c1.id FROM client_status_changing_history c1 " +
                            "           LEFT JOIN client_status_changing_history c2 " +
                            "               ON c2.client_id = c1.client_id " +
                            "               AND c2.date < c1.date " +
                            "               AND DATE_ADD(c2.date, INTERVAL :hours HOUR) > c1.date " +
                            "               AND c2.is_fake IS FALSE " +
                            "           LEFT JOIN client_status_changing_history c3 " +
                            "               ON c3.client_id = c1.client_id " +
                            "               AND c3.date > c1.date " +
                            "               AND c3.date < DATE_ADD(c1.date, INTERVAL 24 HOUR) " +
                            "               AND c3.is_fake IS FALSE " +
                            "       WHERE DATE_ADD(c2.date, INTERVAL :hours HOUR) > c3.date " +
                            "       AND c1.is_fake IS FALSE " +
                            "       AND c1.client_id = c2.client_id " +
                            "       AND c1.client_id = c3.client_id " +
                            "       AND c3.new_status_id = c2.new_status_id) AS cheat " +
                            ");").setParameter("hours", hours)
                    .executeUpdate();
            logger.debug("markAllFakeStatusesByReturningInIntervalRule({}) marks {} fields as fake", hours, result);
        } catch (Exception e) {
            logger.error("Exception while executing markAllFakeStatusesByReturningInIntervalRule()", e);
        }
    }

    /**
     * Задает ближайшей сущности ClientStatusChangingHistory ключ is_client_creation,
     * если эта запись связана с созданием клиента
     *
     * @param client клиент
     * @param date дата создания
     */
    @Override
    @Transactional
    public void setCreationInNearestStatusChangingHistoryForClient(Client client, ZonedDateTime date) {
        try {
            int result = entityManager.createNativeQuery(
                    "UPDATE client_status_changing_history " +
                            "   SET is_client_creation = TRUE " +
                            "     WHERE id IN ( " +
                            "       SELECT cheat.id AS id FROM ( " +
                            "           SELECT c.id FROM client_status_changing_history c " +
                            "               WHERE " +
                            "                   c.client_id= :clientId AND " +
                            "                   c.is_fake IS FALSE " +
                            "               ORDER BY " +
                            "                   ABS(DATEDIFF(:dateCreation , DATE_FORMAT(c.date, '%Y-%m-%d'))), " +
                            "                   ABS(TIMEDIFF(:timeCreation , DATE_FORMAT(c.date, '%H:%i:%s'))) " +
                            "               LIMIT 0, 1 " +
                            "       ) cheat " +
                            "   );")
                    .setParameter("clientId", client.getId())
                    .setParameter("dateCreation", date)
                    .setParameter("timeCreation", date.toLocalTime())
                    .executeUpdate();
            logger.debug("setCreationInNearestStatusChangingHistoryForClient({}, {}) marks {} fields as client_creation", client, date, result);
        } catch (Exception e) {
            logger.error("Exception while executing setCreationInNearestStatusChangingHistoryForClient()", e);
        }
    }

    private String createStringOfExcludeStatusesIds(Status[] excludeStatuses) {
        StringBuilder excludes = new StringBuilder();
        for (Status s :excludeStatuses) {
            excludes.append(", ").append(s.getId());
        }
        return excludes.toString();
    }

    private void fillClientsFromTuples(List<Tuple> tuples, List<ClientDto> clients) {
        for (Tuple tuple :tuples) {
            long id = ((BigInteger) tuple.get("id")).longValue();
            String name = (String) tuple.get("name");
            String lastName = (String) tuple.get("lastName");
            String phone = (String) tuple.get("phone");
            String email = (String) tuple.get("email");
            clients.add(new ClientDto(id, name, lastName, phone, email));
        }
    }

}
