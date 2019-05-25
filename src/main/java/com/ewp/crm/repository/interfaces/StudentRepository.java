package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Student;

import java.util.List;

public interface StudentRepository extends CommonGenericRepository<Student> {

    List<Student> getStudentsByStatusId(Long id);

    Student getStudentByClientId(Long clientId);

    List<Student> getStudentsByClientSocialProfiles_Empty();
}
