package com.crud_app.emp.listeners;

import com.crud_app.emp.context.SpringContextHelper;
import com.crud_app.emp.event.EmployeeUpdateEvent;
import com.crud_app.emp.models.Employee;
import com.crud_app.emp.models.EmployeeAudit;
import com.crud_app.emp.repositories.EmployeeAuditRepository;
import jakarta.persistence.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

import static com.crud_app.emp.context.AuditContext.*;

@Component
public class EmployeeAuditListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void auditUpdate(EmployeeUpdateEvent event){
        System.out.println("Calling audit for update");
        saveEmployeeUpdateAudit(getEmployee(),"UPDATE");
        clearEmployee();
    }

    @PostPersist
    public void afterCreate(Employee employee){
        System.out.println("After persisting employee ------------------------");
        saveEmployeeAudit(employee,"INSERT");
    }

    @PostRemove
    public void beforeDelete(Employee employee){
        System.out.println("after deleting employee ------------------------");
        saveEmployeeAudit(employee,"DELETE");
    }

    @Transactional
    public void saveEmployeeUpdateAudit(Employee employee, String action){
        EmployeeAuditRepository employeeAuditRepository = SpringContextHelper.getBean(EmployeeAuditRepository.class);

        EmployeeAudit employeeAudit = new EmployeeAudit();

        setEmployeeAudit(employeeAudit,employee,action,employeeAuditRepository);

        try {
            System.out.println("Saving Employee Audit : " + employeeAudit);
            EmployeeAudit saved = employeeAuditRepository.saveAndFlush(employeeAudit);
            System.out.println("Employee Audit saved successfully! : " + saved);
        } catch (Exception e) {
            System.out.println("Failed to save Employee Audit: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void saveEmployeeAudit(Employee employee, String action){
        EmployeeAuditRepository employeeAuditRepository = SpringContextHelper.getBean(EmployeeAuditRepository.class);

        EmployeeAudit employeeAudit = new EmployeeAudit();

        setEmployeeAudit(employeeAudit,employee,action,employeeAuditRepository);

        try {
            System.out.println("Saving Employee Audit : " + employeeAudit);
            EmployeeAudit saved = employeeAuditRepository.save(employeeAudit);
            System.out.println("Employee Audit saved successfully! : " + saved);
        } catch (Exception e) {
            System.out.println("Failed to save Employee Audit: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private void setEmployeeAudit(EmployeeAudit employeeAudit, Employee employee, String action, EmployeeAuditRepository employeeAuditRepository){
        employeeAudit.setEmployeeId(employee.getId().longValue());
        employeeAudit.setAction(action);
        employeeAudit.setDob(employee.getDob());
        employeeAudit.setEmail(employee.getEmail());
        employeeAudit.setName(employee.getName());
        employeeAudit.setHireDate(employee.getHireDate());
        employeeAudit.setJobTitle(employee.getJobTitle());
        if(action.equals("INSERT")) {
            employeeAudit.setCreatedBy(employee.getCreatedBy());
            if(employee.getModifiedBy()==null||employee.getModifiedBy().isBlank()){
                employeeAudit.setModifiedBy(employee.getCreatedBy());
            }
            else {
                employeeAudit.setModifiedBy(employee.getModifiedBy());
            }
            employeeAudit.setCreatedAt(LocalDateTime.now());
        }
        else{
            employeeAudit.setCreatedBy(
                    employeeAuditRepository
                            .findByEmployeeId(employeeAudit.getEmployeeId())
                            .getFirst()
                            .getCreatedBy());
            employeeAudit.setModifiedBy(getModifiedBy());
            clearModifiedBy();
            employeeAudit.setCreatedAt(
                    employeeAuditRepository
                            .findByEmployeeId(employeeAudit.getEmployeeId())
                            .getLast()
                            .getCreatedAt());
        }
        employeeAudit.setModifiedAt(LocalDateTime.now());
    }
}


