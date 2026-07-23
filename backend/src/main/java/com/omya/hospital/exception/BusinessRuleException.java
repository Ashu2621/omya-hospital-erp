package com.omya.hospital.exception;

import org.springframework.http.HttpStatus;

public class BusinessRuleException extends ApiException {

  public BusinessRuleException(String message) {
    super(HttpStatus.UNPROCESSABLE_ENTITY, "BUSINESS_RULE_VIOLATION", message);
  }

  public BusinessRuleException(String message, Throwable cause) {
    this(message);
    initCause(cause);
  }
}
