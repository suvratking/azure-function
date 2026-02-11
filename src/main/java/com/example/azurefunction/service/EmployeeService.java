package com.example.azurefunction.service;

import com.example.azurefunction.dto.EmployeeRequest;
import com.example.azurefunction.dto.EmployeeResponse;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    public EmployeeResponse saveEmployee(EmployeeRequest request) {
        System.out.println(request);
        return new EmployeeResponse(request.name());
    }

}
