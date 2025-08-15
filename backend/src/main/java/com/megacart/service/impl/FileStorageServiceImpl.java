package com.megacart.service.impl;

import com.megacart.exception.FileStorageException;
import com.megacart.service.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageServiceImpl(@Value("${file.upload-dir:uploads}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Không thể tạo thư mục để lưu trữ file.", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String subDir) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            if (originalFileName.contains("..")) {
                throw new FileStorageException("Tên file chứa ký tự không hợp lệ: " + originalFileName);
            }

            String fileExtension = "";
            int dotIndex = originalFileName.lastIndexOf('.');
            if (dotIndex > 0) {
                fileExtension = originalFileName.substring(dotIndex);
            }
            String newFileName = UUID.randomUUID().toString() + fileExtension;

            Path targetDir = this.fileStorageLocation.resolve(subDir);
            Files.createDirectories(targetDir);

            Path targetLocation = targetDir.resolve(newFileName);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
            }

            return Paths.get(subDir, newFileName).toString().replace("\\", "/");
        } catch (IOException ex) {
            throw new FileStorageException("Không thể lưu file " + originalFileName + ". Vui lòng thử lại!", ex);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            Path targetLocation = this.fileStorageLocation.resolve(filePath).normalize();
            Files.deleteIfExists(targetLocation);
        } catch (IOException ex) {
            throw new FileStorageException("Không thể xóa file: " + filePath, ex);
        }
    }
}