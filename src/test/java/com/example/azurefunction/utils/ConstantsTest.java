package com.example.azurefunction.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConstantsTest {

    @Test
    void constantsHaveExpectedValues() {
        assertEquals("saveEmp", Constants.SAVE_EMPLOYEE_BEAN);
        assertEquals("findAllEmp", Constants.FIND_ALL_EMPLOYEE_BEAN);
        assertEquals("findEmpById", Constants.FIND_EMPLOYEE_ID_BEAN);
        assertEquals("findEmpByName", Constants.FIND_EMPLOYEE_NAME_BEAN);
        assertEquals("deleteEmpById", Constants.DELETE_EMPLOYEE_ID_BEAN);
        assertEquals("updateEmp", Constants.UPDATE_EMPLOYEE_BEAN);
    }
}
