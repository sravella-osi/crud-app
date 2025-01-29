package com.crud_app.emp.exceptions;

public class EmployeeAlreadyExistsException extends RuntimeException{
    private String message;

    public EmployeeAlreadyExistsException(){ }

    public EmployeeAlreadyExistsException(String message){
        super(message);
        this.message = message;
    }
}
