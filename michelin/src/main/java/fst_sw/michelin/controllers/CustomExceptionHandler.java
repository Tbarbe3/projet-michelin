package fst_sw.michelin.controllers;

import fst_sw.michelin.exceptions.ConflictException;
import fst_sw.michelin.exceptions.NotFoundException;
import fst_sw.michelin.exceptions.ForbiddenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<String> handle(NotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Not found: " + exception.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handle(ConflictException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("Conflict: " + exception.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handle(ForbiddenException exception) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Forbidden: " + exception.getMessage());
    }
}