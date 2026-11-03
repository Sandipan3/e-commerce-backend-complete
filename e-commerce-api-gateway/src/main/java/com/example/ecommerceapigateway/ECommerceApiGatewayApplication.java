package com.example.ecommerceapigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ECommerceApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ECommerceApiGatewayApplication.class, args);
        System.out.println("[API GATEWAY]: I am ready");
    }

}
