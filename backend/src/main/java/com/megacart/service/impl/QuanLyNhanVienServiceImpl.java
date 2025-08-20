package com.megacart.service.impl;

import com.megacart.dto.request.CapNhatTrangThaiTaiKhoanRequest;
import com.megacart.dto.request.CapNhatNhanVienRequest;
import com.megacart.dto.request.ThemNhanVienRequest;
import com.megacart.dto.response.HienThiDanhSachNhanVienResponse;
import com.megacart.dto.response.NhanVienOptionResponse;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    public PagedResponse<HienThiDanhSachNhanVienResponse> getDSNhanVien(String searchField, String searchValue, ViTri viTri, TrangThaiTaiKhoan trangThai, Pageable pageable) {
        Specification<NhanVien> spec;

        // Logic mới: Nếu tìm kiếm theo mã nhân viên, bỏ qua các bộ lọc khác để đảm bảo tìm thấy đúng nhân viên đó.
        if ("maNhanVien".equals(searchField) && StringUtils.hasText(searchValue)) {
            spec = timKiemNhanVienSpecification.filterBy(searchField, searchValue, null, null);
        } else {
            spec = timKiemNhanVienSpecification.filterBy(searchField, searchValue, viTri, trangThai);
        }

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
        HienThiDanhSachNhanVienResponse response = mapToNhanVienResponse(savedNhanVien);
        response.setThongBao("Thêm nhân viên thành công.");
        return response;
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

        updateHoTen(nhanVien, request.getHoTen());
        updateViTri(nhanVien, request.getViTri());
        updateEmail(nhanVien.getTaiKhoan(), request.getEmail());
        updateSoDienThoai(nhanVien.getTaiKhoan(), request.getSoDienThoai());
        updateTrangThai(maNhanVien, nhanVien.getTaiKhoan(), request.getTrangThai());

        // Gọi save() để đảm bảo các thay đổi được đồng bộ và trả về entity đã được cập nhật.
        NhanVien savedNhanVien = nhanVienRepository.save(nhanVien);
        HienThiDanhSachNhanVienResponse response = mapToNhanVienResponse(savedNhanVien);
        response.setThongBao("Cập nhật thông tin nhân viên thành công.");
        return response;
    }

    private void updateHoTen(NhanVien nhanVien, String newHoTen) {
        if (StringUtils.hasText(newHoTen) && !Objects.equals(newHoTen, nhanVien.getHoTen())) {
            nhanVien.setHoTen(newHoTen);
        }
    }

    private void updateViTri(NhanVien nhanVien, ViTri newViTri) {
        if (newViTri != null && !Objects.equals(newViTri, nhanVien.getViTri())) {
            nhanVien.setViTri(newViTri);
        }
    }

    private void updateEmail(TaiKhoan taiKhoan, String newEmail) {
        if (StringUtils.hasText(newEmail) && !Objects.equals(newEmail.toLowerCase(), taiKhoan.getEmail().toLowerCase())) {
            String normalizedNewEmail = newEmail.toLowerCase();
            taiKhoanRepository.findByEmail(normalizedNewEmail).ifPresent(existingAccount -> {
                if (!existingAccount.getMaTaiKhoan().equals(taiKhoan.getMaTaiKhoan())) {
                    throw new UserAlreadyExistsException("Email " + normalizedNewEmail + " đã được sử dụng.");
                }
            });
            taiKhoan.setEmail(normalizedNewEmail);
        }
    }

    private void updateSoDienThoai(TaiKhoan taiKhoan, String newSoDienThoai) {
        if (StringUtils.hasText(newSoDienThoai) && !Objects.equals(newSoDienThoai, taiKhoan.getSoDienThoai())) {
            taiKhoanRepository.findBySoDienThoai(newSoDienThoai).ifPresent(existingAccount -> {
                if (!existingAccount.getMaTaiKhoan().equals(taiKhoan.getMaTaiKhoan())) {
                    throw new UserAlreadyExistsException("Số điện thoại " + newSoDienThoai + " đã được sử dụng.");
                }
            });
            taiKhoan.setSoDienThoai(newSoDienThoai);
        }
    }

    private void updateTrangThai(Integer maNhanVienBiCapNhat, TaiKhoan taiKhoan, TrangThaiTaiKhoan newTrangThai) {
        if (newTrangThai != null && !Objects.equals(newTrangThai, taiKhoan.getTrangThaiTaiKhoan())) {
            // Kiểm tra để ngăn admin tự khóa tài khoản của chính mình
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof TaiKhoan) {
                Integer currentAdminId = ((TaiKhoan) principal).getMaTaiKhoan();
                if (currentAdminId.equals(maNhanVienBiCapNhat) && newTrangThai == TrangThaiTaiKhoan.KHOA) {
                    throw new IllegalArgumentException("Bạn không thể tự khóa tài khoản của chính mình.");
                }
            }
            taiKhoan.setTrangThaiTaiKhoan(newTrangThai);
        }
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

    @Override
    @Transactional(readOnly = true)
    public List<NhanVienOptionResponse> getDSNhanVienGiaoHang() {
        List<NhanVien> nhanViens = nhanVienRepository.findByViTriAndTaiKhoan_TrangThaiTaiKhoan(ViTri.NHAN_VIEN_GIAO_HANG, TrangThaiTaiKhoan.HOAT_DONG);
        return nhanViens.stream()
                .map(nv -> new NhanVienOptionResponse(nv.getMaNhanVien(), nv.getHoTen()))
                .collect(Collectors.toList());
    }

}