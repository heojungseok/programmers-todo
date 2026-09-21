package com.todo.global.response;

import org.springframework.validation.FieldError;

public record FieldErrorResponse(String field, String message) {

    public static FieldErrorResponse from(FieldError error) {
        return new FieldErrorResponse(error.getField(), error.getDefaultMessage());
    }
}
