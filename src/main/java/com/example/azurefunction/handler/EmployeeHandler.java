package com.example.azurefunction.handler;

import com.example.azurefunction.dto.EmployeeRequest;
import com.example.azurefunction.dto.EmployeeResponse;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.springframework.cloud.function.adapter.azure.FunctionInvoker;

import java.util.Optional;

public class EmployeeHandler extends FunctionInvoker<EmployeeRequest, EmployeeResponse> {

    @FunctionName("saveUser")
    public HttpResponseMessage execute(
            @HttpTrigger(
                    name = "saveEmployee",
                    methods = {HttpMethod.POST},
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<EmployeeRequest>> request, ExecutionContext context) {
        context.getLogger().warning("Using Java (" + System.getProperty("java.version") + ")");
        EmployeeResponse employeeResponse = handleRequest(request.getBody().get(), context);
        return request.createResponseBuilder(HttpStatus.OK)
                .body(employeeResponse)
                .build();
    }

}
