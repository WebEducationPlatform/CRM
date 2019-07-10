package com.ewp.crm.repository.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Status;
import com.ewp.crm.repository.interfaces.ClientStatusChangingHistoryRepositoryCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
public class ClientStatusChangingHistoryRepositoryImpl implements ClientStatusChangingHistoryRepositoryCustom {

    private static Logger logger = LoggerFactory.getLogger(PersistentLoginRepositoryImpl.class);

    private final EntityManager entityManager;

    @Autowired
    public ClientStatusChangingHistoryRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
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
    public List<Client> getClientsEverBeenInStatusButExcludeStatuses(Status status, Status... excludeStatuses) {
        List<Client> clients = entityManager.createNativeQuery(
                "SELECT DISTINCT c.* FROM client_status_changing_history csch " +
                        "   RIGHT JOIN client c ON c.client_id = csch.client_id " +
                        "   WHERE " +
                        "       csch.is_fake = false AND " +
                        "       csch.new_status_id = :statusId AND " +
                        "       csch.client_id NOT IN ( " +
                        "           SELECT client_id FROM client_status_changing_history " +
                        "               WHERE " +
                        "                   new_status_id IN (-1" + createStringOfExcludeStatusesIds(excludeStatuses) + ") AND " +
                        "                   is_fake = false " +
                        ");", Client.class)
                .setParameter("statusId", status.getId())
                .getResultList();
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
    public List<Client> getClientsBeenInStatusAtPeriodButExcludeStatuses(Status status, ZonedDateTime beginDate, ZonedDateTime endDate, Status... excludeStatuses) {
        List<Client> clients = entityManager.createNativeQuery(
                "SELECT DISTINCT c.* FROM client_status_changing_history csch " +
                        "   RIGHT JOIN client c ON c.client_id = csch.client_id " +
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
                        ");", Client.class)
                .setParameter("statusId", status.getId())
                .setParameter("beginDate", beginDate)
                .setParameter("endDate", endDate)
                .getResultList();
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
    public List<Client> getClientsBeenInStatusFirstTimeAtPeriodButExcludeStatuses(Status status, ZonedDateTime beginDate, ZonedDateTime endDate, Status... excludeStatuses) {
        List<Client> clients = entityManager.createNativeQuery(
                "SELECT DISTINCT c.* FROM client_status_changing_history csch " +
                        "   RIGHT JOIN client c ON c.client_id = csch.client_id " +
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
                        ");", Client.class)
                .setParameter("statusId", status.getId())
                .setParameter("beginDate", beginDate)
                .setParameter("endDate", endDate)
                .getResultList();
        return clients;
    }

    /**
     * Устанавливает флаг is_fake всем переходам, для которых выполняется условие,
     * что клиент был перемещен в другой статус в течение minutes минут после перехода в данный
     *
     * @param minutes количество минут
     */
    @Override
    public void markAllFakeStatusesByChangingInIntervalRule(int minutes) {
        int result = entityManager.createNativeQuery(
                "UPDATE client_status_changing_history SET is_fake = TRUE WHERE id IN ( " +
                        "   SELECT cheat.id AS id FROM ( " +
                        "       SELECT DISTINCT c2.id FROM client_status_changing_history c1 " +
                        "           RIGHT JOIN client_status_changing_history c2 " +
                        "               ON c2.date > c1.date " +
                        "               AND c2.date < DATE_ADD(c1.date, INTERVAL :minutes MINUTE) " +
                        "               AND c1.client_id = c2.client_id " +
                        "           WHERE c1.client_id = c2.client_id AND is_fake IS FALSE " +
                        "   ) AS cheat " +
                        ");").setParameter("minutes", minutes)
                .executeUpdate();
        logger.debug("markAllFakeStatusesByChangingInIntervalRule({}) marks {} fields as fake", minutes, result);
    }

    /**
     * Устанавливает флаг is_fake всем переходам, для которых выполняется условие,
     * что клиент был возвращен в исходный статус в течение hours часов после перемещения из него
     *
     * @param hours количество часов
     */
    @Override
    public void markAllFakeStatusesByReturningInIntervalRule(int hours) {
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
    public List<Client> getClientsWhoChangedStatusInPeriodButExcludeStatuses(Status sourceStatus, Status destinationStatus, ZonedDateTime beginDate, ZonedDateTime endDate, Status... excludeStatuses) {
        List<Client> result = entityManager.createNativeQuery(
                     "SELECT * FROM client WHERE client_id IN ( " +
                        "   SELECT DISTINCT c1.client_id FROM client_status_changing_history c1 " +
                        "      RIGHT JOIN client_status_changing_history c2 " +
                        "          ON c1.client_id = c2.client_id " +
                        "          AND c1.date > c2.date " +
                        "          AND c2.date >= :beginDate " +
                        "          AND c2.new_status_id = :sourceStatus " +
                        "          AND c2.is_fake IS FALSE " +
                        "      WHERE c1.is_fake IS FALSE " +
                        "      AND c1.date <= :endDate " +
                        "      AND c1.new_status_id = :destinationStatus " +
                        "      AND c1.client_id = c2.client_id " +
                        "      AND c1.client_id NOT IN ( " +
                        "          SELECT c3.client_id FROM client_status_changing_history c3 " +
                        "              WHERE new_status_id IN (-1" + createStringOfExcludeStatusesIds(excludeStatuses) + ") " +
                        "              AND c3.client_id = c1.client_id " +
                        "       )" +
                        ");", Client.class)
                .setParameter("sourceStatus", sourceStatus)
                .setParameter("destinationStatus", destinationStatus)
                .setParameter("beginDate", beginDate)
                .setParameter("endDate", endDate)
                .getResultList();
        return result;
    }

    private String createStringOfExcludeStatusesIds(Status[] excludeStatuses) {
        StringBuilder excludes = new StringBuilder();
        for (Status s :excludeStatuses) {
            excludes.append(", ").append(s.getId());
        }
        return excludes.toString();
    }

}
