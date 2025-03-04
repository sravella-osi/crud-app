package com.crud_app.services;

import com.crud_app.emp.dto.EmployeeDTO;
import com.crud_app.emp.dto.EmployeeSummaryDTO;
import com.crud_app.emp.exceptions.EmployeeAlreadyExistsException;
import com.crud_app.emp.exceptions.EmployeeNotFoundException;
import com.crud_app.emp.exceptions.ErrorResponse;
import com.crud_app.emp.models.Employee;
import com.crud_app.emp.repositories.projections.EmpSummary;
import com.crud_app.emp.repositories.EmployeeRepository;
import com.crud_app.emp.services.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    private final String DATE_FORMAT = "yyyy-MM-dd";
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee repoResult;

    private EmployeeDTO expected;

    private EmployeeDTO given;

    private Integer id;

    private List<EmployeeDTO> expectedList;

    private List<Employee> repoResultList;

    @BeforeEach
    public void setUp(){
        repoResult = convertToEmployee(getEmployeeDTO(1,"Saran","1999-11-02","2024-11-02","PAT","saran@email.com","Admin","Admin"));
        expected = getEmployeeDTO(1,"Saran","1999-11-02","2024-11-02","PAT","saran@email.com","Admin","Admin");
        given = getEmployeeDTO("Saran","1999-11-02","2024-11-02","PAT","saran@email.com","Admin","Admin");
        id = 1;
        expectedList = new ArrayList<>();
        expectedList.add(expected);
        repoResultList = new ArrayList<>();
        repoResultList.add(repoResult);

    }

    @Test
    void shouldSaveEmployee() throws Exception{
        when(employeeRepository.save(any(Employee.class))).thenReturn(repoResult);
        EmployeeDTO actual = employeeService.saveEmployee(given);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void saveEmployeeThrowsExistsException() throws Exception{
        when(employeeRepository.findByName(given.getName())).thenReturn(Optional.of(repoResult));
        assertThrows(EmployeeAlreadyExistsException.class, () -> {
            employeeService.saveEmployee(given);
        });
    }

    @Test
    void shouldGetEmployee() throws Exception{
        when(employeeRepository.findById(id)).thenReturn(Optional.of(repoResult));
        EmployeeDTO actual = employeeService.getEmployee(id);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void getEmployeeThrowsNotFoundException() throws Exception{
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.getEmployee(id);
        });
    }

    @Test
    void shouldGetEmployees() throws Exception{
        when(employeeRepository.findAll()).thenReturn(repoResultList);
        List<EmployeeDTO> actual = employeeService.getAllEmployees();
        assertThat(actual).usingRecursiveComparison().isEqualTo(expectedList);
    }

    @Test
    void getEmployeesThrowsNotFoundException() throws Exception{
        when(employeeRepository.findAll()).thenReturn(new ArrayList<>());
        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.getAllEmployees();
        });
    }

    @Test
    void shouldUpdateEmployee() throws Exception{
        when(employeeRepository.findById(id)).thenReturn(Optional.of(repoResult));
        when(employeeRepository.save(any(Employee.class))).thenReturn(repoResult);
        EmployeeDTO actual = employeeService.updateEmployee(given,id);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void updateEmployeeThrowsNotFoundException() throws Exception{
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.updateEmployee(given, id);
        });
    }

    @Test
    void shouldDeleteEmployee() throws Exception{
        when(employeeRepository.existsById(id)).thenReturn(true);
        employeeService.deleteEmployee(id,"Saran");
        verify(employeeRepository).deleteById(id);
    }

    @Test
    void deleteEmployeeThrowsNotFoundException() throws Exception{
        when(employeeRepository.existsById(id)).thenReturn(false);
        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.deleteEmployee(id,"Saran");
        });
    }

    @Test
    void shouldGetEmployeesPage() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        List<EmpSummary> empSummaries = List.of(
                new EmpSummary() {
                    @Override
                    public Integer getId() {return 1;}
                    @Override
                    public String getName() {return "Saran";}
                    @Override
                    public String getJobTitle() {return "Software Engineer";}
                },
                new EmpSummary() {
                    @Override
                    public Integer getId() {return 2;}
                    @Override
                    public String getName() {return "Sai";}
                    @Override
                    public String getJobTitle() {return "PAT";}
                }
        );
        Page<EmpSummary> employeePage = new PageImpl<>(empSummaries, pageable, empSummaries.size());

        when(employeeRepository.findBy(pageable)).thenReturn(employeePage);

        List<EmployeeSummaryDTO> expected = List.of(
                new EmployeeSummaryDTO(1, "Saran", "Software Engineer"),
                new EmployeeSummaryDTO(2, "Sai", "PAT")
        );

        Page<EmployeeSummaryDTO> actual = employeeService.getAllEmployees(pageable);

        assertThat(actual.getContent()).usingRecursiveComparison().isEqualTo(expected);
    }


    private EmployeeDTO getEmployeeDTO(int id, String name, String dob, String hireDate, String jobTitle, String email, String createdBy, String modifiedBy) {
        return new EmployeeDTO(id,name,dob,hireDate,jobTitle,email,createdBy,modifiedBy);
    }

    private EmployeeDTO getEmployeeDTO(String name, String dob, String hireDate, String jobTitle, String email, String createdBy, String modifiedBy) {
        return new EmployeeDTO(name,dob,hireDate,jobTitle,email,createdBy,modifiedBy);
    }

    private EmployeeSummaryDTO getEmployeeSummaryDTO(int id, String name, String jobTitle) {
        return new EmployeeSummaryDTO(id,name,jobTitle);
    }

    private ErrorResponse getErrorResponse(int status, List<String> errors){
        return new ErrorResponse(status, errors);
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
        return employeeDTO;
    }

    private EmployeeSummaryDTO convertToEmployeeSummaryDTO(EmpSummary employee){
        EmployeeSummaryDTO employeeSummaryDTO = new EmployeeSummaryDTO();
        employeeSummaryDTO.setId(employee.getId());
        employeeSummaryDTO.setJobTitle(employee.getJobTitle());
        employeeSummaryDTO.setName(employee.getName());
        return employeeSummaryDTO;
    }

}
