package com.example.filemanage.service.impl;

import com.example.filemanage.exception.StorageException;
import com.example.filemanage.service.concrete.StorageProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "storage.provider", havingValue = "local")
public class LocalStorageProviderImpl implements StorageProvider {

    private final Path root = Paths.get("uploads");


    @Override
    public String upload(MultipartFile file) {
        try {
            if (!Files.exists(root)) Files.createDirectories(root);
            String storageKey = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), this.root.resolve(storageKey));
            return storageKey;
        } catch (IOException e) {
            throw new StorageException("Local upload xətası!");
        }
    }

    @Override
    public InputStream download(String storageKey) {
        try {
            return Files.newInputStream(this.root.resolve(storageKey));
        } catch (IOException e) {
                throw new StorageException("Local download xətası!");
        }
    }

    @Override
    public void delete(String storageKey) {
        try {
            Files.deleteIfExists(this.root.resolve(storageKey));
        } catch (IOException e) {
            throw new StorageException("Local silmə xətası!");
        }
    }

    @Override
    public String getProviderType() {
        return "LOCAL";
    }
}
