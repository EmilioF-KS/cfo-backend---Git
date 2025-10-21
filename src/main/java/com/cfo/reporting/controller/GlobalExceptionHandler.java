package com.cfo.reporting.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler (EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFound(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler (DataIntegrityViolationException.class)
    public ResponseEntity<String> handleEntityNotFound(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Data Exception error");
    }
}
