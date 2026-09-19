package com.todo.boundedContext.todo.app;

import com.todo.boundedContext.todo.domain.Todo;
import com.todo.boundedContext.todo.dto.TodoRequest;
import com.todo.boundedContext.todo.dto.TodoResponse;
import com.todo.boundedContext.todo.out.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<TodoResponse> listTodos() {
        List<Todo> todoList = todoRepository.findAll();

        return todoList.stream()
                .map(TodoResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public TodoResponse update(Long todoId, TodoRequest request) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 할 일 입니다."));

        return todo.updateTodo(request);
    }

    @Transactional
    public TodoResponse delete(Long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 할 일 입니다."));

        todoRepository.delete(todo);

        return TodoResponse.of(todo);
    }

    @Transactional(readOnly = true)
    public TodoResponse detailTodo(Long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 할 일 입니다."));

        return TodoResponse.from(todo);
    }
}
