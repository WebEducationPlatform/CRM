package com.ewp.crm.rest.api;
import com.ewp.crm.models.Client;
import com.ewp.crm.models.Status;
import com.ewp.crm.service.impl.ClientServiceImpl;
import com.ewp.crm.service.interfaces.ClientService;
import com.google.gson.JsonObject;
import io.restassured.http.ContentType;
import net.minidev.json.JSONObject;
import org.json.JSONException;

;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import junit.framework.Assert;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static io.restassured.RestAssured.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RestApiControllerClientTest {
	@Resource
	ClientService clientService;
	@Test
	public void RestApiControllerClientTest()
	{
		RestAssured.baseURI = "http://localhost:9999/rest/api/client";
		RequestSpecification httpRequest = RestAssured.given();
		Response response = httpRequest.request(Method.GET, "/4");
		String responseBody = response.getBody().asString();
		System.out.println("Response Body is =>  " + responseBody);
	}
	@Test
	public void whenRequestedPost_thenCreated() {
		RestAssured.baseURI ="http://localhost:9999/rest/api/client";
		JSONObject requestParams = new JSONObject();
			requestParams.put("name", "вававвац"); // Cast
		requestParams.put("email", "вафйй@gmail.com");
		Response response = given()
				.baseUri(baseURI)
				.contentType(ContentType.JSON)
				.body(requestParams.toJSONString())
				.post();
		int statusCode = response.getStatusCode();
		Assert.assertEquals(statusCode, 200);
	}
	@Test
	public void updateClient() {
		RestAssured.baseURI ="http://localhost:9999/rest/api/client";
		Client oldClient=clientService.get((long) 38448);

		Assert.assertEquals(oldClient.getName(),"укфний2");
		Assert.assertEquals(oldClient.getEmail().orElse("null"),"орлорh@gmail1.com2");
		JSONObject requestParams = new JSONObject();
		requestParams.put("id","38448");
		requestParams.put("name", "укфний3"); // Cast
		requestParams.put("email", "орлорh@gmail1.com3");
		Response response = given()
				.baseUri(baseURI)
				.basePath("/update")
				.contentType(ContentType.JSON)
				.body(requestParams.toJSONString())
				.put();
		int statusCode = response.getStatusCode();
		Assert.assertEquals(statusCode, 200);
		Client newClient = clientService.get((long) 38448);
		Assert.assertEquals(newClient.getName(),"укфний3");
		Assert.assertEquals(newClient.getEmail().orElse(null),"орлорh@gmail1.com3");


	}
	@Test
	public void updateClientStatus() {
		RestAssured.baseURI ="http://localhost:9999/rest/api/client";
		JSONObject requestParams = new JSONObject();
		requestParams.put("id","38424");
		Response response = given()
				.baseUri(baseURI)
				.basePath("/updatestatus/1")
				.contentType(ContentType.JSON)
				.body(requestParams.toJSONString())
				.put();
		int statusCode = response.getStatusCode();
		Assert.assertEquals(statusCode, 200);
	}
	@Test
	public void deleteTest() {
		RestAssured.baseURI ="http://localhost:9999/rest/api/client";
		Client asclient=clientService.get((long) 38445);
		Assert.assertNotNull(asclient);
		Response response = given()
				.baseUri(baseURI)
				.basePath("/38445")
				.delete();
		int statusCode = response.getStatusCode();
		Assert.assertEquals(statusCode, 200);
		Assert.assertNull(clientService.get((long) 38445));
		System.out.println("Response :" + response.asString());
		System.out.println("Status Code :" + statusCode);
	}

}