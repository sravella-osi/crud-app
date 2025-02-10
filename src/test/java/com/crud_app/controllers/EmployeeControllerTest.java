package com.crud_app.controllers;


import com.crud_app.emp.controllers.EmployeeController;
import com.crud_app.emp.dto.EmployeeDTO;
import com.crud_app.emp.dto.EmployeeSummaryDTO;
import com.crud_app.emp.exceptions.EmployeeAlreadyExistsException;
import com.crud_app.emp.exceptions.EmployeeNotFoundException;
import com.crud_app.emp.exceptions.ErrorResponse;
import com.crud_app.emp.models.Employee;
import com.crud_app.emp.repositories.EmpSummary;
import com.crud_app.emp.services.EmployeeService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EmployeeController.class)
@ExtendWith(MockitoExtension.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Test
    void shouldGetEmployeeDetails() throws Exception{
        EmployeeDTO expected = getEmployeeDTO(1,"Saran","1999-11-02","2024-11-02","PAT","saran@email.com");
        when(employeeService.getEmployee(1)).thenReturn(expected);

        MvcResult mvcResult = mockMvc.perform(get("/api/emp/1")).andExpect(status().isOk()).andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        EmployeeDTO actual = new ObjectMapper().readValue(responseString, EmployeeDTO.class);
        System.out.println("Expected: " + expected + "\nActual: " + actual);
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void getEmployeeDetailsThrowsNotFound() throws Exception {
        List<String> errors = new ArrayList<>();
        errors.add("Employee with id 1 not found!");
        ErrorResponse expected = getErrorResponse(HttpStatus.NOT_FOUND.value(),errors);
        when(employeeService.getEmployee(1)).thenThrow(new EmployeeNotFoundException("Employee with id 1 not found!"));
        MvcResult mvcResult = mockMvc.perform(get("/api/emp/1")).andExpect(status().isNotFound()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        ErrorResponse actual = new ObjectMapper().readValue(response,ErrorResponse.class);
        System.out.println("Expected: " + expected + "\nActual: " + actual);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void shouldHandleInternalServerError() throws Exception{
        List<String> errors = new ArrayList<>();
        errors.add("Unexpected Error");
        ErrorResponse expected = getErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),errors);
        when(employeeService.getEmployee(1)).thenThrow(new RuntimeException("Unexpected Error"));
        MvcResult mvcResult = mockMvc.perform(get("/api/emp/1"))
                .andExpect(status().isInternalServerError())
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        ErrorResponse actual = new ObjectMapper().readValue(response, ErrorResponse.class);
        System.out.println("Expected: " + expected + "\nActual: " + actual);
        assertThat(actual)
                .usingRecursiveComparison()
                .withFailMessage("Internal Server Error Not Handled Properly GET:/api/emp/1")
                .isEqualTo(expected);
    }

    @Test
    void shouldGetEmployeesDetails() throws Exception{
        List<EmployeeDTO> expected = new ArrayList<>();
        EmployeeDTO employeeDTO = getEmployeeDTO(1,"Saran","1999-11-02","2024-11-02","PAT","saran@email.com");
        expected.add(employeeDTO);
        when(employeeService.getAllEmployees()).thenReturn(expected);

        MvcResult mvcResult = mockMvc.perform(get("/api/emp")).andExpect(status().isOk()).andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        List<EmployeeDTO> actual = new ObjectMapper()
                .readValue(responseString, new TypeReference<List<EmployeeDTO>>() {});
        System.out.println("Expected: " + expected + "\nActual: " + actual);
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void getEmployeesDetailsThrowsNotFound() throws Exception {
        List<String> errors = new ArrayList<>();
        errors.add("Employees not found!");
        ErrorResponse expected = getErrorResponse(HttpStatus.NOT_FOUND.value(),errors);
        when(employeeService.getAllEmployees()).thenThrow(new EmployeeNotFoundException("Employees not found!"));
        MvcResult mvcResult = mockMvc.perform(get("/api/emp")).andExpect(status().isNotFound()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        ErrorResponse actual = new ObjectMapper().readValue(response,ErrorResponse.class);
        System.out.println("Expected: " + expected + "\nActual: " + actual);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void shouldGetEmployeeSummary() throws Exception{
        EmployeeSummaryDTO expected = getEmployeeSummaryDTO(1,"Saran","PAT");
        when(employeeService.getEmployeeSummary(1)).thenReturn(expected);

        MvcResult mvcResult = mockMvc.perform(get("/api/emp/summary/1")).andExpect(status().isOk()).andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        EmployeeSummaryDTO actual = new ObjectMapper()
                .readValue(responseString, EmployeeSummaryDTO.class);
        System.out.println("Expected: " + expected + "\nActual: " + actual);
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void getEmployeeSummaryThrowsNotFound() throws Exception {
        List<String> errors = new ArrayList<>();
        errors.add("Employee with id 1 not found!");
        ErrorResponse expected = getErrorResponse(HttpStatus.NOT_FOUND.value(),errors);
        when(employeeService.getEmployee(1)).thenThrow(new EmployeeNotFoundException("Employee with id 1 not found!"));
        MvcResult mvcResult = mockMvc.perform(get("/api/emp/summary/1")).andExpect(status().isNotFound()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        ErrorResponse actual = new ObjectMapper().readValue(response,ErrorResponse.class);
        System.out.println("Expected: " + expected + "\nActual: " + actual);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void shouldGetEmployeesSummary() throws Exception{
        List<EmployeeSummaryDTO> expected = new ArrayList<>();
        EmployeeSummaryDTO employeeDTO = getEmployeeSummaryDTO(1,"Saran","PAT");
        expected.add(employeeDTO);
        when(employeeService.getEmployeesSummary()).thenReturn(expected);

        MvcResult mvcResult = mockMvc.perform(get("/api/emp/summary")).andExpect(status().isOk()).andReturn();

        String responseString = mvcResult.getResponse().getContentAsString();
        List<EmployeeSummaryDTO> actual = new ObjectMapper()
                .readValue(responseString, new TypeReference<List<EmployeeSummaryDTO>>() {});
        System.out.println("Expected: " + expected + "\nActual: " + actual);
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void getEmployeesSummaryThrowsNotFound() throws Exception {
        List<String> errors = new ArrayList<>();
        errors.add("Employees not found!");
        ErrorResponse expected = getErrorResponse(HttpStatus.NOT_FOUND.value(),errors);
        when(employeeService.getAllEmployees()).thenThrow(new EmployeeNotFoundException("Employees not found!"));
        MvcResult mvcResult = mockMvc.perform(get("/api/emp/summary")).andExpect(status().isNotFound()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        ErrorResponse actual = new ObjectMapper().readValue(response,ErrorResponse.class);
        System.out.println("Expected: " + expected + "\nActual: " + actual);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void shouldCreateEmployee() throws Exception {
        EmployeeDTO expected = getEmployeeDTO(
                1,
                "Saran",
                "1999-11-02",
                "2024-11-02",
                "PAT",
                "saran@email.com");
        when(employeeService.saveEmployee(
                getEmployeeDTO(
                "Saran",
                "1999-11-02",
                "2024-11-02",
                "PAT",
                "saran@email.com")
        )).thenReturn(expected);
        MvcResult mvcResult = mockMvc.perform(post("/api/emp").content("{" +
                "\"name\" : \"Saran\"," +
                "\"dob\" : \"1999-11-02\"," +
                "\"hireDate\" : \"2024-11-02\"," +
                "\"jobTitle\" : \"PAT\"," +
                "\"email\" : \"saran@email.com\"" +
                                "}")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isCreated()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        System.out.println(response);
        EmployeeDTO actual = new ObjectMapper().readValue(response,EmployeeDTO.class);
        System.out.println("Expected: " + expected + "\nActual: " + actual);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void saveThrowsEmployeeExistsException() throws Exception {
        List<String> errors = new ArrayList<>();
        errors.add("Employee already exists");
        ErrorResponse expected = getErrorResponse(HttpStatus.CONFLICT.value(), errors);
        when(employeeService.saveEmployee(any(EmployeeDTO.class)))
                .thenThrow(new EmployeeAlreadyExistsException("Employee already exists"));
        MvcResult mvcResult = mockMvc.perform(post("/api/emp").content("{" +
                                "\"name\" : \"Saran\"," +
                                "\"dob\" : \"1999-11-02\"," +
                                "\"hireDate\" : \"2024-11-02\"," +
                                "\"jobTitle\" : \"PAT\"," +
                                "\"email\" : \"saran@email.com\"" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isConflict()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        ErrorResponse actual = new ObjectMapper().readValue(response,ErrorResponse.class);
        System.out.println("Expected: " + expected + "\nActual: " + actual);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void saveEmployeeThrowsValidationErrors() throws Exception {
        List<String> errors = new ArrayList<>();
        errors.add("Employee Name must be valid");
        errors.add("DOB : Date needs to be in 'yyyy-MM-dd' format");
        errors.add("Hire Date : Date needs to be in 'yyyy-MM-dd' format");
        errors.add("Job title must be valid");
        errors.add("Invalid Email");
        ErrorResponse expected = getErrorResponse(HttpStatus.BAD_REQUEST.value(), errors);
        when(employeeService.saveEmployee(any(EmployeeDTO.class)))
                .thenThrow(new EmployeeAlreadyExistsException("Employee already exists"));
        MvcResult mvcResult = mockMvc.perform(post("/api/emp").content("{" +
                                "\"name\" : \"S@ran\"," +
                                "\"dob\" : \"19-11-02\"," +
                                "\"hireDate\" : \"20-11-02\"," +
                                "\"jobTitle\" : \"P4T\"," +
                                "\"email\" : \"saranemail.com\"" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        ErrorResponse actual = new ObjectMapper().readValue(response,ErrorResponse.class);
        System.out.println("Expected: " + expected + "\nActual: " + actual);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void updateThrowsEmployeeNotFound() throws Exception {
        List<String> errors = new ArrayList<>();
        errors.add("Employee with id 1 not found!");
        ErrorResponse expected = getErrorResponse(HttpStatus.NOT_FOUND.value(),errors);
        when(employeeService.updateEmployee(any(EmployeeDTO.class),eq(1)))
                .thenThrow(new EmployeeNotFoundException("Employee with id 1 not found!"));
        MvcResult mvcResult = mockMvc.perform(put("/api/emp/1").content("{" +
                                "\"id\" : 1," +
                                "\"name\" : \"Saran\"," +
                                "\"dob\" : \"1999-11-02\"," +
                                "\"hireDate\" : \"2024-11-02\"," +
                                "\"jobTitle\" : \"PAT\"," +
                                "\"email\" : \"saran@email.com\"" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isNotFound()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        System.out.println(response);
        ErrorResponse actual = new ObjectMapper().readValue(response,ErrorResponse.class);
        System.out.println("Expected: " + expected + "\nActual: " + actual);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void shouldUpdateEmployee() throws Exception {
        EmployeeDTO expected = getEmployeeDTO(
                1,
                "Saran",
                "1999-11-02",
                "2024-11-02",
                "PAT",
                "saran@email.com");
        when(employeeService.updateEmployee(expected,1)).thenReturn(expected);
        MvcResult mvcResult = mockMvc.perform(put("/api/emp/1").content("{" +
                                "\"id\" : 1," +
                                "\"name\" : \"Saran\"," +
                                "\"dob\" : \"1999-11-02\"," +
                                "\"hireDate\" : \"2024-11-02\"," +
                                "\"jobTitle\" : \"PAT\"," +
                                "\"email\" : \"saran@email.com\"" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        System.out.println(response);
        EmployeeDTO actual = new ObjectMapper().readValue(response,EmployeeDTO.class);
        System.out.println("Expected: " + expected + "\nActual: " + actual);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void getPaginatedEmployee() throws Exception {
        EmployeeSummaryDTO emp1 = new EmployeeSummaryDTO(1, "Alice", "Manager");

        EmployeeSummaryDTO emp2 = new EmployeeSummaryDTO(2, "Bob", "Developer");

        Page<EmployeeSummaryDTO> empSummaryPage = new PageImpl<>(List.of(emp1, emp2));

        when(employeeService.getALlEmployees(any(Pageable.class))).thenReturn(empSummaryPage);

        MvcResult mvcResult = mockMvc.perform(get("/api/emp/pages")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "name,asc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        System.out.println("Response: "+response);
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(employeeService).getALlEmployees(pageableCaptor.capture());
        PageRequest pageable = (PageRequest) pageableCaptor.getValue();

        assertThat(pageable.getPageNumber()).isEqualTo(0);
        assertThat(pageable.getPageSize()).isEqualTo(2);
        assertThat(pageable.getSort()).isEqualTo(Sort.by(Sort.Order.asc("name")));
    }


    @Test
    void shouldDeleteEmplpoyee() throws Exception{
        String expected = "Employee with id: 1 deleted.";
        when(employeeService.deleteEmployee(1)).thenReturn(expected);
        MvcResult mvcResult = mockMvc.perform(delete("/api/emp/1"))
                .andExpect(status().isNoContent()).andReturn();
        String actual = mvcResult.getResponse().getContentAsString();
        System.out.println("Expected: " + expected + "\nActual: " + actual);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void deleteEmployeeThrowsNotFound() throws Exception {
        List<String> errors = new ArrayList<>();
        errors.add("Employee with id 1 not found!");
        ErrorResponse expected = getErrorResponse(HttpStatus.NOT_FOUND.value(),errors);
        when(employeeService.deleteEmployee(1)).thenThrow(new EmployeeNotFoundException("Employee with id 1 not found!"));
        MvcResult mvcResult = mockMvc.perform(delete("/api/emp/1")).andExpect(status().isNotFound()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        ErrorResponse actual = new ObjectMapper().readValue(response,ErrorResponse.class);
        System.out.println("Expected: " + expected + "\nActual: " + actual);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    private EmployeeDTO getEmployeeDTO(int id, String name, String dob, String hireDate, String jobTitle, String email) {
        return new EmployeeDTO(id,name,dob,hireDate,jobTitle,email);
    }

    private EmployeeDTO getEmployeeDTO(String name, String dob, String hireDate, String jobTitle, String email) {
        return new EmployeeDTO(name,dob,hireDate,jobTitle,email);
    }

    private EmployeeSummaryDTO getEmployeeSummaryDTO(int id, String name, String jobTitle) {
        return new EmployeeSummaryDTO(id,name,jobTitle);
    }

    private ErrorResponse getErrorResponse(int status, List<String> errors){
        return new ErrorResponse(status, errors);
    }

}
