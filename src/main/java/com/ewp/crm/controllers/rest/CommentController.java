package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Comment;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.CommentService;
import com.ewp.crm.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/rest/comment")
public class CommentController {

    private ClientService clientService;

    private CommentService commentService;

    private UserService userService;

    @Autowired
    public CommentController(ClientService clientService, CommentService commentService, UserService userService) {
        this.clientService = clientService;
        this.commentService = commentService;
        this.userService = userService;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public ResponseEntity<Comment> addComment(@RequestParam(name = "clientId") Long clientId,
                                     @RequestParam(name = "content") String content) {
        User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userFromSession != null) {
            User fromDB = userService.get(userFromSession.getId());
            Client client = clientService.getClientByID(clientId);
            Comment newComment = new Comment(fromDB, client, content);
            commentService.add(newComment);
            return ResponseEntity.status(HttpStatus.OK).body(newComment);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @RequestMapping(value = "addAnswer", method = RequestMethod.POST)
    public ResponseEntity<Comment> addAnswer(@RequestParam(name = "content") String content,
                                             @RequestParam(name = "commentId") Long commentId){
        User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userFromSession != null) {
            User fromDB = userService.get(userFromSession.getId());
            Comment comment = commentService.getById(commentId);
            Comment answer = new Comment(fromDB, null, content, true);
            List<Comment> answers = comment.getAnswers();
            answers.add(answer);
            commentService.update(comment);
            return ResponseEntity.status(HttpStatus.OK).body(answer);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

}
