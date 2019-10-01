package com.ewp.crm.controllers;

import com.ewp.crm.models.Course;
import com.ewp.crm.models.CourseSet;
import com.ewp.crm.models.User;
import com.ewp.crm.service.interfaces.CourseService;
import com.ewp.crm.service.interfaces.CourseSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;

@Controller
@PreAuthorize("hasAnyAuthority('ADMIN', 'USER', 'OWNER', 'HR', 'MENTOR')")
@RequestMapping(value = "/courses")
public class CourseController {

    private final CourseService courseService;
    private final CourseSetService courseSetService;

    @Autowired
    public CourseController(CourseService courseService, CourseSetService courseSetService) {
        this.courseService = courseService;
        this.courseSetService = courseSetService;
    }

    //Возвращает страницу с направлениями
    @GetMapping()
    public ModelAndView allCourses(@AuthenticationPrincipal User userFromSession) {
        ModelAndView modelAndView = new ModelAndView("courses");
        modelAndView.addObject("courses", courseService.getAll());
        return modelAndView;
    }

    //Возвращает страницу добавления направления
    @GetMapping(value = "/add")
    public ModelAndView addCourse() {
        ModelAndView modelAndView = new ModelAndView("course-add");
        return modelAndView;
    }

    //Обработка формы добавления нового направления
    @PostMapping(value = "/add")
    public String addCourse(@RequestParam("courseName") String courseName) {
        courseService.add(new Course(courseName));
        return "redirect:/courses";
    }

    //Обработка формы обновления направления
    @PostMapping(value = "/update")
    public String updateCourse(@RequestParam("courseId") Long id,
                               @RequestParam("courseName") String courseName) {
        Course course = courseService.getCourse(id);
        course.setName(courseName);
        courseService.update(course);
        return "redirect:/courses";
    }

    //Обработка формы удаления направления
    @PostMapping(value = "/delete")
    public String deleteCourse(@RequestParam("delete") Long courseId) {
        courseService.delete(courseId);
        return "redirect:/courses";
    }

    //Страница с наборами курса
    @GetMapping(value = "/set")
    public ModelAndView courseSets(@RequestParam Long id,
                                   @AuthenticationPrincipal User userFromSession) {
        ModelAndView modelAndView = new ModelAndView("course-sets");
        modelAndView.addObject("courseSets", courseService.getCourse(id).getCourseSets());
        modelAndView.addObject("course", courseService.getCourse(id));
        return modelAndView;
    }

    //Страница добавления нового набора
    @GetMapping(value = "/set/add")
    public ModelAndView courseSetAdd(@RequestParam Long id,
                                   @AuthenticationPrincipal User userFromSession) {
        ModelAndView modelAndView = new ModelAndView("course-sets-add");
        modelAndView.addObject("courseSets", courseService.getCourse(id).getCourseSets());
        modelAndView.addObject("course", courseService.getCourse(id));
        return modelAndView;
    }

    //Обработка формы добавления нового набора
    @PostMapping(value = "/set/add")
    public String courseSetAddPost(@RequestParam Long courseId,
                                   @RequestParam String courseSetName,
                                   @RequestParam String courseSetStartDate) {
        CourseSet courseSet = new CourseSet(courseSetName, LocalDate.parse(courseSetStartDate), courseService.getCourse(courseId));
        courseSetService.add(courseSet);
        return "redirect:/courses/set?id="+courseId;
    }

    //Обработка формы обновления набора
    @PostMapping(value = "/set/update")
    public String courseSetUpdatePost(@RequestParam("courseId") Long courseId,
                                   @RequestParam("courseSetName") String courseSetName,
                                   @RequestParam("courseSetStartDate") String courseSetStartDate,
                                   @RequestParam("update") Long courseSetId  ) {
        CourseSet courseSet = courseSetService.get(courseSetId);
        courseSet.setName(courseSetName);
        courseSet.setStartDate(LocalDate.parse(courseSetStartDate));
        courseSetService.update(courseSet);
        return "redirect:/courses/set?id="+courseId;
    }

    //Обработка формы удаления набора
    @PostMapping(value = "/set/delete")
    public String courseSetDeletePost(@RequestParam("courseId") Long courseId,
                                      @RequestParam("delete") Long courseSetId) {
        courseSetService.delete(courseSetId);
        return "redirect:/courses/set?id="+courseId;
    }
}
