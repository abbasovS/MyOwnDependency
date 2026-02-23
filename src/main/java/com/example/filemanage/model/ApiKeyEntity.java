package com.example.filemanage.model;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="api_keys")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiKeyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;
    String hashKey;
    boolean active;
    String name;
    LocalDateTime createdAt=LocalDateTime.now();

}
