package com.todo.boundedContext.todo.in;

import com.todo.boundedContext.todo.app.TodoService;
import com.todo.boundedContext.todo.domain.CompletionStatus;
import com.todo.boundedContext.todo.dto.TodoCreateRequest;
import com.todo.boundedContext.todo.dto.TodoResponse;
import com.todo.boundedContext.todo.dto.TodoUpdateRequest;
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

        TodoCreateRequest todoRequest = new TodoCreateRequest("dddd");

        restClient
                .post().uri("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .body(todoRequest)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void createError() {

        TodoCreateRequest todoRequest = new TodoCreateRequest("");

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
        TodoUpdateRequest todoRequest = new TodoUpdateRequest(response.getTitle() + " 2", CompletionStatus.Y);
        restClient
                .put().uri("/api/todos/" + response.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(todoRequest)
                .exchange()
                .expectStatus().is2xxSuccessful();
    }

}