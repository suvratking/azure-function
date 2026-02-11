package com.example.azurefunction.utils;

import com.microsoft.azure.functions.ExecutionContext;

import java.util.logging.Logger;

public class MappingUtil {

    public static ExecutionContext mapFunction(ExecutionContext original, String beanFunctionName) {
        return new ExecutionContext() {
            @Override
            public String getInvocationId() {
                return original.getInvocationId();
            }

            @Override
            public Logger getLogger() {
                return original.getLogger();
            }

            @Override
            public String getFunctionName() {
                return beanFunctionName;
            }
        };
    }

}
