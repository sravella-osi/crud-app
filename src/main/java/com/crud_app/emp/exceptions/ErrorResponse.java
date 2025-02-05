package com.crud_app.emp.exceptions;

import java.util.List;
import java.util.Map;

public class ErrorResponse {

    private int statusCode;
    private List<String> errors;

    private String ErrorListToString(){
        StringBuilder errorsString = new StringBuilder("\n\t[\n");
        for(String error: this.errors){
            errorsString.append("\t\t").append(error).append("\n");
        }
        errorsString.append("\t]");
        return errorsString.toString();
    }

    public ErrorResponse(List<String> errors){
        super();
        this.errors=errors;
    }

    public ErrorResponse() {
    }

    public ErrorResponse(int statusCode, List<String> errors) {
        this.statusCode = statusCode;
        this.errors = errors;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setMessage(List<String> errors) {
        this.errors = errors;
    }

    @Override
    public String toString() {
        return "\n{" +
                "\n\tstatusCode=" + statusCode +
                ", \n\terrors=" + this.ErrorListToString() +
                "\n}";
    }
}
