package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Comment;
import com.ewp.crm.models.User;

import java.util.List;

public interface CommentDAO extends CommonGenericRepository<Comment> {
    List<Comment> getAllByClient(Client client);
    List<Comment> getAllByUser(User user);
}
