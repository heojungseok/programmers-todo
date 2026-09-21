package com.todo.global.handler;

import com.todo.global.exception.NotFoundEntityException;
import com.todo.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundEntityException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFoundEntity(NotFoundEntityException e) {
        return ApiResponse.respond(HttpStatus.NOT_FOUND, e.getMessage(), null);
    }
}
