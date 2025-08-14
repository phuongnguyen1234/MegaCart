package com.megacart.service;

import com.megacart.model.NhanVien;

public interface ThongBaoService {
    /**
     * Gửi email chào mừng và cung cấp mật khẩu tạm thời cho nhân viên mới.
     * @param nhanVien Nhân viên vừa được tạo.
     * @param matKhauTamThoi Mật khẩu ngẫu nhiên được tạo ra.
     */
    void guiEmailChaoMungNhanVien(NhanVien nhanVien, String matKhauTamThoi);
}