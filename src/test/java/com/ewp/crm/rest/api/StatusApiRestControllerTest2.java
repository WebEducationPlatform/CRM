package com.ewp.crm.rest.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

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

    @Test
    public void addStatusApi() throws Exception {
        this.mockMvc.perform(post(baseUri + "/add?statusName=AxeNameOfTestStatusStillApiStatus"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void getStatusApi() throws Exception {
        this.mockMvc.perform(get(baseUri + "/74"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void updateStatusApi() throws Exception {

    }

    @Test
    public void deleteStatusApi() throws Exception {
        this.mockMvc.perform(delete(baseUri + "/delete/76"))
                .andDo(print())
                .andExpect(status().isOk());
    }


}
