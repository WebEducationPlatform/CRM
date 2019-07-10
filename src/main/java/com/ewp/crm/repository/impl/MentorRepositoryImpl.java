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
        return entityManager.find(Mentor.class, userId);
    }

    @Override
    public Boolean getMentorShowAllClientsById(Long userId) {
        return (Boolean) entityManager.createNativeQuery("SELECT mentor_show_only_my_clients FROM mentor WHERE user_id = " + userId).getSingleResult();
    }

    @Transactional
    @Override
    public void saveMentorShowAllFieldAndUserIdField(boolean showAll, Long userId) {
        entityManager.createNativeQuery("INSERT INTO mentor (mentor_show_only_my_clients,user_id) VALUES (" + showAll + "," + userId + ")").executeUpdate();
    }

    @Transactional
    @Override
    public void updateMentorShowAllFieldAndUserIdField(boolean showAll, Long userId) {
        entityManager.createNativeQuery("UPDATE mentor SET mentor_show_only_my_clients = " + showAll +" WHERE user_id = " + userId).executeUpdate();
    }
}