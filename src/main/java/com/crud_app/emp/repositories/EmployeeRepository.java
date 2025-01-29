package com.crud_app.emp.repositories;

import com.crud_app.emp.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    List<EmpSummary> findBy();
    Optional<EmpSummary> findEmployeeSummaryById(Integer Id);
    Optional<Employee> findByName(String name);
    Page<EmpSummary> findBy(Pageable pageable);
}
