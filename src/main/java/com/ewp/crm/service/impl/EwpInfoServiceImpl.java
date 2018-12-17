package com.ewp.crm.service.impl;

import com.ewp.crm.configs.inteface.EwpConfig;
import com.ewp.crm.models.*;
import com.ewp.crm.models.dto.StudentProgressInfo;
import com.ewp.crm.service.interfaces.EwpInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import org.springframework.web.util.UriComponentsBuilder;

@Service
public class EwpInfoServiceImpl implements EwpInfoService {

    private final RestTemplate restTemplate;

    private final EwpConfig ewpConfig;

    @Autowired
    public EwpInfoServiceImpl(RestTemplate restTemplate, EwpConfig ewpConfig) {
        this.restTemplate = restTemplate;
        this.ewpConfig = ewpConfig;
    }


    @Override
    public List<StudentProgressInfo> getStudentProgressInfo(List<String> listEmail) {
        String[] emails = listEmail.toArray(new String[listEmail.size()]);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ewpConfig.getUriForStudentProgress())
                .queryParam("emails", emails);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ParameterizedTypeReference<List<StudentProgressInfo>> studentProgressInfoListType
                = new ParameterizedTypeReference<List<StudentProgressInfo>>() {
        };

        ResponseEntity<List<StudentProgressInfo>> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                entity,
                studentProgressInfoListType
        );

        return response.getBody();
    }
}