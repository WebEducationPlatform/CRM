package com.ewp.crm.controllers.rest;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/comment")
public class CommentRestController {

	private static Logger logger = LoggerFactory.getLogger(CommentRestController.class);

	private ClientService clientService;

	private CommentService commentService;

	private UserService userService;

	private SendNotificationService sendNotificationService;

	private CommentAnswerService commentAnswerService;

	@Autowired
	public CommentRestController(ClientService clientService, CommentService commentService, UserService userService, SendNotificationService sendNotificationService, CommentAnswerService commentAnswerService) {
		this.clientService = clientService;
		this.commentService = commentService;
		this.userService = userService;
		this.sendNotificationService = sendNotificationService;
		this.commentAnswerService = commentAnswerService;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity<Comment> addComment(@RequestParam(name = "clientId") Long clientId,
	                                          @RequestParam(name = "content") String content) {
		User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Client client = clientService.get(clientId);
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
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity<CommentAnswer> addAnswer(@RequestParam(name = "content") String content, @RequestParam(name = "commentId") Long commentId) {
		User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User fromDB = userService.get(userFromSession.getId());
		Comment comment = commentService.get(commentId);
		Client client = comment.getClient();
		sendNotificationService.sendNotification(content, client);
		CommentAnswer commentAnswer = new CommentAnswer(fromDB, content, client);
		CommentAnswer answer = commentAnswerService.addCommentAnswer(commentAnswer);
		comment.addAnswer(answer);
		commentService.update(comment);
		return ResponseEntity.status(HttpStatus.OK).body(answer);
	}

	@RequestMapping(value = "/delete/answer", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity deleteCommentAnswer(@RequestParam(name = "id") Long id) {
		User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (commentAnswerService.get(id).getUser().equals(userFromSession)) {
			commentAnswerService.delete(id);
			return ResponseEntity.ok(HttpStatus.OK);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@RequestMapping(value = "/edit/answer", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity editCommentAnswer(@RequestParam(name = "id") Long id, @RequestParam(name = "content") String content) {
		User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (commentAnswerService.get(id).getUser().equals(userFromSession)) {
			CommentAnswer commentAnswer = commentAnswerService.get(id);
			commentAnswer.setContent(content);
			commentAnswerService.update(commentAnswer);
			return ResponseEntity.ok(HttpStatus.OK);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseEntity deleteComment(@RequestParam(name = "id") Long id) {
		User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (commentService.get(id).getUser().equals(userFromSession)) {
			commentService.delete(id);
			return ResponseEntity.ok(HttpStatus.OK);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity editComment(@RequestParam(name = "id") Long id, @RequestParam(name = "content") String content) {
		User userFromSession = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (commentService.get(id).getUser().equals(userFromSession)) {
			Comment comment = commentService.get(id);
			comment.setContent(content);
			commentService.update(comment);
			return ResponseEntity.ok(HttpStatus.OK);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	@RequestMapping(value = "/getComments/{clientId}", method = RequestMethod.GET)
	public ResponseEntity<List<Comment>> getComments(@PathVariable Long clientId) {
		List<Comment> comments = commentService.getAllByClient(clientService.get(clientId));
		return ResponseEntity.ok(comments);
	}

}
