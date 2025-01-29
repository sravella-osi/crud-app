package com.crud_app.emp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class EmployeeDTO {

    private Integer id;
    @Size(max = 70, message = "Maximum 70 characters in employee name")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Employee Name must be valid")
    private String name;
    @Pattern(regexp = "^(19|20)\\d{2}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])", message = "Date needs to be in 'yyyy-MM-dd' format")
    private String dob;
    @Pattern(regexp = "^(19|20)\\d{2}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])", message = "Date needs to be in 'yyyy-MM-dd' format")
    private String hireDate;
    @Size(max = 50, message = "Maximum 50 characters in job title")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Job title must be valid")
    private String jobTitle;
    @Email
    private String email;

    public EmployeeDTO() {
    }

    public EmployeeDTO(Integer id, String name, String dob, String hireDate, String jobTitle, String email) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.hireDate = hireDate;
        this.jobTitle = jobTitle;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getHireDate() {
        return hireDate;
    }

    public void setHireDate(String hireDate) {
        this.hireDate = hireDate;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
