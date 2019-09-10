package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.repository.interfaces.ClientRepository;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static io.restassured.RestAssured.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@AutoConfigureMockMvc
public class ClientHistoryRestControllerTest {

    @Value("${uriRestClientHistoryController}")
    private String baseUri;

    @Value("${clientId}")
    private Long clientId;

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ClientRepository clientRepository;

    //Тестовые истории
    private static ClientHistory clientHistory = new ClientHistory();
    private static ClientHistory clientHistoryRes = new ClientHistory();

    @Test
//    @Ignore
    public void addHistory() {
        //Создаем объект для добавления
        clientHistory.setClient(clientRepository.getClientById(clientId));
        clientHistory.setLink("Add Autotest string");
        clientHistory.setTitle("Add Autotest string");
        clientHistory.setType(ClientHistory.Type.CALL);

        //Добавляем
        Response response = given()
                .baseUri(baseUri)
                .basePath("/" + clientId)
                .contentType(ContentType.JSON)
                .body(clientHistory)
                .post();

        //Проверяем
        int responseStatusCode = response.getStatusCode();
        clientHistoryRes = response.getBody().as(ClientHistory.class);

        clientHistory.setId(clientHistoryRes.getId());
        clientHistory.setDate(clientHistoryRes.getDate());

        Assert.assertEquals(responseStatusCode, 200);
        Assert.assertEquals(clientHistoryRes, clientHistory);

    }

    @Test
//    @Ignore
    public void updateHistory() {
        //Меняем созданную историю
        clientHistory.setId(clientHistoryRes.getId());
        clientHistory.setClient(clientRepository.getClientById(clientId));
        clientHistory.setLink("Update Autotest string");
        clientHistory.setTitle("Update Autotest string");
        clientHistory.setType(ClientHistory.Type.UPDATE);

        //Обновляем
        Response response = given()
                .baseUri(baseUri)
                .basePath("/" + clientId)
                .contentType(ContentType.JSON)
                .body(clientHistory)
                .put();

        //Проверяем
        int statusCode = response.getStatusCode();
        clientHistoryRes = response.getBody().as(ClientHistory.class);

        Assert.assertEquals(statusCode, 200);
        Assert.assertEquals(clientHistoryRes, clientHistory);
    }

    @Test
//    @Ignore
    public void deleteHistory() throws Exception {
        //Удаляем созданную историю
        int response = this.mockMvc.perform(delete(baseUri + "/" + clientHistory.getId()))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getStatus();

        //Проверяем
        Assert.assertEquals(response, 204);

    }

}