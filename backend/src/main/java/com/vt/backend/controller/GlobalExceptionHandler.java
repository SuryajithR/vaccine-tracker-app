package com.vt.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleRuntime(RuntimeException ex) {
    Map<String, String> res = new HashMap<>();
    res.put("message", ex.getMessage());
    return res;
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Map<String, String> handleConstraint(DataIntegrityViolationException ex) {
    Map<String, String> res = new HashMap<>();
    res.put("message", "Duplicate entry: this vaccine already has a record for the selected date.");
    return res;
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public Map<String, String> handleOther(Exception ex) {
    Map<String, String> res = new HashMap<>();
    res.put("message", "Server error occurred.");
    return res;
  }
}
