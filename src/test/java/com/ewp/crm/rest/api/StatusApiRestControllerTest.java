package com.ewp.crm.rest.api;

import com.ewp.crm.models.Status;
import com.ewp.crm.service.interfaces.StatusService;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.restassured.RestAssured.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class StatusApiRestControllerTest {

    @Autowired
    private StatusService statusService;

    @Test
    public void testGetStatusWithHasCode200() {

        given().contentType(ContentType.JSON).accept(ContentType.JSON)
                .when()
                .baseUri("http://localhost:9999")
                .get("/rest/api/status/67")
                .then()
                .assertThat()
                .statusCode(200);

    }

    @Test
    public void testUpdateStatusWithCode200() {

        Response start = given().baseUri("http://localhost:9999")
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .get("/rest/api/status/69");

        Status status = statusService.getAll().get(69);
        status.setName("MayTheSameName");

        Response end = given().baseUri("http://localhost:9999")
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(status)
                .when()
                .put("/rest/api/status");

        if (start.equals(end)) {
            System.out.println("Test was fallen!");
        } else {
            System.out.println("Test passed!");
        }


    }

    @Test
    public void testAddStatusWithCode200() {

        given().baseUri("http://localhost:9999")
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .post("/rest/api/status/add?statusName=TestNAME3")
                .then()
                .assertThat()
                .statusCode(200);

    }

    @Test
    public void testDeleteStatusCode200() {

        given().baseUri("http://localhost:9999")
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .delete("/rest/api/status/delete/67")
                .then()
                .assertThat()
                .statusCode(200);

        //todo добавить проверки реальности происходящего во всех методах!

    }

}
