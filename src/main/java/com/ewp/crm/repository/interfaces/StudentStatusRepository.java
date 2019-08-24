package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.StudentStatus;

public interface StudentStatusRepository extends CommonGenericRepository<StudentStatus> {

    StudentStatus getStudentStatusByStatus(String status);
}
