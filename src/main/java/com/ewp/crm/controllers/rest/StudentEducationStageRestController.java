package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Course;
import com.ewp.crm.models.Student;
import com.ewp.crm.models.StudentEducationStage;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.CourseService;
import com.ewp.crm.service.interfaces.StudentEducationStageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/rest/student_education_stage")
public class StudentEducationStageRestController {
    private final StudentEducationStageService studentEducationStageService;
    private final CourseService courseService;
    private final ClientService clientService;

    @Autowired
    public StudentEducationStageRestController(StudentEducationStageService studentEducationStageService,
                                               CourseService courseService, ClientService clientService) {
        this.studentEducationStageService = studentEducationStageService;
        this.courseService = courseService;
        this.clientService = clientService;
    }

    @GetMapping(value = "/get/{clientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER','MENTOR','HR')")
    public ResponseEntity<List<StudentEducationStage>> getStudentEducationStage(@PathVariable("clientId") Long clientId) {
        Client client = clientService.getClientByID(clientId).get();
        Student student = client.getStudent();
        Course course = student.getCourse();
        List<StudentEducationStage> studentEducationStagesList = new LinkedList<>();
        if (course != null) {
            Set<StudentEducationStage> studentEducationStageSet = course.getStudentEducationStage();
            Integer maxLevel = -1;
            //определение максимального уровня обучения
            for (StudentEducationStage set : studentEducationStageSet) {
                if (maxLevel < set.getEducationStageLevel()) {
                    maxLevel = set.getEducationStageLevel();
                }
            }
            for (int i = 0; i <= maxLevel; i++) {
                for (StudentEducationStage set : studentEducationStageSet) {
                    if (set.getEducationStageLevel() == i) {
                        studentEducationStagesList.add(set);
                    }
                }
            }
        }
        return ResponseEntity.ok(studentEducationStagesList);
    }
}
