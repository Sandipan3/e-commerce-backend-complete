package com.example.ecommerceapigateway.exception;



import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Map<String, Object> attrs = super.getErrorAttributes(request, options);
        attrs.put("gateway", "api-gateway");
        attrs.put("correlationId", request.headers().firstHeader("X-Correlation-Id"));
        return attrs;
    }
}