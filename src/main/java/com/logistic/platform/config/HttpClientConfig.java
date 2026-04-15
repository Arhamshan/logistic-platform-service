package com.logistic.platform.config;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfig {

    @Bean
    public CloseableHttpClient httpClient() {
        // Configure connection pooling
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100); // Maximum total connections
        connectionManager.setDefaultMaxPerRoute(20); // Maximum connections per route

        // Configure request timeouts
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000) // Connection timeout (ms)
                .setSocketTimeout(5000) // Socket timeout (ms)
                .setConnectionRequestTimeout(5000) // Connection request timeout (ms)
                .build();

        // Build the CloseableHttpClient
        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setConnectionReuseStrategy((response, context) -> false) // Disable connection reuse
                .build();
    }
}
