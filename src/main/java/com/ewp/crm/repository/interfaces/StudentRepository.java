package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Student;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentRepository extends CommonGenericRepository<Student> {

    List<Student> getStudentsByStatusId(Long id);

    Student getStudentByClientId(Long clientId);

    List<Student> getStudentsByClientSocialProfiles_Empty();

    @Query("SELECT s FROM Student s JOIN s.client c WHERE ?1 MEMBER OF c.clientEmails")
    Student getStudentByEmail(String email);
}
