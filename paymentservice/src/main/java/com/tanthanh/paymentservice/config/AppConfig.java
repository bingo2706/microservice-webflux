package com.tanthanh.paymentservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Value("${accountBaseUrl}")
    private String accountBaseUrl;

    @Bean
    WebClient webClientAccount(){
        return WebClient.builder()
                .baseUrl(accountBaseUrl)
                .build();
    }
}
