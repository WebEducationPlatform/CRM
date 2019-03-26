package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.CommentAnswer;

import java.util.Optional;

public interface CommentAnswerService extends CommonService<CommentAnswer> {
	Optional<CommentAnswer> addCommentAnswer(CommentAnswer commentAnswer);
}
