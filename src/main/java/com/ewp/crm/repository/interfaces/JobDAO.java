package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobDAO extends JpaRepository<Job, Long> {
}
