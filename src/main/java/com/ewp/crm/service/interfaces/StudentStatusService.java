package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.StudentStatus;

import java.util.Optional;

public interface StudentStatusService extends CommonService<StudentStatus> {

    Optional<StudentStatus> getByStatus(String status);

    void save(StudentStatus studentStatus);
}
