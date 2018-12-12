package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Student;

import java.util.List;

public interface StudentRepository extends CommonGenericRepository<Student> {

    List<Student> getStudentsByStatusId(Long id);

    Student getStudentByClient(Client client);
}
