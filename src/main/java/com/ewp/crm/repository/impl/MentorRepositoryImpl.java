package com.ewp.crm.repository.impl;

import com.ewp.crm.models.Mentor;
import com.ewp.crm.repository.interfaces.MentorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

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
        try {
            return (Boolean) entityManager.createNativeQuery("SELECT mentor_show_only_my_clients FROM mentor WHERE user_id = " + userId).getSingleResult();
        } catch (NoResultException e) {
            return false;
        }
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
    @Override
    @Transactional
    public int getQuantityStudentsByMentorId(long id){
        Query query = entityManager.createNativeQuery("SELECT quantity_students from mentor m WHERE user_id = :id");
        query.setParameter("id", id);
        if (query.getResultList().isEmpty()){
            updateUserAsMentorWithDefaultValues(id);
            return 3;
        }
        return ((Number)query.getSingleResult()).intValue();
    }
    @Override
    @Transactional
    public void updateQuantityStudentsByMentorId(long id, int quantityStudents){
        Query updateQuery = entityManager.createNativeQuery("UPDATE mentor SET quantity_students = :quantityStudents WHERE user_id = :id");
        updateQuery.setParameter("quantityStudents",quantityStudents);
        updateQuery.setParameter("id", id);
        updateQuery.executeUpdate();
    }

    @Override
    @Transactional
    public void updateUserAsMentorWithQuantityStudents(long id, int quantityStudents){
        Query updateQuery = entityManager.createNativeQuery("INSERT INTO mentor (quantity_students,user_id) VALUES (:quantityStudents, :id)");
        updateQuery.setParameter("quantityStudents",quantityStudents);
        updateQuery.setParameter("id", id);
        updateQuery.executeUpdate();
    }
    
    @Override
    @Transactional
    public void updateUserAsMentorWithDefaultValues(long id){
        Query updateQuery = entityManager.createNativeQuery("INSERT INTO mentor (user_id) VALUES (:id)");
        updateQuery.setParameter("id", id);
        updateQuery.executeUpdate();
    }
}