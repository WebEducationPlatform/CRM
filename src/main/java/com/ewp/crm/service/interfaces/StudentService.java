package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Student;

import java.util.List;

public interface StudentService extends CommonService<Student> {

    Student addStudentForClient(Client client);
    List<Student> getStudentsByStatusId(Long id);
    List<Student> getStudentsWithTodayNotificationsEnabled();
    List<Student> getStudentsWithOldStatus();
    Student getStudentByClient(Client client);
    void detach(Student student);
}
