package com.example.FinTech.Wallet.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Data
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Object> handleFunds(InsufficientFundsException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "INSUFFICIENT_FUNDS");
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Object> handleConcurrency(ObjectOptimisticLockingFailureException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "CONCURRENCY_ERROR");
        body.put("message", "Transaction failed due to simultaneous updates. Please try again.");
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }
}