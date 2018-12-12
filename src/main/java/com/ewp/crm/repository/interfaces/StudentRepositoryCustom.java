package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Student;

import java.util.List;

public interface StudentRepositoryCustom {

    List<Student> getStudentsWithTodayNotificationsEnabled();

    List<Student> getStudentsWithOldStatus(long timeLimitInSeconds);

    void detach(Student student);
}
