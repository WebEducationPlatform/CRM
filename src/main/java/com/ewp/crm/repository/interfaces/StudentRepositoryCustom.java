package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.SocialProfile;
import com.ewp.crm.models.SocialProfile.SocialNetworkType;
import com.ewp.crm.models.Student;

import java.util.List;

public interface StudentRepositoryCustom {

    List<Student> getStudentsWithoutSocialProfileByType(List<SocialNetworkType> excludeSocialProfiles);

    List<Student> getStudentsWithTodayNotificationsEnabled();

    void detach(Student student);

    void resetColors();
}
