package com.megacart.service.impl;

import com.megacart.dto.request.CapNhatThongTinCoBanRequest;
import com.megacart.dto.payload.CapNhatEmailPayload;
import com.megacart.dto.request.KhoiTaoDoiEmailRequest;
import com.megacart.dto.response.ThongTinKhachHangResponse;
import com.megacart.enumeration.LoaiXacThuc;
import com.megacart.exception.InvalidOtpException;
import com.megacart.exception.UserAlreadyExistsException;
import com.megacart.exception.PhoneNumberAlreadyInUseException;
import com.megacart.exception.ResourceNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.megacart.model.KhachHang;
import com.megacart.model.MaXacThuc;
import com.megacart.model.TaiKhoan;
import com.megacart.repository.KhachHangRepository;
import com.megacart.repository.MaXacThucRepository;
import com.megacart.repository.TaiKhoanRepository;
import com.megacart.service.EmailService;
import com.megacart.service.KhachHangService;
import com.megacart.utils.OtpUtils;
import com.megacart.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class KhachHangServiceImpl implements KhachHangService {

    private final KhachHangRepository khachHangRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final MaXacThucRepository maXacThucRepository;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @Override
    public ThongTinKhachHangResponse getThongTinKhachHang(Integer maKhachHang) {
        // Sử dụng phương thức đã được tối ưu với JOIN FETCH để lấy dữ liệu trong 1 query
        KhachHang khachHang = khachHangRepository.findByIdWithTaiKhoan(maKhachHang)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng với ID: " + maKhachHang));

        return mapToDto(khachHang);
    }

    @Override
    @Transactional
    public ThongTinKhachHangResponse capNhatThongTinCoBan(Integer maKhachHang, CapNhatThongTinCoBanRequest request) {
        // Tương tự, dùng phương thức tối ưu để giảm query
        KhachHang khachHang = khachHangRepository.findByIdWithTaiKhoan(maKhachHang)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng với ID: " + maKhachHang));

        TaiKhoan taiKhoan = khachHang.getTaiKhoan();

        // Tách logic kiểm tra và cập nhật SĐT ra phương thức riêng để code gọn gàng hơn
        validateAndSetSoDienThoai(request.getSoDienThoai(), taiKhoan);

        // Cập nhật thông tin
        khachHang.setTenKhachHang(request.getTenKhachHang());
        khachHang.setDiaChi(request.getDiaChi());

        // Lưu lại thay đổi
        return mapToDto(khachHangRepository.save(khachHang));
    }

    @Override
    @Transactional
    public void khoiTaoDoiEmail(Integer maKhachHang, KhoiTaoDoiEmailRequest request) {
        KhachHang khachHang = khachHangRepository.findByIdWithTaiKhoan(maKhachHang)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng với ID: " + maKhachHang));

        TaiKhoan taiKhoan = khachHang.getTaiKhoan();
        // Chuẩn hóa email về chữ thường để đảm bảo tính duy nhất và nhất quán
        String newEmail = request.getNewEmail().toLowerCase();

        // 1. Kiểm tra xem email mới có hợp lệ và đã tồn tại chưa
        validateNewEmail(newEmail, taiKhoan);

        // 2. Tạo mã OTP
        String otp = OtpUtils.generateOtp();
        MaXacThuc maXacThuc = maXacThucRepository.findByTaiKhoan_MaTaiKhoanAndLoai(maKhachHang, LoaiXacThuc.CAP_NHAT_EMAIL)
                .orElseGet(() -> {
                    MaXacThuc newOtp = new MaXacThuc();
                    newOtp.setTaiKhoan(taiKhoan);
                    newOtp.setLoai(LoaiXacThuc.CAP_NHAT_EMAIL);
                    return newOtp;
                });

        // 3. Lưu thông tin yêu cầu (chỉ cần lưu email mới)
        maXacThuc.setMaOTP(otp);
        maXacThuc.setThoiGianHetHan(Instant.now().plus(5, ChronoUnit.MINUTES));
        CapNhatEmailPayload payload = new CapNhatEmailPayload(newEmail);
        try {
            // Lưu email mới vào payload để sử dụng ở bước xác nhận
            maXacThuc.setDuLieuCho(objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi khi chuyển đổi dữ liệu yêu cầu sang JSON", e);
        }
        maXacThucRepository.save(maXacThuc);

        // 4. Gửi OTP đến email CŨ để xác nhận
        String subject = "Xác nhận thay đổi email tài khoản MegaCart";
        String body = "Chào bạn,\n\n"
                + "Mã OTP để xác nhận yêu cầu thay đổi email của bạn là: " + otp + "\n\n"
                + "Mã này sẽ hết hạn sau 5 phút.\n\n"
                + "Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email.\n\n"
                + "Trân trọng,\n"
                + "Đội ngũ MegaCart";
        emailService.sendEmail(taiKhoan.getEmail(), subject, body);
    }

    @Override
    @Transactional
    public ThongTinKhachHangResponse xacNhanDoiEmail(Integer maKhachHang, String otp) {
        // 1. Tìm và xác thực mã OTP
        MaXacThuc maXacThuc = maXacThucRepository.findByTaiKhoan_MaTaiKhoanAndLoai(maKhachHang, LoaiXacThuc.CAP_NHAT_EMAIL)
                .orElseThrow(() -> new InvalidOtpException("Yêu cầu cập nhật không tồn tại. Vui lòng thử lại."));

        if (maXacThuc.getThoiGianHetHan().isBefore(Instant.now())) {
            maXacThucRepository.delete(maXacThuc);
            throw new InvalidOtpException("Mã OTP đã hết hạn.");
        }

        if (!maXacThuc.getMaOTP().equals(otp)) {
            throw new InvalidOtpException("Mã OTP không chính xác.");
        }

        // 2. Lấy lại email mới từ payload đã lưu
        try {
            CapNhatEmailPayload payload = objectMapper.readValue(maXacThuc.getDuLieuCho(), CapNhatEmailPayload.class);
            TaiKhoan taiKhoan = maXacThuc.getTaiKhoan();

            // Lấy email đã được chuẩn hóa từ payload và cập nhật
            String normalizedNewEmail = payload.getNewEmail();
            // Kiểm tra lại lần cuối trước khi cập nhật để tránh race condition
            validateNewEmail(normalizedNewEmail, taiKhoan);
            taiKhoan.setEmail(normalizedNewEmail);

            maXacThucRepository.delete(maXacThuc);
            return mapToDto(taiKhoan.getKhachHang());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi khi đọc dữ liệu yêu cầu từ JSON", e);
        }
    }

    /**
     * Phương thức private để chuyển đổi từ Entity sang DTO.
     * @param khachHang Đối tượng KhachHang entity.
     * @return Đối tượng ThongTinKhachHangResponse DTO.
     */
    private ThongTinKhachHangResponse mapToDto(KhachHang khachHang) {
        TaiKhoan taiKhoan = khachHang.getTaiKhoan();
        if (taiKhoan == null) {
            throw new IllegalStateException("Khách hàng không có tài khoản liên kết.");
        }
        return ThongTinKhachHangResponse.builder()
                .tenKhachHang(khachHang.getTenKhachHang())
                .email(taiKhoan.getEmail())
                .soDienThoai(taiKhoan.getSoDienThoai())
                .diaChi(khachHang.getDiaChi())
                .build();
    }

    /**
     * Kiểm tra tính hợp lệ của số điện thoại mới và cập nhật nếu hợp lệ.
     * @param newSoDienThoai Số điện thoại mới từ request.
     * @param taiKhoan Đối tượng tài khoản cần cập nhật.
     */
    private void validateAndSetSoDienThoai(String newSoDienThoai, TaiKhoan taiKhoan) {
        // Sử dụng phương thức tiện ích để kiểm tra
        if (StringUtils.isNotBlankAndChanged(newSoDienThoai, taiKhoan.getSoDienThoai())) {
            // Kiểm tra xem SĐT mới có bị trùng với người khác không
            taiKhoanRepository.findBySoDienThoai(newSoDienThoai).ifPresent(existingAccount -> {
                // Nếu tìm thấy tài khoản có SĐT này, và đó không phải là tài khoản hiện tại
                if (!existingAccount.getMaTaiKhoan().equals(taiKhoan.getMaTaiKhoan())) {
                    throw new PhoneNumberAlreadyInUseException("Số điện thoại " + newSoDienThoai + " đã được sử dụng.");
                }
            });
            taiKhoan.setSoDienThoai(newSoDienThoai);
        }
    }

    /**
     * Kiểm tra tính hợp lệ của email mới và cập nhật nếu hợp lệ.
     * @param newEmail Email mới từ request.
     * @param taiKhoan Đối tượng tài khoản cần cập nhật.
     */
    private void validateNewEmail(String newEmail, TaiKhoan taiKhoan) {
        // Sử dụng phương thức tiện ích để kiểm tra
        if (StringUtils.isNotBlankAndChanged(newEmail, taiKhoan.getEmail())) {
            // Kiểm tra xem email mới đã được tài khoản khác sử dụng chưa
            taiKhoanRepository.findByEmail(newEmail).ifPresent(existingAccount -> {
                throw new UserAlreadyExistsException("Email " + newEmail + " đã được sử dụng bởi một tài khoản khác.");
            });
        }
    }
}