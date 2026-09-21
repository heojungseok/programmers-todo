package com.todo.boundedContext.todo.in;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest
@AutoConfigureRestTestClient
@ActiveProfiles("test")
class ApiTodoControllerTest {

    @Autowired
    RestTestClient restClient;

    @Test
    void test1() throws Exception {
        restClient
                .get().uri("/api/health")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(String.class).isEqualTo("ok");
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
}
