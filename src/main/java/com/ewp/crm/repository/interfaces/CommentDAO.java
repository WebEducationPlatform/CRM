package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Comment;
import com.ewp.crm.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentDAO extends JpaRepository<Comment, Long> {

    List<Comment> getAllByClient(Client client);

    List<Comment> getAllByUser(User user);
}
