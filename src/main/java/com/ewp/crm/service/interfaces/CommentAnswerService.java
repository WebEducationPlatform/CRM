package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.CommentAnswer;

public interface CommentAnswerService {

	CommentAnswer add(CommentAnswer commentAnswer);

	void update(CommentAnswer commentAnswer);

	void delete(Long id);

	CommentAnswer getById(Long id);
}
