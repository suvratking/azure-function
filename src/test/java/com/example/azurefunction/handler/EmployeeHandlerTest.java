package com.example.azurefunction.handler;

import com.example.azurefunction.dto.EmployeeRequest;
import com.example.azurefunction.dto.EmployeeResponse;
import com.example.azurefunction.exception.EmployeeNotFoundException;
import com.example.azurefunction.support.AzureTestHttpObjects;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class EmployeeHandlerTest {

    @Test
    void constructorIsCovered() {
        new EmployeeHandler(mock(FunctionCatalog.class));
    }

    @Test
    void saveEmployeeReturnsOkAndMapsToSaveBeanName() {
        EmployeeHandler handler = spy(new EmployeeHandler(mock(FunctionCatalog.class)));
        doReturn(new EmployeeResponse("Ava")).when(handler).invokeFunction(any(), eq("saveEmp"), any(ExecutionContext.class));

        HttpRequestMessage<Optional<EmployeeRequest>> request =
                AzureTestHttpObjects.request(HttpMethod.POST, Optional.of(new EmployeeRequest(null, "Ava")));
        HttpResponseMessage response = handler.saveEmployee(request, AzureTestHttpObjects.context("saveEmployee"));

        assertEquals(200, response.getStatusCode());
        assertEquals("{\"name\":\"Ava\"}", response.getBody());
        ArgumentCaptor<ExecutionContext> ctxCaptor = ArgumentCaptor.forClass(ExecutionContext.class);
        verify(handler).invokeFunction(any(), eq("saveEmp"), ctxCaptor.capture());
        assertEquals("saveEmployee", ctxCaptor.getValue().getFunctionName());
    }

    @Test
    void findAllReturnsOkAndMapsToFindAllBeanName() {
        EmployeeHandler handler = spy(new EmployeeHandler(mock(FunctionCatalog.class)));
        doReturn(List.of(new EmployeeResponse("Ava"))).when(handler)
                .invokeFunction(any(), eq("findAllEmp"), any(ExecutionContext.class));

        HttpRequestMessage<Optional<String>> request = AzureTestHttpObjects.request(HttpMethod.GET, Optional.empty());
        HttpResponseMessage response = handler.findAllEmployee(request, AzureTestHttpObjects.context("findAllEmployee"));

        assertEquals(200, response.getStatusCode());
        assertEquals("[{\"name\":\"Ava\"}]", response.getBody());
        ArgumentCaptor<ExecutionContext> ctxCaptor = ArgumentCaptor.forClass(ExecutionContext.class);
        verify(handler).invokeFunction(any(), eq("findAllEmp"), ctxCaptor.capture());
        assertEquals("findAllEmployee", ctxCaptor.getValue().getFunctionName());
    }

    @Test
    void findEmployeeByIdReturnsOkWhenFound() {
        EmployeeHandler handler = spy(new EmployeeHandler(mock(FunctionCatalog.class)));
        doReturn(new EmployeeResponse("Neo")).when(handler)
                .invokeFunction(any(), eq("findEmpById"), any(ExecutionContext.class));

        HttpRequestMessage<Optional<String>> request = AzureTestHttpObjects.request(HttpMethod.GET, Optional.empty());
        HttpResponseMessage response = handler.findEmployeeById(request, 5L, AzureTestHttpObjects.context("findEmployeeById"));

        assertEquals(200, response.getStatusCode());
        assertEquals("{\"name\":\"Neo\"}", response.getBody());
    }

    @Test
    void findEmployeeByIdReturnsNotFoundForEmployeeNotFoundException() {
        EmployeeHandler handler = spy(new EmployeeHandler(mock(FunctionCatalog.class)));
        doThrow(new RuntimeException(new EmployeeNotFoundException("No employee found with id = 99")))
                .when(handler).invokeFunction(any(), eq("findEmpById"), any(ExecutionContext.class));

        HttpRequestMessage<Optional<String>> request = AzureTestHttpObjects.request(HttpMethod.GET, Optional.empty());
        HttpResponseMessage response = handler.findEmployeeById(request, 99L, AzureTestHttpObjects.context("findEmployeeById"));

        assertEquals(404, response.getStatusCode());
        assertEquals("{\"message\":\"No employee found with id = 99\"}", response.getBody());
    }

    @Test
    void findEmployeeByNameReturnsBadRequestWhenNameMissing() {
        EmployeeHandler handler = new EmployeeHandler(mock(FunctionCatalog.class));
        HttpRequestMessage<Optional<String>> request = AzureTestHttpObjects.request(HttpMethod.GET, Optional.empty());

        HttpResponseMessage response = handler.findEmployeeByName(request, AzureTestHttpObjects.context("findEmployeeByName"));

        assertEquals(400, response.getStatusCode());
        assertEquals("{\"message\":\"Query param 'name' is required.\"}", response.getBody());
    }

    @Test
    void findEmployeeByNameReturnsBadRequestWhenNameBlank() {
        EmployeeHandler handler = new EmployeeHandler(mock(FunctionCatalog.class));
        AzureTestHttpObjects.TestHttpRequestMessage<Optional<String>> request =
                AzureTestHttpObjects.request(HttpMethod.GET, Optional.<String>empty()).addQuery("name", "   ");

        HttpResponseMessage response = handler.findEmployeeByName(request, AzureTestHttpObjects.context("findEmployeeByName"));

        assertEquals(400, response.getStatusCode());
        assertEquals("{\"message\":\"Query param 'name' is required.\"}", response.getBody());
    }

    @Test
    void findEmployeeByNameReturnsOkWhenPresent() {
        EmployeeHandler handler = spy(new EmployeeHandler(mock(FunctionCatalog.class)));
        doReturn(List.of(new EmployeeResponse("Ava"))).when(handler)
                .invokeFunction(any(), eq("findEmpByName"), any(ExecutionContext.class));

        AzureTestHttpObjects.TestHttpRequestMessage<Optional<String>> request =
                AzureTestHttpObjects.request(HttpMethod.GET, Optional.<String>empty()).addQuery("name", "Ava");
        HttpResponseMessage response = handler.findEmployeeByName(request, AzureTestHttpObjects.context("findEmployeeByName"));

        assertEquals(200, response.getStatusCode());
        assertEquals("[{\"name\":\"Ava\"}]", response.getBody());
        ArgumentCaptor<ExecutionContext> ctxCaptor = ArgumentCaptor.forClass(ExecutionContext.class);
        verify(handler).invokeFunction(any(), eq("findEmpByName"), ctxCaptor.capture());
        assertEquals("findEmployeeByName", ctxCaptor.getValue().getFunctionName());
    }

    @Test
    void deleteEmployeeReturnsOkAndMessage() {
        EmployeeHandler handler = spy(new EmployeeHandler(mock(FunctionCatalog.class)));
        doReturn(null).when(handler).invokeFunction(any(), eq("deleteEmpById"), any(ExecutionContext.class));
        HttpRequestMessage<Optional<String>> request = AzureTestHttpObjects.request(HttpMethod.DELETE, Optional.empty());

        HttpResponseMessage response = handler.deleteEmployeeById(request, 3L, AzureTestHttpObjects.context("deleteEmployeeById"));

        assertEquals(200, response.getStatusCode());
        assertEquals("{\"message\":\"Employee deleted successfully\"}", response.getBody());
    }

    @Test
    void updateEmployeeReturnsBadRequestWhenBodyMissing() {
        EmployeeHandler handler = new EmployeeHandler(mock(FunctionCatalog.class));
        HttpRequestMessage<Optional<EmployeeRequest>> request = AzureTestHttpObjects.request(HttpMethod.PUT, Optional.empty());

        HttpResponseMessage response = handler.updateEmployee(request, 1L, AzureTestHttpObjects.context("updateEmployee"));

        assertEquals(400, response.getStatusCode());
        assertEquals("{\"message\":\"Request body is required.\"}", response.getBody());
    }

    @Test
    void updateEmployeeReturnsOkWhenBodyPresent() {
        EmployeeHandler handler = spy(new EmployeeHandler(mock(FunctionCatalog.class)));
        doReturn(new EmployeeResponse("Mia")).when(handler)
                .invokeFunction(any(), eq("updateEmp"), any(ExecutionContext.class));
        HttpRequestMessage<Optional<EmployeeRequest>> request =
                AzureTestHttpObjects.request(HttpMethod.PUT, Optional.of(new EmployeeRequest(null, "Mia")));

        HttpResponseMessage response = handler.updateEmployee(request, 1L, AzureTestHttpObjects.context("updateEmployee"));

        assertEquals(200, response.getStatusCode());
        assertEquals("{\"name\":\"Mia\"}", response.getBody());
        ArgumentCaptor<ExecutionContext> ctxCaptor = ArgumentCaptor.forClass(ExecutionContext.class);
        verify(handler).invokeFunction(any(), eq("updateEmp"), ctxCaptor.capture());
        assertEquals("updateEmployee", ctxCaptor.getValue().getFunctionName());
    }

    @Test
    void nonEmployeeNotFoundExceptionIsRethrown() {
        EmployeeHandler handler = spy(new EmployeeHandler(mock(FunctionCatalog.class)));
        doThrow(new IllegalStateException("boom")).when(handler)
                .invokeFunction(any(), anyString(), any(ExecutionContext.class));
        HttpRequestMessage<Optional<String>> request = AzureTestHttpObjects.request(HttpMethod.DELETE, Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> handler.deleteEmployeeById(request, 10L, AzureTestHttpObjects.context("deleteEmployeeById")));
    }
}
