package com.example.filemanage.service.concrete;

import com.example.filemanage.dto.ApiKeyResponse;
import com.example.filemanage.model.ApiKeyEntity;

import java.util.Optional;

public interface ApiKeyService {
    ApiKeyResponse generateKey(String name);

    boolean isValid(String providedKey);

    ApiKeyEntity getEntityByKey(String rawKey);

    Optional<ApiKeyEntity> ensureBootstrapKey();
}
