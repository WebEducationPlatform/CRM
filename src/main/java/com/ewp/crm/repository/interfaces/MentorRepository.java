package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Mentor;

public interface MentorRepository {

    Mentor getMentorById(Long userId);

    Boolean getMentorShowAllClientsById(Long userId);

    void saveMentorShowAllFieldAndUserIdField(boolean showAll, Long userId);

    void updateMentorShowAllFieldAndUserIdField(boolean showAll, Long userId);
}