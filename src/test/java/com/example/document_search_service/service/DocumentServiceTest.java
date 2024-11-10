package com.example.document_search_service.service;

import com.example.document_search_service.model.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class DocumentServiceTest {

    @Container
    private static final GenericContainer<?> elasticsearch = new GenericContainer<>("docker.elastic.co/elasticsearch/elasticsearch:7.12.0")
            .withExposedPorts(9200)
            .withEnv("discovery.type", "single-node")
            .waitingFor(Wait.forHttp("/").forPort(9200).forStatusCode(200).withStartupTimeout(Duration.ofSeconds(30)));

    @Autowired
    private DocumentService documentService;

    @BeforeAll
    static void setUp() {
        elasticsearch.start();
        // Configure the Elasticsearch connection URL
        System.setProperty("spring.elasticsearch.uris", "http://localhost:" + elasticsearch.getMappedPort(9200));
    }

    @Test
    void testIndexDocument() throws IOException {
        // Create a MockMultipartFile from the specific file path
        File file = new File("/Users/dhiraj/Downloads/Offer Letter Angel One.pdf");
        MockMultipartFile mockFile = new MockMultipartFile("file", "Offer Letter Angel One.pdf", "application/pdf", new FileInputStream(file));

        // Call the indexDocument method
        Document document = documentService.indexDocument(mockFile);

        // Assert that the document is indexed successfully
        assertNotNull(document.getId());
        assertEquals("Offer Letter Angel One.pdf", document.getFilename());
        assertNotNull(document.getContent());
    }

    @Test
    void testSearchDocuments() throws IOException {
        // Index the specific document
        File file = new File("/Users/dhiraj/Downloads/Offer Letter Angel One.pdf");
        MockMultipartFile mockFile = new MockMultipartFile("file", "Offer Letter Angel One.pdf", "application/pdf", new FileInputStream(file));
        documentService.indexDocument(mockFile);

        // Perform a search
        List<Document> documents = documentService.searchDocuments("Angel One");

        // Assert that the search returns the expected document
        assertFalse(documents.isEmpty());
        assertEquals("Offer Letter Angel One.pdf", documents.get(0).getFilename());
    }
}