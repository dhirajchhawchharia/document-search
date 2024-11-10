package com.example.document_search_service.controller;

import com.example.document_search_service.model.Document;
import com.example.document_search_service.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DocumentController {
    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/documents/upload")
    public ResponseEntity<Document> uploadDocument(@RequestParam("file") MultipartFile file) throws IOException {
        Document savedDocument = documentService.indexDocument(file);
        return ResponseEntity.ok(savedDocument);
    }

    @GetMapping("/documents/search")
    public ResponseEntity<List<Document>> searchDocuments(@RequestParam("query") String query) {
        List<Document> results = documentService.searchDocuments(query);
        return ResponseEntity.ok(results);
    }
}