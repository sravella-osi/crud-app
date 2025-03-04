package com.crud_app.emp.models;

import com.crud_app.emp.listeners.EmployeeAuditListener;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

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
}
