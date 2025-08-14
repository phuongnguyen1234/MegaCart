package com.megacart.service.impl;

import com.megacart.dto.request.CapNhatNhanVienRequest;
import com.megacart.dto.request.ThemNhanVienRequest;
import com.megacart.dto.response.HienThiDanhSachNhanVienResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.enumeration.QuyenTruyCap;
import com.megacart.enumeration.TrangThaiTaiKhoan;
import com.megacart.enumeration.ViTri;
import com.megacart.exception.ResourceNotFoundException;
import com.megacart.exception.UserAlreadyExistsException;
import com.megacart.model.NhanVien;

import com.megacart.model.TaiKhoan;
import com.megacart.repository.NhanVienRepository;
import com.megacart.repository.TaiKhoanRepository;
import com.megacart.repository.specification.TimKiemNhanVienSpecification;
import com.megacart.service.QuanLyNhanVienService;
import com.megacart.service.ThongBaoService;
import com.megacart.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import org.springframework.util.StringUtils;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuanLyNhanVienServiceImpl implements QuanLyNhanVienService {

    private final NhanVienRepository nhanVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final TimKiemNhanVienSpecification timKiemNhanVienSpecification;
    private final PasswordEncoder passwordEncoder;
    private final ThongBaoService thongBaoService;


    @Override
    @Transactional(readOnly = true)
    public PagedResponse<HienThiDanhSachNhanVienResponse> getDanhSachNhanVien(String searchField, String searchValue, ViTri viTri, boolean hienThiTaiKhoanBiKhoa, Pageable pageable) {
        Specification<NhanVien> spec = timKiemNhanVienSpecification.filterBy(searchField, searchValue, viTri, hienThiTaiKhoanBiKhoa);

        Page<NhanVien> nhanVienPage = nhanVienRepository.findAll(spec, pageable);
        // Chuyển đổi từ Page<Entity> sang PagedResponse<DTO>
        List<HienThiDanhSachNhanVienResponse> responses = nhanVienPage.getContent().stream()
                .map(this::mapToNhanVienResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(responses, nhanVienPage.getNumber(), nhanVienPage.getSize(), nhanVienPage.getTotalElements(), nhanVienPage.getTotalPages(), null);
    }

    @Override
    @Transactional
    public HienThiDanhSachNhanVienResponse themNhanVien(ThemNhanVienRequest request) {
        // 1. Kiểm tra email và số điện thoại đã tồn tại chưa
        taiKhoanRepository.findByEmail(request.getEmail()).ifPresent(tk -> {
            throw new UserAlreadyExistsException("Email " + request.getEmail() + " đã được sử dụng.");
        });
        taiKhoanRepository.findBySoDienThoai(request.getSoDienThoai()).ifPresent(tk -> {
            throw new UserAlreadyExistsException("Số điện thoại " + request.getSoDienThoai() + " đã được sử dụng.");
        });

        // 2. Tạo mật khẩu ngẫu nhiên
        String matKhauNgauNhien = PasswordUtils.generateRandomPassword();

        // 3. Tạo đối tượng TaiKhoan
        TaiKhoan taiKhoan = TaiKhoan.builder()
                .email(request.getEmail())
                .soDienThoai(request.getSoDienThoai())
                .matKhau(passwordEncoder.encode(matKhauNgauNhien))
                .quyenTruyCap(QuyenTruyCap.NHAN_VIEN)
                .trangThaiTaiKhoan(TrangThaiTaiKhoan.HOAT_DONG)  // Mặc định là hoạt động
                .build();
        TaiKhoan savedTaiKhoan = taiKhoanRepository.save(taiKhoan);

        // 4. Tạo đối tượng NhanVien
        NhanVien nhanVien = NhanVien.builder()
                .taiKhoan(savedTaiKhoan)
                .hoTen(request.getHoTen())
                .viTri(request.getViTri())
                .build();
        NhanVien savedNhanVien = nhanVienRepository.save(nhanVien);

        // 5. Gửi email thông báo mật khẩu
        thongBaoService.guiEmailChaoMungNhanVien(savedNhanVien, matKhauNgauNhien);

        // 6. Trả về DTO của nhân viên vừa tạo
        return mapToNhanVienResponse(savedNhanVien);
    }

    @Override
    @Transactional(readOnly = true)
    public HienThiDanhSachNhanVienResponse getChiTietNhanVien(Integer maNhanVien) {
        NhanVien nhanVien = nhanVienRepository.findByIdWithTaiKhoan(maNhanVien)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên với mã: " + maNhanVien));
        return mapToNhanVienResponse(nhanVien);
    }

    @Override
    @Transactional
    public HienThiDanhSachNhanVienResponse capNhatNhanVien(Integer maNhanVien, CapNhatNhanVienRequest request) {
        NhanVien nhanVien = nhanVienRepository.findByIdWithTaiKhoan(maNhanVien)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên với mã: " + maNhanVien));

        TaiKhoan taiKhoan = nhanVien.getTaiKhoan();

        // Cập nhật họ tên
        if (StringUtils.hasText(request.getHoTen()) && !Objects.equals(request.getHoTen(), nhanVien.getHoTen())) {
            nhanVien.setHoTen(request.getHoTen());
        }

        // Cập nhật vị trí
        if (request.getViTri() != null && !Objects.equals(request.getViTri(), nhanVien.getViTri())) {
            nhanVien.setViTri(request.getViTri());
        }

        // Cập nhật email (kiểm tra trùng lặp)
        if (StringUtils.hasText(request.getEmail()) && !Objects.equals(request.getEmail().toLowerCase(), taiKhoan.getEmail().toLowerCase())) {
            String newEmail = request.getEmail().toLowerCase();
            taiKhoanRepository.findByEmail(newEmail).ifPresent(existingAccount -> {
                if (!existingAccount.getMaTaiKhoan().equals(taiKhoan.getMaTaiKhoan())) {
                    throw new UserAlreadyExistsException("Email " + newEmail + " đã được sử dụng.");
                }
            });
            taiKhoan.setEmail(newEmail);
        }

        // Cập nhật số điện thoại (kiểm tra trùng lặp)
        if (StringUtils.hasText(request.getSoDienThoai()) && !Objects.equals(request.getSoDienThoai(), taiKhoan.getSoDienThoai())) {
            String newPhone = request.getSoDienThoai();
            taiKhoanRepository.findBySoDienThoai(newPhone).ifPresent(existingAccount -> {
                if (!existingAccount.getMaTaiKhoan().equals(taiKhoan.getMaTaiKhoan())) {
                    throw new UserAlreadyExistsException("Số điện thoại " + newPhone + " đã được sử dụng.");
                }
            });
            taiKhoan.setSoDienThoai(newPhone);
        }

        // Không cần gọi save() vì entity đang ở trạng thái managed trong transaction
        return mapToNhanVienResponse(nhanVien);
    }

    private HienThiDanhSachNhanVienResponse mapToNhanVienResponse(NhanVien nhanVien) {
        // Giả định rằng nhanVien.getTaiKhoan() không bao giờ null
        return HienThiDanhSachNhanVienResponse.builder()
                .maNhanVien(nhanVien.getMaNhanVien())
                .tenNhanVien(nhanVien.getHoTen())
                .email(nhanVien.getTaiKhoan().getEmail())
                .soDienThoai(nhanVien.getTaiKhoan().getSoDienThoai())
                .trangThaiTaiKhoan(nhanVien.getTaiKhoan().getTrangThaiTaiKhoan())
                .viTri(nhanVien.getViTri())
                .build();
    }

}