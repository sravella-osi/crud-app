package com.crud_app.emp.services;

import com.crud_app.emp.dto.EmployeeSummaryDTO;
import com.crud_app.emp.event.EmployeeUpdateEvent;
import com.crud_app.emp.exceptions.EmployeeAlreadyExistsException;
import com.crud_app.emp.exceptions.EmployeeNotFoundException;
import com.crud_app.emp.repositories.EmpSummary;
import com.crud_app.emp.dto.EmployeeDTO;
import com.crud_app.emp.models.Employee;
import com.crud_app.emp.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import static com.crud_app.emp.context.AuditContext.setEmployee;
import static com.crud_app.emp.context.AuditContext.setModifiedBy;

@Service
public class EmployeeService {

    private final String DATE_FORMAT = "yyyy-MM-dd";
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    private final ApplicationEventPublisher eventPublisher;

    public EmployeeService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Autowired
    EmployeeRepository employeeRepository;

    public EmployeeDTO saveEmployee(EmployeeDTO employeeDTO){
        if(employeeRepository.findByName(employeeDTO.getName()).isPresent()) {
            throw new EmployeeAlreadyExistsException("Employee with given name: " + employeeDTO.getName() + " already exists.");
        }
        Employee employee = convertToEmployee(employeeDTO);
        return convertToEmployeeDTO(employeeRepository.save(employee));
    }

    public EmployeeDTO getEmployee(Integer id){
        return convertToEmployeeDTO(employeeRepository.findById(id).orElseThrow(
                ()
                        -> new EmployeeNotFoundException("Employee with id " + id + " not found!")));
    }

    public List<EmployeeDTO> getAllEmployees(){
        if(employeeRepository.findAll().isEmpty()){
            throw new EmployeeNotFoundException("No employees found");
        }
        List<EmployeeDTO> employeeDTOList = new ArrayList<>();
        for(Employee employee : employeeRepository.findAll()){
            EmployeeDTO employeeDTO = convertToEmployeeDTO(employee);
            employeeDTOList.add(employeeDTO);
        }

        return employeeDTOList;
    }

    public Page<EmployeeSummaryDTO> getALlEmployees(Pageable pageable){
        return employeeRepository.findBy(pageable).map(
                empSummary -> new EmployeeSummaryDTO(
                        empSummary.getId(),
                        empSummary.getName(),
                        empSummary.getJobTitle()
                )
        );
    }

    @Transactional
    public EmployeeDTO updateEmployee(EmployeeDTO employeeDTO, Integer id){
        employeeRepository.findById(id).orElseThrow(
                ()
                        -> new EmployeeNotFoundException("Employee with id " + id + " not found!"));
        Employee employee = employeeRepository.getReferenceById(id);
        employee = convertToEmployee(employeeDTO);
        employee.setId(id);
        setModifiedBy(employee.getModifiedBy());
        Employee savedEmployee = employeeRepository.saveAndFlush(employee);
        setEmployee(savedEmployee);
        employeeDTO = convertToEmployeeDTO(savedEmployee);
        eventPublisher.publishEvent(new EmployeeUpdateEvent(savedEmployee));
        return employeeDTO;
    }

    public String deleteEmployee(Integer id, String modifiedBy){
        if(employeeRepository.existsById(id)){
            Employee employee = employeeRepository.getReferenceById(id);
            employee.setModifiedBy(modifiedBy);
            setModifiedBy(modifiedBy);
            employeeRepository.delete(employee);
            return "Employee with id: " + id + " deleted.";
        }
        else {
            throw new EmployeeNotFoundException("Employee with id " + id + " not found");
        }
    }

    private Employee convertToEmployee(EmployeeDTO employeeDTO){
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

    private EmployeeDTO convertToEmployeeDTO(Employee employee){
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

    private EmployeeSummaryDTO convertToEmployeeSummaryDTO(EmpSummary employee){
        EmployeeSummaryDTO employeeSummaryDTO = new EmployeeSummaryDTO();
        employeeSummaryDTO.setId(employee.getId());
        employeeSummaryDTO.setJobTitle(employee.getJobTitle());
        employeeSummaryDTO.setName(employee.getName());
        return employeeSummaryDTO;
    }

    public List<EmployeeSummaryDTO> getEmployeesSummary() {
        List<EmployeeSummaryDTO> employeeSummaryDTOList = new ArrayList<>();
        for(EmpSummary employee : employeeRepository.findBy()){
            EmployeeSummaryDTO employeeSummaryDTO = convertToEmployeeSummaryDTO(employee);
            employeeSummaryDTOList.add(employeeSummaryDTO);
        }

        return employeeSummaryDTOList;
    }

    public EmployeeSummaryDTO getEmployeeSummary(Integer id){
        return convertToEmployeeSummaryDTO(employeeRepository.findEmployeeSummaryById(id).orElseThrow(
                ()
                        -> new EmployeeNotFoundException("Employee with id " + id + " not found!")));
    }

}
