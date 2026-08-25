package com.hugo.mabibli.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class OpenLibraryConfig {
    @Bean
    public RestClient openLibraryRestClient(
            @Value("${open-library.base-url}") String baseUrl,
            @Value("${open-library.user-agent}") String userAgent
    ) {
        return RestClient.builder()
                .baseUrl("https://openlibrary.org")
                .defaultHeader("User-Agent", userAgent)
                .build();
    }
}
