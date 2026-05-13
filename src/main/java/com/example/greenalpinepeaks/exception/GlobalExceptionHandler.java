package com.example.greenalpinepeaks.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(
        ResponseStatusException ex, HttpServletRequest request) {

        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(status.value())
            .error(status.getReasonPhrase())
            .message(ex.getReason())
            .path(request.getRequestURI())
            .build();

        LOG.warn("{} {} - {}", status.value(), request.getMethod(), request.getRequestURI());

        if (status == HttpStatus.NOT_FOUND) {
            LOG.info("ОШИБКА 404: Запрашиваемый ресурс не найден. Метод: {}, Путь: {}, Причина: {}",
                request.getMethod(), request.getRequestURI(), ex.getReason());
        } else if (status == HttpStatus.BAD_REQUEST) {
            LOG.info("ОШИБКА 400: Некорректный запрос. Метод: {}, Путь: {}, Причина: {}",
                request.getMethod(), request.getRequestURI(), ex.getReason());
        } else if (status == HttpStatus.CONFLICT) {
            LOG.info("ОШИБКА 409: Конфликт данных. Метод: {}, Путь: {}, Причина: {}",
                request.getMethod(), request.getRequestURI(), ex.getReason());
        } else {
            LOG.info("ОШИБКА {}: Проблема при обработке запроса. Метод: {}, Путь: {}, Причина: {}",
                status.value(), request.getMethod(), request.getRequestURI(), ex.getReason());
        }

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
        MethodArgumentNotValidException ex, HttpServletRequest request) {

        List<ErrorResponse.ValidationError> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(fieldError -> ErrorResponse.ValidationError.builder()
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .build())
            .toList();

        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message("Validation failed")
            .path(request.getRequestURI())
            .errors(errors)
            .build();

        LOG.warn("400 Validation failed - {} errors on {}", errors.size(), request.getRequestURI());

        LOG.info("ОШИБКА 400: Валидация входных данных не пройдена. Метод: {}, Путь: {}",
            request.getMethod(), request.getRequestURI());

        for (ErrorResponse.ValidationError error : errors) {
            LOG.info("   → Поле '{}': {}", error.getField(), error.getMessage());
        }

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
        IllegalArgumentException ex, HttpServletRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();

        LOG.error("400 Bad Request - {} - {}", request.getMethod(), request.getRequestURI());

        LOG.info("ОШИБКА 400: Недопустимый аргумент. Метод: {}, Путь: {}, Причина: {}",
            request.getMethod(), request.getRequestURI(), ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServiceExecutionException.class)
    public ResponseEntity<ErrorResponse> handleServiceExecutionException(
        ServiceExecutionException ex, HttpServletRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .message("Internal server error")
            .path(request.getRequestURI())
            .build();

        LOG.error("500 Internal Server Error - {} - {}", request.getMethod(), request.getRequestURI());

        LOG.error("ОШИБКА 500: Внутренняя ошибка сервера при выполнении сервисного метода. Метод: {}, Путь: {}",
            request.getMethod(), request.getRequestURI());
        LOG.error("   → Детали: {}", ex.getMessage(), ex);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(
        Exception ex, HttpServletRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .message("An unexpected error occurred")
            .path(request.getRequestURI())
            .build();

        LOG.error("500 Unexpected error - {} {} - {}", request.getMethod(), request.getRequestURI(),
            ex.getMessage(), ex);

        LOG.error("ОШИБКА 500: Непредвиденная ошибка в приложении. Метод: {}, Путь: {}",
            request.getMethod(), request.getRequestURI());
        LOG.error("   → Тип ошибки: {}, Причина: {}", ex.getClass().getSimpleName(), ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}