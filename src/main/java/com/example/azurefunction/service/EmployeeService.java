package com.example.azurefunction.service;

import com.example.azurefunction.dto.EmployeeRequest;
import com.example.azurefunction.dto.EmployeeResponse;
import com.example.azurefunction.enitity.Employee;
import com.example.azurefunction.exception.EmployeeNotFoundException;
import com.example.azurefunction.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeResponse saveEmployee(EmployeeRequest request) {
        Employee employee = Employee.builder().name(request.name()).build();
        employee = employeeRepository.save(employee);
        return new EmployeeResponse(employee.getName());
    }

    public List<EmployeeResponse> getAllEmployees() {
        return employeeRepository
                .findAll()
                .stream()
                .map(employee -> new EmployeeResponse(employee.getName())).toList();
    }

    public EmployeeResponse findById(Long id) {
        return employeeRepository
                .findById(id)
                .map(employee -> new EmployeeResponse(employee.getName()))
                .orElseThrow(() -> new EmployeeNotFoundException("No employee found with id = " + id));
    }

    public List<EmployeeResponse> findByName(String name) {
        return employeeRepository
                .findByName(name)
                .stream()
                .map(employee -> new EmployeeResponse(employee.getName()))
                .toList();
    }

    public Void deleteById(Long id) {
        employeeRepository
                .findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("No employee found with id = " + id));
        employeeRepository.deleteById(id);
        return null;
    }

    public EmployeeResponse update(EmployeeRequest request) {
        employeeRepository
                .findById(request.id())
                .orElseThrow(() -> new EmployeeNotFoundException("No employee found with id = " + request.id()));
        Employee employee = Employee.builder().id(request.id()).name(request.name()).build();
        employee = employeeRepository.save(employee);
        return new EmployeeResponse(employee.getName());
    }

}
