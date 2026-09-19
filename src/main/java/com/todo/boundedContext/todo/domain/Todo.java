package com.todo.boundedContext.todo.domain;

import com.todo.boundedContext.todo.dto.TodoRequest;
import com.todo.boundedContext.todo.dto.TodoResponse;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.security.PublicKey;
import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.*;

@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Getter
public class Todo {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @NotNull
    private String title;

    @Column(nullable = false)
    private String completed = "N";

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime modifiedAt;

    private Todo(String title) {
        this.title = title;
    }

    public static Todo createTodo(String title) {
        return new Todo(title);
    }

    public TodoResponse updateTodo(TodoRequest request) {
        this.title = request.getTitle();
        this.completed = request.getCompleted();

        return TodoResponse.from(this);
    }
}
