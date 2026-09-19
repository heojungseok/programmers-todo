package com.todo.boundedContext.todo.in;

import com.todo.boundedContext.todo.dto.TodoRequest;
import com.todo.boundedContext.todo.dto.TodoResponse;
import com.todo.global.response.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestTestClient
@ActiveProfiles("test")
class ApiTodoControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    RestTestClient restClient;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void test1() throws Exception {
        restClient
                .get().uri("/api/health")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(String.class).isEqualTo("ok");
    }

    @Test
    void create() {

        TodoRequest todoRequest = new TodoRequest("dddd", "N");

        restClient
                .post().uri("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .body(todoRequest)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void udpate() {
        TodoRequest todoRequest = new TodoRequest("dddd", "N");

        restClient
                .put().uri("/api/todos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .body(todoRequest)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

}