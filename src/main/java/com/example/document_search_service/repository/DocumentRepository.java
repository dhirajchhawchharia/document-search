package com.example.document_search_service.repository;

import com.example.document_search_service.model.Document;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface DocumentRepository extends ElasticsearchRepository<Document, String> {
    List<Document> findByContentContaining(String query);
}
