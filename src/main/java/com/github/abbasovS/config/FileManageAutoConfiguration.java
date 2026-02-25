package com.github.abbasovS.config;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = "com.example.filemanage")
@EnableJpaRepositories(basePackages = "com.example.filemanage.repository")
@EntityScan(basePackages = "com.example.filemanage.model")
public class FileManageAutoConfiguration {
}
