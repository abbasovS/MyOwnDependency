package com.example.filemanage;

import com.example.filemanage.dto.ApiKeyResponse;
import com.example.filemanage.model.ApiKeyEntity;
import com.example.filemanage.repository.ApiKeyRepository;
import com.example.filemanage.repository.AuditEventRepository;
import com.example.filemanage.service.impl.ApiKeyServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
public class ApiKeyServiceTest {
    @Mock
    private ApiKeyRepository apiKeyRepository;

    @Mock
    private AuditEventRepository auditEventRepository;

    @InjectMocks
    private ApiKeyServiceImpl apiKeyService;

    @Test
    @DisplayName("API Key")
    void generateKey_Success() {
        String appName = "MyTestApp";
        LocalDateTime now = LocalDateTime.now();

        ApiKeyEntity savedEntity = new ApiKeyEntity();
        savedEntity.setName(appName);
        savedEntity.setCreatedAt(now);
        savedEntity.setActive(true);

        when(apiKeyRepository.save(any(ApiKeyEntity.class))).thenReturn(savedEntity);

        ApiKeyResponse response = apiKeyService.generateKey(appName);

        assertNotNull(response);
        assertEquals(appName, response.name());
        assertNotNull(response.rawKey());

        assertEquals(64, response.rawKey().length());

        verify(auditEventRepository, times(1)).save(any());
        verify(apiKeyRepository, times(1)).save(any());
    }
}
