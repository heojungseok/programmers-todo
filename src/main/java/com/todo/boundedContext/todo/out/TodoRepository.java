package com.todo.boundedContext.todo.out;

import com.todo.boundedContext.todo.domain.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    Page<Todo> findByCompleted(String completed, Pageable pageable);

    Page<Todo> findAll(Pageable pageable);
}
