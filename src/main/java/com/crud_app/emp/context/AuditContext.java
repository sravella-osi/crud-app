package com.crud_app.emp.context;

import com.crud_app.emp.models.Employee;

public class AuditContext {
    private static final ThreadLocal<String> modifiedBy = new ThreadLocal<>();
    private static final ThreadLocal<Employee> prevEmployeeThread = new ThreadLocal<>();
    private static final ThreadLocal<Employee> employeeThread = new ThreadLocal<>();

    public static void setModifiedBy(String modifiedByName) {
        modifiedBy.set(modifiedByName);
    }

    public static String getModifiedBy() {
        return modifiedBy.get();
    }

    public static void clearModifiedBy() {
        modifiedBy.remove();
    }

    public static void setEmployee(Employee employee) {
        employeeThread.set(employee);
    }

    public static Employee getEmployee() {
        return employeeThread.get();
    }

    public static void clearEmployee() {
        employeeThread.remove();
    }

    public static void setPrevEmployee(Employee employee) {
        prevEmployeeThread.set(employee);
    }

    public static Employee getPrevEmployee() {
        return prevEmployeeThread.get();
    }

    public static void clearPrevEmployee() {
        prevEmployeeThread.remove();
    }

}