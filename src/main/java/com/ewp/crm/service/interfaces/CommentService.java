package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Comment;
import com.ewp.crm.models.User;

import java.util.List;

public interface CommentService extends CommonService<Comment>{

    List<Comment> getAllCommentsByClient(Client client);

    List<Comment> getAllCommentsByUser(User user);

    void deleteAllCommentsByUserId(Long id);
}
