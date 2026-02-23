package com.example.filemanage.service.concrete;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface StorageProvider {
    String upload(MultipartFile file);
    InputStream download(String storageKey);
    void delete(String storageKey);
    String getProviderType();
}
