package com.todo.global.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ApiResponse<T> {
    private final String resultCode;
    private final String msg;
    private final T data;

    public static <T> ApiResponse<T> of(HttpStatusCode status, String msg, T data) {
        return new ApiResponse<>(toResultCode(status), msg, data);
    }

    // HTTP 상태와 resultCode를 같은 값에서 만들어 둘이 어긋나지 않게 한다.
    public static <T> ResponseEntity<ApiResponse<T>> respond(HttpStatus status, String msg, T data) {
        return ResponseEntity.status(status).body(of(status, msg, data));
    }

    private static String toResultCode(HttpStatusCode status) {
        HttpStatus resolved = HttpStatus.resolve(status.value());
        return resolved != null ? resolved.toString() : String.valueOf(status.value());
    }
}
