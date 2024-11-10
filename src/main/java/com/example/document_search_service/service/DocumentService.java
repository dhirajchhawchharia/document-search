package com.example.document_search_service.service;

import com.example.document_search_service.model.Document;
import com.example.document_search_service.repository.DocumentRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {
    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public Document indexDocument(MultipartFile file) throws IOException {
        logger.info("Indexing document: {}", file.getOriginalFilename());
        try {
            String content = extractTextFromPDF(file);
            Document document = new Document();
            document.setId(UUID.randomUUID().toString());
            document.setFilename(file.getOriginalFilename());
            document.setUploadTimestamp(LocalDateTime.now(ZoneOffset.UTC));
            document.setContent(content);
            return documentRepository.save(document);
        } catch (IOException e) {
            logger.error("Failed to index document: {}", file.getOriginalFilename(), e);
            throw e;
        }
    }

    public List<Document> searchDocuments(String query) {
        logger.info("Searching documents with query: {}", query);
        return documentRepository.findByContentContaining(query);
    }

    private String extractTextFromPDF(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        } catch (IOException e) {
            logger.error("Error while extracting text from pdf : {}", e.getMessage());
            throw e;
        }
    }

    public void deleteAllDocuments() {
        logger.info("Deleting all documents");
        documentRepository.deleteAll();
    }
}