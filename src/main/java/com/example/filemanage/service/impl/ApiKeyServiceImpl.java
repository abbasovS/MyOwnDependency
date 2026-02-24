package com.example.filemanage.service.impl;

import com.example.filemanage.dto.ApiKeyResponse;
import com.example.filemanage.exception.InvalidApiKeyException;
import com.example.filemanage.exception.NotFoundAlgoritm;
import com.example.filemanage.model.ApiKeyEntity;
import com.example.filemanage.model.AuditEventEntity;
import com.example.filemanage.repository.ApiKeyRepository;
import com.example.filemanage.repository.AuditEventRepository;
import com.example.filemanage.service.concrete.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiKeyServiceImpl implements ApiKeyService {
    private final ApiKeyRepository apiKeyRepository;
    private final AuditEventRepository auditEventRepository;

    @Value("${security.bootstrap-api-key:}")
    private String bootstrapApiKey;

    @Override
    public ApiKeyResponse generateKey(String name) {
        String rawKey = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");

        ApiKeyEntity entity = new ApiKeyEntity();
        entity.setName(name);
        entity.setHashKey(hashString(rawKey));
        entity.setActive(true);

        ApiKeyEntity saved = apiKeyRepository.save(entity);
        auditEventRepository.save(AuditEventEntity.builder().action("API_KEY_CREATED").apiKey(saved).build());
        return new ApiKeyResponse(saved.getName(), rawKey, saved.getCreatedAt());
    }

    @Override
    public boolean isValid(String providedKey) {
        if (providedKey == null || providedKey.isBlank()) {
            return false;
        }

        String hashed = hashString(providedKey);
        return apiKeyRepository.findByHashKeyAndActiveTrue(hashed).isPresent();
    }

    @Override
    public ApiKeyEntity getEntityByKey(String rawKey) {
        if (rawKey == null || rawKey.isBlank()) {
            throw new InvalidApiKeyException("API key is missing.");
        }

        String hashed = hashString(rawKey);
        return apiKeyRepository.findByHashKeyAndActiveTrue(hashed)
                .orElseThrow(() -> new InvalidApiKeyException("Invalid API key."));
    }

    @Override
    public Optional<ApiKeyEntity> ensureBootstrapKey() {
        if (bootstrapApiKey == null || bootstrapApiKey.isBlank()) {
            log.warn("security.bootstrap-api-key is empty; no initial API key seeded.");
            return Optional.empty();
        }

        String hashed = hashString(bootstrapApiKey);
        Optional<ApiKeyEntity> existing = apiKeyRepository.findByHashKeyAndActiveTrue(hashed);
        if (existing.isPresent()) {
            return existing;
        }

        ApiKeyEntity entity = new ApiKeyEntity();
        entity.setName("bootstrap");
        entity.setHashKey(hashed);
        entity.setActive(true);
        ApiKeyEntity saved = apiKeyRepository.save(entity);
        log.info("Bootstrap API key seeded (name=bootstrap). Keep it secret and rotate in production.");
        return Optional.of(saved);
    }

    private String hashString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new NotFoundAlgoritm("SHA-256 algorithm not available", e);
        }
    }
}
