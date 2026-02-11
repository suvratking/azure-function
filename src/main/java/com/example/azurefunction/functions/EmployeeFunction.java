package com.example.azurefunction.functions;

import com.example.azurefunction.dto.EmployeeRequest;
import com.example.azurefunction.dto.EmployeeResponse;
import com.example.azurefunction.service.EmployeeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.function.Function;

@Configuration
public class EmployeeFunction {

    private final EmployeeService employeeService;

    public EmployeeFunction(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Bean(name = "saveEmp")
    public Function<EmployeeRequest, EmployeeResponse> save() {
        return employeeService::saveEmployee;
    }

    @Bean(name = "findAllEmp")
    public Function<Void, List<EmployeeResponse>> findAll() {
        return v -> employeeService.getAllEmployees();
    }

    @Bean(name = "findEmpById")
    public Function<Long, EmployeeResponse> findById() {
        return employeeService::findById;
    }

    @Bean(name = "findEmpByName")
    public Function<String, List<EmployeeResponse>> findByName() {
        return employeeService::findByName;
    }

    @Bean(name = "deleteEmpById")
    public Function<Long, Void> deleteById() {
        return employeeService::deleteById;
    }

    @Bean(name = "updateEmp")
    public Function<EmployeeRequest, EmployeeResponse> update() {
        return employeeService::update;
    }

}
