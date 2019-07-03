package com.ewp.crm.repository.impl;

import com.ewp.crm.models.Mentor;
import com.ewp.crm.repository.interfaces.MentorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Repository
public class MentorRepositoryImpl implements MentorRepository {

    EntityManager entityManager;

    @Autowired
    public MentorRepositoryImpl(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    @Override
    public Mentor getMentorById(Long userId) {
        return entityManager.find(Mentor.class, new Long(userId));
    }

    @Transactional
    @Override
    public void save(boolean showAll, Long userId) {
        entityManager.createNativeQuery("INSERT INTO mentor (mentor_show_only_my_clients,user_id) VALUES (" + showAll + "," + userId + ")").executeUpdate();
    }

    @Transactional
    @Override
    public void update(boolean showAll, Long userId) {
        entityManager.createNativeQuery("UPDATE mentor SET mentor_show_only_my_clients = " + showAll +" WHERE user_id = " + userId).executeUpdate();
    }
}