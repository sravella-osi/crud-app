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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<Map<String,String>> changes = getChangedFieldAndPreviousValue(employee);
        if(action.equals("DELETE")){
            changes = preDeleteValues();
        }
        Map<String,String> beforeChanges = changes.get(0);
        Map<String,String> afterChanges = changes.get(1);
        if(action.equals("INSERT")) {
            beforeChanges = new HashMap<>();
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
        employeeAudit.setBeforeChanges(beforeChanges.toString());
        employeeAudit.setAfterChanges(afterChanges.toString());

    }

    private List<Map<String,String>> getChangedFieldAndPreviousValue(Employee employee){
        Employee previousDetails = getPrevEmployee();
        clearPrevEmployee();
        List<String> changedFields = new ArrayList<>();
        List<String> previousValues = new ArrayList<>();
        List<String> currentValues = new ArrayList<>();

        if(previousDetails == null){
            previousValues.add(null);
            previousValues.add(null);
            previousValues.add(null);
            previousValues.add(null);
            previousValues.add(null);
            changedFields.add("Name");
            changedFields.add("Email");
            changedFields.add("DOB");
            changedFields.add("HireDate");
            changedFields.add("JobTitle");
            currentValues.add(employee.getName());
            currentValues.add(employee.getEmail());
            currentValues.add(employee.getDob().toString());
            currentValues.add(employee.getHireDate().toString());
            currentValues.add(employee.getJobTitle());
        } else {
            if(!previousDetails.getName().equals(employee.getName())){
                changedFields.add("Name");
                previousValues.add(previousDetails.getName());
                currentValues.add(employee.getName());
            }
            if(!previousDetails.getEmail().equals(employee.getEmail())){
                changedFields.add("Email");
                previousValues.add(previousDetails.getEmail());
                currentValues.add(employee.getEmail());
            }
            if(previousDetails.getDob() != employee.getDob()){
                changedFields.add("DOB");
                previousValues.add(previousDetails.getDob().toString());
                currentValues.add(employee.getDob().toString());
            }
            if(previousDetails.getHireDate() != employee.getHireDate()){
                changedFields.add("HireDate");
                previousValues.add(previousDetails.getHireDate().toString());
                currentValues.add(employee.getHireDate().toString());
            }
            if(!previousDetails.getJobTitle().equals(employee.getJobTitle())){
                changedFields.add("JobTitle");
                previousValues.add(previousDetails.getJobTitle());
                currentValues.add(employee.getJobTitle());
            }
        }

        Map<String,String> beforeChanges = new HashMap<>();
        for(int i = 0; i<changedFields.size(); i++){
            beforeChanges.put(changedFields.get(i), previousValues.get(i));
        }
        Map<String,String> afterChanges = new HashMap<>();
        for(int i = 0; i<changedFields.size(); i++){
            afterChanges.put(changedFields.get(i), currentValues.get(i));
        }

        return List.of(beforeChanges,afterChanges);

    }

    private List<Map<String,String>> preDeleteValues(){
        Employee previousDetails = getPrevEmployee();
        clearPrevEmployee();
        List<String> changedFields = new ArrayList<>();
        List<String> previousValues = new ArrayList<>();
        List<String> currentValues = new ArrayList<>();
        currentValues.add(null);
        currentValues.add(null);
        currentValues.add(null);
        currentValues.add(null);
        currentValues.add(null);
        changedFields.add("Name");
        changedFields.add("Email");
        changedFields.add("DOB");
        changedFields.add("HireDate");
        changedFields.add("JobTitle");
        previousValues.add(previousDetails.getName());
        previousValues.add(previousDetails.getEmail());
        previousValues.add(previousDetails.getDob().toString());
        previousValues.add(previousDetails.getHireDate().toString());
        previousValues.add(previousDetails.getJobTitle());

        Map<String,String> beforeChanges = new HashMap<>();
        for(int i = 0; i<changedFields.size(); i++){
            beforeChanges.put(changedFields.get(i), previousValues.get(i));
        }
        Map<String,String> afterChanges = new HashMap<>();
        for(int i = 0; i<changedFields.size(); i++){
            afterChanges.put(changedFields.get(i), currentValues.get(i));
        }

        return List.of(beforeChanges,afterChanges);
    }

}
