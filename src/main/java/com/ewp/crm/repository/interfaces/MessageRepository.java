package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
