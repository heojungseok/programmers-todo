package com.todo.boundedContext.todo.app;

import com.todo.boundedContext.todo.domain.Todo;
import com.todo.boundedContext.todo.dto.TodoRequest;
import com.todo.boundedContext.todo.dto.TodoResponse;
import com.todo.boundedContext.todo.out.TodoRepository;
import com.todo.global.exception.NotFoundEntityException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    @Transactional
    public TodoResponse create(String title) {

        Todo todo = Todo.createTodo(title);
        todoRepository.save(todo);

        return TodoResponse.from(todo);
    }

    @Transactional(readOnly = true)
    public Page<TodoResponse> listTodos(String completed, Pageable pageable) {
        boolean isFiltered = "Y".equalsIgnoreCase(completed) || "N".equalsIgnoreCase(completed);
        Page<Todo> todoPage  =
                isFiltered ? todoRepository.findByCompleted(completed, pageable)
                        :todoRepository.findAll(pageable);

        return todoPage.map(TodoResponse::from);
    }

    @Transactional
    public TodoResponse update(Long todoId, TodoRequest request) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new NotFoundEntityException("존재하지 않는 할 일 입니다."));

        return todo.updateTodo(request);
    }

    @Transactional
    public TodoResponse delete(Long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new NotFoundEntityException("존재하지 않는 할 일 입니다."));

        todoRepository.delete(todo);

        return TodoResponse.of(todo);
    }

    @Transactional(readOnly = true)
    public TodoResponse detailTodo(Long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new NotFoundEntityException("존재하지 않는 할 일 입니다."));

        return TodoResponse.from(todo);
    }
}
