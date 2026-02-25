package com.example.filemanage;

import com.example.filemanage.dto.FileResponse;
import com.example.filemanage.model.ApiKeyEntity;
import com.example.filemanage.model.FileMetaDataEntity;
import com.example.filemanage.repository.AuditEventRepository;
import com.example.filemanage.repository.FileMetadataRepository;
import com.example.filemanage.service.concrete.ApiKeyService;
import com.example.filemanage.service.concrete.StorageProvider;
import com.example.filemanage.service.impl.FileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {
    @Mock
    private StorageProvider storageProvider;
    @Mock
    private FileMetadataRepository fileMetadataRepository;
    @Mock
    private AuditEventRepository auditEventRepository;
    @Mock
    private ApiKeyService apiKeyService;
    @InjectMocks
    private FileServiceImpl fileService;
    private ApiKeyEntity mockApiKey;

    @BeforeEach
    void setUp() {
        mockApiKey = new ApiKeyEntity();
        mockApiKey.setHashKey("test-api-key");
    }

    @Test
    @DisplayName("Fayl yükleme")
    void uploadFile_Success() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "content".getBytes());
        String apiKeyHeader = "test-api-key";
        String generatedStorageKey = "uuid-storage-key";
        UUID randomId = UUID.randomUUID();
        FileMetaDataEntity savedMetadata = FileMetaDataEntity.builder()
                .id(randomId)
                .fileName("test.txt")
                .storageKey(generatedStorageKey)
                .providerType("MINIO")
                .build();

        when(apiKeyService.getEntityByKey(apiKeyHeader)).thenReturn(mockApiKey);
        when(storageProvider.upload(file)).thenReturn(generatedStorageKey);
        when(storageProvider.getProviderType()).thenReturn("MINIO");
        when(fileMetadataRepository.save(any(FileMetaDataEntity.class))).thenReturn(savedMetadata);

        FileResponse response = fileService.uploadFile(file, apiKeyHeader);

        assertNotNull(response);
        assertEquals(randomId, response.id());
        assertEquals("test.txt", response.fileName());
        assertEquals(generatedStorageKey, response.storageKey());
    }
}
