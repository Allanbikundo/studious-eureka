package com.scalum.starter.config;

import io.weaviate.client6.v1.api.WeaviateClient;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class WeaviateConfigTest {

    @Autowired private WeaviateClient weaviateClient;

    @Test
    void testWeaviateConnection() throws IOException {
        weaviateClient.cluster.listNodes();
    }
}
