package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Comment;
import com.ewp.crm.models.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentDAO extends CommonGenericRepository<Comment> {

    List<Comment> getAllByClient(Client client);

    List<Comment> getAllByUser(User user);

    @Modifying
    @Transactional
//    @Query("DELETE FROM Comment c WHERE c.id IN(SELECT c.id FROM Comment c JOIN c.user cu WHERE cu.id = :userId)")
    @Query("DELETE FROM Comment c WHERE c.user.id = :userId")
    void deleteAllCommentsByUserId(@Param("userId") long id);

}
