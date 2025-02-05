package com.crud_app.emp.controllers;

import com.crud_app.emp.dto.EmployeeDTO;
import com.crud_app.emp.exceptions.EmployeeNotFoundException;
import com.crud_app.emp.services.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emp")
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployee(@PathVariable("id") Integer id){
        if(employeeService.getEmployee(id)!=null){
            return ResponseEntity.status(HttpStatus.OK).body(employeeService.getEmployee(id));
        }
        else {
            throw new EmployeeNotFoundException("Employee with id " + id + " not found!");
        }
    }

    @GetMapping("")
    public ResponseEntity<?> getAllEmployee(){
        if(employeeService.getAllEmployees()!=null){
            return ResponseEntity.status(HttpStatus.OK).body(employeeService.getAllEmployees());
        }
        else {
            throw new EmployeeNotFoundException("Employees Not found");
        }
    }

    @PostMapping("")
    public ResponseEntity<?> addEmployee(@Valid @RequestBody EmployeeDTO employeeDTO){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeService.saveEmployee(employeeDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable("id") Integer id, @Valid @RequestBody EmployeeDTO employeeDTO){
        return ResponseEntity.status(HttpStatus.OK)
                .body(employeeService.updateEmployee(employeeDTO, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable("id") Integer id){
        String message = employeeService.deleteEmployee(id);
        if(message.contains("not found")){
            throw new EmployeeNotFoundException("Employee with id " + id + " not found");
        }
        else {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(message);
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getEmployeesSummary(){
        if(!employeeService.getEmployeesSummary().isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body(employeeService.getEmployeesSummary());
        }
        else {
            throw new EmployeeNotFoundException("Employees not found!");
        }
    }

    @GetMapping("/summary/{id}")
    public ResponseEntity<?> getEmployeesSummary(@PathVariable("id") Integer id){
        if(employeeService.getEmployeeSummary(id)!=null){
            return ResponseEntity.status(HttpStatus.OK).body(employeeService.getEmployeeSummary(id));
        }
        else {
            throw new EmployeeNotFoundException("Employee with id " + id + " not found!");
        }
    }

//    @GetMapping("/pages")
//    public Page<EmpSummary> getAllEmployees(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "3") int size,
//            @RequestParam(defaultValue = "id") String sortBy,
//            @RequestParam(defaultValue = "true") boolean ascending
//    ){
//        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
//        Pageable pageable = PageRequest.of(page, size, sort);
//        return employeeService.getALlEmployees(pageable);
//    }

    @GetMapping("/pages")
    public ResponseEntity<?> getAllEmployees(Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(employeeService.getALlEmployees(pageable));
    }

}
