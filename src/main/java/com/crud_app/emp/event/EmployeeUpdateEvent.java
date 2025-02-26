package com.crud_app.emp.event;

import com.crud_app.emp.models.Employee;

public class EmployeeUpdateEvent {
    private final Employee employee;

    public EmployeeUpdateEvent(Employee employee) {
        this.employee = employee;
    }

    public Employee getEmployee() {
        return employee;
    }
}
