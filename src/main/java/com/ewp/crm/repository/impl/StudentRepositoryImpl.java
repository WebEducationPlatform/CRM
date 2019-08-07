package com.ewp.crm.repository.impl;

import com.ewp.crm.models.SocialProfile.SocialNetworkType;
import com.ewp.crm.models.Student;
import com.ewp.crm.repository.interfaces.StudentRepositoryCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
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
    public List<Student> getStudentsWithTodayTrialNotificationsEnabled() {
        // Получаем всех студентов, у которых статус клиента = 3 (На пробных), включен любой способ нотификации
        // и дата удовлетворяет заданному условию
        return entityManager.createNativeQuery(
                "SELECT * FROM student s WHERE (((s.notify_email = '1') OR (s.notify_sms = '1') OR " +
                        "(s.notify_vk = '1') OR (s.notify_slack = '1')) AND " +
                        "(s.end_trial >= CURDATE() AND s.end_trial < CURDATE() + INTERVAL 1 DAY)) AND " +
                        "s.client_id IN (SELECT sc.user_id FROM status_clients sc WHERE sc.status_id = '3');", Student.class)
                .getResultList();
    }

    @Override
    public long countActiveByDateAndStatuses(ZonedDateTime day, List<Long> studentStatuses) {
        String query = "SELECT COUNT(*) FROM (" +
                "SELECT DISTINCT csch.client_id FROM client_status_changing_history csch" +
                "   RIGHT JOIN status s " +
                "       ON " +
                "           s.status_id = csch.new_status_id AND " +
                "           s.create_student IS TRUE" +
                "       WHERE " +
                "       s.create_student IS TRUE AND" +
                "       csch.new_status_id IN (:statuses) AND" +
                "       csch.date <= :day AND" +
                "           csch.client_id NOT IN (" +
                "           SELECT csch.client_id FROM client_status_changing_history csch" +
                "           RIGHT JOIN status s " +
                "               ON " +
                "                   s.status_id = csch.new_status_id AND " +
                "                   s.create_student IS FALSE" +
                "           LEFT JOIN (" +
                "               SELECT csch.client_id, MAX(csch.date) AS date FROM client_status_changing_history csch" +
                "               RIGHT JOIN status s " +
                "                   ON " +
                "                       s.status_id = csch.new_status_id AND " +
                "                       s.create_student IS TRUE" +
                "               WHERE " +
                "                   s.create_student IS TRUE AND" +
                "                   csch.new_status_id IN (:statuses) AND" +
                "                   csch.date <= :day" +
                "               GROUP BY csch.client_id" +
                "               ORDER BY csch.date DESC" +
                "               ) d ON d.client_id = csch.client_id" +
                "           WHERE " +
                "               s.create_student IS FALSE AND" +
                "               csch.date <= :day AND" +
                "               csch.date > d.date" +
                "           GROUP BY csch.client_id" +
                "           ORDER BY csch.date DESC" +
                "           )" +
                "   GROUP BY csch.client_id" +
                "   ORDER BY csch.date DESC" +
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
}
