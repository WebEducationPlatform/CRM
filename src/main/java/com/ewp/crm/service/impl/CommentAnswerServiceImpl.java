package com.ewp.crm.service.impl;

import com.ewp.crm.models.CommentAnswer;
import com.ewp.crm.repository.interfaces.CommentAnswerRepository;
import com.ewp.crm.service.interfaces.CommentAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentAnswerServiceImpl implements CommentAnswerService {

	private final CommentAnswerRepository commentAnswerRepository;

	@Autowired
	public CommentAnswerServiceImpl(CommentAnswerRepository commentAnswerRepository) {
		this.commentAnswerRepository = commentAnswerRepository;
	}

	@Override
	public CommentAnswer add(CommentAnswer commentAnswer) {
		return commentAnswerRepository.saveAndFlush(commentAnswer);
	}

	@Override
	public void update(CommentAnswer commentAnswer) {
		commentAnswerRepository.saveAndFlush(commentAnswer);
	}

	@Override
	public void delete(Long id) {
		commentAnswerRepository.delete(id);
	}

	@Override
	public CommentAnswer getById(Long id) {
		return commentAnswerRepository.getById(id);
	}
}
