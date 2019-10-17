package com.ewp.crm.service.impl;

import com.ewp.crm.models.UserTask;
import com.ewp.crm.repository.interfaces.UserTaskRepository;
import com.ewp.crm.service.interfaces.UserTaskService;
import org.springframework.stereotype.Service;

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
}
