package com.example.filemanage.config;

import com.example.filemanage.service.concrete.ApiKeyService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BootstrapConfig {

    @Bean
    CommandLineRunner bootstrapApiKey(ApiKeyService apiKeyService) {
        return args -> apiKeyService.ensureBootstrapKey();
    }
}
