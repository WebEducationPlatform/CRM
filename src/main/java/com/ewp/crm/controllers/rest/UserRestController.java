package com.ewp.crm.controllers.rest;

import com.ewp.crm.configs.ImageConfig;
import com.ewp.crm.models.SocialNetworkType;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.SocialNetworkTypeService;
import com.ewp.crm.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class UserRestController {

    private static Logger logger = LoggerFactory.getLogger(ClientRestController.class);

    private final UserService userService;
    private ImageConfig imageConfig;
    private final SocialNetworkTypeService socialNetworkTypeService;

    @Autowired
    public UserRestController(UserService userService, ImageConfig imageConfig, SocialNetworkTypeService socialNetworkTypeService) {
        this.userService = userService;
        this.imageConfig = imageConfig;
        this.socialNetworkTypeService = socialNetworkTypeService;
    }

	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@RequestMapping(value = "/rest/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<User>> getAll() {
		User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List <User> users = userService.getAll();
		users.remove(userService.get(currentUser.getId()));
		return ResponseEntity.ok(users);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value = "/admin/rest/user/update", method = RequestMethod.POST)
	public ResponseEntity updateUser(@RequestBody User user) {
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Optional<String> userPhoto = Optional.ofNullable(user.getPhoto());
		Optional<String> currentPhoto = Optional.ofNullable(userService.get(user.getId()).getPhoto());
		if (currentPhoto.isPresent() && !userPhoto.isPresent()) {
			user.setPhoto(currentPhoto.get());
		}
		user.setNotifications(userService.get(user.getId()).getNotifications());
		userService.update(user);
		logger.info("{} has updated user: id {}, email {}", currentAdmin.getFullName(), user.getId(), user.getEmail());
		return ResponseEntity.ok(HttpStatus.OK);
	}

	//Workers will be deactivated, not deleted
	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value = {"/admin/rest/user/update/photo"}, method = RequestMethod.POST)
	public ResponseEntity addAvatar(@RequestParam("0") MultipartFile file, @RequestParam("id") Long id) {
		User user = userService.get(id);
		userService.addPhoto(file, user);
		return ResponseEntity.ok().body("{\"msg\":\"Сохранено\"}");
	}

	@PreAuthorize("hasAnyAuthority('ADMIN', 'USER')")
	@RequestMapping(value = {"/user/socialNetworkTypes"}, method = RequestMethod.GET)
	public ResponseEntity<Map<Long, String>> getSocialNetworkTypes() {
		List<SocialNetworkType> socialNetworkTypes = socialNetworkTypeService.getAll();
		Map<Long, String> socialTypeNames = new HashMap<>();
		for (SocialNetworkType socialNetworkType : socialNetworkTypes) {
			socialTypeNames.put(socialNetworkType.getId(), socialNetworkType.getName());
		}
		return ResponseEntity.ok(socialTypeNames);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@RequestMapping(value = "/admin/rest/user/add", method = RequestMethod.POST)
	public ResponseEntity addUser(@RequestBody User user) {
		User currentAdmin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		userService.add(user);
		logger.info("{} has added user: email {}", currentAdmin.getFullName(), user.getEmail());
		return ResponseEntity.ok().body(userService.getUserByEmail(user.getEmail()).getId());
	}

    @ResponseBody
	@PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(value = "/admin/avatar/{file}", method = RequestMethod.GET)
    public byte[] getPhoto(@PathVariable("file") String file) throws IOException {
        Path fileLocation = Paths.get(imageConfig.getPathForAvatar() + file + ".png");
        return Files.readAllBytes(fileLocation);
    }

	@GetMapping("rest/client/getPrincipal")
	public ResponseEntity getPrinciapal() {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return ResponseEntity.ok(user);
	}
}
