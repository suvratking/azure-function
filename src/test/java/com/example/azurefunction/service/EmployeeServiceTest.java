package com.example.azurefunction.service;

import com.example.azurefunction.dto.EmployeeRequest;
import com.example.azurefunction.dto.EmployeeResponse;
import com.example.azurefunction.enitity.Employee;
import com.example.azurefunction.exception.EmployeeNotFoundException;
import com.example.azurefunction.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(employeeRepository);
    }

    @Test
    void saveEmployeePersistsAndReturnsResponse() {
        when(employeeRepository.save(any(Employee.class)))
                .thenReturn(Employee.builder().id(1L).name("Ava").build());

        EmployeeResponse response = employeeService.saveEmployee(new EmployeeRequest(null, "Ava"));

        assertEquals("Ava", response.name());
    }

    @Test
    void getAllEmployeesMapsResults() {
        when(employeeRepository.findAll()).thenReturn(List.of(
                Employee.builder().id(1L).name("Ava").build(),
                Employee.builder().id(2L).name("Neo").build()
        ));

        List<EmployeeResponse> response = employeeService.getAllEmployees();

        assertEquals(2, response.size());
        assertEquals("Ava", response.getFirst().name());
        assertEquals("Neo", response.get(1).name());
    }

    @Test
    void findByIdReturnsValueWhenPresent() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(Employee.builder().id(1L).name("Ava").build()));

        EmployeeResponse response = employeeService.findById(1L);

        assertEquals("Ava", response.name());
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(employeeRepository.findById(7L)).thenReturn(Optional.empty());

        EmployeeNotFoundException ex = assertThrows(EmployeeNotFoundException.class, () -> employeeService.findById(7L));

        assertEquals("No employee found with id = 7", ex.getMessage());
    }

    @Test
    void findByNameReturnsMappedList() {
        when(employeeRepository.findByName("Ava")).thenReturn(List.of(
                Employee.builder().id(1L).name("Ava").build(),
                Employee.builder().id(2L).name("Ava").build()
        ));

        List<EmployeeResponse> response = employeeService.findByName("Ava");

        assertEquals(2, response.size());
        assertEquals("Ava", response.getFirst().name());
    }

    @Test
    void deleteByIdDeletesWhenPresent() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(Employee.builder().id(1L).name("Ava").build()));

        Void result = employeeService.deleteById(1L);

        assertNull(result);
        verify(employeeRepository).deleteById(1L);
    }

    @Test
    void deleteByIdThrowsWhenMissing() {
        when(employeeRepository.findById(9L)).thenReturn(Optional.empty());

        EmployeeNotFoundException ex = assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteById(9L));

        assertEquals("No employee found with id = 9", ex.getMessage());
    }

    @Test
    void updatePersistsWhenPresent() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(Employee.builder().id(1L).name("Old").build()));
        when(employeeRepository.save(Employee.builder().id(1L).name("New").build()))
                .thenReturn(Employee.builder().id(1L).name("New").build());

        EmployeeResponse response = employeeService.update(new EmployeeRequest(1L, "New"));

        assertEquals("New", response.name());
    }

    @Test
    void updateThrowsWhenMissing() {
        when(employeeRepository.findById(11L)).thenReturn(Optional.empty());

        EmployeeNotFoundException ex = assertThrows(EmployeeNotFoundException.class,
                () -> employeeService.update(new EmployeeRequest(11L, "Nope")));

        assertEquals("No employee found with id = 11", ex.getMessage());
    }
}
