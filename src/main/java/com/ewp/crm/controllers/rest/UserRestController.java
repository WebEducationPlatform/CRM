package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.SocialProfile.SocialNetworkType;
import com.ewp.crm.models.User;
import com.ewp.crm.models.dto.UserDtoForBoard;
import com.ewp.crm.service.interfaces.RoleService;
import com.ewp.crm.service.interfaces.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Api(value = "user rest controller")
public class UserRestController {

    private final UserService userService;
	private final RoleService roleService;

    @Autowired
    public UserRestController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

	@GetMapping(value = "/rest/user", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'HR')")
//	@ApiOperation(value=" show all consumers")
	public ResponseEntity<List<User>> getAll(@AuthenticationPrincipal User userFromSession) {
		List <User> users = userService.getAll();
		users.remove(userService.get(userFromSession.getId())); //Список всех, кроме текущего!
		return ResponseEntity.ok(users);
	}

	@GetMapping(value = "/rest/user/isverified", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'HR')")
	@ApiOperation(value=" show all verified users")
	public ResponseEntity<List<User>> getAllVerified() {
		List <User> userList = userService.getAll();
		return ResponseEntity.ok(userList.stream().filter(User::isVerified).collect(Collectors.toList()));
	}

	@GetMapping(value = "/rest/user/unverified", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'HR')")
	@ApiOperation(value=" show all unverified users")
	public ResponseEntity<List<User>> getAllUnverified() {
		List <User> userList = userService.getAll();
		return ResponseEntity.ok(userList.stream().filter(x -> !x.isVerified()).collect(Collectors.toList()));
	}

	@GetMapping(value = "/rest/users", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'HR')")
	@ApiOperation(value = "show all users")
	public ResponseEntity<List<User>> getAllUsers() {
		List <User> users = userService.getAll();
		return ResponseEntity.ok(users);
	}

	@GetMapping(value = "/rest/user/usersWithoutMentors", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'HR')")
	@ApiOperation(value = "show all users without mentor")
	public ResponseEntity<List<UserDtoForBoard>> getAllWithoutMentors() {
		Optional<List<UserDtoForBoard>> userList = userService.getAllWithoutMentorsForDto();
		if (!userList.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(userList.get());
	}

	@GetMapping(value = "/rest/user/mentors", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'HR')")
	@ApiOperation(value = "show all mentors")
	public ResponseEntity<List<UserDtoForBoard>> getAllMentors() {
		Optional<List<UserDtoForBoard>> userList = userService.getAllMentorsForDto();
		if (!userList.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(userList.get());
	}

	@GetMapping(value = {"/user/socialNetworkTypes"})
	@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'MENTOR', 'HR')")
	@ApiOperation(value = "show all socialNetworkTypes")
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
