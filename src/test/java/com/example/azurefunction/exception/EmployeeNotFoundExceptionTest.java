package com.example.azurefunction.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmployeeNotFoundExceptionTest {

    @Test
    void storesMessage() {
        EmployeeNotFoundException exception = new EmployeeNotFoundException("not found");
        assertEquals("not found", exception.getMessage());
    }
}
