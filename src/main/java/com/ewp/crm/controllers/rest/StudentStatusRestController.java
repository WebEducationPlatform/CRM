package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.StudentStatus;
import com.ewp.crm.service.interfaces.StudentService;
import com.ewp.crm.service.interfaces.StudentStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/student/status")
@PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER', 'HR')")
public class StudentStatusRestController {

    private static Logger logger = LoggerFactory.getLogger(StudentStatusRestController.class);

    private final StudentStatusService studentStatusService;
    private final StudentService studentService;

    @Autowired
    public StudentStatusRestController(StudentStatusService studentStatusService, StudentService studentService) {
        this.studentStatusService = studentStatusService;
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<List<StudentStatus>> getAllStudentStatuses() {
        List<StudentStatus> studentStatuses = studentStatusService.getAll();
        if (studentStatuses == null) {
            return ResponseEntity.notFound().build();
        }
        return new ResponseEntity<>(studentStatuses, HttpStatus.OK);
    }

    @GetMapping ("/{id}")
    public ResponseEntity<StudentStatus> getStudentById(@PathVariable("id") Long id) {
        ResponseEntity result;
        StudentStatus status = studentStatusService.get(id);
        if (status != null) {
            result = ResponseEntity.ok(status);
        } else {
            logger.info("Student status with id {} not found", id);
            result = new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return result;
    }

    @GetMapping ("/delete/{id}")
    public HttpStatus deleteStudentStatus(@PathVariable("id") Long id) {
        HttpStatus result;
        if (studentService.getStudentsByStatusId(id).isEmpty()) {
            studentStatusService.delete(id);
            result = HttpStatus.OK;
        } else {
            logger.info("StudentStatus with id {} can not be deleted, because its used by Students", id);
            result = HttpStatus.CONFLICT;
        }
        return result;
    }

    @PostMapping ("/create")
    public HttpStatus createStudentStatus(@RequestBody StudentStatus studentStatus) {
        studentStatusService.add(studentStatus);
        return HttpStatus.OK;
    }

    @PostMapping ("/update")
    public HttpStatus updateStudentStatus(@RequestBody StudentStatus status) {
        studentStatusService.update(status);
        return HttpStatus.OK;
    }

}
