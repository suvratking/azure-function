package com.example.azurefunction.enitity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmployeeTest {

    @Test
    void builderGetterSetterToStringAndConstructors() {
        Employee employee = Employee.builder().id(1L).name("Ava").build();
        assertEquals(1L, employee.getId());
        assertEquals("Ava", employee.getName());
        assertTrue(employee.toString().contains("Ava"));

        Employee noArgs = new Employee();
        assertNull(noArgs.getId());
        noArgs.setId(2L);
        noArgs.setName("Neo");
        assertEquals(2L, noArgs.getId());
        assertEquals("Neo", noArgs.getName());

        Employee allArgs = new Employee(3L, "Mia");
        assertEquals(3L, allArgs.getId());
        assertEquals("Mia", allArgs.getName());
    }

    @Test
    void equalsAndHashCodeBehavior() {
        Employee a = Employee.builder().id(1L).name("A").build();
        Employee b = Employee.builder().id(1L).name("B").build();
        Employee c = Employee.builder().id(2L).name("C").build();
        Employee nullId = Employee.builder().name("N").build();

        assertEquals(a, a);
        assertEquals(a, b);
        assertNotEquals(a, c);
        assertNotEquals(a, null);
        assertNotEquals(a, "notEmployee");
        assertNotEquals(a, nullId);
        assertNotNull(a.hashCode());
    }
}
