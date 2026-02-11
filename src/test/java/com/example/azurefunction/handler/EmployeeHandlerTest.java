package com.example.azurefunction.handler;

import com.example.azurefunction.dto.EmployeeRequest;
import com.example.azurefunction.dto.EmployeeResponse;
import com.example.azurefunction.exception.EmployeeNotFoundException;
import com.example.azurefunction.support.AzureTestHttpObjects;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Answers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EmployeeHandlerTest {

    @Test
    void constructorIsCovered() {
        new EmployeeHandler();
    }

    @Test
    void saveEmployeeReturnsOkAndMapsToSaveBeanName() {
        EmployeeHandler handler = mock(EmployeeHandler.class, Answers.CALLS_REAL_METHODS);
        doAnswer(inv -> new EmployeeResponse("Ava")).when(handler).handleRequest(any(), any(ExecutionContext.class));

        HttpRequestMessage<Optional<EmployeeRequest>> request =
                AzureTestHttpObjects.request(HttpMethod.POST, Optional.of(new EmployeeRequest(null, "Ava")));
        HttpResponseMessage response = handler.saveEmployee(request, AzureTestHttpObjects.context("saveEmployee"));

        assertEquals(200, response.getStatusCode());
        assertEquals("Ava", ((EmployeeResponse) response.getBody()).name());
        ArgumentCaptor<ExecutionContext> ctxCaptor = ArgumentCaptor.forClass(ExecutionContext.class);
        verify(handler).handleRequest(any(), ctxCaptor.capture());
        assertEquals("saveEmp", ctxCaptor.getValue().getFunctionName());
    }

    @Test
    void findAllReturnsOkAndMapsToFindAllBeanName() {
        EmployeeHandler handler = mock(EmployeeHandler.class, Answers.CALLS_REAL_METHODS);
        doAnswer(inv -> List.of(new EmployeeResponse("Ava"))).when(handler).handleRequest(any(), any(ExecutionContext.class));

        HttpRequestMessage<Optional<String>> request = AzureTestHttpObjects.request(HttpMethod.GET, Optional.empty());
        HttpResponseMessage response = handler.findAllEmployee(request, AzureTestHttpObjects.context("findAllEmployee"));

        assertEquals(200, response.getStatusCode());
        assertEquals(1, ((List<?>) response.getBody()).size());
        ArgumentCaptor<ExecutionContext> ctxCaptor = ArgumentCaptor.forClass(ExecutionContext.class);
        verify(handler).handleRequest(any(), ctxCaptor.capture());
        assertEquals("findAllEmp", ctxCaptor.getValue().getFunctionName());
    }

    @Test
    void findEmployeeByIdReturnsOkWhenFound() {
        EmployeeHandler handler = mock(EmployeeHandler.class, Answers.CALLS_REAL_METHODS);
        doAnswer(inv -> new EmployeeResponse("Neo")).when(handler).handleRequest(any(), any(ExecutionContext.class));

        HttpRequestMessage<Optional<String>> request = AzureTestHttpObjects.request(HttpMethod.GET, Optional.empty());
        HttpResponseMessage response = handler.findEmployeeById(request, 5L, AzureTestHttpObjects.context("findEmployeeById"));

        assertEquals(200, response.getStatusCode());
        assertEquals("Neo", ((EmployeeResponse) response.getBody()).name());
    }

    @Test
    void findEmployeeByIdReturnsNotFoundForEmployeeNotFoundException() {
        EmployeeHandler handler = mock(EmployeeHandler.class, Answers.CALLS_REAL_METHODS);
        doThrow(new RuntimeException(new EmployeeNotFoundException("No employee found with id = 99")))
                .when(handler).handleRequest(any(), any(ExecutionContext.class));

        HttpRequestMessage<Optional<String>> request = AzureTestHttpObjects.request(HttpMethod.GET, Optional.empty());
        HttpResponseMessage response = handler.findEmployeeById(request, 99L, AzureTestHttpObjects.context("findEmployeeById"));

        assertEquals(404, response.getStatusCode());
        assertEquals(Map.of("message", "No employee found with id = 99"), response.getBody());
    }

    @Test
    void findEmployeeByNameReturnsBadRequestWhenNameMissing() {
        EmployeeHandler handler = mock(EmployeeHandler.class, Answers.CALLS_REAL_METHODS);
        HttpRequestMessage<Optional<String>> request = AzureTestHttpObjects.request(HttpMethod.GET, Optional.empty());

        HttpResponseMessage response = handler.findEmployeeByName(request, AzureTestHttpObjects.context("findEmployeeByName"));

        assertEquals(400, response.getStatusCode());
        assertEquals("Query param 'name' is required.", response.getBody());
    }

    @Test
    void findEmployeeByNameReturnsBadRequestWhenNameBlank() {
        EmployeeHandler handler = mock(EmployeeHandler.class, Answers.CALLS_REAL_METHODS);
        AzureTestHttpObjects.TestHttpRequestMessage<Optional<String>> request =
                AzureTestHttpObjects.request(HttpMethod.GET, Optional.<String>empty()).addQuery("name", "   ");

        HttpResponseMessage response = handler.findEmployeeByName(request, AzureTestHttpObjects.context("findEmployeeByName"));

        assertEquals(400, response.getStatusCode());
        assertEquals("Query param 'name' is required.", response.getBody());
    }

    @Test
    void findEmployeeByNameReturnsOkWhenPresent() {
        EmployeeHandler handler = mock(EmployeeHandler.class, Answers.CALLS_REAL_METHODS);
        doAnswer(inv -> List.of(new EmployeeResponse("Ava"))).when(handler).handleRequest(any(), any(ExecutionContext.class));

        AzureTestHttpObjects.TestHttpRequestMessage<Optional<String>> request =
                AzureTestHttpObjects.request(HttpMethod.GET, Optional.<String>empty()).addQuery("name", "Ava");
        HttpResponseMessage response = handler.findEmployeeByName(request, AzureTestHttpObjects.context("findEmployeeByName"));

        assertEquals(200, response.getStatusCode());
        assertInstanceOf(List.class, response.getBody());
        ArgumentCaptor<ExecutionContext> ctxCaptor = ArgumentCaptor.forClass(ExecutionContext.class);
        verify(handler).handleRequest(any(), ctxCaptor.capture());
        assertEquals("findEmpByName", ctxCaptor.getValue().getFunctionName());
    }

    @Test
    void deleteEmployeeReturnsOkAndMessage() {
        EmployeeHandler handler = mock(EmployeeHandler.class, Answers.CALLS_REAL_METHODS);
        doAnswer(inv -> null).when(handler).handleRequest(any(), any(ExecutionContext.class));
        HttpRequestMessage<Optional<String>> request = AzureTestHttpObjects.request(HttpMethod.DELETE, Optional.empty());

        HttpResponseMessage response = handler.deleteEmployeeById(request, 3L, AzureTestHttpObjects.context("deleteEmployeeById"));

        assertEquals(200, response.getStatusCode());
        assertEquals(Map.of("message", "Employee deleted successfully"), response.getBody());
    }

    @Test
    void updateEmployeeReturnsBadRequestWhenBodyMissing() {
        EmployeeHandler handler = mock(EmployeeHandler.class, Answers.CALLS_REAL_METHODS);
        HttpRequestMessage<Optional<EmployeeRequest>> request = AzureTestHttpObjects.request(HttpMethod.PUT, Optional.empty());

        HttpResponseMessage response = handler.updateEmployee(request, 1L, AzureTestHttpObjects.context("updateEmployee"));

        assertEquals(400, response.getStatusCode());
        assertEquals("Request body is required.", response.getBody());
    }

    @Test
    void updateEmployeeReturnsOkWhenBodyPresent() {
        EmployeeHandler handler = mock(EmployeeHandler.class, Answers.CALLS_REAL_METHODS);
        doAnswer(inv -> new EmployeeResponse("Mia")).when(handler).handleRequest(any(), any(ExecutionContext.class));
        HttpRequestMessage<Optional<EmployeeRequest>> request =
                AzureTestHttpObjects.request(HttpMethod.PUT, Optional.of(new EmployeeRequest(null, "Mia")));

        HttpResponseMessage response = handler.updateEmployee(request, 1L, AzureTestHttpObjects.context("updateEmployee"));

        assertEquals(200, response.getStatusCode());
        assertEquals("Mia", ((EmployeeResponse) response.getBody()).name());
        ArgumentCaptor<ExecutionContext> ctxCaptor = ArgumentCaptor.forClass(ExecutionContext.class);
        verify(handler).handleRequest(any(), ctxCaptor.capture());
        assertEquals("updateEmp", ctxCaptor.getValue().getFunctionName());
    }

    @Test
    void nonEmployeeNotFoundExceptionIsRethrown() {
        EmployeeHandler handler = mock(EmployeeHandler.class, Answers.CALLS_REAL_METHODS);
        doThrow(new IllegalStateException("boom")).when(handler).handleRequest(any(), any(ExecutionContext.class));
        HttpRequestMessage<Optional<String>> request = AzureTestHttpObjects.request(HttpMethod.DELETE, Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> handler.deleteEmployeeById(request, 10L, AzureTestHttpObjects.context("deleteEmployeeById")));
    }
}
