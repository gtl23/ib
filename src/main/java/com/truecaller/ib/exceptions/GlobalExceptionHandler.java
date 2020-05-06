package com.truecaller.ib.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> BadRequestException(BadRequestException exception){
        Map<String, String> response = new HashMap<>();
        response.put("error_msg", exception.getErrorMsg());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> NotFoundException(NotFoundException exception){
        Map<String, String> response = new HashMap<>();
        response.put("error_msg", exception.getErrorMsg());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}
