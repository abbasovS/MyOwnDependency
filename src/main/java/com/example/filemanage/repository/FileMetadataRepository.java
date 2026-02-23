package com.example.filemanage.repository;

import com.example.filemanage.model.FileMetaDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FileMetadataRepository extends JpaRepository<FileMetaDataEntity, UUID> {
    Optional<FileMetaDataEntity> findByStorageKey(String storageKey);
}
