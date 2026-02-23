package com.example.filemanage.service.concrete;

import com.example.filemanage.dto.FileResponse;
import com.example.filemanage.exception.InvalidApiKeyException;
import com.example.filemanage.exception.NotFoundAlgoritm;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface FileService {
    FileResponse uploadFile(MultipartFile file, String apiKeyHeader) throws NotFoundAlgoritm, InvalidApiKeyException;
    InputStream downloadFile(String storageKey, String apiKeyHeader) throws NotFoundAlgoritm, InvalidApiKeyException;
    void deleteFile(String storageKey, String apiKeyHeader) throws NotFoundAlgoritm, InvalidApiKeyException;

}
