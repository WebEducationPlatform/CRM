package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobDAO extends JpaRepository<Job, Long> {

    List<Job> findAllByClientId(Long id);
}
