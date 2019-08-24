package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Mentor;
import org.springframework.transaction.annotation.Transactional;

public interface MentorRepository {

    Mentor getMentorById(Long userId);

    Boolean getMentorShowAllClientsById(Long userId);

    void saveMentorShowAllFieldAndUserIdField(boolean showAll, Long userId);

    void updateMentorShowAllFieldAndUserIdField(boolean showAll, Long userId);

    int getQuantityStudentsByMentorId(long id);

    void updateQuantityStudentsByMentorId(long id, int quantityStudents);

    @Transactional
    void updateUserAsMentorWithQuantityStudents(long id, int quantityStudents);

    @Transactional
    void updateUserAsMentorWithDefaultValues(long id);
}