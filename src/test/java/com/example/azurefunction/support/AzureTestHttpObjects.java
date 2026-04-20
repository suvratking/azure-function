package com.example.azurefunction.support;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.HttpStatusType;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public final class AzureTestHttpObjects {

    private AzureTestHttpObjects() {
    }

    public static <T> TestHttpRequestMessage<T> request(HttpMethod method, T body) {
        return new TestHttpRequestMessage<>(method, body);
    }

    public static ExecutionContext context(String functionName) {
        return new ExecutionContext() {
            @Override
            public Logger getLogger() {
                return Logger.getLogger("test");
            }

            @Override
            public String getInvocationId() {
                return UUID.randomUUID().toString();
            }

            @Override
            public String getFunctionName() {
                return functionName;
            }
        };
    }

    public static final class TestHttpRequestMessage<T> implements HttpRequestMessage<T> {
        private final HttpMethod method;
        private final T body;
        private final Map<String, String> headers = new HashMap<>();
        private final Map<String, String> queryParameters = new HashMap<>();
        private URI uri;

        public TestHttpRequestMessage(HttpMethod method, T body) {
            this.method = method;
            this.body = body;
            try {
                this.uri = new URI("http://localhost:7072/api/test");
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        public TestHttpRequestMessage<T> addQuery(String key, String value) {
            this.queryParameters.put(key, value);
            return this;
        }

        @Override
        public URI getUri() {
            return uri;
        }

        @Override
        public HttpMethod getHttpMethod() {
            return method;
        }

        @Override
        public Map<String, String> getHeaders() {
            return headers;
        }

        @Override
        public Map<String, String> getQueryParameters() {
            return queryParameters;
        }

        @Override
        public T getBody() {
            return body;
        }

        @Override
        public HttpResponseMessage.Builder createResponseBuilder(HttpStatus status) {
            return new TestHttpResponseBuilder(status);
        }

        @Override
        public HttpResponseMessage.Builder createResponseBuilder(HttpStatusType status) {
            return new TestHttpResponseBuilder(status);
        }
    }

    public static final class TestHttpResponse implements HttpResponseMessage {
        private final HttpStatusType status;
        private final Map<String, String> headers;
        private final Object body;

        public TestHttpResponse(HttpStatusType status, Map<String, String> headers, Object body) {
            this.status = status;
            this.headers = headers;
            this.body = body;
        }

        @Override
        public HttpStatusType getStatus() {
            return status;
        }

        @Override
        public String getHeader(String key) {
            return headers.get(key);
        }

        @Override
        public Object getBody() {
            return body;
        }
    }

    public static final class TestHttpResponseBuilder implements HttpResponseMessage.Builder {
        private HttpStatusType status;
        private final Map<String, String> headers = new HashMap<>();
        private Object body;

        public TestHttpResponseBuilder(HttpStatusType status) {
            this.status = status;
        }

        @Override
        public HttpResponseMessage.Builder status(HttpStatusType httpStatusType) {
            this.status = httpStatusType;
            return this;
        }

        @Override
        public HttpResponseMessage.Builder header(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        @Override
        public HttpResponseMessage.Builder body(Object body) {
            this.body = body;
            return this;
        }

        @Override
        public HttpResponseMessage build() {
            return new TestHttpResponse(status, headers, body);
        }
    }
}
