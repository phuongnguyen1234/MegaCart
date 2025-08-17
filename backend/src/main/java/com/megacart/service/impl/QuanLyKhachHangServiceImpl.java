package com.megacart.service.impl;

import com.megacart.dto.request.CapNhatTrangThaiTaiKhoanRequest;
import com.megacart.dto.response.HienThiDanhSachKhachHangResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.enumeration.TrangThaiDonHang;
import com.megacart.enumeration.TrangThaiTaiKhoan;
import com.megacart.exception.ResourceNotFoundException;
import com.megacart.model.KhachHang;
import com.megacart.repository.DonHangRepository;
import com.megacart.repository.KhachHangRepository;
import com.megacart.repository.TaiKhoanRepository;
import com.megacart.repository.specification.TimKiemKhachHangSpecification;
import com.megacart.service.QuanLyKhachHangService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuanLyKhachHangServiceImpl implements QuanLyKhachHangService {

    private final KhachHangRepository khachHangRepository;
    private final TaiKhoanRepository taiKhoanRepository; // Sẽ cần để cập nhật trạng thái
    private final DonHangRepository donHangRepository;
    private final TimKiemKhachHangSpecification timKiemKhachHangSpecification;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<HienThiDanhSachKhachHangResponse> getDSKhachHang(String searchField, String searchValue, boolean hienThiTaiKhoanBiKhoa, Pageable pageable) {
        // 1. Tạo Specification để xây dựng câu lệnh WHERE động
        Specification<KhachHang> spec = timKiemKhachHangSpecification.filterBy(searchField, searchValue, hienThiTaiKhoanBiKhoa);

        // 2. Thực thi truy vấn với các điều kiện lọc và phân trang
        Page<KhachHang> khachHangPage = khachHangRepository.findAll(spec, pageable);

        // 3. Chuyển đổi từ Page<Entity> sang PagedResponse<DTO>
        List<HienThiDanhSachKhachHangResponse> responses = khachHangPage.getContent().stream()
                .map(this::mapToAdminKhachHangResponse)
                .collect(Collectors.toList());

        return new PagedResponse<>(responses, khachHangPage.getNumber(), khachHangPage.getSize(), khachHangPage.getTotalElements(), khachHangPage.getTotalPages(), null);
    }

    @Override
    @Transactional(readOnly = true)
    public HienThiDanhSachKhachHangResponse getChiTietKhachHang(Integer maKhachHang) {
        KhachHang khachHang = khachHangRepository.findByIdWithTaiKhoan(maKhachHang)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng với mã: " + maKhachHang));
        return mapToAdminKhachHangResponse(khachHang);
    }

    @Override
    @Transactional
    public HienThiDanhSachKhachHangResponse capNhatTrangThai(Integer maKhachHang, CapNhatTrangThaiTaiKhoanRequest request) {
        // Chỉ kiểm tra khi hành động là "KHÓA" tài khoản
        if (request.getTrangThai() == TrangThaiTaiKhoan.KHOA) {
            List<TrangThaiDonHang> activeStatuses = List.of(TrangThaiDonHang.CHO_XAC_NHAN, TrangThaiDonHang.CHO_XU_LY, TrangThaiDonHang.DANG_GIAO);
            if (donHangRepository.existsByKhachHang_MaKhachHangAndTrangThaiIn(maKhachHang, activeStatuses)) {
                throw new IllegalArgumentException("Không thể khóa tài khoản của khách hàng đang có đơn hàng chưa hoàn thành.");
            }
        }

        // Lấy khách hàng và tài khoản liên kết để cập nhật
        KhachHang khachHang = khachHangRepository.findByIdWithTaiKhoan(maKhachHang)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng với mã: " + maKhachHang));

        // Cập nhật trạng thái
        khachHang.getTaiKhoan().setTrangThaiTaiKhoan(request.getTrangThai());

        HienThiDanhSachKhachHangResponse response = mapToAdminKhachHangResponse(khachHang);
        response.setThongBao("Cập nhật trạng thái tài khoản thành công.");
        return response;
    }

    private HienThiDanhSachKhachHangResponse mapToAdminKhachHangResponse(KhachHang khachHang) {
        // Giả định rằng khachHang.getTaiKhoan() không bao giờ null ở đây vì query đã JOIN
        return HienThiDanhSachKhachHangResponse.builder().maKhachHang(khachHang.getMaKhachHang())
                .tenKhachHang(khachHang.getTenKhachHang())
                .email(khachHang.getTaiKhoan().getEmail())
                .diaChi(khachHang.getDiaChi())
                .soDienThoai(khachHang.getTaiKhoan().getSoDienThoai())
                .trangThaiTaiKhoan(khachHang.getTaiKhoan().getTrangThaiTaiKhoan())
                .build();
    }
}