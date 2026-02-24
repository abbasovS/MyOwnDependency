package com.example.filemanage.service.impl;

import com.example.filemanage.dto.FileResponse;
import com.example.filemanage.model.ApiKeyEntity;
import com.example.filemanage.model.AuditEventEntity;
import com.example.filemanage.model.FileMetaDataEntity;
import com.example.filemanage.repository.AuditEventRepository;
import com.example.filemanage.repository.FileMetadataRepository;
import com.example.filemanage.service.concrete.ApiKeyService;
import com.example.filemanage.service.concrete.FileService;
import com.example.filemanage.service.concrete.StorageProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final StorageProvider storageProvider;
    private final FileMetadataRepository fileMetadataRepository;
    private final AuditEventRepository auditEventRepository;
    private final ApiKeyService apiKeyService;

    @Override
    @Transactional
    public FileResponse uploadFile(MultipartFile file, String apiKeyHeader) {
        ApiKeyEntity apiKeyEntity = apiKeyService.getEntityByKey(apiKeyHeader);
        String storageKey = storageProvider.upload(file);

        FileMetaDataEntity metadata = FileMetaDataEntity.builder()
                .fileName(file.getOriginalFilename())
                .storageKey(storageKey)
                .contentType(file.getContentType() == null ? "application/octet-stream" : file.getContentType())
                .fileSize(file.getSize())
                .providerType(storageProvider.getProviderType())
                .apiKey(apiKeyEntity)
                .build();

        FileMetaDataEntity saved = fileMetadataRepository.save(metadata);
        auditEventRepository.save(AuditEventEntity.builder().action("FILE_UPLOAD").apiKey(apiKeyEntity).fileMetadata(saved).build());

        return new FileResponse(saved.getId(), saved.getFileName(), saved.getStorageKey(), saved.getProviderType());
    }

    @Override
    public InputStream downloadFile(String storageKey, String apiKeyHeader) {
        ApiKeyEntity apiKeyEntity = apiKeyService.getEntityByKey(apiKeyHeader);
        FileMetaDataEntity metadata = fileMetadataRepository.findByStorageKey(storageKey)
                .orElseThrow(() -> new IllegalArgumentException("File metadata not found: " + storageKey));
        auditEventRepository.save(AuditEventEntity.builder().action("FILE_DOWNLOAD").apiKey(apiKeyEntity).fileMetadata(metadata).build());
        return storageProvider.download(storageKey);
    }

    @Override
    @Transactional
    public void deleteFile(String storageKey, String apiKeyHeader) {
        ApiKeyEntity apiKeyEntity = apiKeyService.getEntityByKey(apiKeyHeader);
        FileMetaDataEntity metadata = fileMetadataRepository.findByStorageKey(storageKey)
                .orElseThrow(() -> new IllegalArgumentException("File metadata not found: " + storageKey));

        storageProvider.delete(storageKey);
        auditEventRepository.save(AuditEventEntity.builder().action("FILE_DELETE").apiKey(apiKeyEntity).fileMetadata(metadata).build());
        fileMetadataRepository.delete(metadata);
    }
}
