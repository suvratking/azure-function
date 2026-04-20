package com.example.azurefunction.utils;

import com.microsoft.azure.functions.ExecutionContext;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class MappingUtilTest {

    @Test
    void constructorIsCovered() {
        new MappingUtil();
    }

    @Test
    void mapFunctionOverridesFunctionNameAndKeepsLoggerAndInvocationId() {
        ExecutionContext original = new ExecutionContext() {
            @Override
            public Logger getLogger() {
                return Logger.getLogger("original");
            }

            @Override
            public String getInvocationId() {
                return "inv-1";
            }

            @Override
            public String getFunctionName() {
                return "originalName";
            }
        };

        ExecutionContext mapped = MappingUtil.mapFunction(original, "mappedName");

        assertEquals("mappedName", mapped.getFunctionName());
        assertEquals("inv-1", mapped.getInvocationId());
        assertSame(original.getLogger(), mapped.getLogger());
    }
}
