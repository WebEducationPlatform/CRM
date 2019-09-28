package com.ewp.crm.controllers.rest.api;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Comment;
import com.ewp.crm.models.CommentAnswer;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("rest/api/comment")
public class CommentApiRestController {

	private static Logger logger = LoggerFactory.getLogger(CommentApiRestController.class);

	private ClientService clientService;
	private CommentService commentService;
	private UserService userService;
	private SendNotificationService sendNotificationService;
	private CommentAnswerService commentAnswerService;

	@Autowired
	public CommentApiRestController(ClientService clientService,
									CommentService commentService,
									UserService userService,
									SendNotificationService sendNotificationService,
									CommentAnswerService commentAnswerService) {
		this.clientService = clientService;
		this.commentService = commentService;
		this.userService = userService;
		this.sendNotificationService = sendNotificationService;
		this.commentAnswerService = commentAnswerService;
	}

	@GetMapping(value = "/getComments/{clientId}")
	public ResponseEntity<List<Comment>> getComments(@PathVariable Long clientId) {
		List<Comment> comments = commentService.getAllCommentsByClient(clientService.get(clientId));
		return ResponseEntity.ok(comments);
	}

	@PostMapping(value = "/add")
	public ResponseEntity<Comment> addComment(@RequestParam(name = "clientId") Long clientId,
	                                          @RequestParam(name = "content") String content,
                                              @RequestParam(name = "email") String email) {
		Client client = clientService.get(clientId);
		if (client == null) {
			logger.error("Can`t add comment, client with id {} not found", clientId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		//added 10.09.2019
		Optional<User> optionalUser = userService.getUserByEmail(email);
		User user;
		if (optionalUser.isPresent()) {
		    user = optionalUser.get();
        } else {
            logger.error("Can`t add comment, user with email {} not found", email);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
		sendNotificationService.sendNotification(content, client);
		Comment newComment = new Comment(user, client, content);
		commentService.add(newComment);
		return ResponseEntity.ok(newComment);
	}

	@PostMapping(value = "/add/answer")
	public ResponseEntity<CommentAnswer> addAnswer(@RequestParam(name = "content") String content,
                                                   @RequestParam(name = "commentId") Long commentId,
                                                   @RequestParam(name = "email") String email
												  /* @AuthenticationPrincipal User userFromSession*/) {
        //added 10.09.2019
        Optional<User> optionalUser = userService.getUserByEmail(email);
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            logger.error("Can`t add comments answer, user with email {} not found", email);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
		//TODO по идее надо проверять и существование комментария к которому создается ответ
        Comment comment = commentService.get(commentId);
		CommentAnswer commentAnswer = new CommentAnswer(user, content, comment);
		comment.addAnswer(commentAnswer);
		return ResponseEntity.ok(commentAnswer);
	}

	@PostMapping(value = "/delete/answer/{id}")
	public ResponseEntity deleteCommentAnswer(@PathVariable Long id,
											  @RequestParam(name = "email") String email
			/* @AuthenticationPrincipal User userFromSession*/) {

		Optional<User> optionalUser = userService.getUserByEmail(email);
		User user;
		if (optionalUser.isPresent()) {
			user = optionalUser.get();
		} else {
			logger.error("Can`t delete comments answer, user with email {} not found", email);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		try {
			if (commentAnswerService.get(id).getUser().equals(user)) {
				commentAnswerService.delete(id);
				return ResponseEntity.ok(HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
		} catch (EntityNotFoundException | NullPointerException e) {
			logger.error("Can`t delete comments answer, {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@PostMapping(value = "/edit/answer/{id}")
	public ResponseEntity<CommentAnswer> editCommentAnswer(@PathVariable Long id,
														   @RequestParam(name = "content") String content,
														   @RequestParam(name = "email") String email
			/*@AuthenticationPrincipal User userFromSession*/) {

		Optional<User> optionalUser = userService.getUserByEmail(email);
		User user;
		if (optionalUser.isPresent()) {
			user = optionalUser.get();
		} else {
			logger.error("Can`t edit comments answer, user with email {} not found", email);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		try {
			if (commentAnswerService.get(id).getUser().equals(user)) {
				CommentAnswer commentAnswer = commentAnswerService.get(id);
				commentAnswer.setContent(content);
				commentAnswerService.update(commentAnswer);
				return ResponseEntity.status(HttpStatus.OK).body(commentAnswerService.get(id));
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
		} catch (EntityNotFoundException | NullPointerException e) {
			logger.error("Can`t edit comments answer, {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@PostMapping(value = "/delete/{id}")
	public ResponseEntity deleteComment(@PathVariable Long id,
										@RequestParam(name = "email") String email
			/*@AuthenticationPrincipal User userFromSession*/) {

		Optional<User> optionalUser = userService.getUserByEmail(email);
		User user;
		if (optionalUser.isPresent()) {
			user = optionalUser.get();
		} else {
			logger.error("Can`t delete comment, user with email {} not found", email);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}

		try {
			if (commentService.get(id).getUser().equals(user)) {
				commentService.delete(id);
				return ResponseEntity.ok(HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
		} catch (EntityNotFoundException | NullPointerException e) {
			logger.error("Can`t delete comments, {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@PostMapping(value = "/edit/{id}")
	public ResponseEntity<Comment> editComment(@PathVariable Long id,
											   @RequestParam(name = "content") String content,
											   @RequestParam(name = "email") String email
			/*  @AuthenticationPrincipal User userFromSession*/) {

		Optional<User> optionalUser = userService.getUserByEmail(email);
		User user;
		if (optionalUser.isPresent()) {
			user = optionalUser.get();
		} else {
			logger.error("Can`t edit comment, user with email {} not found", email);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		try {
			if (commentService.get(id).getUser().equals(user)) {
				Comment comment = commentService.get(id);
				comment.setContent(content);
				commentService.update(comment);
				return ResponseEntity.status(HttpStatus.OK).body(commentService.get(id));
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
		} catch (EntityNotFoundException | NullPointerException e) {
			logger.error("Can`t edit comments, {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

}
