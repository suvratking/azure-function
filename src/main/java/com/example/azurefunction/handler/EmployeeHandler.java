package com.example.azurefunction.handler;

import com.example.azurefunction.AzureFunctionApplication;
import com.example.azurefunction.dto.EmployeeRequest;
import com.example.azurefunction.dto.EmployeeResponse;
import com.example.azurefunction.exception.EmployeeNotFoundException;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.springframework.cloud.function.adapter.azure.FunctionInvoker;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static com.example.azurefunction.utils.Constants.*;
import static com.example.azurefunction.utils.MappingUtil.mapFunction;

public class EmployeeHandler extends FunctionInvoker<Object, Object> {

    public EmployeeHandler() {
        super(AzureFunctionApplication.class);
    }

    @FunctionName("saveEmployee")
    public HttpResponseMessage saveEmployee(
            @HttpTrigger(
                    name = "saveEmployee",
                    methods = {HttpMethod.POST},
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<EmployeeRequest>> request, ExecutionContext context) {
        return withGlobalExceptionHandling(request, () -> {
            context.getLogger().info("Using Java (" + System.getProperty("java.version") + ")");
            EmployeeResponse employeeResponse = (EmployeeResponse) handleRequest(request.getBody().orElse(null), mapFunction(context, SAVE_EMPLOYEE_BEAN));
            return request.createResponseBuilder(HttpStatus.OK)
                    .body(employeeResponse)
                    .header("Content-Type", "application/json")
                    .build();
        });
    }

    @FunctionName("findAllEmployee")
    public HttpResponseMessage findAllEmployee(
            @HttpTrigger(
                    name = "findAllEmployee",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request, ExecutionContext context) {
        return withGlobalExceptionHandling(request, () -> {
            context.getLogger().info("Using Java (" + System.getProperty("java.version") + ")");
            @SuppressWarnings("unchecked")
            List<EmployeeResponse> employeeResponse = (List<EmployeeResponse>) handleRequest(null, mapFunction(context, FIND_ALL_EMPLOYEE_BEAN));
            return request.createResponseBuilder(HttpStatus.OK)
                    .body(employeeResponse)
                    .header("Content-Type", "application/json")
                    .build();
        });
    }
    
    @FunctionName("findEmployeeById")
    public HttpResponseMessage findEmployeeById(
            @HttpTrigger(
                    name = "findEmployeeById",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "findEmployeeById/{id}")
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") Long id,
            ExecutionContext context) {
        return withGlobalExceptionHandling(request, () -> {
            context.getLogger().info("Using Java (" + System.getProperty("java.version") + ")");
            EmployeeResponse employeeResponse =
                    (EmployeeResponse) handleRequest(id, mapFunction(context, FIND_EMPLOYEE_ID_BEAN));
            return request.createResponseBuilder(HttpStatus.OK)
                    .body(employeeResponse)
                    .header("Content-Type", "application/json")
                    .build();
        });
    }

    @FunctionName("findEmployeeByName")
    public HttpResponseMessage findEmployeeByName(
            @HttpTrigger(
                    name = "findEmployeeByName",
                    methods = {HttpMethod.GET},
                    authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "findEmployeeByName")
            HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {
        return withGlobalExceptionHandling(request, () -> {
            context.getLogger().info("Using Java (" + System.getProperty("java.version") + ")");
            String name = request.getQueryParameters().get("name");
            if (name == null || name.isBlank()) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body("Query param 'name' is required.")
                        .header("Content-Type", "application/json")
                        .build();
            }
            @SuppressWarnings("unchecked")
            List<EmployeeResponse> employeeResponse =
                    (List<EmployeeResponse>) handleRequest(name, mapFunction(context, FIND_EMPLOYEE_NAME_BEAN));
            return request.createResponseBuilder(HttpStatus.OK)
                    .body(employeeResponse)
                    .header("Content-Type", "application/json")
                    .build();
        });
    }

    @FunctionName("deleteEmployeeById")
    public HttpResponseMessage deleteEmployeeById(
            @HttpTrigger(
                    name = "deleteEmployeeById",
                    methods = {HttpMethod.DELETE},
                    authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "deleteEmployeeById/{id}")
            HttpRequestMessage<Optional<String>> request,
            @BindingName("id") Long id,
            ExecutionContext context) {
        return withGlobalExceptionHandling(request, () -> {
            context.getLogger().info("Using Java (" + System.getProperty("java.version") + ")");
            handleRequest(id, mapFunction(context, DELETE_EMPLOYEE_ID_BEAN));
            return request.createResponseBuilder(HttpStatus.OK)
                    .body(Map.of("message", "Employee deleted successfully"))
                    .header("Content-Type", "application/json")
                    .build();
        });
    }

    @FunctionName("updateEmployee")
    public HttpResponseMessage updateEmployee(
            @HttpTrigger(
                    name = "updateEmployee",
                    methods = {HttpMethod.PUT},
                    authLevel = AuthorizationLevel.ANONYMOUS,
                    route = "updateEmployee/{id}")
            HttpRequestMessage<Optional<EmployeeRequest>> request, @BindingName("id") Long id, ExecutionContext context) {
        return withGlobalExceptionHandling(request, () -> {
            context.getLogger().info("Using Java (" + System.getProperty("java.version") + ")");
            EmployeeRequest employeeRequest = request.getBody().orElse(null);
            if (employeeRequest == null) {
                return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                        .body("Request body is required.")
                        .header("Content-Type", "application/json")
                        .build();
            }
            employeeRequest = new EmployeeRequest(id, employeeRequest.name());
            EmployeeResponse employeeResponse = (EmployeeResponse) handleRequest(employeeRequest, mapFunction(context, UPDATE_EMPLOYEE_BEAN));
            return request.createResponseBuilder(HttpStatus.OK)
                    .body(employeeResponse)
                    .header("Content-Type", "application/json")
                    .build();
        });
    }

    private HttpResponseMessage withGlobalExceptionHandling(
            HttpRequestMessage<?> request,
            Supplier<HttpResponseMessage> action) {
        try {
            return action.get();
        } catch (Exception exception) {
            EmployeeNotFoundException employeeNotFoundException = findEmployeeNotFoundException(exception);
            if (employeeNotFoundException != null) {
                return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", employeeNotFoundException.getMessage()))
                        .header("Content-Type", "application/json")
                        .build();
            }
            throw exception;
        }
    }

    private EmployeeNotFoundException findEmployeeNotFoundException(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            if (current instanceof EmployeeNotFoundException exception) {
                return exception;
            }
            current = current.getCause();
        }
        return null;
    }

}
