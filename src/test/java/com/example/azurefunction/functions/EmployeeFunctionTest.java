package com.example.azurefunction.functions;

import com.example.azurefunction.dto.EmployeeRequest;
import com.example.azurefunction.dto.EmployeeResponse;
import com.example.azurefunction.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeFunctionTest {

    @Mock
    private EmployeeService employeeService;

    private EmployeeFunction employeeFunction;

    @BeforeEach
    void setUp() {
        employeeFunction = new EmployeeFunction(employeeService);
    }

    @Test
    void saveFunctionDelegatesToService() {
        EmployeeRequest request = new EmployeeRequest(1L, "Ava");
        when(employeeService.saveEmployee(request)).thenReturn(new EmployeeResponse("Ava"));

        EmployeeResponse response = employeeFunction.save().apply(request);

        assertEquals("Ava", response.name());
    }

    @Test
    void findAllFunctionDelegatesToService() {
        when(employeeService.getAllEmployees()).thenReturn(List.of(new EmployeeResponse("Ava")));

        List<EmployeeResponse> response = employeeFunction.findAll().apply(null);

        assertEquals(1, response.size());
        assertEquals("Ava", response.getFirst().name());
    }

    @Test
    void findByIdFunctionDelegatesToService() {
        when(employeeService.findById(5L)).thenReturn(new EmployeeResponse("Neo"));

        EmployeeResponse response = employeeFunction.findById().apply(5L);

        assertEquals("Neo", response.name());
    }

    @Test
    void findByNameFunctionDelegatesToService() {
        when(employeeService.findByName("Ava")).thenReturn(List.of(new EmployeeResponse("Ava")));

        List<EmployeeResponse> response = employeeFunction.findByName().apply("Ava");

        assertEquals(1, response.size());
    }

    @Test
    void deleteByIdFunctionDelegatesToService() {
        Void result = employeeFunction.deleteById().apply(7L);

        assertNull(result);
    }

    @Test
    void updateFunctionDelegatesToService() {
        EmployeeRequest request = new EmployeeRequest(7L, "Mia");
        when(employeeService.update(request)).thenReturn(new EmployeeResponse("Mia"));

        EmployeeResponse response = employeeFunction.update().apply(request);

        assertEquals("Mia", response.name());
    }
}
