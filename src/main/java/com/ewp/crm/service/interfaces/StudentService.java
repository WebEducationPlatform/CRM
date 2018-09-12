package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Student;

public interface StudentService extends CommonService<Student> {

    Student addStudentForClient(Client client);
}
