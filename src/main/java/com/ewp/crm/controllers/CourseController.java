package com.ewp.crm.controllers;

import com.ewp.crm.models.Course;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'OWNER', 'HR', 'MENTOR')")
@RequestMapping(value = "/courses")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping()
    public ModelAndView allCourses(@AuthenticationPrincipal User userFromSession) {
        ModelAndView modelAndView = new ModelAndView("courses");
        modelAndView.addObject("courses", courseService.getAll());
        return modelAndView;
    }

    @GetMapping(value = "/add")
    public ModelAndView addCourse() {
        ModelAndView modelAndView = new ModelAndView("course-add");

        return modelAndView;
    }

    @PostMapping(value = "/add")
    public String addCourse(@RequestParam("courseName") String courseName) {
        courseService.add(new Course(courseName));
        return "redirect:/courses";
    }

    @PostMapping(value = "/update")
    public String updateCourse(@RequestParam("courseId") Long id,
                               @RequestParam("courseName") String courseName) {
        Course course = courseService.getCourse(id);
        course.setName(courseName);
        courseService.update(course);
        return "redirect:/courses";
    }

    @PostMapping(value = "/delete")
    public String deleteCourse(@RequestParam("delete") Long courseId) {
        courseService.delete(courseId);
        return "redirect:/courses";
    }
}
