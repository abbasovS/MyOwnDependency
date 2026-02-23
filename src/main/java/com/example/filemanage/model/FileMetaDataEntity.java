package com.example.filemanage.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "file_metadata")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class FileMetaDataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    UUID id;
    String fileName;
    String storageKey;
    String contentType;
    Long fileSize;
    String providerType;
    @ManyToOne(fetch = FetchType.LAZY)
    ApiKeyEntity apiKey;
    LocalDateTime createdAt = LocalDateTime.now();

}
