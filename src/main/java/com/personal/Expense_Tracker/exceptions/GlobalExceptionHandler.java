package com.personal.Expense_Tracker.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handles InsufficientBalanceException specifically
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientBalance(InsufficientBalanceException ex) {
        log.error("Insufficient balance: {}", ex.getMessage());
        Map<String, Object> error = new HashMap<>();
        error.put("status", 400);
        error.put("error", "Insufficient Balance");
        error.put("message", ex.getMessage());
        error.put("timestamp", new Date());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Handles ALL business logic errors (friend system, debt, receivable, user)
    // e.g. "Already friends", "User not found", "Request not found"
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        log.error("Business error: {}", ex.getMessage());
        Map<String, Object> error = new HashMap<>();
        error.put("status", 400);
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());
        error.put("timestamp", new Date());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Catches any unexpected bug (NullPointerException, etc.)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        Map<String, Object> error = new HashMap<>();
        error.put("status", 500);
        error.put("error", "Internal Server Error");
        error.put("message", "Something went wrong. Please try again.");
        error.put("timestamp", new Date());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
