package com.ewp.crm.controllers.rest.api;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("/application-test.properties")
@Sql(value ={"/foreign_key_checks_off.sql", "/dump.sql", "/foreign_key_checks_on.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CommentApiRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CommentApiRestController commentApiRestController;

    private String url = "/rest/api/comment";

    @Test
    public void commentApiRestControllerStatus() throws Exception {
        assertThat(commentApiRestController).isNotNull();
    }

    public MvcResult addCommentsForTest() throws Exception{
        return this.mockMvc.perform(post(url+"/add")
                .param("clientId", "1")
                .param("content", "bla-bla-bla")
                .param("email", "sevostyanovg.d@gmail.com"))
                .andDo(print())
                .andReturn();
    }

    public MvcResult addAnswerForTest() throws Exception{
        return this.mockMvc
                .perform(post(url+"/add/answer")
                        .param("commentId", "17")
                        .param("content", "Re: bla-bla-bla")
                        .param("email", "sevostyanovg.d@gmail.com"))
                .andDo(print())
                .andReturn();
    }

    @Test
    public void getComments() throws Exception{
        this.mockMvc.perform(get(url+"/getComments/1"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void addComments() throws Exception{
        this.mockMvc.perform(post(url+"/add")
                            .param("clientId", "1")
                            .param("content", "bla-bla-bla")
                            .param("email", "sevostyanovg.d@gmail.com"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("bla-bla-bla")));
    }

    @Test
    public void addCommentsErrorClientIdError() throws Exception{
        this.mockMvc.perform(post(url+"/add")
                .param("clientId", "11111")
                .param("content", "bla-bla-bla")
                .param("email", "sevostyanovg.d@gmail.com"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void addCommentsErrorUserEmail() throws Exception{
        this.mockMvc.perform(post(url+"/add")
                .param("clientId", "1")
                .param("content", "bla-bla-bla")
                .param("email", "sevostyanovg.d@gmail.co"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addAnswer() throws Exception{
        this.mockMvc
                .perform(post(url+"/add/answer")
                .param("commentId", "17")
                .param("content", "Re: bla-bla-bla")
                .param("email", "sevostyanovg.d@gmail.com"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("bla-bla-bla")));
    }

    @Test
    public void addAnswerCommentIdError() throws Exception{
        String content = addCommentsForTest().getResponse().getContentAsString();
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(content);
        String id = json.getAsString("id");
        Long idLong = Long.parseLong(id)+1;
        id = idLong.toString();
        this.mockMvc
                .perform(post(url+"/add/answer")
                        .param("commentId", id)
                        .param("content", "Re: bla-bla-bla")
                        .param("email", "sevostyanovg.d@gmail.com"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteCommentAnswer() throws Exception{
        String content = addAnswerForTest().getResponse().getContentAsString();
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(content);
        String id = json.getAsString("id");
        this.mockMvc
                .perform(post(url+"/delete/answer")
                        .param("id", id)
                        .param("email", "sevostyanovg.d@gmail.com"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void deleteCommentAnswerErrorEmail() throws Exception{
        String content = addCommentsForTest().getResponse().getContentAsString();
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(content);
        String id = json.getAsString("id");
        this.mockMvc
                .perform(post(url+"/delete/answer")
                        .param("id", id)
                        .param("email", "sevostyanovg.d@gmail.com"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteCommentAnswerIdError() throws Exception{
        String content = addAnswerForTest().getResponse().getContentAsString();
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(content);
        String id = json.getAsString("id");
        Long idLong = Long.parseLong(id)+1;
        id = idLong.toString();
        this.mockMvc
                .perform(post(url+"/delete/answer")
                        .param("id", id)
                        .param("email", "sevostyanovg.d@gmail.co"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void editCommentAnswer() throws Exception{
        String content = addAnswerForTest().getResponse().getContentAsString();
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(content);
        String id = json.getAsString("id");

        this.mockMvc
                .perform(post(url+"/edit/answer")
                        .param("id", id)
                        .param("content", "ha-ha-ha")
                        .param("email", "sevostyanovg.d@gmail.com"))
                .andDo(print())
                .andExpect(content().string(containsString("ha-ha-ha")))
                .andExpect(status().isOk());
    }

    @Test
    public void editCommentAnswerEmailError() throws Exception{
        String content = addAnswerForTest().getResponse().getContentAsString();
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(content);
        String id = json.getAsString("id");

        this.mockMvc
                .perform(post(url+"/edit/answer")
                        .param("id", id)
                        .param("content", "ha-ha-ha")
                        .param("email", "sevostyanovg.d@gmail.c"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void editCommentAnswerIdError() throws Exception{
        String content = addAnswerForTest().getResponse().getContentAsString();
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(content);
        String id = json.getAsString("id");
        Long idLong = Long.parseLong(id)+1;
        id = idLong.toString();
        this.mockMvc
                .perform(post(url+"/edit/answer")
                        .param("id", id)
                        .param("content", "ha-ha-ha")
                        .param("email", "sevostyanovg.d@gmail.c"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteComment() throws Exception{
        String content = addCommentsForTest().getResponse().getContentAsString();
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(content);
        String id = json.getAsString("id");

        this.mockMvc
                .perform(post(url+"/delete")
                        .param("id", id)
                        .param("email", "sevostyanovg.d@gmail.com"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void deleteCommentIdError() throws Exception{
        String content = addCommentsForTest().getResponse().getContentAsString();
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(content);
        String id = json.getAsString("id");
        Long idLong = Long.parseLong(id)+1;
        id = idLong.toString();
        this.mockMvc
                .perform(post(url+"/delete")
                        .param("id", id)
                        .param("email", "sevostyanovg.d@gmail.com"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteCommentEmailError() throws Exception{
        String content = addCommentsForTest().getResponse().getContentAsString();
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(content);
        String id = json.getAsString("id");
        this.mockMvc
                .perform(post(url+"/delete")
                        .param("id", id)
                        .param("email", "sevostyanovg.d@gmail.c"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void editComment() throws Exception{
        String content = addCommentsForTest().getResponse().getContentAsString();
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(content);
        String id = json.getAsString("id");

        this.mockMvc
                .perform(post(url+"/edit")
                        .param("id", id)
                        .param("content", "ha-ha-ha")
                        .param("email", "sevostyanovg.d@gmail.com"))
                .andDo(print())
                .andExpect(content().string(containsString("ha-ha-ha")))
                .andExpect(status().isOk());
    }

    @Test
    public void editCommentIdError() throws Exception{
        String content = addCommentsForTest().getResponse().getContentAsString();
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(content);
        String id = json.getAsString("id");
        Long idLong = Long.parseLong(id)+1;
        id = idLong.toString();
        this.mockMvc
                .perform(post(url+"/edit")
                        .param("id", id)
                        .param("content", "ha-ha-ha")
                        .param("email", "sevostyanovg.d@gmail.com"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void editCommentEmailError() throws Exception{
        String content = addCommentsForTest().getResponse().getContentAsString();
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(content);
        String id = json.getAsString("id");
        this.mockMvc
                .perform(post(url+"/edit")
                        .param("id", id)
                        .param("content", "ha-ha-ha")
                        .param("email", "sevostyanovg.d@gmail.co"))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}