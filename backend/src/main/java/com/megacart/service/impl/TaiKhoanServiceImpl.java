package com.megacart.service.impl;

import com.megacart.service.EmailService;
import com.megacart.service.JwtService;
import com.megacart.dto.request.DangKyRequest;
import com.megacart.dto.request.DangNhapRequest;
import com.megacart.dto.response.AuthResponse;
import com.megacart.enumeration.QuyenTruyCap;
import com.megacart.enumeration.TrangThaiTaiKhoan;
import com.megacart.model.GioHang;
import com.megacart.model.KhachHang;
import com.megacart.model.MaXacThuc;
import com.megacart.model.NhanVien;
import com.megacart.model.TaiKhoan;
import com.megacart.repository.NhanVienRepository;
import com.megacart.repository.KhachHangRepository;
import com.megacart.repository.TaiKhoanRepository;
import com.megacart.repository.MaXacThucRepository;
import com.megacart.service.TaiKhoanService;
import com.megacart.service.TokenBlacklistService;
import com.megacart.enumeration.LoaiXacThuc;
import com.megacart.utils.OtpUtils;
import lombok.RequiredArgsConstructor;
import com.megacart.exception.ResourceNotFoundException;
import com.megacart.exception.InvalidOtpException;
import com.megacart.exception.UserAlreadyExistsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaiKhoanServiceImpl implements TaiKhoanService {

    private final TaiKhoanRepository taiKhoanRepository;
    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;
    private final MaXacThucRepository maXacThucRepository;
    private final TokenBlacklistService tokenBlacklistService;
    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponse dangKy(DangKyRequest request) {
        taiKhoanRepository.findByEmail(request.getEmail()).ifPresent(t -> {
            throw new UserAlreadyExistsException("Email " + request.getEmail() + " đã được đăng ký.");
        });

        var taiKhoan = TaiKhoan.builder()
                .email(request.getEmail())
                .matKhau(passwordEncoder.encode(request.getMatKhau()))
                .quyenTruyCap(QuyenTruyCap.KHACH_HANG)
                .trangThaiTaiKhoan(TrangThaiTaiKhoan.HOAT_DONG)
                .build();
        var savedTaiKhoan = taiKhoanRepository.save(taiKhoan);

        var khachHang = KhachHang.builder()
                .taiKhoan(savedTaiKhoan)
                .tenKhachHang(savedTaiKhoan.getTenKhachHangMacDinh())
                .build();

        var gioHang = new GioHang();
        khachHang.setGioHang(gioHang); // Phương thức helper sẽ tự động xử lý mối quan hệ hai chiều

        khachHangRepository.save(khachHang);

        // Chuẩn bị các thông tin (claims) để nhúng vào token
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", savedTaiKhoan.getMaTaiKhoan());
        extraClaims.put("role", savedTaiKhoan.getQuyenTruyCap().name());
        extraClaims.put("fullName", savedTaiKhoan.getTenKhachHangMacDinh()); // Tại thời điểm đăng ký, đây chính là tên của khách hàng

        var jwtToken = jwtService.generateToken(extraClaims, savedTaiKhoan);
        return AuthResponse.builder().token(jwtToken).build();
    }

    @Override
    public AuthResponse xacThucTaiKhoan(DangNhapRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getMatKhau()));
        var taiKhoan = taiKhoanRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new ResourceNotFoundException("Tài khoản không tồn tại sau khi xác thực. Lỗi hệ thống."));

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("userId", taiKhoan.getMaTaiKhoan());
        extraClaims.put("role", taiKhoan.getQuyenTruyCap().name());
        extraClaims.put("fullName", getFullNameForTaiKhoan(taiKhoan));

        var jwtToken = jwtService.generateToken(extraClaims, taiKhoan);
        return AuthResponse.builder().token(jwtToken).build();
    }

    @Override
    public void dangXuat(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Có thể ném một exception hoặc đơn giản là không làm gì cả
            // tùy theo yêu cầu nghiệp vụ.
            return;
        }
        final String jwt = authHeader.substring(7);

        tokenBlacklistService.blacklistToken(jwt);
    }

    @Override
    @Transactional
    public void quenMatKhau(String email) {
        // Để tránh lộ thông tin, không báo lỗi nếu email không tồn tại
        // mà chỉ thực hiện tiếp nếu email có thật.
        taiKhoanRepository.findByEmail(email).ifPresent(taiKhoan -> {
            // 1. Tạo mã OTP ngẫu nhiên 6 chữ số
            String otp = OtpUtils.generateOtp();

            // 2. Tìm MaXacThuc hiện có hoặc tạo một cái mới.
            MaXacThuc maXacThuc = maXacThucRepository.findByTaiKhoan_EmailAndLoai(email, LoaiXacThuc.DAT_LAI_MAT_KHAU)
                    .orElseGet(() -> {
                        MaXacThuc newOtp = new MaXacThuc();
                        // Liên kết với tài khoản, ID sẽ được tự động tạo
                        newOtp.setTaiKhoan(taiKhoan);
                        newOtp.setLoai(LoaiXacThuc.DAT_LAI_MAT_KHAU);
                        return newOtp;
                    });

            // 3. Cập nhật các giá trị mới (cho cả trường hợp tạo mới và cập nhật)
            maXacThuc.setMaOTP(otp);
            maXacThuc.setThoiGianHetHan(Instant.now().plus(5, ChronoUnit.MINUTES));
            maXacThucRepository.save(maXacThuc);

            // 4. Gửi email chứa mã OTP
            String subject = "Mã OTP đặt lại mật khẩu MegaCart";
            String body = "Chào bạn,\n\n"
                    + "Mã OTP để đặt lại mật khẩu của bạn là: " + otp + "\n\n"
                    + "Mã này sẽ hết hạn sau 5 phút.\n\n"
                    + "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.\n\n"
                    + "Trân trọng,\n"
                    + "Đội ngũ MegaCart";
            emailService.sendEmail(email, subject, body);
        });
    }

    @Override
    public void xacThucOtp(String email, String otp) {
        // Phương thức này chỉ xác thực OTP để cho phép người dùng chuyển sang màn hình
        // đặt lại mật khẩu trên frontend.
        xacThucMaXacThuc(email, otp);
    }

    @Override
    @Transactional
    public void datLaiMatKhau(String email, String otp, String newPassword) {
        // 1. Xác thực lại mã OTP để đảm bảo an toàn
        MaXacThuc maXacThuc = xacThucMaXacThuc(email, otp);

        // 2. Lấy tài khoản tương ứng
        TaiKhoan taiKhoan = maXacThuc.getTaiKhoan();
        if (taiKhoan == null) {
            // Trường hợp này rất hiếm khi xảy ra nếu CSDL có ràng buộc khóa ngoại
            // nhưng vẫn nên kiểm tra để đảm bảo an toàn.
            throw new ResourceNotFoundException("Không tìm thấy tài khoản tương ứng với email: " + email);
        }

        // 3. Cập nhật mật khẩu mới
        taiKhoan.setMatKhau(passwordEncoder.encode(newPassword));
        taiKhoanRepository.save(taiKhoan);

        // 4. Xóa mã OTP đã sử dụng để tránh bị dùng lại
        maXacThucRepository.delete(maXacThuc);
    }

    /**
     * Phương thức private để xác thực mã OTP.
     * @param email Email của người dùng.
     * @param otp Mã OTP người dùng nhập.
     * @return Đối tượng MaXacThuc nếu hợp lệ.
     * @throws InvalidOtpException nếu mã không hợp lệ, không tồn tại hoặc hết hạn.
     */
    private MaXacThuc xacThucMaXacThuc(String email, String otp) {
        MaXacThuc maXacThuc = maXacThucRepository.findByTaiKhoan_EmailAndLoai(email, LoaiXacThuc.DAT_LAI_MAT_KHAU)
                .orElseThrow(() -> new InvalidOtpException("Mã OTP không hợp lệ hoặc không tồn tại."));

        // Kiểm tra mã OTP đã hết hạn chưa
        if (maXacThuc.getThoiGianHetHan().isBefore(Instant.now())) {
            maXacThucRepository.delete(maXacThuc); // Xóa mã hết hạn
            throw new InvalidOtpException("Mã OTP đã hết hạn.");
        }

        // Nếu mã chưa hết hạn, mới kiểm tra xem có khớp không
        if (!maXacThuc.getMaOTP().equals(otp)) {
            throw new InvalidOtpException("Mã OTP không chính xác.");
        }

        return maXacThuc;
    }

    /**
     * Lấy tên đầy đủ của người dùng dựa trên loại tài khoản.
     * @param taiKhoan Đối tượng tài khoản cần lấy tên.
     * @return Tên đầy đủ của người dùng.
     */
    private String getFullNameForTaiKhoan(TaiKhoan taiKhoan) {
        switch (taiKhoan.getQuyenTruyCap()) {
            case KHACH_HANG:
                // Tìm khách hàng theo ID, nếu có thì lấy tên, nếu không thì dùng tên mặc định từ email
                return khachHangRepository.findById(taiKhoan.getMaTaiKhoan())
                        .map(KhachHang::getTenKhachHang)
                        .orElse(taiKhoan.getTenKhachHangMacDinh());
            case NHAN_VIEN:
                // Tìm nhân viên theo ID, nếu có thì lấy tên, nếu không thì báo lỗi
                return nhanVienRepository.findById(taiKhoan.getMaTaiKhoan())
                        .map(NhanVien::getHoTen)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Không tìm thấy hồ sơ nhân viên cho tài khoản: " + taiKhoan.getEmail()));
            case ADMIN:
                return "Administrator"; // Tên mặc định cho Admin
            default:
                throw new IllegalStateException("Quyền truy cập không được hỗ trợ: " + taiKhoan.getQuyenTruyCap());
        }
    }
}
