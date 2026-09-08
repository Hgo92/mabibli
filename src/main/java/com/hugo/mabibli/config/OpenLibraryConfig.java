package com.hugo.mabibli.config;

import com.hugo.mabibli.config.properties.OpenLibraryProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;

@Configuration
public class OpenLibraryConfig {
    @Bean
    public RestClient openLibraryRestClient(
            OpenLibraryProperties properties
    ) {
        HttpClient httpClient =
                HttpClient.newBuilder()
                        .connectTimeout(
                                properties.connectTimeout()
                        )
                        .followRedirects(
                                HttpClient.Redirect.NORMAL
                        )
                        .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);

        requestFactory.setReadTimeout(properties.readTimeout());

        return RestClient.builder()
                .baseUrl(properties.baseUrl().toString())
                .defaultHeader("User-Agent", properties.userAgent())
                .build();
    }
}
