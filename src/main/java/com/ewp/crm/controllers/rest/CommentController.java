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

    @RequestMapping(value = "deleteComment", method = RequestMethod.POST)
    public ResponseEntity deleteComment(@RequestParam(name = "id") Long id) {
        User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (commentService.getById(id).getUser().equals(userFromSession)) {
            commentService.delete(id);
            return ResponseEntity.ok(HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @RequestMapping(value = "deleteAnswer", method = RequestMethod.POST)
    public ResponseEntity deleteAnswer(@RequestParam(name = "answerId") Long answerId,
                                       @RequestParam(name = "commentId") Long commentId) {
        User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Comment answer = commentService.getById(answerId);
        if (answer.getUser().equals(userFromSession)) {
            Comment comment = commentService.getById(commentId);
            List<Comment> answers = comment.getAnswers();
            answers.remove(commentService.getById(answerId));
            comment.setAnswers(answers);
            commentService.update(comment);
            commentService.delete(answerId);
            return ResponseEntity.ok(HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }



    @RequestMapping(value = "editComment", method = RequestMethod.POST)
    public ResponseEntity editComment(@RequestParam(name = "id") Long id,
                                      @RequestParam(name = "content") String content) {
        User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (commentService.getById(id).getUser().equals(userFromSession)) {
            Comment comment = commentService.getById(id);
            comment.setContent(content);
            commentService.update(comment);
            return ResponseEntity.ok(HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

}
