package com.titan.dispatch.web.exception;

import com.titan.dispatch.domain.exception.SafetyInterlockException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SafetyInterlockException.class)
    public ProblemDetail handleSafetyInterlock(SafetyInterlockException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        problemDetail.setTitle("Dispatch Safety Interlock Triggered");
        // Extensible for custom error codes parsed by the frontend
        problemDetail.setProperty("error_category", "COMPLIANCE");
        return problemDetail;
    }
}