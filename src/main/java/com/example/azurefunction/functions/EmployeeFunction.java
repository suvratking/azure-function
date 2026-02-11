package com.example.azurefunction.functions;

import com.example.azurefunction.dto.EmployeeRequest;
import com.example.azurefunction.dto.EmployeeResponse;
import com.example.azurefunction.service.EmployeeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class EmployeeFunction {

    private final EmployeeService employeeService;

    public EmployeeFunction(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Bean(name = "saveUser")
    public Function<EmployeeRequest, EmployeeResponse> saveUser() {
        return employeeService::saveEmployee;
    }

}
