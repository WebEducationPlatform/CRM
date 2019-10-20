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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<UserTask> saveNewUserTask(@RequestBody UserTaskDto userTaskDto){
        User author = userService.get(userTaskDto.getAuthor_id());
        User manager = userService.get(userTaskDto.getManager_id());
         User executor = userService.get(userTaskDto.getExecutor_id());
         UserTask userTask = new UserTask(userTaskDto.getTask(),userTaskDto.getDate(),userTaskDto.getExpiry_date(),
                 author,manager,executor,clientService.get(userTaskDto.getClient_id()));
         userTaskService.add(userTask);
        return  new ResponseEntity<>(userTask, HttpStatus.OK);

    }
}
