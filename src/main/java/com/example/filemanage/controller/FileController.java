package com.example.filemanage.controller;

import com.example.filemanage.dto.FileResponse;
import com.example.filemanage.service.concrete.FileService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponse> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestHeader("X-API-Key") String apiKey) {

        return ResponseEntity.ok(fileService.uploadFile(file, apiKey));
    }

    @Operation(summary = "Faylı yüklə")
    @GetMapping("/download/{storageKey}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String storageKey,
            @RequestHeader("X-API-Key") String apiKey) {

        InputStream fileStream = fileService.downloadFile(storageKey, apiKey);
        Resource resource = new InputStreamResource(fileStream);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + storageKey + "\"")
                .body(resource);
    }

    @Operation(summary = "fayli sil")
    @DeleteMapping("/delete/{storageKey}")
    public ResponseEntity<Void> deleteFile(
            @PathVariable String storageKey,
            @RequestHeader("X-API-Key") String apiKey) {
        fileService.deleteFile(storageKey, apiKey);
        return ResponseEntity.noContent().build();
    }
}
