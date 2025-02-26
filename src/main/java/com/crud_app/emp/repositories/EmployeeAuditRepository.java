package com.crud_app.emp.repositories;

import com.crud_app.emp.models.Employee;
import com.crud_app.emp.models.EmployeeAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeAuditRepository extends JpaRepository<EmployeeAudit, Long> {
    List<EmployeeAudit> findByEmployeeId(Long employeeId);
}
