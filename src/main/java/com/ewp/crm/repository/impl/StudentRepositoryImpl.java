package com.ewp.crm.repository.impl;

import com.ewp.crm.models.Student;
import com.ewp.crm.repository.interfaces.StudentRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class StudentRepositoryImpl implements StudentRepositoryCustom {

    private final EntityManager entityManager;

    @Autowired
    public StudentRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Student> getStudentsWithTodayNotificationsEnabled() {
        LocalDateTime today = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrow = LocalDate.now().plusDays(1).atStartOfDay();
        return entityManager.createQuery("SELECT s FROM Student s WHERE (((s.notifyEmail = TRUE)" +
                " OR (s.notifySMS = TRUE) OR (s.notifyVK = TRUE))" +
                " AND (s.nextPaymentDate >= :today AND s.nextPaymentDate < :tomorrow))")
                .setParameter("today", today)
                .setParameter("tomorrow", tomorrow)
                .getResultList();
    }

    @Override
    public List<String> getEmailsStudentsWithOldStatus(long timeLimitInSeconds) {
        LocalDateTime dateTimeLimit = LocalDateTime.now().minusSeconds(timeLimitInSeconds);
        return entityManager.createQuery("SELECT DISTINCT cl.email FROM Student st LEFT JOIN st.client cl " +
                "WHERE ((st.statusDate < :dateTimeLimit) OR (st.statusDate IS NULL))")
                .setParameter("dateTimeLimit", dateTimeLimit)
                .getResultList();
    }

    @Override
    public void detach(Student student) {
        entityManager.detach(student);
    }
}
