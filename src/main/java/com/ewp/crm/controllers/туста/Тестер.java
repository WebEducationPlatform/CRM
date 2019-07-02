package com.ewp.crm.controllers.туста;

import com.ewp.crm.service.interfaces.SlackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Тестер {
    @Autowired
    SlackService slackService;
    @RequestMapping("myTest")
    public ResponseEntity<String> getStr() {
    slackService.tryLinkSlackAccountToAllStudents();
    return new ResponseEntity<>("", HttpStatus.OK);

    }


}
