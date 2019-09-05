package com.ewp.crm.rest.api;

import io.restassured.http.ContentType;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class StatusApiRestControllerTest {


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

        JSONObject status = new JSONObject();
        status.put("name", "Volos");

        given().baseUri("http://localhost:9999")
                .contentType(ContentType.JSON).accept(ContentType.JSON)
                .body(status)
                .when()
                .put("/rest/api/status")
                .then()
                .assertThat()
                .statusCode(200);

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
