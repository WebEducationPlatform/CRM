package com.ewp.crm.controllers.rest;


import com.ewp.crm.models.Client;
import com.ewp.crm.models.Student;
import com.ewp.crm.models.StudentStatus;
import com.ewp.crm.models.User;
import com.ewp.crm.models.dto.StudentProgressInfo;
import com.ewp.crm.service.interfaces.*;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/init")
public class InitRestController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EwpInfoService ewpInfoService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private StudentStatusService studentStatusService;

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


//    @Value("2")
//    private int REQUEST_PORTION;
//
//    @GetMapping(value = "/checkstatuses")
//    public ResponseEntity getStatusesFromEwp() {
//
//        List<Student> listStudent = studentService.getStudentsWithOldStatus();
//
//        for (int i = 0; i <= listStudent.size() % REQUEST_PORTION; i++) {
//            List<Student> portionListStudent = listStudent.subList(
//                    REQUEST_PORTION * i, Math.min(REQUEST_PORTION * (i + 1),listStudent.size()));
//
//            List<StudentProgressInfo> listStudentProgressInfo = ewpInfoService.getStudentProgressInfo(portionListStudent);
//
//            for (StudentProgressInfo info : listStudentProgressInfo) {
//                String studentStatusNewName =
//                        info.getCourse()
//                        + " - "
//                        + info.getModule()
//                        + " - "
//                        + info.getChapter();
//
//                StudentStatus studentStatusNew = null;
//
//                Optional<StudentStatus> studentStatusOptional = Optional.ofNullable(studentStatusService.getStudentStatusByName(studentStatusNewName));
//                if (!studentStatusOptional.isPresent()) {
//                    studentStatusNew = studentStatusService.add(new StudentStatus(studentStatusNewName));
//                } else {
//                    studentStatusNew = studentStatusOptional.get();
//                }
//
//                Client client = clientService.getClientByEmail(info.getEmail());
//
//                Student student = null;
//                Optional<Client> optionalClient = Optional.ofNullable(clientService.getClientByEmail(info.getEmail()));
//                if (optionalClient.isPresent()) {
//                    Optional<Student> studentOptional = Optional.ofNullable(studentService.getStudentByClient(optionalClient.get()));
//                    if (studentOptional.isPresent()) {
//                        student = studentOptional.get();
//                    }
//                }
//                if (Optional.ofNullable(student).isPresent() && student.getStatus() != studentStatusNew) {
//                    student.setStatus(studentStatusNew);
//                    student.setStatusDate(LocalDateTime.now());
//                    studentService.update(student);
//                }
//            }
//        }
//        return ResponseEntity.status(HttpStatus.OK).body("Statuses are received");
//
////        listStudent.add(new Student());
////        try {
////            List<Student> response = ewpInfoService.getStudentStatuses(listStudent);
////            return ResponseEntity.status(HttpStatus.OK).body("Statuses are received");
////        }
////        catch (JSONException ex) {
////            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Statuses are not received");
////        }
//    }

}