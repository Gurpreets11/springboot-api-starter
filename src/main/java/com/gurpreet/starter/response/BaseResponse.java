package com.gurpreet.starter.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Common response envelope used across all API endpoints.
 * Every controller should return ResponseEntity<BaseResponse<T>>.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {

    private boolean success;
    private String message;
    private int statusCode;
    private T data;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private Object errors; // validation errors / extra error detail, only set on failure

    public static <T> BaseResponse<T> success(T data, String message, int statusCode) {
        return BaseResponse.<T>builder()
                .success(true)
                .message(message)
                .statusCode(statusCode)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> BaseResponse<T> success(T data) {
        return success(data, "Success", 200);
    }

    public static <T> BaseResponse<T> error(String message, int statusCode) {
        return BaseResponse.<T>builder()
                .success(false)
                .message(message)
                .statusCode(statusCode)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> BaseResponse<T> error(String message, int statusCode, Object errors) {
        return BaseResponse.<T>builder()
                .success(false)
                .message(message)
                .statusCode(statusCode)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
