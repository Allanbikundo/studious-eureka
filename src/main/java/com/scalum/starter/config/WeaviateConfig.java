package com.scalum.starter.config;

import io.weaviate.client6.v1.api.WeaviateClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

// @Configuration
public class WeaviateConfig {

    @Value("${weaviate.host}")
    private String host;

    @Value("${weaviate.gprc}")
    private String gprcHost;

    @Value("${weaviate.scheme}")
    private String scheme;

    @Value("${weaviate.api-key}")
    private String apiKey;

    @Bean
    public WeaviateClient weaviateClient() {

        return WeaviateClient.connectToWeaviateCloud(host, apiKey);
    }
}
