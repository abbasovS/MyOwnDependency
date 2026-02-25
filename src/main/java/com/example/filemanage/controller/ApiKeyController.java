package com.example.filemanage.controller;

import com.example.filemanage.dto.ApiKeyResponse;
import com.example.filemanage.service.concrete.ApiKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/management/keys")
@RequiredArgsConstructor
public class ApiKeyController {
    private final ApiKeyService apiKeyService;

    @PostMapping
    public ResponseEntity<ApiKeyResponse> createKey(@RequestParam(defaultValue = "client") String name) {
        return ResponseEntity.ok(apiKeyService.generateKey(name));
    }

    @GetMapping
    public ResponseEntity<ApiKeyResponse> createKeyViaGet(@RequestParam(defaultValue = "client") String name) {
        return ResponseEntity.ok(apiKeyService.generateKey(name));
    }
}
