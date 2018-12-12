package com.ewp.crm.service.impl;

import com.ewp.crm.configs.inteface.EwpConfig;
import com.ewp.crm.models.*;
import com.ewp.crm.models.dto.StudentProgressInfo;
import com.ewp.crm.service.interfaces.EwpInfoService;
import com.ewp.crm.service.interfaces.StudentService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.server.reactive.HttpHeadResponseDecorator;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class EwpInfoServiceImpl implements EwpInfoService {

    @Autowired
    private StudentService studentService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EwpConfig ewpConfig;


    @Override
    public List<StudentProgressInfo> getStudentProgressInfo(List<Student> listStudents) {
        String[] emails = listStudents.stream().map(student -> student.getClient().getEmail()).toArray(String[]::new);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ewpConfig.getLinkForStatusStudent())
                .queryParam("emails", emails);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ParameterizedTypeReference<List<StudentProgressInfo>> studentProgressInfoListType
                = new ParameterizedTypeReference<List<StudentProgressInfo>>() {};

        ResponseEntity<List<StudentProgressInfo>> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                entity,
                studentProgressInfoListType
        );

        return response.getBody();
    }
}