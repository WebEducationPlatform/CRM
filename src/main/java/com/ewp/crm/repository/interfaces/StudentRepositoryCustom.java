package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.SocialProfile.SocialNetworkType;
import com.ewp.crm.models.Student;
import com.ewp.crm.models.dto.all_students_page.StudentDto;

import java.time.ZonedDateTime;
import java.util.List;

public interface StudentRepositoryCustom {

    List<Student> getStudentsWithoutSocialProfileByType(List<SocialNetworkType> excludeSocialProfiles);

    List<Student> getStudentsWithTodayNotificationsEnabled();

    long countActiveByDateAndStatuses(ZonedDateTime day, List<Long> studentStatuses);

    void detach(Student student);

    void resetColors();

    List<StudentDto> getStudentDtoForAllStudentsPage();
}
