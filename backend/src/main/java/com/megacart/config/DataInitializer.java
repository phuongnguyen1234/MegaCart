package com.megacart.config;

import com.megacart.enumeration.QuyenTruyCap;
import com.megacart.enumeration.TrangThaiTaiKhoan;
import com.megacart.model.GioHang;
import com.megacart.model.KhachHang;
import com.megacart.model.TaiKhoan;
import com.megacart.repository.KhachHangRepository;
import com.megacart.repository.TaiKhoanRepository;
import lombok.RequiredArgsConstructor;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final TaiKhoanRepository taiKhoanRepository;
    private final KhachHangRepository khachHangRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. Tạo tài khoản Admin nếu chưa tồn tại
        createAdminUserIfNotExists();

        // 2. Tạo tài khoản khách hàng mẫu nếu chưa tồn tại
        createSampleCustomerIfNotExists();
    }

    private void createAdminUserIfNotExists() {
        // Kiểm tra xem admin đã tồn tại chưa
        if (taiKhoanRepository.findByEmail("admin@megacart.com").isEmpty()) {
            log.info("Tạo tài khoản ADMIN với MaTaiKhoan = 0...");

            // Phải dùng native query để bypass cơ chế IDENTITY của Hibernate
            // và để kiểm soát sql_mode của session.

            // Bước 1: Tạm thời cho phép chèn giá trị 0 vào cột AUTO_INCREMENT
            entityManager.createNativeQuery("SET @@SESSION.sql_mode = 'STRICT_TRANS_TABLES,NO_ENGINE_SUBSTITUTION';")
                    .executeUpdate();

            // Bước 2: Chèn tài khoản admin với MaTaiKhoan = 0
            String encodedPassword = passwordEncoder.encode("admin123");
            String insertQuery = String.format(
                "INSERT INTO TaiKhoan (MaTaiKhoan, Email, MatKhau, QuyenTruyCap, TrangThaiTaiKhoan) " +
                "VALUES (0, 'admin@megacart.com', '%s', 'ADMIN', 'HOAT_DONG')",
                encodedPassword
            );
            entityManager.createNativeQuery(insertQuery).executeUpdate();
            log.info("Đã tạo tài khoản ADMIN với MaTaiKhoan = 0 thành công.");
        }
    }

    private void createSampleCustomerIfNotExists() {
        if (taiKhoanRepository.findByEmail("testuser@example.com").isEmpty()) {
            log.info("Tạo tài khoản khách hàng mẫu: testuser@example.com");
            var customerAccount = TaiKhoan.builder()
                .email("testuser@example.com")
                .matKhau(passwordEncoder.encode("password123"))
                .quyenTruyCap(QuyenTruyCap.KHACH_HANG)
                .trangThaiTaiKhoan(TrangThaiTaiKhoan.HOAT_DONG)
                .build();
            var savedCustomerAccount = taiKhoanRepository.save(customerAccount);

            var khachHang = KhachHang.builder()
                .taiKhoan(savedCustomerAccount)
                .tenKhachHang("Test User")
                .build();

            khachHang.setGioHang(new GioHang());
            khachHang.getGioHang().setKhachHang(khachHang);
            khachHangRepository.save(khachHang);
        }
    }
}
