package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Client;
import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.SocialProfile.SocialNetworkType;
import com.ewp.crm.models.Student;

import java.util.List;
import java.util.Optional;

public interface StudentService extends CommonService<Student> {

    List<Student> getStudentsWithoutSocialProfileByType(List<SocialNetworkType> excludeSocialProfiles);
    Optional<Student> addStudentForClient(Client client);
    List<Student> getStudentsByStatusId(Long id);
    List<Student> getStudentsWithTodayNotificationsEnabled();
    void detach(Student student);
    Optional<Student> getStudentByClientId(Long clientId);
    void save(Student student);
    void resetColors();
    Student getStudentByEmail(String email);
}
