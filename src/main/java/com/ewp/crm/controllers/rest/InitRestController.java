package com.ewp.crm.controllers.rest;


import com.ewp.crm.models.Client;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.SlackService;
import com.ewp.crm.service.interfaces.UserService;
import com.ewp.crm.service.interfaces.VKService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/init")
public class InitRestController {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final VKService vkService;

    private final ClientService clientService;

    private final SlackService slackService;

    private final static Logger logger = LoggerFactory.getLogger(InitRestController.class);

    private Environment env;

    @Autowired
    public InitRestController(UserService userService, PasswordEncoder passwordEncoder,
                              VKService vkService, ClientService clientService,
                              SlackService slackService, Environment env) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.vkService = vkService;
        this.clientService = clientService;
        this.slackService = slackService;
        this.env = env;
    }
    @GetMapping(value = "/updatepassword")
    public ResponseEntity updateVkTrackedClub() {
        List<User> listUsers = userService.getAll();
        for (User user : listUsers) {
            String curPass = user.getPassword();
            if (!curPass.startsWith("$2a$")) {
                user.setPassword(passwordEncoder.encode(curPass));
                userService.update(user);
            }
        }
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping(value = "/updateclientfromvk")
    public ResponseEntity getVkinfoById() {
        List<Client> clients = clientService.getAll();
        for (Client client : clients) {
            vkService.fillClientFromProfileVK(client);
            clientService.updateClient(client);
            logger.info("Update client id={} from profile vk", client.getId());
        }
        return ResponseEntity.ok(env.getProperty("messaging.client.initRest.updated"));
    }

    @GetMapping(value = "/updateSlackProfiles")
    public ResponseEntity updateSlackProfiles() {
        slackService.tryLinkSlackAccountToAllStudents();
        return new ResponseEntity(HttpStatus.OK);
    }
}
