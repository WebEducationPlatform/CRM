package com.ewp.crm.repository.impl;

import com.ewp.crm.models.SocialProfile.SocialNetworkType;
import com.ewp.crm.models.Student;
import com.ewp.crm.models.dto.all_students_page.ClientDtoForAllStudentsPage;
import com.ewp.crm.models.dto.all_students_page.StudentDto;
import com.ewp.crm.repository.interfaces.StudentRepositoryCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StudentRepositoryImpl implements StudentRepositoryCustom {

    private static Logger logger = LoggerFactory.getLogger(StudentRepositoryImpl.class);

    private final EntityManager entityManager;

    @Autowired
    public StudentRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Student> getStudentsWithoutSocialProfileByType(List<SocialNetworkType> excludeSocialProfiles) {
        return entityManager.createQuery("SELECT s FROM Student s JOIN s.client AS c JOIN c.socialProfiles AS sp  WHERE sp.socialNetworkType NOT IN :excludes")
                .setParameter("excludes", excludeSocialProfiles)
                .getResultList();
    }

    @Override
    public List<Student> getStudentsWithTodayNotificationsEnabled() {
        LocalDateTime today = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrow = LocalDate.now().plusDays(1).atStartOfDay();
        return entityManager.createQuery("SELECT s FROM Student s WHERE (((s.notifyEmail = TRUE)" +
                " OR (s.notifySMS = TRUE) OR (s.notifyVK = TRUE) OR (s.notifySlack = TRUE))" +
                " AND (s.nextPaymentDate >= :today AND s.nextPaymentDate < :tomorrow))")
                .setParameter("today", today)
                .setParameter("tomorrow", tomorrow)
                .getResultList();
    }

    @Override
    public long countActiveByDateAndStatuses(ZonedDateTime day, List<Long> studentStatuses) {
        String query = "SELECT COUNT(*) FROM (" +
                "SELECT csch.client_id FROM client_status_changing_history csch " +
                "RIGHT JOIN status s " +
                "ON " +
                "   s.status_id = csch.new_status_id " +
                "WHERE " +
                "   csch.new_status_id IN (:statuses) AND" +
                "   csch.date <= :day AND" +
                "   csch.client_id NOT IN (" +
                "       SELECT csch.client_id FROM client_status_changing_history csch" +
                "       RIGHT JOIN status s " +
                "       ON " +
                "           s.status_id = csch.new_status_id" +
                "       LEFT JOIN (" +
                "           SELECT csch.client_id, MAX(csch.date) AS date FROM client_status_changing_history csch" +
                "           RIGHT JOIN status s " +
                "           ON " +
                "               s.status_id = csch.new_status_id" +
                "           WHERE " +
                "               csch.new_status_id IN (:statuses) AND" +
                "               csch.date <= :day" +
                "           GROUP BY csch.client_id" +
                "       ) d ON d.client_id = csch.client_id" +
                "       WHERE " +
                "           csch.date <= :day AND" +
                "           csch.date > d.date" +
                "       GROUP BY csch.client_id" +
                "   )" +
                "GROUP BY csch.client_id" +
                ") x;";
        try {
            return ((BigInteger) entityManager.createNativeQuery(query)
                    .setParameter("day", day)
                    .setParameter("statuses", studentStatuses)
                    .getSingleResult()).longValue();
        } catch (Exception e) {
            logger.error("Failed to count students by date {}", day, e);
        }
        return 0;
    }

    @Override
    public void detach(Student student) {
        entityManager.detach(student);
    }

    @Override
    @Transactional
    public void resetColors() {
        entityManager.createQuery("UPDATE Student s SET s.color = null WHERE s.color IS NOT null").executeUpdate();
    }

    @Override
    public List<StudentDto> getStudentDtoForAllStudentsPage() {

        List<StudentDto> result = new ArrayList<>();

        List<Tuple> tupleStudents = entityManager.createNativeQuery(
                "SELECT s.id AS id, s.notes AS notes, s.color AS color, s.end_trial AS trialEndDate," +
                "s.next_pay AS nextPaymentDate, s.price AS price, s.amount AS payment_amount," +
                "s.later AS payLater, s.notify_email AS notifyEmail, s.notify_sms AS notifySms," +
                "s.notify_vk AS notifyVK, s.notify_slack AS notifySlack, s.status_id AS studentStatusID," +
                "c.client_id AS client_id, c.first_name AS first_name, c.last_name AS last_name," +
                "c.phone_number AS phoneNumber, c.email AS clientEmail, " +
                "ss.status AS studentStatusName," +
                "st.status_id AS statusID , st.status_name AS statusName " +
                "FROM student s, student_status ss, client c, status st, status_clients sc " +
                "WHERE s.client_id = c.client_id " +
                        "AND s.status_id = ss.id " +
                        "AND st.status_id = sc.status_id " +
                        "AND c.client_id = sc.user_id;",

                Tuple.class).getResultList();
        /**
         * todo надо завершить выборку и проверить, как она работает!
         */

        for (Tuple tuple : tupleStudents) {
            long statusId = ((BigInteger) tuple.get("id")).longValue();
            String notes = tuple.get("notes") == null ? "" : (String) tuple.get("notes");
            String color = tuple.get("color") == null ? "" : (String) tuple.get("color");
            LocalDateTime trialEndDate = ((LocalDateTime) tuple.get("trialEndDate"));
            LocalDateTime nextPaymentDate = ((LocalDateTime) tuple.get("nextPaymentDate"));
            BigDecimal price = ((BigDecimal) tuple.get("price"));
            BigDecimal paymentAmount = ((BigDecimal) tuple.get("payment_amount"));
            BigDecimal payLater = ((BigDecimal) tuple.get("payLater"));
            boolean notifyEmail = (boolean) tuple.get("notifyEmail");
            boolean notifySms = (boolean) tuple.get("notifySms");
            boolean notifyVK = (boolean) tuple.get("notifyVK");
            boolean notifySlack = (boolean) tuple.get("notifySlack");


        }

        return result;
    }
}
