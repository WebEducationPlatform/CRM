package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.SocialProfileType;
import com.ewp.crm.models.Student;

import java.util.List;

public interface StudentRepositoryCustom {

    List<Student> getStudentsWithoutSocialProfileByType(List<SocialProfileType> excludeSocialProfiles);

    List<Student> getStudentsWithTodayNotificationsEnabled();

    void detach(Student student);

    void resetColors();
}
