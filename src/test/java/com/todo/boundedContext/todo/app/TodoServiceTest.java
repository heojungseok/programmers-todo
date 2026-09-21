package com.todo.boundedContext.todo.app;

import com.todo.boundedContext.todo.domain.Todo;
import com.todo.boundedContext.todo.dto.TodoRequest;
import com.todo.boundedContext.todo.dto.TodoResponse;
import com.todo.boundedContext.todo.out.TodoRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class TodoServiceTest {

    @Autowired
    TodoRepository todoRepository;
    @Autowired
    TodoService todoService;
    @Autowired
    EntityManager entityManager;

    @Test
    @Transactional
    void delete() {
        String title = "title";

        TodoResponse response = todoService.create(title);

        long before = todoRepository.count();

        todoService.delete(response.getId());

        long after = todoRepository.count();

        assertThat(before).isNotEqualTo(after);

    }

    @Test
    @Transactional
    void update() {

        TodoResponse origin = todoService.create("title");
        TodoRequest request = new TodoRequest("바뀐 제목", "Y");

        todoService.update(origin.getId(), request);

        entityManager.flush();
        entityManager.clear();

        Todo refreshed = todoRepository.findById(origin.getId()).get();

        log.info("{}, {}", refreshed.getTitle(), refreshed.getModifiedAt());
        assertThat(origin.getModifiedAt()).isNotEqualTo(refreshed.getModifiedAt());
        assertThat(origin.getTitle()).isNotEqualTo(refreshed.getTitle());
        assertThat(origin.getCompleted()).isNotEqualTo(refreshed.getCompleted());

    }

}