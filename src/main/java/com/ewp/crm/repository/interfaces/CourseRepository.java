package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Course;

import java.util.List;
import java.util.Set;

public interface CourseRepository extends CommonGenericRepository<Course>, CourseRepositoryCustom {
    List<Course> getCoursesByClients(Set<Client> clients);
}
