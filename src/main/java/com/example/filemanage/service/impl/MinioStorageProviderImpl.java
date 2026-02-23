package com.example.filemanage.service.impl;

import com.example.filemanage.exception.StorageException;
import com.example.filemanage.service.concrete.StorageProvider;
import io.minio.*;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "storage.provider", havingValue = "minio")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MinioStorageProviderImpl implements StorageProvider {
    final MinioClient minioClient;
    @Value("${minio.bucket}")
     String bucketName;

    @PostConstruct
    public void init() {
        try {
            boolean found = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Bucket yaradıla bilmədi: " + e.getMessage());
        }
    }
    @Override
    public String upload(MultipartFile file) {
        String storageKey = UUID.randomUUID() + "_" + file.getOriginalFilename();
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(storageKey)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return storageKey;
        } catch (Exception e) {
            throw new StorageException("MinIO upload xətası!");
        }
    }

    @Override
    public InputStream download(String storageKey) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder().bucket(bucketName).object(storageKey).build()
            );
        } catch (Exception e) {
            throw new StorageException("MinIO download xətası!");
        }
    }

    @Override
    public void delete(String storageKey) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(storageKey).build()
            );
        } catch (Exception e) {
            throw new StorageException("MinIO silmə xətası!");
        }
    }

    @Override
    public String getProviderType() {
        return "MINIO";
    }
}
