package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Course;

import java.util.List;

public interface CourseService {
    Course add(Course entity);

    void update(Course entity);

    void delete(Long id);

    List<Course> getAll();

    Course getCourse(Long id);

    List<Course> getCoursesByClient(Client clients);
}
