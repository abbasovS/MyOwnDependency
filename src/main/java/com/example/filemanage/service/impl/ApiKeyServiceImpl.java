package com.example.filemanage.service.impl;

import com.example.filemanage.dto.ApiKeyResponse;
import com.example.filemanage.exception.InvalidApiKeyException;
import com.example.filemanage.exception.NotFoundAlgoritm;
import com.example.filemanage.model.ApiKeyEntity;
import com.example.filemanage.repository.ApiKeyRepository;
import com.example.filemanage.service.concrete.ApiKeyService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApiKeyServiceImpl implements ApiKeyService {
    ApiKeyRepository apiKeyRepository;

    @Override
    public ApiKeyResponse generateKey(String name) throws NotFoundAlgoritm {
        String rawKey = UUID.randomUUID().toString().replace("-", "");

        ApiKeyEntity entity = new ApiKeyEntity();
        entity.setName(name);
        entity.setHashKey(hashString(rawKey));
        entity.setActive(true);

        apiKeyRepository.save(entity);

        return new ApiKeyResponse(entity.getName(), rawKey, entity.getCreatedAt());
    }

    @Override
    public boolean isValid(String providedKey) {
        if (providedKey == null || providedKey.isBlank()) return false;

        String hashed = null;
        try {
            hashed = hashString(providedKey);
        } catch (NotFoundAlgoritm e) {
            throw new RuntimeException(e);
        }
        return apiKeyRepository.findByHashKeyAndActiveTrue(hashed).isPresent();
    }

    @Override
    public ApiKeyEntity getEntityByKey(String rawKey) throws InvalidApiKeyException, NotFoundAlgoritm {
        if (rawKey == null || rawKey.isBlank()) {
            throw new InvalidApiKeyException("API Key boş ola bilməz!");
        }

        String hashed = hashString(rawKey);
        return apiKeyRepository.findByHashKeyAndActiveTrue(hashed)
                .orElseThrow(() -> new InvalidApiKeyException("Aktiv API Key tapılmadı!"));
    }

    private String hashString(String input) throws NotFoundAlgoritm {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new NotFoundAlgoritm("SHA-256 alqoritmi tapılmadı!");
        }
    }
}
