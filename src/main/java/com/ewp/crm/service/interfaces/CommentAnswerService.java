package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.CommentAnswer;

public interface CommentAnswerService extends CommonService<CommentAnswer> {

    CommentAnswer addCommentAnswer(CommentAnswer commentAnswer);

}
