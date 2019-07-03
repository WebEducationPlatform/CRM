package com.ewp.crm.repository.interfaces;

import com.ewp.crm.models.Mentor;

public interface MentorRepository {

    Mentor getMentorById(Long userId);

    void save(boolean showAll, Long userId);

    void update(boolean showAll, Long userId);
}