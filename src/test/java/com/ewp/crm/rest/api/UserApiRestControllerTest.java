package com.ewp.crm.rest.api;

import com.ewp.crm.service.interfaces.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserApiRestControllerTest {

    @LocalServerPort
    private int port;

    private final String url = "http://localhost:" + port + "/rest/api/user/";
    private String eMail = "sir.SidorenkoMV@yandex.ru";
    private final MockMvc mockMvc;
    private final UserService userService;

    @Autowired
    public UserApiRestControllerTest(MockMvc mockMvc,
                                     UserService userService) {
        this.mockMvc = mockMvc;
        this.userService = userService;
    }

    @Test
    public void addUser() throws Exception {
        String json = "{\"id\":null," +
                "\"firstName\":\"Maksim\"," +
                "\"lastName\":\"Sidorenko\"," +
                "\"birthDate\":\"1982-03-02\"," +
                "\"phoneNumber\":\"89885648715\"," +
                "\"email\":\"" + eMail + "\"," +
                "\"password\":\"admin\"," +
                "\"vk\":null," +
                "\"sex\":\"MALE\"," +
                "\"city\":\"Taganrog\"," +
                "\"country\":\"Russia\"," +
                "\"photo\":null," +
                "\"photoType\":null," +
                "\"ipTelephony\":true," +
                "\"newClientNotifyIsEnabled\":true," +
                "\"autoAnswer\":null," +
                "\"vkToken\":null," +
                "\"googleToken\":null," +
                "\"enableMailNotifications\":false," +
                "\"enableSmsNotifications\":false," +
                "\"enableAsignMentorMailNotifications\":false," +
                "\"role\":[{\"id\":2,\"roleName\":\"USER\"},{\"id\":1,\"roleName\":\"ADMIN\"},{\"id\":3,\"roleName\":\"OWNER\"}]," +
                "\"colorBackground\":null," +
                "\"studentPageFilters\":null," +
                "\"lastClientDate\":null," +
                "\"verified\":true," +
                "\"enabled\":true," +
                "\"rowStatusDirection\":false}";

        MvcResult result = mockMvc.perform(post(url)
                .contentType(APPLICATION_JSON_VALUE)
                .content(json)
                .accept(APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        assertTrue(result.getResponse().getContentAsString().equals("\"OK\""));
    }

    @Test
    public void getAllUsers() throws Exception {
        mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void getUserById() throws Exception {
        Long user_id = userService.getUserByEmail(eMail).get().getId();
        mockMvc.perform(get(url + user_id))
                .andDo(print())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void updateUser() throws Exception {
            Long user_id = userService.getUserByEmail(eMail).get().getId();
            String json = "{\"id\":" + user_id + "," +
                    "\"firstName\":\"Maksim\"," +
                    "\"lastName\":\"Sidorenko\"," +
                    "\"birthDate\":\"1982-03-02\"," +
                    "\"phoneNumber\":\"89885648716\"," + //изменил телефон
                    "\"email\":\"sir.SidorenkoMV@yandex.ru\"," +
                    "\"password\":\"admin\"," +
                    "\"vk\":null," +
                    "\"sex\":\"MALE\"," +
                    "\"city\":\"Taganrog\"," +
                    "\"country\":\"Russia\"," +
                    "\"photo\":null," +
                    "\"photoType\":null," +
                    "\"ipTelephony\":true," +
                    "\"newClientNotifyIsEnabled\":true," +
                    "\"autoAnswer\":null," +
                    "\"vkToken\":null," +
                    "\"googleToken\":null," +
                    "\"enableMailNotifications\":false," +
                    "\"enableSmsNotifications\":false," +
                    "\"enableAsignMentorMailNotifications\":false," +
                    "\"role\":[{\"id\":2,\"roleName\":\"USER\"},{\"id\":1,\"roleName\":\"ADMIN\"},{\"id\":3,\"roleName\":\"OWNER\"}]," +
                    "\"colorBackground\":null," +
                    "\"studentPageFilters\":null," +
                    "\"lastClientDate\":null," +
                    "\"verified\":true," +
                    "\"enabled\":true," +
                    "\"rowStatusDirection\":false}";

            MvcResult result = mockMvc.perform(put(url)
                    .contentType(APPLICATION_JSON_VALUE)
                    .content(json)
                    .accept(APPLICATION_JSON_VALUE))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();
            assertTrue(result.getResponse().getContentAsString().equals("\"OK\""));
    }

    @Test
    public void deleteUserTransferClients() throws Exception {
        Long user_id = userService.getUserByEmail(eMail).get().getId();
        MvcResult result = mockMvc.perform(delete(url + "?id_delete=" + user_id + "&id_transfer=21"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        assertTrue(result.getResponse().getContentAsString().equals("\"OK\""));
    }

    @Test
    public void deleteUser() throws Exception {
        Long user_id = userService.getUserByEmail(eMail).get().getId();
        MvcResult result = mockMvc.perform(delete(url + user_id))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        assertTrue(result.getResponse().getContentAsString().equals("\"OK\""));
    }
}
