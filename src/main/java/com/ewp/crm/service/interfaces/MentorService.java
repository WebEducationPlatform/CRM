package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Mentor;

public interface MentorService {
    Mentor getMentorById(Long userId);

    Boolean getMentorShowAllClientsById(Long userId);

    void saveMentorShowAllFieldAndUserIdField(boolean showAll, Long userId);

    void updateMentorShowAllFieldAndUserIdField(boolean showAll, Long userId);
}