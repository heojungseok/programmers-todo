package com.todo.boundedContext.todo.dto;

import com.todo.boundedContext.todo.domain.Todo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class TodoResponse {

    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String completed;

    private TodoResponse(Todo todo) {
        this(
                todo.getId(),
                todo.getTitle(),
                todo.getCreatedAt(),
                todo.getModifiedAt(),
                todo.getCompleted()
        );
    }

    public static TodoResponse from(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getCreatedAt(),
                todo.getModifiedAt(),
                todo.getCompleted()
        );
    }

    public static TodoResponse of(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                null,
                null,
                null,
                "N"
        );
    }
}
