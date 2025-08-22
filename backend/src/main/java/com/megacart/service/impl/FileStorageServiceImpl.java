package com.megacart.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.megacart.service.FileStorageService;
import com.megacart.exception.FileStorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final Cloudinary cloudinary;

    @Override
    public String storeFile(MultipartFile file, String subDir) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty() || originalFilename.contains("..")) {
                throw new FileStorageException("Tên file không hợp lệ: " + originalFilename);
            }

            // Upload file lên Cloudinary và đặt trong thư mục con
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", subDir // Cloudinary sẽ tự tạo thư mục này
            ));

            // Trả về URL an toàn (https) của ảnh
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new FileStorageException("Lỗi khi lưu file " + originalFilename, e);
        }
    }

    @Override
    @Async // Đánh dấu phương thức này để chạy bất đồng bộ trên một luồng khác
    public CompletableFuture<String> storeFileAsync(MultipartFile file, String subDir) {
        // Gọi lại phương thức đồng bộ và bọc kết quả trong một CompletableFuture
        return CompletableFuture.completedFuture(storeFile(file, subDir));
    }

    @Async
    @Override
    public void deleteFileAsync(String filePath) {
        try {
            // URL từ Cloudinary có dạng: https://res.cloudinary.com/cloud-name/image/upload/v123/sanpham/33/abc.jpg
            // Chúng ta cần trích xuất "public_id" để xóa, nó là phần path không bao gồm version.
            // Ví dụ: "sanpham/33/abc"
            if (!StringUtils.hasText(filePath)) {
                return;
            }

            // Tìm vị trí của chuỗi "/upload/" để xác định điểm bắt đầu của phần version và public_id
            final String uploadMarker = "/upload/";
            int uploadMarkerIndex = filePath.indexOf(uploadMarker);
            if (uploadMarkerIndex == -1) {
                log.warn("URL không hợp lệ, không chứa '{}'. Không thể trích xuất public_id từ: {}", uploadMarker, filePath);
                return;
            }

            // Tìm vị trí của dấu gạch chéo đầu tiên sau version (ví dụ: sau /v12345/)
            int publicIdStartIndex = filePath.indexOf('/', uploadMarkerIndex + uploadMarker.length());
            if (publicIdStartIndex == -1) {
                log.warn("Định dạng URL không hợp lệ, không tìm thấy version. Không thể trích xuất public_id từ: {}", filePath);
                return;
            }

            // public_id là chuỗi từ sau version cho đến dấu chấm cuối cùng (loại bỏ đuôi file)
            String publicId = filePath.substring(publicIdStartIndex + 1, filePath.lastIndexOf('.'));

            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Đã gửi yêu cầu xóa file trên Cloudinary với public_id: {}", publicId);
        } catch (IOException e) {
            log.error("Không thể xóa file: {}", filePath, e);
        }
    }
}