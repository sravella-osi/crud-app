package com.crud_app.emp.exceptions;

import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = EmployeeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public @ResponseBody ErrorResponse handleException(EmployeeNotFoundException e) {
        List<String> errors =  new ArrayList<>();
        errors.add(e.getMessage());
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), errors);
    }

    @ExceptionHandler(value = EmployeeAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public @ResponseBody ErrorResponse handleException(EmployeeAlreadyExistsException e) {
        List<String> errors =  new ArrayList<>();
        errors.add(e.getMessage());
        return new ErrorResponse(HttpStatus.CONFLICT.value(), errors);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public @ResponseBody ErrorResponse handleException(MethodArgumentNotValidException e){
        List<String> fieldOrder = List.of("name", "dob", "hireDate", "jobTitle", "email");

        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .sorted(Comparator.comparingInt(error -> fieldOrder.indexOf(error.getField())))
                        .map(FieldError::getDefaultMessage)
                                .collect(Collectors.toList()));
    }

    @ExceptionHandler(value = PropertyReferenceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody ErrorResponse handleException(PropertyReferenceException e){
        List<String> errors =  new ArrayList<>();
        errors.add(e.getMessage());
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), errors);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public @ResponseBody ErrorResponse handleException(Exception ex) {
        // Customize the error message and status code
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),errors);
    }

}
