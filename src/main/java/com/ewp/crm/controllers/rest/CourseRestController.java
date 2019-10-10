package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.Course;
import com.ewp.crm.models.CourseSet;
import com.ewp.crm.service.interfaces.CourseService;
import com.ewp.crm.service.interfaces.CourseSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rest/courses")
public class CourseRestController {
    private final CourseService courseService;
    private final CourseSetService courseSetService;

    @Autowired
    public CourseRestController(CourseService courseService, CourseSetService courseSetService) {
        this.courseService = courseService;
        this.courseSetService = courseSetService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER','MENTOR','HR')")
    public ResponseEntity<List<Course>> getCourses() {
        return ResponseEntity.ok(courseService.getAll());
    }

    @GetMapping(value = "/sets",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN', 'USER','MENTOR','HR')")
    public ResponseEntity<List<CourseSet>> getCourseSets() {
        return ResponseEntity.ok(courseSetService.getAll());
    }
}
