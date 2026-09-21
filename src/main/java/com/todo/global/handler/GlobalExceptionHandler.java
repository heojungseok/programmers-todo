package com.todo.global.handler;

import com.todo.global.exception.NotFoundEntityException;
import com.todo.global.response.ApiResponse;
import com.todo.global.response.FieldErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Arrays;
import java.util.List;

/**
 * 모든 오류를 ApiResponse 모양으로 돌려준다.
 * Spring MVC 내부 예외(검증, 본문 파싱, 405, 415, 없는 URL 등)는 ResponseEntityExceptionHandler가 받고,
 * 본문을 만드는 handleExceptionInternal 을 재정의해 모양을 통일한다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundEntityException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFoundEntity(NotFoundEntityException e) {
        return ApiResponse.respond(HttpStatus.NOT_FOUND, e.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception e) {
        log.error("처리되지 않은 예외", e);
        return ApiResponse.respond(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.", null);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request
    ) {
        List<FieldErrorResponse> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new FieldErrorResponse(e.getField(), e.getDefaultMessage()))
                .toList();
        return ResponseEntity.status(status).headers(headers)
                .body(ApiResponse.of(status, "입력값이 올바르지 않습니다.", errors));
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request
    ) {
        return handleExceptionInternal(ex, typeMismatchMessage(ex), headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request
    ) {
        String msg = "요청 본문을 읽을 수 없습니다. JSON 형식과 필드 값을 확인해 주세요.";
        return handleExceptionInternal(ex, msg, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(
            NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request
    ) {
        return handleExceptionInternal(ex, "존재하지 않는 API 경로입니다.", headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request
    ) {
        return ResponseEntity.status(statusCode).headers(headers)
                .body(ApiResponse.of(statusCode, resolveMessage(ex, body), null));
    }

    private String typeMismatchMessage(TypeMismatchException ex) {
        String name = ex.getPropertyName();
        Class<?> type = ex.getRequiredType();
        if (type != null && type.isEnum()) {
            return "'%s' 값은 %s 중 하나여야 합니다.".formatted(name, Arrays.toString(type.getEnumConstants()));
        }
        return "'%s' 값의 형식이 올바르지 않습니다.".formatted(name);
    }

    private String resolveMessage(Exception ex, Object body) {
        if (body instanceof String msg) {
            return msg;
        }
        if (body instanceof ProblemDetail detail && detail.getDetail() != null) {
            return detail.getDetail();
        }
        return ex.getMessage();
    }
}
