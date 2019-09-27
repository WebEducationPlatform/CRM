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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/comment")
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'MENTOR', 'HR')")
public class CommentRestController {

	private static Logger logger = LoggerFactory.getLogger(CommentRestController.class);

	private ClientService clientService;
	private CommentService commentService;
	private UserService userService;
	private SendNotificationService sendNotificationService;
	private CommentAnswerService commentAnswerService;

	@Autowired
	public CommentRestController(ClientService clientService,
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
											  @AuthenticationPrincipal User userFromSession) {
		Client client = clientService.get(clientId);
		if (client == null) {
			logger.error("Can`t add comment, client with id {} not found", clientId);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		sendNotificationService.sendNotification(content, client);
		Comment newComment = new Comment(userFromSession, client, content);
		commentService.add(newComment);
		return ResponseEntity.ok(newComment);
	}

	@PostMapping(value = "/add/answer")
	public ResponseEntity<CommentAnswer> addAnswer(@RequestParam(name = "content") String content,
                                                   @RequestParam(name = "commentId") Long commentId,
												   @AuthenticationPrincipal User userFromSession) {
		User currentUser = userService.get(userFromSession.getId());
		Comment originalComment = commentService.get(commentId);
		Client client = originalComment.getClient();
		sendNotificationService.sendNotification(content, client);
		CommentAnswer commentAnswer = new CommentAnswer(currentUser, content, originalComment);
		commentAnswerService.addCommentAnswer(commentAnswer);
		return ResponseEntity.ok(commentAnswer);
	}

	@PostMapping(value = "/delete/answer")
	public ResponseEntity deleteCommentAnswer(@RequestParam(name = "id") Long id,
											  @AuthenticationPrincipal User userFromSession) {
		if (commentAnswerService.get(id).getUser().equals(userFromSession)) {
			commentAnswerService.delete(id);
			return ResponseEntity.ok(HttpStatus.OK);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@PostMapping(value = "/edit/answer")
	public ResponseEntity editCommentAnswer(@RequestParam(name = "id") Long id,
                                            @RequestParam(name = "content") String content,
											@AuthenticationPrincipal User userFromSession) {
		if (commentAnswerService.get(id).getUser().equals(userFromSession)) {
			CommentAnswer commentAnswer = commentAnswerService.get(id);
			commentAnswer.setContent(content);
			commentAnswerService.update(commentAnswer);
			return ResponseEntity.ok(HttpStatus.OK);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@PostMapping(value = "/delete")
	public ResponseEntity deleteComment(@RequestParam(name = "id") Long id,
										@AuthenticationPrincipal User userFromSession) {
		if (commentService.get(id).getUser().equals(userFromSession)) {
			commentService.delete(id);
			return ResponseEntity.ok(HttpStatus.OK);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	@PostMapping(value = "/edit")
	public ResponseEntity editComment(@RequestParam(name = "id") Long id,
                                      @RequestParam(name = "content") String content,
									  @AuthenticationPrincipal User userFromSession) {
		if (commentService.get(id).getUser().equals(userFromSession)) {
			Comment comment = commentService.get(id);
			comment.setContent(content);
			commentService.update(comment);
			return ResponseEntity.ok(HttpStatus.OK);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

}
