package com.crud_app.emp.repositories;

import com.crud_app.emp.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    List<EmpSummary> findBy();
    EmpSummary findEmployeeSummaryById(Integer Id);

    Page<EmpSummary> findBy(Pageable pageable);
}
