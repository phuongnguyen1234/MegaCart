package com.megacart.service.impl;

import com.megacart.model.NhanVien;
import com.megacart.service.EmailService;
import com.megacart.service.ThongBaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ThongBaoServiceImpl implements ThongBaoService {

    private final EmailService emailService;

    @Override
    public void guiEmailChaoMungNhanVien(NhanVien nhanVien, String matKhauTamThoi) {
        String subject = "Chào mừng bạn đến với MegaCart - Thông tin tài khoản nhân viên";
        String body = String.format("Chào %s,\n\n" + "Một tài khoản nhân viên đã được tạo cho bạn trên hệ thống MegaCart.\n\n"
                + "Vui lòng sử dụng thông tin dưới đây để đăng nhập:\n" + "Email: %s\n" + "Mật khẩu tạm thời: %s\n\n"
                + "Bạn nên đổi mật khẩu ngay sau lần đăng nhập đầu tiên để đảm bảo an toàn.\n\n" + "Trân trọng,\n"
                + "Admin MegaCart", nhanVien.getHoTen(), nhanVien.getTaiKhoan().getEmail(), matKhauTamThoi);
        emailService.sendEmail(nhanVien.getTaiKhoan().getEmail(), subject, body);
    }
}