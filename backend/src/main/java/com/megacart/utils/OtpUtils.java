package com.megacart.utils;

import java.security.SecureRandom;

public final class OtpUtils {

    // Sử dụng SecureRandom cho các mục đích bảo mật để tạo ra số ngẫu nhiên
    // khó dự đoán hơn so với java.util.Random.
    private static final SecureRandom random = new SecureRandom();

    private OtpUtils() {
        // Lớp tiện ích không nên được khởi tạo
    }

    /**
     * Tạo một mã OTP ngẫu nhiên gồm 6 chữ số.
     * @return một chuỗi String chứa mã OTP.
     */
    public static String generateOtp() {
        return String.format("%06d", random.nextInt(1000000));
    }
}