package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.FacebookMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacebookMessageDAO extends JpaRepository<FacebookMessage, Long> {
}
