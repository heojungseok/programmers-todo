package com.todo.boundedContext.todo.app;

import com.todo.boundedContext.todo.domain.CompletionStatus;
import com.todo.boundedContext.todo.domain.Todo;
import com.todo.boundedContext.todo.dto.TodoUpdateRequest;
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

        Todo todo = new Todo(title);
        todoRepository.save(todo);

        return TodoResponse.from(todo);
    }

    @Transactional(readOnly = true)
    public Page<TodoResponse> listTodos(CompletionStatus completed, Pageable pageable) {
        Page<Todo> todoPage = completed != null
                ? todoRepository.findByCompleted(completed, pageable)
                : todoRepository.findAll(pageable);

        return todoPage.map(TodoResponse::from);
    }

    @Transactional
    public TodoResponse update(Long todoId, TodoUpdateRequest request) {
        Todo todo = findTodo(todoId);

        todo.update(request.getTitle(), request.getCompleted());
        // @LastModifiedDate는 flush 시점에 채워지므로 응답 전에 반영한다.
        todoRepository.flush();

        return TodoResponse.from(todo);
    }

    @Transactional
    public void delete(Long todoId) {
        Todo todo = findTodo(todoId);

        todoRepository.delete(todo);
    }

    @Transactional(readOnly = true)
    public TodoResponse detailTodo(Long todoId) {
        Todo todo = findTodo(todoId);

        return TodoResponse.from(todo);
    }

    private Todo findTodo(Long todoId) {
        return todoRepository.findById(todoId)
                .orElseThrow(() -> new NotFoundEntityException("존재하지 않는 할 일 입니다."));
    }
}
