package com.ewp.crm.service.impl;

import com.ewp.crm.models.Mentor;
import com.ewp.crm.repository.interfaces.MentorRepository;
import com.ewp.crm.service.interfaces.MentorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MentorServiceImpl implements MentorService {

    private final MentorRepository mentorRepository;

    @Autowired
    public MentorServiceImpl(MentorRepository mentorRepository) {
        this.mentorRepository = mentorRepository;
    }


    @Override
    public Mentor getMentorById(Long userId) {
        return mentorRepository.getMentorById(userId);
    }

    @Override
    public Boolean getMentorShowAllClientsById(Long userId) {
        return mentorRepository.getMentorShowAllClientsById(userId);
    }

    @Override
    public void saveMentorShowAllFieldAndUserIdField(boolean showAll, Long userId) {
        mentorRepository.saveMentorShowAllFieldAndUserIdField(showAll, userId);
    }

    @Override
    public void updateMentorShowAllFieldAndUserIdField(boolean showAll, Long userId) {
        mentorRepository.updateMentorShowAllFieldAndUserIdField(showAll, userId);
    }
}