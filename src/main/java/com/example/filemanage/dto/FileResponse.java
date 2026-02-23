package com.example.filemanage.dto;

import java.util.UUID;

public record FileResponse(UUID id,
                           String fileName,
                           String storageKey,
                           String providerType) {
}
