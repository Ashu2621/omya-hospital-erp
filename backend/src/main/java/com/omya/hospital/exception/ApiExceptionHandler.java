package com.omya.hospital.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(
    ApiExceptionHandler.class
  );

  @ExceptionHandler(ApiException.class)
  ResponseEntity<ApiError> api(
    ApiException exception,
    HttpServletRequest request
  ) {
    return ResponseEntity.status(exception.getStatus()).body(
      error(
        exception.getStatus(),
        exception.getCode(),
        exception.getMessage(),
        request.getRequestURI(),
        Map.of()
      )
    );
  }

  @ExceptionHandler(NoSuchElementException.class)
  ResponseEntity<ApiError> notFound(
    NoSuchElementException exception,
    HttpServletRequest request
  ) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
      error(
        HttpStatus.NOT_FOUND,
        "RESOURCE_NOT_FOUND",
        "The requested resource was not found.",
        request.getRequestURI(),
        Map.of()
      )
    );
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  ResponseEntity<ApiError> conflict(
    DataIntegrityViolationException exception,
    HttpServletRequest request
  ) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(
      error(
        HttpStatus.CONFLICT,
        "DATA_CONFLICT",
        "The request conflicts with an existing record.",
        request.getRequestURI(),
        Map.of()
      )
    );
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiError> validation(
    MethodArgumentNotValidException exception,
    HttpServletRequest request
  ) {
    Map<String, String> fields = new LinkedHashMap<>();
    exception
      .getBindingResult()
      .getFieldErrors()
      .forEach(error ->
        fields.putIfAbsent(error.getField(), error.getDefaultMessage())
      );
    return ResponseEntity.badRequest().body(
      error(
        HttpStatus.BAD_REQUEST,
        "VALIDATION_FAILED",
        "One or more fields are invalid.",
        request.getRequestURI(),
        fields
      )
    );
  }

  @ExceptionHandler(ConstraintViolationException.class)
  ResponseEntity<ApiError> constraint(
    ConstraintViolationException exception,
    HttpServletRequest request
  ) {
    Map<String, String> fields = new LinkedHashMap<>();
    exception
      .getConstraintViolations()
      .forEach(violation ->
        fields.putIfAbsent(
          violation.getPropertyPath().toString(),
          violation.getMessage()
        )
      );
    return ResponseEntity.badRequest().body(
      error(
        HttpStatus.BAD_REQUEST,
        "VALIDATION_FAILED",
        "One or more parameters are invalid.",
        request.getRequestURI(),
        fields
      )
    );
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  ResponseEntity<ApiError> malformed(
    HttpMessageNotReadableException exception,
    HttpServletRequest request
  ) {
    return ResponseEntity.badRequest().body(
      error(
        HttpStatus.BAD_REQUEST,
        "MALFORMED_REQUEST",
        "The request body is missing or malformed.",
        request.getRequestURI(),
        Map.of()
      )
    );
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<ApiError> unexpected(
    Exception exception,
    HttpServletRequest request
  ) {
    log.error(
      "Unhandled API error path={}",
      request.getRequestURI(),
      exception
    );
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
      error(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "INTERNAL_ERROR",
        "The request could not be completed.",
        request.getRequestURI(),
        Map.of()
      )
    );
  }

  private ApiError error(
    HttpStatus status,
    String code,
    String message,
    String path,
    Map<String, String> fields
  ) {
    return new ApiError(
      Instant.now(),
      status.value(),
      code,
      message,
      path,
      fields
    );
  }

  public record ApiError(
    Instant timestamp,
    int status,
    String code,
    String message,
    String path,
    Map<String, String> fieldErrors
  ) {}
}
