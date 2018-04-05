package com.ewp.crm.service.impl;

import com.ewp.crm.models.Job;
import com.ewp.crm.repository.interfaces.JobDAO;
import com.ewp.crm.service.interfaces.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobServiceImpl implements JobService {

    private final JobDAO jobDAO;

    @Autowired
    public JobServiceImpl(JobDAO jobDAO) {
        this.jobDAO = jobDAO;
    }

    @Override
    public void add(Job job) {
        jobDAO.saveAndFlush(job);
    }

    @Override
    public void update(Job job) {
        jobDAO.saveAndFlush(job);
    }

    @Override
    public void delete(Job job) {
        jobDAO.delete(job);
    }

}
