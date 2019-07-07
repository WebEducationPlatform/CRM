package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.SocialProfile.SocialNetworkType;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserRestController {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

	@GetMapping(value = "/rest/user", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'HR')")
	public ResponseEntity<List<User>> getAll(@AuthenticationPrincipal User userFromSession) {
		List <User> users = userService.getAll();
		users.remove(userService.get(userFromSession.getId()));
		return ResponseEntity.ok(users);
	}

	@GetMapping(value = "/rest/users", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'HR')")
	public ResponseEntity<List<User>> getAllUsers() {
		List <User> users = userService.getAll();
		return ResponseEntity.ok(users);
	}

	@GetMapping(value = {"/user/socialNetworkTypes"})
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'MENTOR', 'HR')")
	public ResponseEntity<Map<Long, String>> getSocialNetworkTypes() {
		SocialProfile socialProfile = new SocialProfile();
		List<SocialNetworkType> socialNetworkTypes = socialProfile.getAllSocialNetworkTypes();
		Map<Long, String> socialTypeNames = new HashMap<>();
		for (SocialNetworkType socialNetworkType : socialNetworkTypes) {
			socialTypeNames.put(socialNetworkType.getId(), socialNetworkType.getName().toUpperCase());
		}
		return ResponseEntity.ok(socialTypeNames);
	}

	@GetMapping("rest/client/getPrincipal")
	public ResponseEntity getPrincipal(@AuthenticationPrincipal User userFromSession) {
		return ResponseEntity.ok(userFromSession);
	}

	@PostMapping(value = "/user/ColorBackground")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER','HR')")
	public ResponseEntity addColor(@RequestParam(name = "color") String color,
								   @AuthenticationPrincipal User userFromSession) {
		userService.setColorBackground(color, userFromSession);
		return ResponseEntity.ok(color);
	}

	@GetMapping(value = "/user/ColorBackground")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER','HR')")
	public ResponseEntity getColor(@AuthenticationPrincipal User userFromSession) {
		return ResponseEntity.ok(userFromSession.getColorBackground());
	}

}
