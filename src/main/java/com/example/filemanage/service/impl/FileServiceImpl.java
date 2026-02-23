package com.example.filemanage.service.impl;

import com.example.filemanage.dto.FileResponse;
import com.example.filemanage.exception.InvalidApiKeyException;
import com.example.filemanage.exception.NotFoundAlgoritm;
import com.example.filemanage.model.ApiKeyEntity;
import com.example.filemanage.model.FileMetaDataEntity;
import com.example.filemanage.repository.FileMetadataRepository;
import com.example.filemanage.service.concrete.ApiKeyService;
import com.example.filemanage.service.concrete.FileService;
import com.example.filemanage.service.concrete.StorageProvider;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileServiceImpl implements FileService {
    StorageProvider storageProvider;
    FileMetadataRepository fileMetadataRepository;
    ApiKeyService apiKeyService;


    @Override
    public FileResponse uploadFile(MultipartFile file, String apiKeyHeader) throws NotFoundAlgoritm, InvalidApiKeyException {
        ApiKeyEntity apiKeyEntity = apiKeyService.getEntityByKey(apiKeyHeader);

        String storageKey = storageProvider.upload(file);

        FileMetaDataEntity metadata = FileMetaDataEntity.builder()
                .fileName(file.getOriginalFilename())
                .storageKey(storageKey)
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .providerType(storageProvider.getProviderType())
                .apiKey(apiKeyEntity)
                .build();

        fileMetadataRepository.save(metadata);

        return new FileResponse(
                metadata.getId(),
                metadata.getFileName(),
                metadata.getStorageKey(),
                metadata.getProviderType()
        );
    }

    @Override
    public InputStream downloadFile(String storageKey, String apiKeyHeader) throws NotFoundAlgoritm, InvalidApiKeyException {
        apiKeyService.getEntityByKey(apiKeyHeader);
        return storageProvider.download(storageKey);
    }

    @Override
    @Transactional
    public void deleteFile(String storageKey, String apiKeyHeader) throws NotFoundAlgoritm, InvalidApiKeyException {
        apiKeyService.getEntityByKey(apiKeyHeader);
        storageProvider.delete(storageKey);
        fileMetadataRepository.findByStorageKey(storageKey)
                .ifPresent(fileMetadataRepository::delete);
    }
}

