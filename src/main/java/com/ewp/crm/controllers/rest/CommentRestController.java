package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Comment;
import com.ewp.crm.models.Notification;
import com.ewp.crm.models.User;
import com.ewp.crm.service.email.MailNotificationService;
import com.ewp.crm.service.interfaces.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/comment")
@EnableAsync
public class CommentRestController {

	private ClientService clientService;

	private CommentService commentService;

	private UserService userService;


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
		if (userFromSession != null) {
			User fromDB = userService.get(userFromSession.getId());
			Client client = clientService.getClientByID(clientId);
			sendNotificationService.sendNotification(content, client);
			Comment newComment = new Comment(fromDB, client, content);
			commentService.add(newComment);
			return ResponseEntity.status(HttpStatus.OK).body(newComment);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
	}

	@RequestMapping(value = "/markAsRead", method = RequestMethod.POST)
	public ResponseEntity markAsRead(@RequestParam(name = "id") Long id) {
		User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Client client = clientService.getClientByID(id);
		notificationService.deleteNotificationsByClientAndUserToNotify(client, userFromSession);
		return ResponseEntity.ok(HttpStatus.OK);
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
