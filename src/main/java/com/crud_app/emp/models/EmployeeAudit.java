package com.crud_app.emp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "employee_audit")
public class EmployeeAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String action;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private  LocalDateTime modifiedAt;

    @NotNull
    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @NotNull
    @Column(name = "modified_by",nullable = false)
    private String modifiedBy;

    private Long employeeId;
    private String beforeChanges;
    private String afterChanges;
}
