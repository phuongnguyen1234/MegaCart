package com.megacart.service.impl;

import com.megacart.service.FileStorageService;
import com.megacart.exception.FileStorageException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        try {
            this.rootLocation = Paths.get(uploadDir);
            if (Files.notExists(this.rootLocation)) {
                Files.createDirectories(this.rootLocation);
                log.info("Đã tạo thư mục upload tại: {}", this.rootLocation.toAbsolutePath());
            } else {
                log.info("Sử dụng thư mục upload hiện có tại: {}", this.rootLocation.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new FileStorageException("Không thể khởi tạo thư mục lưu trữ file: " + uploadDir, e);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String subDir) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty() || originalFilename.contains("..")) {
                throw new FileStorageException("Tên file không hợp lệ: " + originalFilename);
            }

            String fileExtension = "";
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex > 0) {
                fileExtension = originalFilename.substring(dotIndex);
            }
            String newFileName = UUID.randomUUID().toString() + fileExtension;

            Path destinationDir = this.rootLocation.resolve(Paths.get(subDir)).normalize();
            Files.createDirectories(destinationDir);

            Path destinationFile = destinationDir.resolve(newFileName).normalize();

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            // Tạo đường dẫn tương đối
            String relativePath = Paths.get(subDir).resolve(newFileName).toString().replace('\\', '/');

            // Trả về đường dẫn URL đầy đủ, bắt đầu bằng / để frontend dễ dàng sử dụng
            return "/uploads/" + relativePath;
        } catch (IOException e) {
            throw new FileStorageException("Lỗi khi lưu file " + originalFilename, e);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            // filePath từ CSDL sẽ có dạng /uploads/sanpham/123/abc.jpg
            // Chúng ta cần loại bỏ phần /uploads/ để có đường dẫn tương đối chính xác trên hệ thống file.
            String relativePath = filePath;
            if (relativePath != null && relativePath.startsWith("/uploads/")) {
                relativePath = relativePath.substring("/uploads/".length());
            }
            Path fileToDelete = this.rootLocation.resolve(relativePath).normalize();
            Files.deleteIfExists(fileToDelete);
        } catch (IOException e) {
            log.error("Không thể xóa file: {}", filePath, e);
        }
    }
}