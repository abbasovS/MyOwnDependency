package com.example.filemanage;

import com.example.secretsanta.service.impl.PairingService;
import com.github.abbasovS.config.SecretSantaAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;


@SpringBootApplication
public class FileManageApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileManageApplication.class, args);
    }


}
