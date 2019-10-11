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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
    @PostMapping(value = "/student/addAll/{courseSetId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER','MENTOR','HR')")
    public ResponseEntity addStudentToCourseSet(@PathVariable Long courseSetId,
                                        @RequestBody String clientsId) throws IOException {

        Long[] clientIds = new ObjectMapper().readValue(clientsId, Long[].class);
        CourseSet courseSet = courseSetService.get(courseSetId);
        Course course = courseSet.getCourse();
        Set<Student> clientsFromDB = course.getStudent();
        //Получаем список студентов, а клиентов добавляем в список из БД
        Student student = null;
        List<Student> students = new ArrayList<>();
        for (Long id : clientIds) {
            student = clientService.getClientByID(id).get().getStudent();
            clientsFromDB.add(student);
            students.add(student);
        }

        course.setStudent(clientsFromDB); //Изменяем список клиентов на Направлении

        /*Т.к. связь Направление-Студенты один-ко-многим, то сначала нужно удалить из таблицы course_set_students
        запись для каждого студента, если она существует*/
        List<CourseSet> listCourseSet = courseSetService.getAll();
        for (Student stdnt : students) {
             for (CourseSet cs : listCourseSet) {
                courseSetService.removeFromSetIfContains(cs, stdnt);
            }
        }
        //Теперь можно записать Студента в Набор
        Set<Student> set = courseSet.getStudents();
        for (Student stdnt : students) {
            set.add(stdnt);
        }
        courseSet.setStudents(set);
        courseSetService.update(courseSet); //Обновляем список студентов в Наборе
        courseService.update(course); //Обновляем список клиентов на Направлении
        return new ResponseEntity(HttpStatus.OK);
    }
}
