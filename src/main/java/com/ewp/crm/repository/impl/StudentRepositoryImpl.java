package com.ewp.crm.repository.impl;

import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.SocialProfile.SocialNetworkType;
import com.ewp.crm.models.Student;
import com.ewp.crm.repository.interfaces.StudentRepositoryCustom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    public void detach(Student student) {
        entityManager.detach(student);
    }

    @Override
    @Transactional
    public void resetColors() {
        entityManager.createQuery("UPDATE Student s SET s.color = null WHERE s.color IS NOT null").executeUpdate();
    }
}
