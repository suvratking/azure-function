package com.example.azurefunction.handler;

import com.example.azurefunction.dto.EmployeeRequest;
import com.example.azurefunction.dto.EmployeeResponse;
import com.example.azurefunction.exception.EmployeeNotFoundException;
import com.microsoft.azure.functions.*;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.BindingName;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import org.springframework.cloud.function.adapter.azure.AzureFunctionUtil;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.example.azurefunction.utils.Constants.*;

@Component
public class EmployeeHandler {

    private static final String CONTENT_TYPE_JSON = "application/json";
    private final FunctionCatalog functionCatalog;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public EmployeeHandler(FunctionCatalog functionCatalog) {
        this.functionCatalog = functionCatalog;
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
            EmployeeResponse employeeResponse =
                    (EmployeeResponse) invokeFunction(request.getBody().orElse(null), SAVE_EMPLOYEE_BEAN, context);
            return jsonResponse(request, HttpStatus.OK, employeeResponse);
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
            List<EmployeeResponse> employeeResponse =
                    (List<EmployeeResponse>) invokeFunction(null, FIND_ALL_EMPLOYEE_BEAN, context);
            return jsonResponse(request, HttpStatus.OK, employeeResponse);
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
                    (EmployeeResponse) invokeFunction(id, FIND_EMPLOYEE_ID_BEAN, context);
            return jsonResponse(request, HttpStatus.OK, employeeResponse);
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
                return jsonResponse(request, HttpStatus.BAD_REQUEST, Map.of("message", "Query param 'name' is required."));
            }
            @SuppressWarnings("unchecked")
            List<EmployeeResponse> employeeResponse =
                    (List<EmployeeResponse>) invokeFunction(name, FIND_EMPLOYEE_NAME_BEAN, context);
            return jsonResponse(request, HttpStatus.OK, employeeResponse);
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
            invokeFunction(id, DELETE_EMPLOYEE_ID_BEAN, context);
            return jsonResponse(request, HttpStatus.OK, Map.of("message", "Employee deleted successfully"));
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
                return jsonResponse(request, HttpStatus.BAD_REQUEST, Map.of("message", "Request body is required."));
            }
            employeeRequest = new EmployeeRequest(id, employeeRequest.name());
            EmployeeResponse employeeResponse =
                    (EmployeeResponse) invokeFunction(employeeRequest, UPDATE_EMPLOYEE_BEAN, context);
            return jsonResponse(request, HttpStatus.OK, employeeResponse);
        });
    }

    protected Object invokeFunction(Object input, String beanName, ExecutionContext context) {
        @SuppressWarnings("unchecked")
        Function<Object, Object> function = (Function<Object, Object>) functionCatalog.lookup(beanName);
        if (function == null) {
            throw new IllegalStateException("No Spring Cloud Function bean found for '" + beanName + "'");
        }
        return function.apply(AzureFunctionUtil.enhanceInputIfNecessary(input, context));
    }

    private HttpResponseMessage withGlobalExceptionHandling(
            HttpRequestMessage<?> request,
            Supplier<HttpResponseMessage> action) {
        try {
            return action.get();
        } catch (Exception exception) {
            EmployeeNotFoundException employeeNotFoundException = findEmployeeNotFoundException(exception);
            if (employeeNotFoundException != null) {
                return jsonResponse(request, HttpStatus.NOT_FOUND,
                        Map.of("message", employeeNotFoundException.getMessage()));
            }
            throw exception;
        }
    }

    private HttpResponseMessage jsonResponse(HttpRequestMessage<?> request, HttpStatusType status, Object payload) {
        return request.createResponseBuilder(status)
                .header("Content-Type", CONTENT_TYPE_JSON)
                .body(toJson(payload))
                .build();
    }

    private String toJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JacksonException exception) {
            throw new IllegalStateException("Failed to serialize function response", exception);
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
