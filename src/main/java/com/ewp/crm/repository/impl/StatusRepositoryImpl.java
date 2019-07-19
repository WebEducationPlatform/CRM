package com.ewp.crm.repository.impl;

import com.ewp.crm.models.*;
import com.ewp.crm.models.dto.ClientDtoForBoard;
import com.ewp.crm.models.dto.StatusDtoForBoard;
import com.ewp.crm.repository.interfaces.StatusRepositoryCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StatusRepositoryImpl implements StatusRepositoryCustom {

    private static Logger logger = LoggerFactory.getLogger(StatusRepositoryImpl.class);

    @PersistenceContext
    EntityManager entityManager;

    /**
     *  Метод получает из базы все статусы, которые доступны для данного пользователя и формирует список
     *  DTO данных статусов. Также, для каждого статуса, который не скрыт для показа на доске, в DTO
     *  загружается список DTO всех клиентов, находящихся в данном статусе
     *
     * @param userId id пользователя, для которого формируюся данные
     * @param roles список всех ролей необходим для передачи в дто
     * @param roleId роль пользователя, для которой загружаем видимые для этой роли статусы
     * @return список DTO статусов со списком DTO клиентов в каждом статусе
     */
    @Override
    public List<StatusDtoForBoard> getStatusesForBoard(long userId, List<Role> roles, long roleId) {
        logger.debug("{} getStatusesForBoard({}, {}, {}) started", StatusRepositoryImpl.class.getName(), userId, roles, roleId);
        List<StatusDtoForBoard> result = new ArrayList<>();

        // Получаем все видимые статусы, которые доступны роли roleId, отсортированные по позиции
        List<Tuple> tupleStatuses = entityManager.createNativeQuery(
                "SELECT DISTINCT s.status_id AS id, s.status_name AS name, s.is_invisible, s.create_student, s.position, s.trial_offset, s.next_payment_offset," +
                        " ss.sorting_type, fs.filter_type, fs.filter_id " +
                        "   FROM status s " +
                        "   LEFT JOIN status_roles sr " +
                        "       ON sr.status_id = s.status_id " +
                        "       AND sr.role_id = :roleId " +
                        "   LEFT JOIN permissions p " +
                        "       ON p.role_id = sr.role_id " +
                        "       AND p.user_id = :userId " +
                        "   LEFT JOIN sorted_statuses ss " +
                        "       ON ss.status_status_id = s.status_id " +
                        "       AND ss.user_user_id = p.user_id " +
                        "       AND ss.user_user_id = :userId " +
                        "   LEFT JOIN filter_statuses fs " +
                        "       ON fs.status_status_id = s.status_id " +
                        "       AND fs.user_user_id = :userId " +
                        "   WHERE s.is_invisible IS FALSE " +
                        "   GROUP BY s.status_id " +
                        "   ORDER BY s.position ASC;", Tuple.class)
                .setParameter("userId", userId)
                .setParameter("roleId", roleId)
                .getResultList();

        for (Tuple tuple :tupleStatuses) {
            long statusId = ((BigInteger) tuple.get("id")).longValue();
            String statusName = tuple.get("name") == null ? "" : (String) tuple.get("name");
            boolean isInvisible = (boolean) tuple.get("is_invisible");
            boolean createStudent = (boolean) tuple.get("create_student");
            long position = ((BigInteger) tuple.get("position")).longValue();
            int trialOffset = (int) tuple.get("trial_offset");
            int nextPaymentOffset = (int) tuple.get("next_payment_offset");
            String sortingType = (String) tuple.get("sorting_type");
            String filterType = (String) tuple.get("filter_type");
            BigInteger filterId = (BigInteger) tuple.get("filter_id");
            // Задаем значения для сортировки клиентов внутри статусов
            String sortDirection = " ORDER BY c.date DESC ";
            String historyJoin = "";
            if (sortingType != null) {
                switch (SortedStatuses.SortingType.valueOf(sortingType)) {
                    case NEW_FIRST:
                        sortDirection = " ORDER BY c.date DESC ";
                        break;
                    case OLD_FIRST:
                        sortDirection = " ORDER BY c.date ASC ";
                        break;
                    case NEW_CHANGES_FIRST:
                        historyJoin =
                                "   LEFT JOIN (" +
                                        "       SELECT hc.client_id, MAX(h.date) AS date " +
                                        "           FROM history_client hc " +
                                        "               LEFT JOIN history h ON h.history_id = hc.history_id " +
                                        "               GROUP BY hc.client_id " +
                                        "   ) hist ON hist.client_id = c.client_id ";
                        sortDirection = " ORDER BY hist.date DESC ";
                        break;
                    case OLD_CHANGES_FIRST:
                        historyJoin =
                                "   LEFT JOIN (" +
                                        "       SELECT hc.client_id, MAX(h.date) AS date " +
                                        "           FROM history_client hc " +
                                        "               LEFT JOIN history h ON h.history_id = hc.history_id " +
                                        "               GROUP BY hc.client_id " +
                                        "   ) hist ON hist.client_id = c.client_id ";
                        sortDirection = " ORDER BY hist.date ASC ";
                        break;
                }
            }
            //Фильтр
            String filter = "";
            if (filterType != null) {
                switch (FilterStatuses.FilteredType.valueOf(filterType)) {
                    case BY_MENTOR:
                        filter = " AND c.owner_mentor_id =" + filterId.toString();
                        break;
                }
            }

            // Для статуса получаем всех клиентов с нужной сортировкой
            List<Tuple> tupleClients = entityManager.createNativeQuery(
                    "SELECT DISTINCT c.client_id AS id, c.first_name AS name, c.last_name, c.hide_card, ce.client_email AS email, cp.client_phone AS phone, c.skype, c.city, c.country, " +
                            "   own.user_id AS own_user_id, own.first_name AS own_first_name, own.last_name AS own_last_name, " +
                            "   men.user_id AS men_user_id, men.first_name AS men_first_name, men.last_name AS men_last_name " +
                            "       FROM client c " +
                            "           LEFT JOIN client_emails ce ON ce.client_id = c.client_id AND ce.number_in_list = 0 " +
                            "           LEFT JOIN client_phones cp ON cp.client_id = c.client_id AND cp.number_in_list = 0 " +
                            "           LEFT JOIN status_clients sc ON sc.user_id = c.client_id " +
                            "           LEFT JOIN user own ON own.user_id = c.owner_user_id " +
                            "           LEFT JOIN user men ON men.user_id = c.owner_mentor_id " +
                                        historyJoin +
                            "           WHERE sc.status_id = :statusId " +
                                         filter +
                            "           GROUP BY c.client_id " +
                                        sortDirection + " ;", Tuple.class)
                    .setParameter("statusId", statusId)
                    .getResultList();

            List<ClientDtoForBoard> clients = new ArrayList<>();
            for (Tuple userTuple :tupleClients) {
                long clientId = ((BigInteger) userTuple.get("id")).longValue();
                String clientFirstName = (String) userTuple.get("name");
                String clientLastName = (String) userTuple.get("last_name");
                boolean hideCard = (boolean) userTuple.get("hide_card");
                String clientEmail = (String) userTuple.get("email");
                String clientPhone = (String) userTuple.get("phone");
                String clientSkype = (String) userTuple.get("skype");
                String clientCity = (String) userTuple.get("city");
                String clientCountry = (String) userTuple.get("country");

                long ownUserId = userTuple.get("own_user_id") == null ? -1 : ((BigInteger) userTuple.get("own_user_id")).longValue();
                String ownFirstName = (String) userTuple.get("own_first_name");
                String ownLastName = (String) userTuple.get("own_last_name");
                User owner = ownUserId == -1 ? null : new User(ownUserId, ownFirstName, ownLastName);

                long menUserId = userTuple.get("men_user_id") == null ? -1 : ((BigInteger) userTuple.get("men_user_id")).longValue();
                String menFirstName = (String) userTuple.get("men_first_name");
                String menLastName = (String) userTuple.get("men_last_name");
                User mentor = menUserId == -1 ? null : new User(menUserId, menFirstName, menLastName);

                // Каждому клиенту получаем список его социальных сетей (используется для поиска на доске)
                List<SocialProfile> socials = entityManager.createNativeQuery(
                        "SELECT id, social_id, social_network_type " +
                                "   FROM social_network " +
                                "   WHERE id IN ( " +
                                "       SELECT social_network_id FROM client_social_network WHERE client_id = :clientId " +
                                "   );", SocialProfile.class)
                        .setParameter("clientId", clientId)
                        .getResultList();

                ClientDtoForBoard newClientDto = new ClientDtoForBoard(clientId, clientFirstName, clientLastName, owner, hideCard, clientEmail, clientPhone, clientSkype, clientCity, clientCountry, socials, mentor);
                clients.add(newClientDto);
            }

            result.add(new StatusDtoForBoard(statusId, statusName, isInvisible, createStudent, clients, position, roles, trialOffset, nextPaymentOffset));

        }

        // Получаем все скрытые статусы, которые доступны для роли roleId. Эти статусы будут без клиентов внутри
        tupleStatuses = entityManager.createNativeQuery(
                "SELECT DISTINCT s.status_id AS id, s.status_name AS name, s.is_invisible, s.create_student, s.position, s.trial_offset, s.next_payment_offset, ss.sorting_type " +
                        "   FROM status s " +
                        "   LEFT JOIN status_roles sr " +
                        "       ON sr.status_id = s.status_id " +
                        "       AND sr.role_id = :roleId " +
                        "   LEFT JOIN permissions p " +
                        "       ON p.role_id = sr.role_id " +
                        "       AND p.user_id = :userId " +
                        "   LEFT JOIN sorted_statuses ss " +
                        "       ON ss.status_status_id = s.status_id " +
                        "       AND ss.user_user_id = p.user_id " +
                        "       AND ss.user_user_id = :userId " +
                        "   WHERE s.is_invisible IS TRUE " +
                        "   GROUP BY s.status_id " +
                        "   ORDER BY s.position ASC;", Tuple.class)
                .setParameter("userId", userId)
                .setParameter("roleId", roleId)
                .getResultList();

        for (Tuple tuple :tupleStatuses) {
            long statusId = ((BigInteger) tuple.get("id")).longValue();
            String statusName = tuple.get("name") == null ? "" : (String) tuple.get("name");
            boolean isInvisible = (boolean) tuple.get("is_invisible");
            boolean createStudent = (boolean) tuple.get("create_student");
            long position = ((BigInteger) tuple.get("position")).longValue();
            int trialOffset = (int) tuple.get("trial_offset");
            int nextPaymentOffset = (int) tuple.get("next_payment_offset");

            result.add(new StatusDtoForBoard(statusId, statusName, isInvisible, createStudent, null, position, roles, trialOffset, nextPaymentOffset));
        }

        logger.debug("{} getStatusesForBoard({}, {}, {}) finished", StatusRepositoryImpl.class.getName(), userId, roles, roleId);
        return result;
    }

}
