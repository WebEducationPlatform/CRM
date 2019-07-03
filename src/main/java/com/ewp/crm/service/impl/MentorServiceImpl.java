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
    public void save(boolean showAll, Long userId) {
        mentorRepository.save(showAll, userId);
    }

    @Override
    public void update(boolean showAll, Long userId) {
        mentorRepository.update(showAll, userId);
    }

    @Override
    public String getFirstLetterFromNameAndSurname(Mentor mentor) {
        String name = mentor.getFirstName();
        String lastName = mentor.getLastName();
        String firstLetterFromNameAndSurname = name.substring(0,1) + lastName.substring(0,1);
        return firstLetterFromNameAndSurname;
    }
}
