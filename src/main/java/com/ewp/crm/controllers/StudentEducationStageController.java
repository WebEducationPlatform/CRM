package com.ewp.crm.controllers;

import com.ewp.crm.models.Course;
import com.ewp.crm.models.StudentEducationStage;
import com.ewp.crm.service.interfaces.CourseService;
import com.ewp.crm.service.interfaces.StudentEducationStageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'OWNEN', 'HR', 'MENTOR')")
@RequestMapping(value = "/educationstage")
public class StudentEducationStageController {

    private final StudentEducationStageService studentEducationStageService;
    private final CourseService courseService;

    @Autowired
    public StudentEducationStageController(StudentEducationStageService studentEducationStageService, CourseService courseService) {
        this.studentEducationStageService = studentEducationStageService;
        this.courseService = courseService;
    }

    @GetMapping(value = "/{courseId}")
    public ModelAndView getAllStudentEducationStageByCourse(@PathVariable("courseId") Long courseId) {
        Course course = courseService.getCourse(courseId);
        ModelAndView modelAndView = new ModelAndView("student_education_stage");
        modelAndView.addObject("student_education_stage", studentEducationStageService.getStudentEducationStageByCourse(course));
        return modelAndView;
    }

    @GetMapping(value = "/add")
    public String addStudentEducationStage(){
       return "/add";
    }
    @PostMapping (value = "/add")
    public String addStudentEducationStage(@RequestParam("studentEducationStageName") String studentEducationStageName,
                                           @PathVariable("studentEducationStageLevel") Integer studentEducationStageLevel, @RequestParam("courseId") Long courseId) {
        StudentEducationStage studentEducationStage = new StudentEducationStage();
        studentEducationStage.setEducationStageLevel(studentEducationStageLevel);
        studentEducationStage.setEducationStageName(studentEducationStageName);
        Course course = courseService.getCourse(courseId);
        studentEducationStageService.add(studentEducationStage, course);
        return "redirect:/courses";
    }

    @RequestMapping
}
