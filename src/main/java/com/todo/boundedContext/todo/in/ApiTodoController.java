package com.todo.boundedContext.todo.in;

import com.todo.boundedContext.todo.app.TodoService;
import com.todo.boundedContext.todo.dto.TodoRequest;
import com.todo.boundedContext.todo.dto.TodoResponse;
import com.todo.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Todo API", description = "할 일 관리를 위한 API")
public class ApiTodoController {

    private final TodoService todoService;

    @GetMapping("/health")
    public String healthcheck() {

        return "ok";
    }

    @GetMapping("/todos")
    public ResponseEntity<ApiResponse<Page<TodoResponse>>> getTodos(
            @RequestParam(required = false) String completed, Pageable pageable
    ) {
        Page<TodoResponse> response = todoService.listTodos(completed, pageable);
        log.info("todo list response: {}", response);
        return ApiResponse.respond(HttpStatus.OK, "목록 조회 완료", response);
    }

    @GetMapping("/todos/{todoId}")
    public ResponseEntity<ApiResponse<TodoResponse>> detail(@PathVariable Long todoId) {
        TodoResponse response = todoService.detailTodo(todoId);
        log.info("detail response: {}", response);
        return ApiResponse.respond(HttpStatus.OK, "상세 조회 완료", response);
    }

    @PostMapping("/todos")
    public ResponseEntity<ApiResponse<TodoResponse>> create(@Valid @RequestBody TodoRequest request) {

        TodoResponse response = todoService.create(request.getTitle());
        log.info("create response: {}", response);
        URI location = URI.create("/api/todos/" + response.getId());
        return ResponseEntity.created(location)
                .body(ApiResponse.of(HttpStatus.CREATED, "할 일이 생성됐습니다.", response));
    }

    @PutMapping("/todos/{todoId}")
    public ResponseEntity<ApiResponse<TodoResponse>> update(@Valid @RequestBody TodoRequest request, @PathVariable Long todoId) {
        TodoResponse response = todoService.update(todoId, request);
        log.info("update response: {}", response);
        return ApiResponse.respond(HttpStatus.OK, "할 일이 변경됐습니다.", response);
    }

    @DeleteMapping("/todos/{todoId}")
    public ResponseEntity<ApiResponse<TodoResponse>> delete(@Valid @RequestBody TodoRequest request, @PathVariable Long todoId) {
        TodoResponse response = todoService.delete(todoId);
        log.info("delete response: {}", response);
        return ApiResponse.respond(HttpStatus.OK, "할 일이 삭제됐습니다.", response);
    }
}
