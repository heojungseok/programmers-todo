package com.todo.boundedContext.todo.in;

import com.todo.boundedContext.todo.app.TodoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * HTTP 상태 코드와 응답 본문 모양({resultCode, msg, data})을 검증한다.
 */
@SpringBootTest
@AutoConfigureRestTestClient
@ActiveProfiles("test")
class ApiTodoResponseTest {

    private static final long MISSING_ID = Long.MAX_VALUE;

    @Autowired
    RestTestClient restClient;
    @Autowired
    TodoService todoService;

    private Long createTodo(String title) {
        return todoService.create(title).getId();
    }

    private RestTestClient.ResponseSpec postJson(String uri, String json) {
        return restClient.post().uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange();
    }

    private RestTestClient.ResponseSpec putJson(String uri, String json) {
        return restClient.put().uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(json)
                .exchange();
    }

    @Test
    void 생성하면_201과_Location을_반환한다() {
        postJson("/api/todos", "{\"title\":\"우유 사기\"}")
                .expectStatus().isCreated()
                .expectHeader().valueMatches("Location", "/api/todos/\\d+")
                .expectBody()
                .jsonPath("$.resultCode").isEqualTo("201 CREATED")
                .jsonPath("$.data.title").isEqualTo("우유 사기")
                .jsonPath("$.data.completed").isEqualTo("N")
                .jsonPath("$.data.createdAt").exists();
    }

    @Test
    void 공백_제목은_400과_필드_오류를_반환한다() {
        postJson("/api/todos", "{\"title\":\"   \"}")
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.resultCode").isEqualTo("400 BAD_REQUEST")
                .jsonPath("$.data[0].field").isEqualTo("title")
                .jsonPath("$.data[0].message").isEqualTo("제목은 필수입니다.");
    }

    @Test
    void 제목이_200자를_넘으면_400을_반환한다() {
        String title = "a".repeat(201);

        postJson("/api/todos", "{\"title\":\"" + title + "\"}")
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.data[0].message").isEqualTo("제목은 200자를 초과할 수 없습니다.");
    }

    @Test
    void 깨진_JSON은_400을_반환한다() {
        postJson("/api/todos", "{\"title\":")
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.resultCode").isEqualTo("400 BAD_REQUEST")
                .jsonPath("$.data").doesNotExist();
    }

    @Test
    void 없는_할_일_조회는_404를_반환한다() {
        restClient.get().uri("/api/todos/" + MISSING_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.resultCode").isEqualTo("404 NOT_FOUND")
                .jsonPath("$.msg").isEqualTo("존재하지 않는 할 일 입니다.");
    }

    @Test
    void 없는_할_일_수정은_404를_반환한다() {
        putJson("/api/todos/" + MISSING_ID, "{\"title\":\"x\",\"completed\":\"Y\"}")
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.resultCode").isEqualTo("404 NOT_FOUND");
    }

    @Test
    void 없는_할_일_삭제는_404를_반환한다() {
        restClient.delete().uri("/api/todos/" + MISSING_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.resultCode").isEqualTo("404 NOT_FOUND");
    }

    @Test
    void 숫자가_아닌_id는_400을_반환한다() {
        restClient.get().uri("/api/todos/abc")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.msg").isEqualTo("'todoId' 값의 형식이 올바르지 않습니다.");
    }

    @Test
    void 완료_처리하면_completed가_Y로_바뀐다() {
        Long id = createTodo("완료할 일");

        putJson("/api/todos/" + id, "{\"title\":\"완료할 일\",\"completed\":\"Y\"}")
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.resultCode").isEqualTo("200 OK")
                .jsonPath("$.data.completed").isEqualTo("Y");
    }

    @Test
    void 수정_시_completed가_없으면_500이_아니라_400을_반환한다() {
        Long id = createTodo("수정할 일");

        putJson("/api/todos/" + id, "{\"title\":\"수정\"}")
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.data[0].field").isEqualTo("completed");
    }

    @Test
    void 수정_시_허용되지_않은_completed는_400을_반환한다() {
        Long id = createTodo("수정할 일");

        putJson("/api/todos/" + id, "{\"title\":\"수정\",\"completed\":\"MAYBE\"}")
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.resultCode").isEqualTo("400 BAD_REQUEST");
    }

    @Test
    void 완료_여부로_목록을_필터링한다() {
        Long id = createTodo("필터 대상");
        putJson("/api/todos/" + id, "{\"title\":\"필터 대상\",\"completed\":\"Y\"}")
                .expectStatus().isOk();

        restClient.get().uri("/api/todos?completed=Y&size=100")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.content[?(@.id == " + id + ")]").exists()
                .jsonPath("$.data.content[?(@.completed != 'Y')]").isEmpty();
    }

    @Test
    void 허용되지_않은_필터_값은_400을_반환한다() {
        restClient.get().uri("/api/todos?completed=MAYBE")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.msg").isEqualTo("'completed' 값은 [Y, N] 중 하나여야 합니다.");
    }

    @Test
    void 없는_필드로_정렬하면_400을_반환한다() {
        restClient.get().uri("/api/todos?sort=nope")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.resultCode").isEqualTo("400 BAD_REQUEST")
                .jsonPath("$.msg").isEqualTo("'nope'은(는) 정렬할 수 없는 필드입니다.");
    }

    @Test
    void 삭제하면_204를_반환하고_이후_조회는_404다() {
        Long id = createTodo("삭제할 일");

        restClient.delete().uri("/api/todos/" + id)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        restClient.get().uri("/api/todos/" + id)
                .exchange()
                .expectStatus().isNotFound();
    }
}
