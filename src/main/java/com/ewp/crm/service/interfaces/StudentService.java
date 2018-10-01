package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Student;

import java.util.List;

public interface StudentService extends CommonService<Student> {

    Student addStudentForClient(Client client);
    List<Student> getStudentsByStatusId(Long id);
}
