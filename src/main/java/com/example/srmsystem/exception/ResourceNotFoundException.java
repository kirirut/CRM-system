package com.example.srmsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

  @SuppressWarnings("checkstyle:Indentation")
  public ResourceNotFoundException(String message) {
    super(message);
  }
}
