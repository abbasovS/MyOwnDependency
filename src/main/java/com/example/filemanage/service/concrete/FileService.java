package com.example.filemanage.service.concrete;

import com.example.filemanage.dto.FileResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface FileService {
    FileResponse uploadFile(MultipartFile file, String apiKeyHeader);

    InputStream downloadFile(String storageKey, String apiKeyHeader);

    void deleteFile(String storageKey, String apiKeyHeader);
}
