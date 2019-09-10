package com.ewp.crm.rest.api;

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

    private String url = "http://localhost:" + port + "/rest/api/user/";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

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
        mockMvc.perform(get(url + "9"))
                .andDo(print())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void addUser() throws Exception {
        String json = "{\"id\":null," +
                "\"firstName\":\"Maksim\"," +
                "\"lastName\":\"Sidorenko\"," +
                "\"birthDate\":\"1982-03-02\"," +
                "\"phoneNumber\":\"89885648715\"," +
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
    public void updateUser() throws Exception {

            String json = "{\"id\":31," +
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
        MvcResult result = mockMvc.perform(delete(url + "?id_delete=33&id_transfer=21"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        assertTrue(result.getResponse().getContentAsString().equals("\"OK\""));
    }

    @Test
    public void deleteUser() throws Exception {
        MvcResult result = mockMvc.perform(delete(url + "32"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        assertTrue(result.getResponse().getContentAsString().equals("\"OK\""));
    }
}

//        Не использую сериализацию, потому-что пароль закрыт на сериализацию
//        User user = new User(
//                "Maksim",
//                "Sidorenko",
//                LocalDate.of(1982, 03, 02),
//                "89885648715",
//                "sir.SidorenkoMV@yandex.ru",
//                "admin",
//                null,
//                Client.Sex.MALE.toString(),
//                "Taganrog",
//                "Russia",
//                Arrays.asList(roleService.getRoleByName("USER"), roleService.getRoleByName("ADMIN"),
//                        roleService.getRoleByName("OWNER")),
//                true,
//                true);
//
//        mapper.configure(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, true);
//        String json = mapper.writeValueAsString(user);
