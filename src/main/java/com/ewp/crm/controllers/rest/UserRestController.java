package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.SocialProfileType;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.SocialProfileTypeService;
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
    private final SocialProfileTypeService socialProfileTypeService;

    @Autowired
    public UserRestController(UserService userService,
							  SocialProfileTypeService socialProfileTypeService) {
        this.userService = userService;
        this.socialProfileTypeService = socialProfileTypeService;
    }

	@GetMapping(value = "/rest/user", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity<List<User>> getAll(@AuthenticationPrincipal User userFromSession) {
		List <User> users = userService.getAll();
		users.remove(userService.get(userFromSession.getId()));
		return ResponseEntity.ok(users);
	}

	@GetMapping(value = {"/user/socialNetworkTypes"})
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity<Map<Long, String>> getSocialNetworkTypes() {
		List<SocialProfileType> socialProfileTypes = socialProfileTypeService.getAll();
		Map<Long, String> socialTypeNames = new HashMap<>();
		for (SocialProfileType socialProfileType : socialProfileTypes) {
			socialTypeNames.put(socialProfileType.getId(), socialProfileType.getName());
		}
		return ResponseEntity.ok(socialTypeNames);
	}

	@GetMapping("rest/client/getPrincipal")
	public ResponseEntity getPrincipal(@AuthenticationPrincipal User userFromSession) {
		return ResponseEntity.ok(userFromSession);
	}

	@PostMapping(value = "/user/ColorBackground")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity addColor(@RequestParam(name = "color") String color,
								   @AuthenticationPrincipal User userFromSession) {
		userService.setColorBackground(color, userFromSession);
		return ResponseEntity.ok(color);
	}

	@GetMapping(value = "/user/ColorBackground")
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
	public ResponseEntity getColor(@AuthenticationPrincipal User userFromSession) {
		return ResponseEntity.ok(userFromSession.getColorBackground());
	}

}
