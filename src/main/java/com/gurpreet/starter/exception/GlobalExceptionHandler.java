package com.gurpreet.starter.exception;

import com.gurpreet.starter.response.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Single place that converts every exception thrown anywhere in the app
 * into a consistent BaseResponse<?> JSON body.
 *
 * Consuming projects should NOT need to write their own try/catch blocks
 * in controllers/services for these common cases — just throw the
 * appropriate exception (or a custom one extending CustomException) and
 * this handles the HTTP mapping.
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    // ---- Application/business exceptions ----
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<BaseResponse<Object>> handleCustomException(CustomException ex, HttpServletRequest req) {
        log.warn("CustomException at [{}]: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(ex.getStatus())
                .body(BaseResponse.error(ex.getMessage(), ex.getStatus().value()));
    }

    // ---- Bean validation on @Valid request bodies ----
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        return ResponseEntity.badRequest()
                .body(BaseResponse.error("Validation failed", HttpStatus.BAD_REQUEST.value(), errors));
    }

    // ---- Malformed JSON body ----
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BaseResponse<Object>> handleUnreadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(BaseResponse.error("Malformed request body", HttpStatus.BAD_REQUEST.value()));
    }

    // ---- Wrong type in path/query param, e.g. ?id=abc when it expects Long ----
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Parameter '%s' should be of type %s",
                ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        return ResponseEntity.badRequest().body(BaseResponse.error(message, HttpStatus.BAD_REQUEST.value()));
    }

    // ---- DB constraint violations (unique key, FK, etc.) ----
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BaseResponse<Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.error("Data integrity violation", ex);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(BaseResponse.error("Data integrity violation - possibly a duplicate or invalid reference",
                        HttpStatus.CONFLICT.value()));
    }

    // ---- Spring Security ----
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<BaseResponse<Object>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(BaseResponse.error("Invalid username or password", HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(BaseResponse.error("You do not have permission to perform this action",
                        HttpStatus.FORBIDDEN.value()));
    }

    // ---- 404 route not found (requires spring.mvc.throw-exception-if-no-handler-found=true) ----
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<BaseResponse<Object>> handleNoHandler(NoHandlerFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.error("No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL(),
                        HttpStatus.NOT_FOUND.value()));
    }

    // ---- Catch-all fallback ----
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("Unhandled exception at [{}]", req.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Something went wrong. Please try again later.",
                        HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
