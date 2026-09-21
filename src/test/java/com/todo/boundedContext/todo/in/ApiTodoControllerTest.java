package com.todo.boundedContext.todo.in;

import com.todo.boundedContext.todo.app.TodoService;
import com.todo.boundedContext.todo.dto.TodoRequest;
import com.todo.boundedContext.todo.dto.TodoResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest
@AutoConfigureRestTestClient
@ActiveProfiles("test")
class ApiTodoControllerTest {

    @Autowired
    RestTestClient restClient;
    @Autowired
    TodoService todoService;

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
    void createError() {

        TodoRequest todoRequest = new TodoRequest("", "N");

        restClient
                .post().uri("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .body(todoRequest)
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    void notFound() {
        restClient
                .get().uri("/api/notfound")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.resultCode").isEqualTo("404 NOT_FOUND")
                .jsonPath("$.msg").isEqualTo("존재하지 않는 API 경로입니다.")
                .jsonPath("$.data").doesNotExist();
    }

    @Test
    void udpate() {

        TodoResponse response = todoService.create("test todo");
        TodoRequest todoRequest = new TodoRequest(response.getTitle() + " 2", "Y");
        restClient
                .put().uri("/api/todos/" + response.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(todoRequest)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

}