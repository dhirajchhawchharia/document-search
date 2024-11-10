package com.example.document_search_service.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Getter
@Setter
@org.springframework.data.elasticsearch.annotations.Document(indexName = "documents")
public class Document {
    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String filename;

    @Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime uploadTimestamp;

    @Field(type = FieldType.Text)
    private String content;
}