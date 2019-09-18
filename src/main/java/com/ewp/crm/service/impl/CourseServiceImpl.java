package com.ewp.crm.service.impl;

import com.ewp.crm.models.Course;
import com.ewp.crm.repository.interfaces.CourseRepository;
import com.ewp.crm.service.interfaces.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl extends CommonServiceImpl<Course> implements CourseService {
    private final CourseRepository courseRepository;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public Course add(Course entity) {
        return courseRepository.saveAndFlush(entity);
    }

    @Override
    public void update(Course entity) {
        courseRepository.saveAndFlush(entity);
    }

    @Override
    public void delete(Long id) {
        courseRepository.deleteById(id);
    }

    @Override
    public List<Course> getAll() {
        return courseRepository.findAll();
    }

    @Override
    public Course getCourse(Long id) {
        return courseRepository.getOne(id);
    }
}
