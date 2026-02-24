package com.example.filemanage.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "audit_events")
public class AuditEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "api_key_id")
    private ApiKeyEntity apiKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_metadata_id")
    private FileMetaDataEntity fileMetadata;

    private String requestIp;

    private LocalDateTime createdAt;

    @PrePersist
    void initCreatedAt() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
