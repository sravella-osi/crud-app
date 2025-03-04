package com.crud_app.emp.services;

import com.crud_app.emp.dto.EmployeeSummaryDTO;
import com.crud_app.emp.event.EmployeeUpdateEvent;
import com.crud_app.emp.exceptions.EmployeeAlreadyExistsException;
import com.crud_app.emp.exceptions.EmployeeNotFoundException;
import com.crud_app.emp.mapper.EmployeeMapper;
import com.crud_app.emp.repositories.projections.EmpSummary;
import com.crud_app.emp.dto.EmployeeDTO;
import com.crud_app.emp.models.Employee;
import com.crud_app.emp.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.crud_app.emp.context.AuditContext.*;

@Service
public class EmployeeService {

    private final ApplicationEventPublisher eventPublisher;

    public EmployeeService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Autowired
    EmployeeMapper employeeMapper;

    @Autowired
    EmployeeRepository employeeRepository;

    public EmployeeDTO saveEmployee(EmployeeDTO employeeDTO){
        if(employeeRepository.findByName(employeeDTO.getName()).isPresent()) {
            throw new EmployeeAlreadyExistsException("Employee with given name: " + employeeDTO.getName() + " already exists.");
        }
        Employee employee = employeeMapper.convertToEmployee(employeeDTO);
        return employeeMapper.convertToEmployeeDTO(employeeRepository.save(employee));
    }

    public EmployeeDTO getEmployee(Integer id){
        return employeeMapper.convertToEmployeeDTO(employeeRepository.findById(id).orElseThrow(
                ()
                        -> new EmployeeNotFoundException(id)));
    }

    public List<EmployeeDTO> getAllEmployees(){
        if(employeeRepository.findAll().isEmpty()){
            throw new EmployeeNotFoundException("No employees found");
        }
        List<EmployeeDTO> employeeDTOList = new ArrayList<>();
        for(Employee employee : employeeRepository.findAll()){
            EmployeeDTO employeeDTO = employeeMapper.convertToEmployeeDTO(employee);
            employeeDTOList.add(employeeDTO);
        }

        return employeeDTOList;
    }

    public Page<EmployeeSummaryDTO> getAllEmployees(Pageable pageable){
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
                        -> new EmployeeNotFoundException(id));
        Employee employee = employeeRepository.getReferenceById(id) ;
        setPrevEmployee(getNewEmployee(employee));
        employee = employeeMapper.convertToEmployee(employeeDTO);
        employee.setId(id);
        setModifiedBy(employee.getModifiedBy());
        Employee savedEmployee = employeeRepository.saveAndFlush(employee);
        setEmployee(savedEmployee);
        employeeDTO = employeeMapper.convertToEmployeeDTO(savedEmployee);
        eventPublisher.publishEvent(new EmployeeUpdateEvent(savedEmployee));
        return employeeDTO;
    }

    public String deleteEmployee(Integer id, String modifiedBy){
        if(employeeRepository.existsById(id)){
            Employee employee = employeeRepository.getReferenceById(id);
            setPrevEmployee(getNewEmployee(employee));
            employee.setModifiedBy(modifiedBy);
            setModifiedBy(modifiedBy);
            employeeRepository.delete(employee);
            return "Employee with id: " + id + " deleted.";
        }
        else {
            throw new EmployeeNotFoundException(id);
        }
    }

    public List<EmployeeSummaryDTO> getEmployeesSummary() {
        List<EmployeeSummaryDTO> employeeSummaryDTOList = new ArrayList<>();
        for(EmpSummary employee : employeeRepository.findBy()){
            EmployeeSummaryDTO employeeSummaryDTO = employeeMapper.convertToEmployeeSummaryDTO(employee);
            employeeSummaryDTOList.add(employeeSummaryDTO);
        }

        return employeeSummaryDTOList;
    }

    public EmployeeSummaryDTO getEmployeeSummary(Integer id){
        return employeeMapper.convertToEmployeeSummaryDTO(employeeRepository.findEmployeeSummaryById(id).orElseThrow(
                ()
                        -> new EmployeeNotFoundException(id)));
    }

    private Employee getNewEmployee(Employee employee){
        return new Employee(
                employee.getId(),
                employee.getName(),
                employee.getDob(),
                employee.getHireDate(),
                employee.getJobTitle(),
                employee.getEmail());
    }
}
