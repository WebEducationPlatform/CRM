package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Job;

import java.util.List;

public interface JobService {

    void add(Job job);

    void update(Job job);

    void delete(Job job);

    List<Job> getJobsByClientId(Long id);

}
