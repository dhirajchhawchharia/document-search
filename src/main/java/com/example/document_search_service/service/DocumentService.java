package com.example.document_search_service.service;

import com.example.document_search_service.model.Document;
import com.example.document_search_service.repository.DocumentRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public Document indexDocument(MultipartFile file) throws IOException {
        String content = extractTextFromPDF(file);

        Document document = new Document();
        document.setId(UUID.randomUUID().toString()); // Generate a unique ID
        document.setFilename(file.getOriginalFilename());
        document.setUploadTimestamp(LocalDateTime.now(ZoneOffset.UTC)); // Use UTC time
        document.setContent(content);

        return documentRepository.save(document);
    }

    public List<Document> searchDocuments(String query) {
        return documentRepository.findByContentContaining(query);
    }

    private String extractTextFromPDF(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }
}