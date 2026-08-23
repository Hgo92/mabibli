package com.hugo.mabibli.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class OpenLibraryConfig {
    @Bean
    public RestClient openLibraryRestClient() {
        return RestClient.builder()
                .baseUrl("https://openlibrary.org")
                .build();
    }
}
