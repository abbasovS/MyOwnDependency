package com.example.filemanage.repository;

import com.example.filemanage.model.ApiKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKeyEntity, UUID> {
    Optional<ApiKeyEntity> findByHashKeyAndActiveTrue(String hashKey);

}
