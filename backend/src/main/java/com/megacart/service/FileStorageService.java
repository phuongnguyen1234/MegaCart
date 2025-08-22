package com.megacart.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file, String subDir);

    CompletableFuture<String> storeFileAsync(MultipartFile file, String subDir);

    void deleteFileAsync(String filePath);
}