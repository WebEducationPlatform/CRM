package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Mentor;

public interface MentorService {
    Mentor getMentorById(Long userId);

    Boolean getMentorShowAllClientsById(Long userId);

    void saveMentorShowAllFieldAndUserIdField(boolean showAll, Long userId);

    void updateMentorShowAllFieldAndUserIdField(boolean showAll, Long userId);

    int getQuantityStudentsByMentorId(long id);

    void updateQuantityStudentsByMentorId(long id, int quantityStudents);

    void updateUserAsMentorWithQuantityStudents(long id, int quantityStudents);

    void updateUserAsMentorWithDefaultValues(long id);
}