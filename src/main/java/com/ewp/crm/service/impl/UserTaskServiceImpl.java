package com.ewp.crm.service.impl;

import com.ewp.crm.models.UserTask;
import com.ewp.crm.repository.interfaces.UserTaskRepository;
import com.ewp.crm.service.interfaces.UserTaskService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class UserTaskServiceImpl implements UserTaskService {
    private final UserTaskRepository userTaskRepository;

    public UserTaskServiceImpl(UserTaskRepository userTaskRepository) {
        this.userTaskRepository = userTaskRepository;
    }

    @Override
    public List<UserTask> getAll() {
        return userTaskRepository.findAll();
    }

    @Override
    public  UserTask add (UserTask userTask){
        return userTaskRepository.saveAndFlush(userTask);
    }

    @Override
    public UserTask getById(Long userTaskId) {
        return userTaskRepository.getOne(userTaskId);
    }

    @Override
    public void update(UserTask userTaskFromDb) {
     userTaskRepository.saveAndFlush(userTaskFromDb);
    }

    @Override
    public boolean delete(Long id) {
        try{
            UserTask userTaskFromDB = userTaskRepository.getOne(id);
            userTaskRepository.delete(userTaskFromDB);
            return  true;
        }
        catch (EntityNotFoundException e){
            return false;
        }

    }
}
