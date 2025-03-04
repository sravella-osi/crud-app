package com.crud_app.emp.repositories;

import com.crud_app.emp.models.EmployeeAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeAuditRepository extends JpaRepository<EmployeeAudit, Long> {
    List<EmployeeAudit> findByEmployeeId(Long employeeId);
}
