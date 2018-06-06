package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.CommentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentAnswerRepository extends JpaRepository<CommentAnswer, Long> {

	CommentAnswer getById(Long id);
}
