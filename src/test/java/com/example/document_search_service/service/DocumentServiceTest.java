package com.example.document_search_service.service;

import com.example.document_search_service.model.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class DocumentServiceTest {

    private static final String TEST_FILE_PATH_1 = "test_files/test_document_1.pdf";
    private static final String TEST_FILE_PATH_2 = "test_files/test_document_2.pdf";

    @Container
    private static final GenericContainer<?> elasticsearch = new GenericContainer<>("docker.elastic.co/elasticsearch/elasticsearch:8.10.4")
            .withExposedPorts(9200)
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "false")
            .waitingFor(Wait.forHttp("/")
                    .forPort(9200)
                    .forStatusCode(200)
                    .withStartupTimeout(Duration.ofMinutes(2)));

    @Autowired
    private DocumentService documentService;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.uris", () -> "http://localhost:" + elasticsearch.getMappedPort(9200));
    }

    @AfterEach
    void tearDown() {
         documentService.deleteAllDocuments();
    }

    @Test
    void testElasticsearchConnection() {
        assertTrue(elasticsearch.isRunning());
        Integer mappedPort = elasticsearch.getMappedPort(9200);
        try {
            URL url = new URL("http://localhost:" + mappedPort);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            assertEquals(200, responseCode);
        } catch (IOException e) {
            fail("Failed to connect to Elasticsearch: " + e.getMessage());
        }
    }

    @Test
    void testIndexDocument() throws IOException {
        ClassPathResource resource = new ClassPathResource(TEST_FILE_PATH_1);
        MockMultipartFile mockFile = new MockMultipartFile("file", "test_document_1.pdf", "application/pdf", resource.getInputStream());
        Document document = documentService.indexDocument(mockFile);
        assertNotNull(document.getId());
        assertEquals("test_document_1.pdf", document.getFilename());
        assertNotNull(document.getContent());
    }

    @Test
    void testIndexMultipleDocuments() throws IOException {
        ClassPathResource resource1 = new ClassPathResource(TEST_FILE_PATH_1);
        MockMultipartFile mockFile1 = new MockMultipartFile("file", "test_document_1.pdf", "application/pdf", resource1.getInputStream());
        ClassPathResource resource2 = new ClassPathResource(TEST_FILE_PATH_2);
        MockMultipartFile mockFile2 = new MockMultipartFile("file", "test_document_2.pdf", "application/pdf", resource2.getInputStream());
        Document document1 = documentService.indexDocument(mockFile1);
        Document document2 = documentService.indexDocument(mockFile2);
        assertNotNull(document1.getId());
        assertEquals("test_document_1.pdf", document1.getFilename());
        assertNotNull(document1.getContent());

        assertNotNull(document2.getId());
        assertEquals("test_document_2.pdf", document2.getFilename());
        assertNotNull(document2.getContent());
    }

    @Test
    void testSearchDocuments() throws IOException {
        ClassPathResource resource = new ClassPathResource(TEST_FILE_PATH_1);
        MockMultipartFile mockFile = new MockMultipartFile("file", "test_document_1.pdf", "application/pdf", resource.getInputStream());
        documentService.indexDocument(mockFile);
        List<Document> documents = documentService.searchDocuments("darkest");
        assertFalse(documents.isEmpty());
        assertEquals("test_document_1.pdf", documents.get(0).getFilename());
    }

    @Test
    void testSearchDocumentsWithMultipleResults() throws IOException {
        ClassPathResource resource1 = new ClassPathResource(TEST_FILE_PATH_1);
        MockMultipartFile mockFile1 = new MockMultipartFile("file", "test_document_1.pdf", "application/pdf", resource1.getInputStream());
        documentService.indexDocument(mockFile1);
        ClassPathResource resource2 = new ClassPathResource(TEST_FILE_PATH_2);
        MockMultipartFile mockFile2 = new MockMultipartFile("file", "test_document_2.pdf", "application/pdf", resource2.getInputStream());
        documentService.indexDocument(mockFile2);
        List<Document> documents = documentService.searchDocuments("life");
        assertEquals(2, documents.size());
    }

    @Test
    void testSearchDocumentsWithNoResults() throws IOException {
        List<Document> documents = documentService.searchDocuments("nonexistent");
        assertTrue(documents.isEmpty());
    }
}