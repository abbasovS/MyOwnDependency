package com.example.filemanage.service.impl;

import com.example.filemanage.exception.StorageException;
import com.example.filemanage.service.concrete.StorageProvider;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "storage.provider", havingValue = "local")
public class LocalStorageProviderImpl implements StorageProvider {

    @Value("${storage.local.root-path:uploads}")
    private String rootPath;

    private Path root;

    @PostConstruct
    void init() {
        this.root = Path.of(rootPath);
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new StorageException("Failed to initialize local storage path.");
        }
    }

    @Override
    public String upload(MultipartFile file) {
        try {
            String storageKey = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), root.resolve(storageKey));
            return storageKey;
        } catch (IOException e) {
            throw new StorageException("Local upload failed.");
        }
    }

    @Override
    public InputStream download(String storageKey) {
        try {
            return Files.newInputStream(root.resolve(storageKey));
        } catch (IOException e) {
            throw new StorageException("Local download failed.");
        }
    }

    @Override
    public void delete(String storageKey) {
        try {
            Files.deleteIfExists(root.resolve(storageKey));
        } catch (IOException e) {
            throw new StorageException("Local delete failed.");
        }
    }

    @Override
    public String getProviderType() {
        return "LOCAL";
    }
}
