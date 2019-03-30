package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.Student;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.SlackService;
import com.ewp.crm.service.interfaces.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * TODO before start.
 * Docs about slack functions installation
 * 1. Goto https://api.slack.com/apps
 * 2. Choice app for Workspace (JavaMentor or something else)
 * 3. Goto Features -> Event Subscriptions
 * 4. Turn ON "Enable Events'
 * TODO after start
 * 5. Put at "Request URL" app-IP/slack or app-URL/slack
 * 6. Wait for verify
 * 7. Add to "Add Bot User Event" event with name "member_joined_channel"
 * 8. TODO Проверить и дописать инструкцию. Продублировать.
 */

@RestController
@RequestMapping("/slack")
public class SlackRestController {

    private final SlackService slackService;
    private final ClientService clientService;
    private final StudentService studentService;

    @Autowired
    public SlackRestController(ClientService clientService, SlackService slackService, StudentService studentService) {
        this.slackService = slackService;
        this.clientService = clientService;
        this.studentService = studentService;
    }

    @GetMapping("/find/client/{clientId}")
    public ResponseEntity<String> findClientSlackProfile(@PathVariable long clientId) {
        Optional<Client> client = clientService.getClientByID(clientId);
        if (client.isPresent() && client.get().getStudent() != null) {
            return findStudentSlackProfile(client.get().getStudent().getId());
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/find/student/{studentId}")
    public ResponseEntity<String> findStudentSlackProfile(@PathVariable long studentId) {
        if (slackService.tryLinkSlackAccountToStudent(studentId)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/send/student/{studentId}")
    public ResponseEntity sendMessageToStudent(@PathVariable long studentId, @RequestParam("text") String text) {
        if (slackService.trySendSlackMessageToStudent(studentId, text)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/send/all")
    public ResponseEntity sendMessageToAllSlackUsers(@RequestParam("text") String text) {
        if (slackService.trySendMessageToAllSlackUsers(text)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/get/emails")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    public ResponseEntity<String> getAllEmailsFromSlack() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "text/plain;charset=UTF-8");
        return new ResponseEntity<>(slackService.getAllEmailsFromSlack().orElse("Error"), headers, HttpStatus.OK);
    }

    @GetMapping("/get/ids/all")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    public ResponseEntity<String> getAllIdsFromSlack() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "text/plain;charset=UTF-8");
        return new ResponseEntity<>(slackService.getAllIdsFromSlack().orElse("Error"), headers, HttpStatus.OK);
    }

    @GetMapping("/get/ids/students")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
    public ResponseEntity<String> getAllStudentsIdsFromSlack() {
        StringBuilder result = new StringBuilder();
        for (Student student :studentService.getAll()) {
            for (SocialProfile socialProfile :student.getClient().getSocialProfiles()) {
                if ("slack".equals(socialProfile.getSocialProfileType().getName())) {
                    result.append(socialProfile.getSocialId()).append("\n");
                }
            }
        }
        String returnValue = result.toString().isEmpty() ? "Error" : result.toString();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "text/plain;charset=UTF-8");
        return new ResponseEntity<>(returnValue, headers, HttpStatus.OK);
    }
}
