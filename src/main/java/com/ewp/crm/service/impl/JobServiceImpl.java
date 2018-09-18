package com.ewp.crm.service.impl;

import com.ewp.crm.models.Job;
import com.ewp.crm.repository.interfaces.JobDAO;
import com.ewp.crm.service.interfaces.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobServiceImpl extends CommonServiceImpl<Job> implements JobService {
    private final JobDAO jobDAO;

    @Autowired
    public JobServiceImpl(JobDAO jobDAO) {
        this.jobDAO = jobDAO;
    }
}
