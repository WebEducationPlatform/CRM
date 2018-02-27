package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Comment;
import com.ewp.crm.models.User;
import com.ewp.crm.repository.interfaces.CommentDAO;
import com.ewp.crm.service.interfaces.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private CommentDAO commentDAO;

    @Autowired
    public CommentServiceImpl(CommentDAO commentDAO) {
        this.commentDAO = commentDAO;
    }

    @Override
    public List<Comment> getAll() {
        return commentDAO.findAll();
    }

    @Override
    public Comment getById(Long id) {
        return commentDAO.getOne(id);
    }

    @Override
    public List<Comment> getAllByClient(Client client) {
        return commentDAO.getAllByClient(client);
    }

    @Override
    public List<Comment> getAllByUser(User user) {
        return commentDAO.getAllByUser(user);
    }

    @Override
    public void add(Comment comment) {
        commentDAO.saveAndFlush(comment);
    }

    @Override
    public void update(Comment comment) {
        commentDAO.saveAndFlush(comment);
    }

    @Override
    public void delete(Long id) {
        commentDAO.delete(id);
    }
}
