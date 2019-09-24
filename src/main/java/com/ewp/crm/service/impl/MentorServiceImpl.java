package com.ewp.crm.service.impl;

import com.ewp.crm.models.Mentor;
import com.ewp.crm.repository.interfaces.MentorRepository;
import com.ewp.crm.service.interfaces.MentorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import javax.annotation.Resources;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@PropertySource("file:./slackbot.properties")
public class MentorServiceImpl implements MentorService {

    private final MentorRepository mentorRepository;
    private final RestTemplate restTemplate = new RestTemplate();

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

    @Value("${slackbot.domain}")
    private String slackBotDomain;
    @Value("${slackbot.access.protocol}")
    private String slackBotAccessProtocol;

    @Override
    public int getQuantityStudentsByMentorEmail(String mentorEmail) {
        String url = slackBotAccessProtocol + slackBotDomain + "/mentor/students/quantity";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("mentorEmail",mentorEmail);
        return restTemplate.getForObject(builder.toUriString(), Integer.class);
    }

    @Override
    public void updateQuantityStudentsByMentorEmail(String mentorEmail, int quantityStudents) {
        HttpHeaders headers = new HttpHeaders();

        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        String url = slackBotAccessProtocol + slackBotDomain + "/mentor/students/quantity";
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
        .queryParam("mentorEmail", mentorEmail)
        .queryParam("quantity", quantityStudents);
        restTemplate.exchange(builder.toUriString(),HttpMethod.PUT, new HttpEntity<>(headers),String.class);
    }

}