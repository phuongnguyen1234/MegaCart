package com.megacart.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.megacart.dto.request.CapNhatHoSoRequest;
import com.megacart.dto.response.CapNhatHoSoResponse;
import com.megacart.dto.payload.CapNhatEmailPayload;
import com.megacart.dto.response.ThongTinKhachHangResponse;
import com.megacart.enumeration.LoaiXacThuc;
import com.megacart.exception.InvalidOtpException;
import com.megacart.exception.PhoneNumberAlreadyInUseException;
import com.megacart.dto.response.AuthResponse;
import com.megacart.exception.ResourceNotFoundException;
import com.megacart.exception.UserAlreadyExistsException;
import com.megacart.model.KhachHang;
import com.megacart.model.MaXacThuc;
import com.megacart.model.TaiKhoan;
import com.megacart.repository.KhachHangRepository;
import com.megacart.repository.MaXacThucRepository;
import com.megacart.repository.TaiKhoanRepository;
import com.megacart.service.EmailService;
import com.megacart.service.JwtService;
import com.megacart.service.KhachHangService;
import com.megacart.utils.OtpUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class KhachHangServiceImpl implements KhachHangService {

    private final KhachHangRepository khachHangRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final MaXacThucRepository maXacThucRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Override
    public ThongTinKhachHangResponse getThongTinKhachHang(Integer maKhachHang) {
        // Sử dụng phương thức đã được tối ưu với JOIN FETCH để lấy dữ liệu trong 1 query
        KhachHang khachHang = khachHangRepository.findByIdWithTaiKhoan(maKhachHang)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng với ID: " + maKhachHang));

        return mapToThongTinKhachHangResponse(khachHang);
    }

    @Override
    @Transactional
    public CapNhatHoSoResponse capNhatHoSo(Integer maKhachHang, CapNhatHoSoRequest request) {
        KhachHang khachHang = khachHangRepository.findByIdWithTaiKhoan(maKhachHang)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng với ID: " + maKhachHang));

        TaiKhoan taiKhoan = khachHang.getTaiKhoan();

        boolean daThayDoiThongTinCoBan = capNhatThongTinCoBan(khachHang, taiKhoan, request);
        boolean daKhoiTaoDoiEmail = khoiTaoDoiEmailNeuCan(taiKhoan, request);

        if (!daThayDoiThongTinCoBan && !daKhoiTaoDoiEmail) {
            return CapNhatHoSoResponse.builder()
                    .message("Không có thông tin nào được thay đổi.")
                    .emailUpdateInitiated(false)
                    .thongTinCapNhat(mapToThongTinKhachHangResponse(khachHang))
                    .build();
        }

        if (daKhoiTaoDoiEmail) {
            String message = daThayDoiThongTinCoBan
                    ? "Thông tin cơ bản đã được cập nhật. Vui lòng kiểm tra email cũ để xác nhận thay đổi email."
                    : "Yêu cầu thay đổi email đã được gửi. Vui lòng kiểm tra email cũ để xác nhận.";
            return CapNhatHoSoResponse.emailVerificationNeeded(message);
        }

        // Chỉ có thông tin cơ bản được thay đổi
        return CapNhatHoSoResponse.success(
                mapToThongTinKhachHangResponse(khachHang),
                "Cập nhật thông tin thành công."
        );
    }

    @Override
    @Transactional
    public AuthResponse xacNhanDoiEmail(Integer maKhachHang, String otp) {
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
            taiKhoanRepository.findByEmail(normalizedNewEmail).ifPresent(existingAccount -> {
                if (!existingAccount.getMaTaiKhoan().equals(taiKhoan.getMaTaiKhoan())) {
                    throw new UserAlreadyExistsException("Email " + normalizedNewEmail + " đã được sử dụng bởi một tài khoản khác.");
                }
            });
            taiKhoan.setEmail(normalizedNewEmail);

            maXacThucRepository.delete(maXacThuc);

            // 3. Tạo và trả về một token MỚI với email đã được cập nhật
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("userId", taiKhoan.getMaTaiKhoan());
            extraClaims.put("role", taiKhoan.getQuyenTruyCap().name());
            extraClaims.put("fullName", taiKhoan.getKhachHang().getTenKhachHang());

            String newJwtToken = jwtService.generateToken(extraClaims, taiKhoan);

            return AuthResponse.builder().token(newJwtToken).build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi khi đọc dữ liệu yêu cầu từ JSON", e);
        }
    }

    /**
     * Phương thức private để chuyển đổi từ Entity sang DTO.
     */
    private boolean capNhatThongTinCoBan(KhachHang khachHang, TaiKhoan taiKhoan, CapNhatHoSoRequest request) {
        boolean hasChanged = false;

        if (StringUtils.hasText(request.getTenKhachHang()) && !Objects.equals(request.getTenKhachHang(), khachHang.getTenKhachHang())) {
            khachHang.setTenKhachHang(request.getTenKhachHang());
            hasChanged = true;
        }

        if (StringUtils.hasText(request.getSoDienThoai()) && !Objects.equals(request.getSoDienThoai(), taiKhoan.getSoDienThoai())) {
            taiKhoanRepository.findBySoDienThoai(request.getSoDienThoai()).ifPresent(existingAccount -> {
                if (!existingAccount.getMaTaiKhoan().equals(taiKhoan.getMaTaiKhoan())) {
                    throw new PhoneNumberAlreadyInUseException("Số điện thoại " + request.getSoDienThoai() + " đã được sử dụng.");
                }
            });
            taiKhoan.setSoDienThoai(request.getSoDienThoai());
            hasChanged = true;
        }

        if (request.getDiaChi() != null && !Objects.equals(request.getDiaChi(), khachHang.getDiaChi())) {
            khachHang.setDiaChi(request.getDiaChi());
            hasChanged = true;
        }
        return hasChanged;
    }

    private boolean khoiTaoDoiEmailNeuCan(TaiKhoan taiKhoan, CapNhatHoSoRequest request) {
        String newEmail = request.getEmailMoi();
        if (!StringUtils.hasText(newEmail) || Objects.equals(newEmail.toLowerCase(), taiKhoan.getEmail().toLowerCase())) {
            return false;
        }

        String normalizedNewEmail = newEmail.toLowerCase();
        taiKhoanRepository.findByEmail(normalizedNewEmail).ifPresent(existingAccount -> {
            throw new UserAlreadyExistsException("Email " + normalizedNewEmail + " đã được sử dụng bởi một tài khoản khác.");
        });

        String otp = OtpUtils.generateOtp();
        MaXacThuc maXacThuc = maXacThucRepository.findByTaiKhoan_MaTaiKhoanAndLoai(taiKhoan.getMaTaiKhoan(), LoaiXacThuc.CAP_NHAT_EMAIL)
                .orElseGet(() -> {
                    MaXacThuc newOtp = new MaXacThuc();
                    newOtp.setTaiKhoan(taiKhoan);
                    newOtp.setLoai(LoaiXacThuc.CAP_NHAT_EMAIL);
                    return newOtp;
                });

        maXacThuc.setMaOTP(otp);
        maXacThuc.setThoiGianHetHan(Instant.now().plus(5, ChronoUnit.MINUTES));
        CapNhatEmailPayload payload = new CapNhatEmailPayload(normalizedNewEmail);
        try {
            maXacThuc.setDuLieuCho(objectMapper.writeValueAsString(payload));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi khi chuyển đổi dữ liệu yêu cầu sang JSON", e);
        }
        maXacThucRepository.save(maXacThuc);

        String subject = "Xác nhận thay đổi email tài khoản MegaCart";
        String body = "Chào bạn,\n\n"
                + "Mã OTP để xác nhận yêu cầu thay đổi email của bạn là: " + otp + "\n\n"
                + "Mã này sẽ hết hạn sau 5 phút.\n\n"
                + "Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email.\n\n"
                + "Trân trọng,\n"
                + "Đội ngũ MegaCart";
        emailService.sendEmail(taiKhoan.getEmail(), subject, body);

        return true;
    }

    private ThongTinKhachHangResponse mapToThongTinKhachHangResponse(KhachHang khachHang) {
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
}