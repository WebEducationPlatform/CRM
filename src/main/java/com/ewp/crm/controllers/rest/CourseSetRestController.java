package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Course;
import com.ewp.crm.models.CourseSet;
import com.ewp.crm.models.Student;
import com.ewp.crm.service.interfaces.ClientService;
import com.ewp.crm.service.interfaces.CourseService;
import com.ewp.crm.service.interfaces.CourseSetService;
import com.ewp.crm.service.interfaces.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;


@RestController
@RequestMapping("/rest/courseSet")
public class CourseSetRestController {
    private final CourseService courseService;
    private final CourseSetService courseSetService;
    private final ClientService clientService;
    private final StudentService studentService;

    @Autowired
    public CourseSetRestController(CourseService courseService, CourseSetService courseSetService, ClientService clientService, StudentService studentService) {
        this.courseService = courseService;
        this.courseSetService = courseSetService;
        this.clientService = clientService;
        this.studentService = studentService;
    }

    //Запись списка студентов в Набор
    @PostMapping(value = "/student/addAll/{courseSetId}",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER','MENTOR','HR')")
    public ResponseEntity getCourseSets(@PathVariable Long courseSetId,
                                        @RequestBody String arr) throws IOException {
        Long[] clientIds = new ObjectMapper().readValue(arr, Long[].class); //Получаем список ID Клиентов
        CourseSet courseSet = null;
        Client client = null;
        Course course = null;
        Student student = null;
        for (Long id : clientIds) {
            client = clientService.getClientByID(id).get(); //Получаем Клиента
            student = client.getStudent(); //Получаем Студента
            courseSet = courseSetService.get(courseSetId); //Получаем Набор
            student.setCourseSet(courseSet); //Записываем Студента в Набор
            studentService.update(student); //Обновляем Студента
            //А клиента записываем на соответствующее Набору Направление
            course = courseService.getCourse(courseSet.getCourse().getId());
            course.setClient(client);
            courseService.update(course);
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
