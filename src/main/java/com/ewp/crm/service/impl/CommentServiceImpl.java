package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Comment;
import com.ewp.crm.models.User;
import com.ewp.crm.repository.interfaces.CommentDAO;
import com.ewp.crm.service.interfaces.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl extends CommonServiceImpl<Comment> implements CommentService {

    private CommentDAO commentDAO;

    @Autowired
    public CommentServiceImpl(CommentDAO commentDAO) {
        this.commentDAO = commentDAO;
    }

    @Override
    public List<Comment> getAllCommentsByClient(Client client) {
        return commentDAO.getAllByClient(client);
    }

    @Override
    public List<Comment> getAllCommentsByUser(User user) {
        return commentDAO.getAllByUser(user);
    }

    @Override
    public void deleteAllCommentsByUserId(Long id) {
        commentDAO.deleteAllCommentsByUserId(id);
    }
}
