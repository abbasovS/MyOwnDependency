package com.example.filemanage.service.concrete;

import com.example.filemanage.dto.ApiKeyResponse;
import com.example.filemanage.exception.InvalidApiKeyException;
import com.example.filemanage.exception.NotFoundAlgoritm;
import com.example.filemanage.model.ApiKeyEntity;

public interface ApiKeyService {
    ApiKeyResponse generateKey(String name) throws NotFoundAlgoritm;
    boolean isValid(String providedKey);
    ApiKeyEntity getEntityByKey(String rawKey) throws InvalidApiKeyException, NotFoundAlgoritm;

}
