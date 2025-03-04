package com.crud_app.emp.mapper;

import com.crud_app.emp.dto.EmployeeDTO;
import com.crud_app.emp.dto.EmployeeSummaryDTO;
import com.crud_app.emp.models.Employee;
import com.crud_app.emp.repositories.projections.EmpSummary;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class EmployeeMapper {

    private final String DATE_FORMAT = "yyyy-MM-dd";
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public Employee convertToEmployee(EmployeeDTO employeeDTO){
        Employee employee = new Employee();
        if(employeeDTO.getId()!=null){
            employee.setId(employeeDTO.getId());
        }
        try {
            employee.setDob(LocalDate.parse(employeeDTO.getDob(),dateTimeFormatter));
            employee.setHireDate(LocalDate.parse(employeeDTO.getHireDate(),dateTimeFormatter));
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }
        employee.setJobTitle(employeeDTO.getJobTitle());
        employee.setName(employeeDTO.getName());
        employee.setEmail(employeeDTO.getEmail());
        employee.setCreatedBy(employeeDTO.getCreatedBy());
        employee.setModifiedBy(employeeDTO.getModifiedBy());
        return employee;
    }

    public EmployeeDTO convertToEmployeeDTO(Employee employee){
        EmployeeDTO employeeDTO = new EmployeeDTO();
        try {
            employeeDTO.setDob(employee.getDob().toString());
            employeeDTO.setHireDate(employee.getHireDate().toString());
        } catch (DateTimeParseException e) {
            e.printStackTrace();
        }
        employeeDTO.setId(employee.getId());
        employeeDTO.setJobTitle(employee.getJobTitle());
        employeeDTO.setName(employee.getName());
        employeeDTO.setEmail(employee.getEmail());
        employeeDTO.setCreatedBy(employee.getCreatedBy());
        employeeDTO.setModifiedBy(employee.getModifiedBy());
        return employeeDTO;
    }

    public EmployeeSummaryDTO convertToEmployeeSummaryDTO(EmpSummary employee){
        EmployeeSummaryDTO employeeSummaryDTO = new EmployeeSummaryDTO();
        employeeSummaryDTO.setId(employee.getId());
        employeeSummaryDTO.setJobTitle(employee.getJobTitle());
        employeeSummaryDTO.setName(employee.getName());
        return employeeSummaryDTO;
    }

}
