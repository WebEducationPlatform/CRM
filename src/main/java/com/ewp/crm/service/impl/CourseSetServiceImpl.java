package com.ewp.crm.service.impl;

import com.ewp.crm.models.CourseSet;
import com.ewp.crm.repository.interfaces.CourseSetRepository;
import com.ewp.crm.service.interfaces.CourseSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseSetServiceImpl extends CommonServiceImpl<CourseSet> implements CourseSetService {
    private final CourseSetRepository courseSetRepository;

    @Autowired
    public CourseSetServiceImpl(CourseSetRepository courseSetRepository) {
        this.courseSetRepository = courseSetRepository;
    }

    @Override
    public CourseSet get(Long id) {
        return courseSetRepository.getOne(id);
    }

    @Override
    public CourseSet add(CourseSet entity) {
        return courseSetRepository.saveAndFlush(entity);
    }

    @Override
    public List<CourseSet> getAll() {
        return courseSetRepository.findAll();
    }

    @Override
    public void update(CourseSet entity) {
        courseSetRepository.saveAndFlush(entity);
    }

    @Override
    public void delete(Long id) {
        courseSetRepository.deleteById(id);
    }

    @Override
    public void delete(CourseSet entity) {
        courseSetRepository.delete(entity);
    }
}