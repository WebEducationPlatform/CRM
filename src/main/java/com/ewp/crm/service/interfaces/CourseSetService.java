package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.CourseSet;
import com.ewp.crm.models.Student;

import java.util.List;
import java.util.Set;

public interface CourseSetService {

    CourseSet get(Long id);
    CourseSet add(CourseSet entity);
    List<CourseSet> getAll();
    void update(CourseSet entity);
    void delete(Long id);
    void delete(CourseSet entity);
    void removeFromSetIfContains(CourseSet courseSet, Student student);
}