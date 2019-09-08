package com.ewp.crm.rest.api;

import com.ewp.crm.controllers.rest.api.APIRestClientController;
import com.ewp.crm.models.Client;
import com.ewp.crm.service.interfaces.ClientService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import junit.framework.Assert;
import net.minidev.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.annotation.Resource;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RestApiControllerClientTest {
	private String baseUri ="http://localhost:9999/rest/api/client";

	@Resource
	ClientService clientService;
	@Autowired
	APIRestClientController apiRestClientController;
	@Autowired
	MockMvc mockMvc;

	@Test
	public void addStatusApiold() throws Exception {
		assertThat(apiRestClientController).isNotNull();
	}
	@org.junit.Test
	public void RestApiControllerClientTest() throws Exception {
		this.mockMvc.perform(get(baseUri + "/38450"))
				.andDo(print())
				.andExpect(status().isOk());
	}



	@org.junit.Test
	public void whenRequestedPost_thenCreated() throws Exception {
		JSONObject requestParams = new JSONObject();
		requestParams.put("name", "51"); // Cast
		requestParams.put("email", "51@gmail.com");
		this.mockMvc.perform(post(baseUri)
				.contentType(APPLICATION_JSON_UTF8)
				.content(String.valueOf(requestParams)))
				.andDo(print())
				.andExpect(status().isOk());
	}

	@org.junit.Test
	public void updateClient() throws Exception {
		Client oldClient = clientService.get((long) 38450);
		Assert.assertEquals(oldClient.getName(), "absolnew1");
		Assert.assertEquals(oldClient.getEmail().orElse("null"), "abson1@gmail1.com");
		JSONObject requestParams = new JSONObject();
		requestParams.put("id", "38450");
		requestParams.put("name", "absolnew2"); // Cast
		requestParams.put("email", "abson2@gmail1.com");
		this.mockMvc.perform(put(baseUri+"/update")
				.contentType(APPLICATION_JSON_UTF8)
				.content(String.valueOf(requestParams)))
				.andDo(print())
				.andExpect(status().isOk());
		Client newClient = clientService.get((long) 38450);
		Assert.assertEquals(newClient.getName(), "absolnew2");
		Assert.assertEquals(newClient.getEmail().orElse("null"), "abson2@gmail1.com");
	}


	@org.junit.Test
	public void updateClientStatus() throws Exception {
		JSONObject requestParams = new JSONObject();
		requestParams.put("id", "38424");
		this.mockMvc.perform(put(baseUri+"/updatestatus/3")
				.contentType(APPLICATION_JSON_UTF8)
				.content(String.valueOf(requestParams)))
				.andDo(print())
				.andExpect(status().isOk());
	}

	@org.junit.Test
	public void deleteTest() throws Exception {
		Assert.assertNotNull(clientService.get((long) 38451));
		mockMvc.perform(delete(baseUri+"/38451"))
				.andExpect(status().isOk());
		Assert.assertNull(clientService.get((long) 38451));

	}
	@Test
	public void addStatusApi() throws Exception {
		assertThat(apiRestClientController).isNotNull();
	}


}