package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.StudentStatus;

public interface StudentStatusService extends CommonService<StudentStatus> {

    StudentStatus getByName(String status);

    void save(StudentStatus studentStatus);
}
