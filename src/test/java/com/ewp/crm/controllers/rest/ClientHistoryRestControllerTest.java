package com.ewp.crm.controllers.rest;

import com.ewp.crm.models.ClientHistory;
import com.ewp.crm.repository.interfaces.ClientRepository;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static io.restassured.RestAssured.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ClientHistoryRestControllerTest {

    private String baseUri = "http://localhost:9999/rest/api/client/history";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientHistoryRestController clientHistoryRestController;

    @Autowired
    private ClientRepository clientRepository;

    private static ClientHistory clientHistory = new ClientHistory();
    private static ClientHistory clientHistoryRes = new ClientHistory();

    @Test
    public void addHistory() throws Exception {
        //Создаем объект для добавления
        clientHistory.setClient(clientRepository.getClientById(37465L));
        clientHistory.setLink("Add Autotest string");
        clientHistory.setTitle("Add Autotest string");
        clientHistory.setType(ClientHistory.Type.CALL);

        //Добавляем
        Response response = given()
                .baseUri(baseUri)
                .basePath("/rest/addHistory/37465")
                .contentType(ContentType.JSON)
                .body(clientHistory)
                .post();

        //Проверяем
        int responseStatusCode = response.getStatusCode();
        clientHistoryRes = response.getBody().as(ClientHistory.class);

        Assert.assertEquals(responseStatusCode, 200);
        Assert.assertEquals(clientHistoryRes.getTitle(), clientHistory.getTitle());
        Assert.assertEquals(clientHistoryRes.getLink(), clientHistory.getLink());
        Assert.assertEquals(clientHistoryRes.getType(), clientHistory.getType());
    }

    @Test
    public void updateHistory() throws Exception {
        //Меняем созданную историю
        clientHistory.setId(clientHistoryRes.getId());
        clientHistory.setClient(clientRepository.getClientById(37465L));
        clientHistory.setLink("Update Autotest string");
        clientHistory.setTitle("Update Autotest string");
        clientHistory.setType(ClientHistory.Type.UPDATE);

        //Обновляем
        Response response = given()
                .baseUri(baseUri)
                .basePath("/rest/updateHistory/37465")
                .contentType(ContentType.JSON)
                .body(clientHistory)
                .put();

        //Проверяем
        int statusCode = response.getStatusCode();
        clientHistoryRes = response.getBody().as(ClientHistory.class);

        Assert.assertEquals(statusCode, 200);
        Assert.assertEquals(clientHistoryRes.getTitle(), clientHistory.getTitle());
        Assert.assertEquals(clientHistoryRes.getLink(), clientHistory.getLink());
        Assert.assertEquals(clientHistoryRes.getType(), clientHistory.getType());
    }

    @Test
    public void deleteHistory() throws Exception {
        //Удаляем созданную историю
        int response = this.mockMvc.perform(delete(baseUri + "/rest/deleteHistory/"+clientHistory.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getStatus();

        //Проверяем
        Assert.assertEquals(response, 200);

    }

}

