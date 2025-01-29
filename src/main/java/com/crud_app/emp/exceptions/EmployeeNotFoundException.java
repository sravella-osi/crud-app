package com.crud_app.emp.exceptions;

public class EmployeeNotFoundException extends RuntimeException{
    private String message;

    public EmployeeNotFoundException(){ }

    public EmployeeNotFoundException(String message){
        super(message);
        this.message = message;
    }
}
