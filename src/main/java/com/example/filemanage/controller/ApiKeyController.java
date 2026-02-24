package com.example.filemanage.controller;

import com.example.filemanage.dto.ApiKeyResponse;
import com.example.filemanage.service.concrete.ApiKeyService;
import com.github.abbasovS.config.SecretSantaAutoConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/management/keys")
@RequiredArgsConstructor
public class ApiKeyController {
    private final ApiKeyService apiKeyService;

    @PostMapping
    public ResponseEntity<ApiKeyResponse> createKey(@RequestParam String name) {
        return ResponseEntity.ok(apiKeyService.generateKey(name));
    }


}
