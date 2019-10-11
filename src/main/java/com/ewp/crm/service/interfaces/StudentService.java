package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.Course;
import com.ewp.crm.models.SocialProfile.SocialNetworkType;
import com.ewp.crm.models.Student;
import com.ewp.crm.models.StudentEducationStage;
import com.ewp.crm.models.dto.all_students_page.StudentDto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface StudentService extends CommonService<Student> {

    List<Student> getStudentsWithoutSocialProfileByType(List<SocialNetworkType> excludeSocialProfiles);

    Optional<Student> addStudentForClient(Client client);

    List<Student> getStudentsByStatusId(Long id);

    List<Student> getStudentsWithTodayNotificationsEnabled();

    List<Student> getStudentsWithTodayTrialNotificationsEnabled();

    void detach(Student student);

    Optional<Student> getStudentByClientId(Long clientId);

    void save(Student student);

    void resetColors();

    Student getStudentByEmail(String email);

    List<StudentDto> getStudentDtoForAllStudentsPage();

    void updateStudentEducationStage(StudentEducationStage studentEducationStage, Student student);

    void updateCourse(Course course, Student student);
}
