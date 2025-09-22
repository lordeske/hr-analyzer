package com.hr_analyzer.cv.exception;



import com.hr_analyzer.auth.exceptions.UserAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>>handleValidateException(MethodArgumentNotValidException ex)
    {
        Map<String, Object> body = new HashMap<>();
        Map<String , String> errors = new HashMap<>();

        for(FieldError error : ex.getBindingResult().getFieldErrors())
        {

            errors.put(error.getField(), error.getDefaultMessage());

        }

        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("errors", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);

    }


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String,Object>>handleEntityNotFound(EntityNotFoundException ex)
    {

        Map<String, Object> body = new HashMap<>();

        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", ex.getMessage());

        return new ResponseEntity<>(body,HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String,Object>> handleRuntimeException(RuntimeException ex)
    {

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex)
    {
        Map<String, Object> body = new HashMap<>();

        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Doslo je do greske, kontaktaktirajte nas");

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex)
    {

        Map<String, Object> body = new HashMap<>();

        List<String> errors = ex.getConstraintViolations()
                .stream()
                .map(cv -> cv.getPropertyPath()+": "+ cv.getMessage())
                .toList();

        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("errors", errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);

    }


    @ExceptionHandler(AiAnalysisException.class)
    public ResponseEntity<Map<String , Object>> handleAiAnalysisException(AiAnalysisException ex)
    {
        Map<String, Object> body = new HashMap<>();

        body.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        body.put("error", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.SERVICE_UNAVAILABLE);

    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotExist(UserAlreadyExistsException ex)
    {
        Map<String, Object> body = new HashMap<>();

        body.put("status", HttpStatus.CONFLICT.value());
        body.put("error", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.CONFLICT);


    }

    @ExceptionHandler(CvNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCvNotFound(CvNotFoundException ex)
    {


        Map<String, Object> body = new HashMap<>();

        body.put("status", HttpStatus.NO_CONTENT);
        body.put("error", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NO_CONTENT);

    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.NOT_FOUND);
        body.put("error", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.FORBIDDEN);
        body.put("error", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalStateException(IllegalStateException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.UNAUTHORIZED);
        body.put("error", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }




}
