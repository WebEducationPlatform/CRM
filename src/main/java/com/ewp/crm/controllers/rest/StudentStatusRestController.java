package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.StudentStatus;
import com.ewp.crm.service.interfaces.StudentStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/student/status")
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER')")
public class StudentStatusRestController {

    private final StudentStatusService studentStatusService;

    @Autowired
    public StudentStatusRestController(StudentStatusService studentStatusService) {
        this.studentStatusService = studentStatusService;
    }

    @GetMapping
    public ResponseEntity<List<StudentStatus>> getAllStudentStatuses() {
        return ResponseEntity.ok(studentStatusService.getAll());
    }

    @PostMapping ("/create")
    private HttpStatus createStudentStatus(@RequestBody StudentStatus studentStatus) {
        System.out.println("222 " + studentStatus);
        //TODO Null pointer
        studentStatusService.add(studentStatus);
        return HttpStatus.OK;
    }
}
