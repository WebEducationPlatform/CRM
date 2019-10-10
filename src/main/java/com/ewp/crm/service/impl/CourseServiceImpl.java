package com.ewp.crm.service.impl;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Course;
import com.ewp.crm.repository.interfaces.CourseRepository;
import com.ewp.crm.service.interfaces.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Override
    public List<Course> getCoursesByClient(Client client) {
        Set<Client> clients = new HashSet<>();
        clients.add(client);
        return courseRepository.getCoursesByClients(clients);
    }

}
