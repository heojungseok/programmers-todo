package com.todo.global.handler;

import com.todo.global.exception.NotFoundEntityException;
import com.todo.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundEntityException.class)
    public ApiResponse<String> handlerNotFoundEntityException(Exception e) {
        return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
