package com.example.filemanage.dto;

import java.time.LocalDateTime;

public record ApiKeyResponse(String name,
                             String rawKey,
                             LocalDateTime createdAt) {
}
