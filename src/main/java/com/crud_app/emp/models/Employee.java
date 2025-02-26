package com.crud_app.emp.models;


import com.crud_app.emp.listeners.EmployeeAuditListener;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "employee")
@EntityListeners(EmployeeAuditListener.class)
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "employee_name")
    private String name;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "email")
    private String email;

    @Transient
    private String createdBy;

    @Transient
    private String modifiedBy;

    public Employee(Integer id, String name, LocalDate dob, LocalDate hireDate, String jobTitle, String email) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.hireDate = hireDate;
        this.jobTitle = jobTitle;
        this.email = email;
    }

    public Employee() {
    }
//
//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public LocalDate getDob() {
//        return dob;
//    }
//
//    public void setDob(LocalDate dob) {
//        this.dob = dob;
//    }
//
//    public LocalDate getHireDate() {
//        return hireDate;
//    }
//
//    public void setHireDate(LocalDate hireDate) {
//        this.hireDate = hireDate;
//    }
//
//    public String getJobTitle() {
//        return jobTitle;
//    }
//
//    public void setJobTitle(String jobTitle) {
//        this.jobTitle = jobTitle;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
}
