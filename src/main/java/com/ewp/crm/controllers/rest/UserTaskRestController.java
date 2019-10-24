package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.User;
import com.ewp.crm.models.UserTask;
import com.ewp.crm.models.dto.UserTaskDto;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.UserService;
import com.ewp.crm.service.interfaces.UserTaskService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(value = "userTask rest controller")
public class UserTaskRestController {

    private UserService userService;
    private UserTaskService userTaskService;
    private ClientService clientService;

    @Autowired
    public UserTaskRestController(UserService userService, UserTaskService userTaskService, ClientService clientService) {
        this.userService = userService;
        this.userTaskService = userTaskService;
        this.clientService = clientService;
    }

    @RequestMapping(value = "/rest/usertask", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.PUT)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'MENTOR', 'HR')")
    public ResponseEntity<UserTask> saveNewUserTask(@RequestBody UserTaskDto userTaskDto) {
        User author = userService.get(userTaskDto.getAuthorId());
        User manager = userService.get(userTaskDto.getManagerId());
        User executor = userService.get(userTaskDto.getExecutorId());
        UserTask userTask = new UserTask(userTaskDto.getTask(), userTaskDto.getDate(), userTaskDto.getExpiry_date(),
                author, manager, executor, clientService.get(userTaskDto.getClientId()));
        userTaskService.add(userTask);
        return new ResponseEntity<>(userTask, HttpStatus.OK);

    }

    @GetMapping("/rest/usertask/{userTaskId}")
    public ResponseEntity<UserTaskDto> getUserTaskById(@PathVariable("userTaskId") Long userTaskId) {
        UserTask userTask = userTaskService.getById(userTaskId);
        if (userTask == null) {
            userTask = new UserTask();
        }
        return new ResponseEntity<>(UserTaskDto.getUserTaskDto( userTask), HttpStatus.OK);
    }
}
