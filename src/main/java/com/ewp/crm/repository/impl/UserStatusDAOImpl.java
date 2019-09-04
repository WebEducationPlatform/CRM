package com.ewp.crm.repository.impl;

import com.ewp.crm.models.*;
import com.ewp.crm.models.dto.ClientDtoForBoard;
import com.ewp.crm.models.dto.StatusDtoForBoard;
import com.ewp.crm.models.dto.StatusPositionIdNameDTO;
import com.ewp.crm.repository.interfaces.UserStatusDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
public class UserStatusDAOImpl implements UserStatusDAO {

    @Autowired
    EntityManager entityManager;

    @Override
    public void addStatusAllUsers(Status status) {
        entityManager.createNativeQuery("insert into user_status (user_id, status_id, status_position, status_visible)" +
                "select u.user_id, :status_id, 0, false from user u")
                .setParameter("status_id", status.getId())
                .executeUpdate();
    }

    @Override
    public void addUserAllStatus(User user) {
        entityManager.createNativeQuery("insert into user_status (user_id, status_id, status_position, status_visible)" +
                "select :user_id, s.status_id, 0, false from status s")
                .setParameter("user_id", user.getId())
                .executeUpdate();
    }

    @Override
    public void deleteStatus(Long status_id) {
        entityManager.createNativeQuery("delete from user_status where status_id=:status_id")
                .setParameter("status_id", status_id)
                .executeUpdate();
    }

    @Override
    public void deleteUser(Long user_id) {
        entityManager.createNativeQuery("delete from user_status where user_id=:user_id")
                .setParameter("user_id", user_id)
                .executeUpdate();
    }

    @Override
    public void updateUserStatus(Status status, User user) {
        entityManager.createNativeQuery("update user_status set status_visible = :status_visible," +
                "status_position = :status_position where user_id=:user_id and status_id=:status_id")
                .setParameter("status_visible", status.getInvisible())
                .setParameter("status_position", status.getPosition())
                .setParameter("user_id", user.getId())
                .setParameter("status_id", status.getId())
                .executeUpdate();
    }

    @Override
    public UserStatus getStatus(Long status_id, Long user_id) {
        UserStatus userStatus = entityManager.createQuery(
                "select us from UserStatus us where us.user_id=:user_id and us.status_id=:status_id", UserStatus.class)
                .setParameter("user_id", user_id)
                .setParameter("status_id", status_id)
                .getSingleResult();
        return userStatus;
    }

    @Override
    public List<StatusPositionIdNameDTO> getAllStatusVisibleTrue(Long user_id) {
        List<StatusPositionIdNameDTO> statusPositionIdNameDTOList = new ArrayList<>();
        List<Tuple> tupleStatuses = entityManager.createNativeQuery(
                "SELECT distinct s.status_id, s.status_name, us.status_visible, s.create_student," +
                        "us.status_position, s.trial_offset, s.next_payment_offset, ss.sorting_type " +
                        "FROM status s left join sorted_statuses ss on ss.status_status_id = s.status_id  and ss.user_user_id = :user_id," +
                        "status_roles sr, user_status us, permissions ps " +
                        "where sr.status_id = s.status_id  AND sr.role_id = ps.role_id and ps.user_id = :user_id " +
                        "and us.status_id = s.status_id and us.user_id = :user_id and " +
                        "us.status_visible = 1 ORDER BY us.status_position ASC;", Tuple.class)
                .setParameter("user_id", user_id)
                .getResultList();
        for (Tuple tuple : tupleStatuses) {
            long statusId = ((BigInteger) tuple.get("status_id")).longValue();
            String statusName = tuple.get("status_name") == null ? "" : (String) tuple.get("status_name");
            boolean isVisible = (boolean) tuple.get("status_visible");
            long position = ((BigInteger) tuple.get("status_position")).longValue();
            statusPositionIdNameDTOList.add(new StatusPositionIdNameDTO(statusId, statusName, position, isVisible));
        }
        return statusPositionIdNameDTOList;
    }

    @Override
    public List<StatusDtoForBoard> getStatusesForBoard(Long user_id, List<Role> roleList) {
        List<StatusDtoForBoard> result = new ArrayList<>();
        List<Tuple> tupleStatuses = entityManager.createNativeQuery(
                "SELECT distinct s.status_id, s.status_name, us.status_visible, s.create_student," +
                        "us.status_position, s.trial_offset, s.next_payment_offset, ss.sorting_type " +
                        "FROM status s left join sorted_statuses ss on ss.status_status_id = s.status_id  and ss.user_user_id = :user_id," +
                        "status_roles sr, user_status us, permissions ps " +
                        "where sr.status_id = s.status_id  AND sr.role_id = ps.role_id and ps.user_id = :user_id " +
                        "and us.status_id = s.status_id and us.user_id = :user_id and " +
                        "us.status_visible = 1 ORDER BY us.status_position ASC;", Tuple.class)
                .setParameter("user_id", user_id)
                .getResultList();
        for (Tuple tuple : tupleStatuses) {
            long statusId = ((BigInteger) tuple.get("status_id")).longValue();
            String statusName = tuple.get("status_name") == null ? "" : (String) tuple.get("status_name");
            boolean isInvisible = (boolean) tuple.get("status_visible");
            boolean createStudent = (boolean) tuple.get("create_student");
            long position = ((BigInteger) tuple.get("status_position")).longValue();
            int trialOffset = (int) tuple.get("trial_offset");
            int nextPaymentOffset = (int) tuple.get("next_payment_offset");
            String sortingType = (String) tuple.get("sorting_type");
            List<Role> statusRoles = entityManager.createQuery(
                    "SELECT s.role FROM Status s WHERE s.id = :statusId")
                    .setParameter("statusId", statusId)
                    .getResultList();

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
                            "           GROUP BY c.client_id " +
                            sortDirection + " ;", Tuple.class)
                    .setParameter("statusId", statusId)
                    .getResultList();

            List<ClientDtoForBoard> clients = new ArrayList<>();
            for (Tuple userTuple : tupleClients) {
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
            result.add(new StatusDtoForBoard(statusId, statusName, isInvisible, createStudent, clients, position, statusRoles, trialOffset, nextPaymentOffset));
        }

        // Получаем все скрытые статусы, которые доступны для роли roleId. Эти статусы будут без клиентов внутри
        tupleStatuses = entityManager.createNativeQuery(
                "SELECT distinct s.status_id, s.status_name, us.status_visible, s.create_student," +
                        "us.status_position, s.trial_offset, s.next_payment_offset, ss.sorting_type " +
                        "FROM status s left join sorted_statuses ss on ss.status_status_id = s.status_id  and ss.user_user_id = :user_id," +
                        "status_roles sr, user_status us, permissions ps " +
                        "where sr.status_id = s.status_id  AND sr.role_id = ps.role_id and ps.user_id = :user_id " +
                        "and us.status_id = s.status_id and us.user_id = :user_id and " +
                        "us.status_visible = 0 ORDER BY us.status_position ASC;", Tuple.class)
                .setParameter("user_id", user_id)
                .getResultList();

        for (Tuple tuple : tupleStatuses) {
            long statusId = ((BigInteger) tuple.get("status_id")).longValue();
            String statusName = tuple.get("status_name") == null ? "" : (String) tuple.get("status_name");
            boolean isInvisible = (boolean) tuple.get("status_visible");
            boolean createStudent = (boolean) tuple.get("create_student");
            long position = ((BigInteger) tuple.get("status_position")).longValue();
            int trialOffset = (int) tuple.get("trial_offset");
            int nextPaymentOffset = (int) tuple.get("next_payment_offset");

            List<Role> statusRoles = entityManager.createQuery(
                    "SELECT s.role FROM Status s WHERE s.id = :statusId")
                    .setParameter("statusId", statusId)
                    .getResultList();

            result.add(new StatusDtoForBoard(statusId, statusName, isInvisible, createStudent, null, position, statusRoles, trialOffset, nextPaymentOffset));
        }

        return result;
    }

    @Override
    public List<UserStatus> getAllUserStatus() {
        List<UserStatus> userStatusList = entityManager.createQuery("select us from UserStatus us", UserStatus.class).getResultList();
        return userStatusList;
    }
}
