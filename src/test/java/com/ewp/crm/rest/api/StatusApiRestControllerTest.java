package com.ewp.crm.rest.api;

import com.ewp.crm.models.Status;
import com.ewp.crm.service.interfaces.StatusService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

import static io.restassured.RestAssured.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class StatusApiRestControllerTest {

    @Autowired
    private StatusService statusService;

    @Test
    public void testGetStatusWithHasCode200() {

        int response = given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .baseUri("http://localhost:9999")
                .get("/rest/api/status/72")
                .andReturn()
                .statusCode();




        if (response == 200) {
            System.out.println("Test passed!");
        } else {
            System.out.println("Test failed!");
        }

        //todo допилить остаток метода, а затем приняться за интеграционные тесты
        // и после того как это будет завершено, удалить туду комменатрии!
    }

    @Test
    public void testUpdateStatusWithCode200() {

        RestAssured.baseURI ="http://localhost:9999/rest/api/status";

        Status status = statusService.get(72L).get();
        status.setName("TestName");
        status.setRole(Collections.emptyList()); // Без этого не работает!

        Response response = given()
                .baseUri(RestAssured.baseURI)
                .basePath("/update")
                .contentType(ContentType.JSON)
                .body(status)
                .put();

        int statusCode = response.getStatusCode();

        if (statusCode == 200) {
            System.out.println("Test passed!");
        } else {
            System.out.println("Test failed!");
        }

        // Я должен был с этим промучаться два дня, чтобы наконец-то его сделать!
        // Я был обязан так промучиться, чтобы он заработал!
        // В самой книге мироздания были описаны эти дни, как мучения Азамата с методом апдейт в тестах!
        // Я поверил в судьбу после этих слов!

    }

    @Test
    public void testAddStatusWithCode200() {

        int response = given().baseUri("http://localhost:9999")
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .post("/rest/api/status/add?statusName=NameOfTestStatus")
                .andReturn()
                .statusCode();


        if (response == 200) {
            System.out.println("Test passed!");
        } else {
            System.out.println("Test failed!");
        }

    }

    @Test
    public void testDeleteStatusCode200() {

        boolean response = given().baseUri("http://localhost:9999")
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .delete("/rest/api/status/delete/71")
                .andReturn()
                .equals(given().get("http://localhost:9999/rest/api/delete/71"));

        if (response) {
            System.out.println("Test failed!");
        } else {
            System.out.println("Test passed!");
        }

        //todo добавить проверки реальности происходящего во всех методах!

    }

}
