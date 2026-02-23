package com.example.filemanage.controller;

import com.example.filemanage.dto.ApiKeyResponse;
import com.example.filemanage.exception.NotFoundAlgoritm;
import com.example.filemanage.service.impl.ApiKeyServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/management/keys")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ApiKeyController {
      ApiKeyServiceImpl apiKeyService;

    @PostMapping
    public ResponseEntity<ApiKeyResponse> createKey(@RequestParam String name) throws NotFoundAlgoritm {
        return ResponseEntity.ok(apiKeyService.generateKey(name));
    }
}
