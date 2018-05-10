package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Comment;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/rest/comment")
@EnableAsync
public class CommentRestController {

	private static Logger logger = LoggerFactory.getLogger(CommentRestController.class);

	private ClientService clientService;

	private CommentService commentService;

	private UserService userService;

	//TODO не используется
	private NotificationService notificationService;

	private SendNotificationService sendNotificationService;

	@Autowired
	public CommentRestController(ClientService clientService, CommentService commentService, UserService userService, NotificationService notificationService, SendNotificationService sendNotificationService) {
		this.clientService = clientService;
		this.commentService = commentService;
		this.userService = userService;
		this.notificationService = notificationService;
		this.sendNotificationService = sendNotificationService;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ResponseEntity<Comment> addComment(@RequestParam(name = "clientId") Long clientId,
	                                          @RequestParam(name = "content") String content) {
		User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Client client = clientService.getClientByID(clientId);
		if (client == null) {
			logger.error("Can`t add comment, client with id {} not found", clientId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		sendNotificationService.sendNotification(content, client);
		Comment newComment = new Comment(userFromSession, client, content);
		commentService.add(newComment);
		return ResponseEntity.status(HttpStatus.OK).body(newComment);
	}

	@RequestMapping(value = "/add/answer", method = RequestMethod.POST)
	public ResponseEntity<Comment> addAnswer(@RequestParam(name = "content") String content, @RequestParam(name = "commentId") Long commentId) {
		User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User fromDB = userService.get(userFromSession.getId());
		Comment comment = commentService.getById(commentId);
		Client client = comment.getClient();
		sendNotificationService.sendNotification(content, client);
		Comment answer = new Comment(fromDB, client, comment, content);
		commentService.add(answer);
		return ResponseEntity.status(HttpStatus.OK).body(answer);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseEntity deleteComment(@RequestParam(name = "id") Long id) {
		User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (commentService.getById(id).getUser().equals(userFromSession)) {
			commentService.delete(id);
			return ResponseEntity.ok(HttpStatus.OK);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public ResponseEntity editComment(@RequestParam(name = "id") Long id, @RequestParam(name = "content") String content) {
		User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (commentService.getById(id).getUser().equals(userFromSession)) {
			Comment comment = commentService.getById(id);
			comment.setContent(content);
			commentService.update(comment);
			return ResponseEntity.ok(HttpStatus.OK);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

}
