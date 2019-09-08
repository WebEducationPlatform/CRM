package com.ewp.crm.rest.api;

import com.ewp.crm.controllers.rest.api.StatusApiRestController;
import com.ewp.crm.models.Status;
import com.ewp.crm.service.interfaces.StatusService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StatusApiRestControllerTest2 {

    private String baseUri = "http://localhost:9999/rest/api/status";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StatusService statusService;

    @Autowired
    private StatusApiRestController statusApiRestController;

    @Test
    public void addStatusApi() throws Exception {

        String statusName = "TestNameForStatus"; //На всякий пожарный проверять, есть ли такое имя в базе!

        int response = this.mockMvc.perform(post(baseUri + "/" + statusName))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getStatus();

        int desired = statusApiRestController.getStatusByName(statusName).getStatusCodeValue();

        Assert.assertEquals(response, desired);
    }

    @Test
    public void getStatusApi() throws Exception {

        int status = this.mockMvc.perform(get(baseUri + "/64").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getStatus();

        int desired = statusApiRestController.getStatusById(64L).getStatusCode().value();

        Assert.assertEquals(status, desired);

    }

    @Test
    public void updateStatusApi() throws Exception {

        Status status = statusService.get(64L).get();
        status.setName("TestNameToo");
        status.setRole(Collections.emptyList()); // Без этого не работает!

        Response response = given()
                .baseUri(baseUri)
                .contentType(ContentType.JSON)
                .body(status)
                .put();

        int statusCode = response.getStatusCode();

        this.mockMvc.perform(get(baseUri + "/name/" + status.getName()))
                .andDo(print())
                .andExpect(status().isOk());

        Assert.assertEquals(statusCode, 200);

    }

    @Test
    public void deleteStatusApi() throws Exception {

        int response = this.mockMvc.perform(delete(baseUri + "/64"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getStatus();

        Assert.assertEquals(response, 200);

    }


}
