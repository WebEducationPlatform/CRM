package com.ewp.crm.controllers.rest.admin;

import com.ewp.crm.configs.ImageConfig;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.CommentService;
import com.ewp.crm.service.interfaces.SMSInfoService;
import com.ewp.crm.service.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'HR')")
public class AdminRestUserController {

    private static Logger logger = LoggerFactory.getLogger(AdminRestUserController.class);

    private final UserService userService;
    private final ImageConfig imageConfig;
    private final ClientService clientService;
    private final SMSInfoService smsInfoService;
    private final CommentService commentService;

    @Autowired
    public AdminRestUserController(UserService userService,
                                   ImageConfig imageConfig,
                                   ClientService clientService, SMSInfoService smsInfoService, CommentService commentService) {
        this.userService = userService;
        this.imageConfig = imageConfig;
        this.clientService = clientService;
        this.smsInfoService = smsInfoService;
        this.commentService = commentService;
    }

    @ResponseBody
    @GetMapping(value = "/admin/avatar/{file}")
    public byte[] getPhoto(@PathVariable("file") String file) throws IOException {
        Path fileLocation = Paths.get(imageConfig.getPathForAvatar() + file);
        return Files.readAllBytes(fileLocation);
    }

    @PostMapping(value = "/admin/rest/user/update")
    public ResponseEntity updateUser(@Valid @RequestBody User user,
                                     @AuthenticationPrincipal User currentAdmin) {
        Optional<String> userPhoto = Optional.ofNullable(user.getPhoto());
        Optional<String> currentPhoto = Optional.ofNullable(userService.get(user.getId()).getPhoto());
        if (currentPhoto.isPresent() && !userPhoto.isPresent()) {
            user.setPhoto(currentPhoto.get());
        }
        userService.update(user);
        logger.info("{} has updated user: id {}, email {}", currentAdmin.getFullName(), user.getId(), user.getEmail());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping(value = {"/admin/rest/user/update/photo"})
    public ResponseEntity addAvatar(@RequestParam("0") MultipartFile file,
                                    @RequestParam("id") Long id) {
        User user = userService.get(id);
        userService.addPhoto(file, user);
        return ResponseEntity.ok().body("{\"msg\":\"Сохранено\"}");
    }

    @PostMapping(value = "/admin/rest/user/filters")
    public HttpStatus setFiltersForAllStudents(@RequestParam("filters") String filters, @AuthenticationPrincipal User currentAdmin) {
        User user = userService.get(currentAdmin.getId());
        user.setStudentPageFilters(filters);
        currentAdmin.setStudentPageFilters(filters);
        userService.update(user);
        return HttpStatus.OK;
    }

    @PostMapping(value = "/admin/rest/user/add")
    public ResponseEntity addUser(@Valid @RequestBody User user,
                                  @AuthenticationPrincipal User currentAdmin) {
        ResponseEntity result;
        if (!userService.getUserByEmail(user.getEmail()).isPresent()) {
            user.setEnabled(true);
            userService.add(user);
            result = new ResponseEntity(user, HttpStatus.OK);
            logger.info("{} has added user: email {}", currentAdmin.getFullName(), user.getEmail());
        } else {
            result = new ResponseEntity(HttpStatus.CONFLICT);
            logger.info("{} user with that email already exists: email {}", currentAdmin.getFullName(), user.getEmail());
        }
        return result;
    }

    //Workers will be deactivated, not deleted
    @PostMapping(value = "/admin/rest/user/reaviable")
    public ResponseEntity reaviableUser(@RequestParam Long deleteId,
                                        @AuthenticationPrincipal User currentAdmin) {
        User currentUser = userService.get(deleteId);
        currentUser.setEnabled(!currentUser.isEnabled());
        userService.update(currentUser);
        logger.info("{} has reavailable user: id {}, email {}", currentAdmin.getFullName(), currentUser.getId(), currentUser.getEmail());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    // Delete user with clients transfer to receiver user
    @RequestMapping(value = "/admin/rest/user/deleteWithTransfer", method = RequestMethod.POST)
    public ResponseEntity deleteUserWithClientTransfer(@RequestParam Long deleteId,
                                                       @RequestParam Long receiverId,
                                                       @AuthenticationPrincipal User currentAdmin) {
        User deletedUser = Optional.of(userService.get(deleteId))
                                    .orElseThrow(() -> new IllegalArgumentException("Wrong delete user id!"));
        User receiver = Optional.of(userService.get(receiverId))
                                .orElseThrow(() -> new IllegalArgumentException("Wrong receiver user id!"));
        clientService.transferClientsBetweenOwners(deletedUser, receiver);
        commentService.deleteAllCommentsByUserId(deleteId);
        smsInfoService.deleteAllSMSByUserId(deleteId);
        userService.delete(deleteId);
        logger.info("{} has deleted user: id {}, email {}", currentAdmin.getFullName(), deletedUser.getId(), deletedUser.getEmail());
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping(value = "/admin/rest/user/delete")
    public ResponseEntity deleteNewUser(@RequestParam Long deleteId,
                                        @AuthenticationPrincipal User currentAdmin) {
        User currentUser = userService.get(deleteId);
        userService.delete(deleteId);
        logger.info("{} has deleted user: id {}, email {}", currentAdmin.getFullName(), currentUser.getId(), currentUser.getEmail());
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
