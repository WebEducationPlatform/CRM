package com.ewp.crm.service.impl;

import com.ewp.crm.models.CommentAnswer;
import com.ewp.crm.repository.interfaces.CommentAnswerRepository;
import com.ewp.crm.service.interfaces.CommentAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentAnswerServiceImpl extends CommonServiceImpl<CommentAnswer> implements CommentAnswerService {
	private final CommentAnswerRepository commentAnswerRepository;

	@Autowired
	public CommentAnswerServiceImpl(CommentAnswerRepository commentAnswerRepository) {
		this.commentAnswerRepository = commentAnswerRepository;
	}

	@Override
    public Optional<CommentAnswer> addCommentAnswer(CommentAnswer commentAnswer){
	    return Optional.of(commentAnswerRepository.saveAndFlush(commentAnswer));
    }
}
