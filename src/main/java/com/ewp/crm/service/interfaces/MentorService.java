package com.ewp.crm.service.interfaces;

import com.ewp.crm.models.Mentor;

public interface MentorService {
    Mentor getMentorById(Long userId);

    void save(boolean showAll, Long userId);

    void update(boolean showAll, Long userId);

    String getFirstLetterFromNameAndSurname(Mentor mentor);
}