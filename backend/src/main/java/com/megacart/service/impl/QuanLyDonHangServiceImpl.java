package com.megacart.service.impl;

import com.megacart.dto.request.CapNhatDonHangRequest;
import com.megacart.dto.request.GanGiaoHangRequest;
import com.megacart.dto.response.DonHangDangGiaoQuanLyResponse;
import com.megacart.dto.response.ChiTietGiaoHangQuanLyResponse;
import com.megacart.dto.response.ChiTietDonHangQuanLyResponse;
import com.megacart.dto.response.DonHangQuanLyResponse;
import com.megacart.dto.response.MessageResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.enumeration.TrangThaiDonHang;
import com.megacart.enumeration.TrangThaiThanhToan;
import com.megacart.enumeration.TrangThaiTaiKhoan;
import com.megacart.dto.projection.TongTienDonHangProjection;
import com.megacart.enumeration.ViTri;
import com.megacart.exception.ResourceNotFoundException;
import com.megacart.model.ChiTietDonHang;
import com.megacart.model.DonHang;
import com.megacart.model.Kho;
import com.megacart.model.NhanVien;
import com.megacart.model.SanPham;
import com.megacart.repository.NhanVienRepository;
import com.megacart.repository.DonHangRepository;
import com.megacart.repository.ChiTietDonHangRepository;
import com.megacart.repository.specification.TimKiemDonHangSpecification;
import com.megacart.service.QuanLyDonHangService;
import com.megacart.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class QuanLyDonHangServiceImpl implements QuanLyDonHangService {

    private final DonHangRepository donHangRepository;
    private final TimKiemDonHangSpecification timKiemDonHangSpecification;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final NhanVienRepository nhanVienRepository;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<DonHangQuanLyResponse> getDSDonHang(String searchField, String searchValue, TrangThaiDonHang trangThai, LocalDate ngayDat, Pageable pageable) {
        // Bước 1: Lấy trang danh sách đơn hàng.
        // Query này đã được tối ưu bằng @EntityGraph trong DonHangRepository để lấy luôn KhachHang.
        Specification<DonHang> spec;
        // Logic mới: Nếu tìm kiếm theo mã đơn hàng, bỏ qua các bộ lọc khác.
        if ("maDonHang".equals(searchField) && StringUtils.hasText(searchValue)) {
            spec = timKiemDonHangSpecification.filterBy(searchField, searchValue, null, null);
        } else {
            spec = timKiemDonHangSpecification.filterBy(searchField, searchValue, trangThai, ngayDat);
        }
        Page<DonHang> donHangPage = donHangRepository.findAll(spec, pageable);

        List<DonHang> donHangsOnPage = donHangPage.getContent();
        if (donHangsOnPage.isEmpty()) {
            return new PagedResponse<>(Collections.emptyList(), donHangPage.getNumber(), donHangPage.getSize(), donHangPage.getTotalElements(), donHangPage.getTotalPages(), null);
        }

        // Bước 2: Tối ưu - Lấy tổng tiền cho tất cả đơn hàng trên trang trong 1 query duy nhất
        List<Integer> donHangIds = donHangsOnPage.stream().map(DonHang::getMaDonHang).toList();
        Map<Integer, Long> tongTienTheoDonHang = chiTietDonHangRepository.findTongTienByDonHangIds(donHangIds)
                .stream()
                .collect(Collectors.toMap(TongTienDonHangProjection::getMaDonHang, TongTienDonHangProjection::getTongTien));

        // Bước 3: Ánh xạ sang DTO, sử dụng tổng tiền đã tính toán
        List<DonHangQuanLyResponse> responses = donHangsOnPage.stream()
                .map(donHang -> mapToDonHangQuanLyResponse(donHang, tongTienTheoDonHang.getOrDefault(donHang.getMaDonHang(), 0L)))
                .collect(Collectors.toList());

        return new PagedResponse<>(responses, donHangPage.getNumber(), donHangPage.getSize(), donHangPage.getTotalElements(), donHangPage.getTotalPages(), null);
    }

    private DonHangQuanLyResponse mapToDonHangQuanLyResponse(DonHang donHang, long tongTien) {
        return DonHangQuanLyResponse.builder()
                .maDonHang(donHang.getMaDonHang())
                // Lấy tên người nhận hàng trực tiếp từ đơn hàng để nhất quán
                .tenKhachHang(donHang.getKhachHang() != null ? donHang.getKhachHang().getTenKhachHang() : "Khách vãng lai")
                .thoiGianDatHang(donHang.getThoiGianDatHang())
                .trangThai(donHang.getTrangThai())
                .tongTien(tongTien)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ChiTietDonHangQuanLyResponse getChiTietDonHang(Integer maDonHang) {
        DonHang donHang = donHangRepository.findById(maDonHang)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với mã: " + maDonHang));

        return mapToChiTietDonHangQuanLyResponse(donHang);
    }

    private ChiTietDonHangQuanLyResponse mapToChiTietDonHangQuanLyResponse(DonHang donHang) {
        List<ChiTietDonHangQuanLyResponse.Item> items = donHang.getChiTietDonHangs().stream()
                .map(chiTiet -> {
                    SanPham sanPham = chiTiet.getSanPham();
                    String anhChinhUrl = (sanPham != null) ? ImageUtils.getAnhMinhHoaChinhUrl(sanPham.getAnhMinhHoas()) : null;

                    return ChiTietDonHangQuanLyResponse.Item.builder()
                            .maSanPham(sanPham != null ? sanPham.getMaSanPham() : null)
                            .tenSanPham(chiTiet.getTenSanPham())
                            .anhMinhHoaChinh(anhChinhUrl)
                            .donGia(chiTiet.getDonGia())
                            .donVi(chiTiet.getDonVi())
                            .soLuong(chiTiet.getSoLuong())
                            .tongTien((long) chiTiet.getDonGia() * chiTiet.getSoLuong())
                            .build();
                })
                .collect(Collectors.toList());

        long tongTienDonHang = items.stream().mapToLong(ChiTietDonHangQuanLyResponse.Item::getTongTien).sum();

        return ChiTietDonHangQuanLyResponse.builder()
                .maDonHang(donHang.getMaDonHang())
                .tongTien(tongTienDonHang)
                .tenNguoiNhan(donHang.getTenKhachHang())
                .sdtNhanHang(donHang.getSdtNhanHang())
                .diaChiNhanHang(donHang.getDiaChiNhanHang())
                .thoiGianDatHang(donHang.getThoiGianDatHang())
                .hinhThucGiaoHang(donHang.getHinhThucNhanHang())
                .hinhThucThanhToan(donHang.getHinhThucThanhToan())
                .trangThai(donHang.getTrangThai())
                .trangThaiThanhToan(donHang.getTrangThaiThanhToan())
                .thoiGianThanhToan(donHang.getThoiGianThanhToan())
                .duKienGiaoHang(donHang.getDuKienGiaoHang())
                .ghiChu(donHang.getGhiChu())
                .items(items)
                .build();
    }

    @Override
    @Transactional
    public ChiTietDonHangQuanLyResponse capNhatDonHang(Integer maDonHang, CapNhatDonHangRequest request) {
        DonHang donHang = donHangRepository.findById(maDonHang)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với mã: " + maDonHang));

        // Cập nhật trạng thái đơn hàng (có ràng buộc nghiệp vụ)
        if (request.getTrangThai() != null && request.getTrangThai() != donHang.getTrangThai()) {
            xuLyThayDoiTrangThaiDonHang(donHang, request.getTrangThai());
        }

        // Cập nhật trạng thái thanh toán
        if (request.getTrangThaiThanhToan() != null && request.getTrangThaiThanhToan() != donHang.getTrangThaiThanhToan()) {
            donHang.setTrangThaiThanhToan(request.getTrangThaiThanhToan());
            // Nếu thanh toán thành công, ghi nhận thời gian
            if (request.getTrangThaiThanhToan() == TrangThaiThanhToan.DA_THANH_TOAN) {
                donHang.setThoiGianThanhToan(LocalDateTime.now());
            }
        }

        // Cập nhật ngày giao hàng dự kiến
        // Chỉ cập nhật và validate nếu giá trị mới thực sự khác với giá trị cũ
        if (request.getDuKienGiaoHang() != null && !Objects.equals(request.getDuKienGiaoHang(), donHang.getDuKienGiaoHang())) {
            // Ràng buộc: Không cho phép đặt ngày giao hàng trong quá khứ
            if (request.getDuKienGiaoHang().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Ngày giao hàng dự kiến mới không thể là một ngày trong quá khứ.");
            }
             donHang.setDuKienGiaoHang(request.getDuKienGiaoHang());
        }

        // Cập nhật ghi chú
        if (request.getGhiChu() != null) {
            donHang.setGhiChu(request.getGhiChu());
        }

        // Lưu lại các thay đổi
        DonHang savedDonHang = donHangRepository.save(donHang);

        // Trả về response với thông tin đã cập nhật và thông báo thành công
        ChiTietDonHangQuanLyResponse response = mapToChiTietDonHangQuanLyResponse(savedDonHang);
        response.setThongBao("Cập nhật đơn hàng thành công.");
        return response;
    }

    private void xuLyThayDoiTrangThaiDonHang(DonHang donHang, TrangThaiDonHang trangThaiMoi) {
        TrangThaiDonHang trangThaiHienTai = donHang.getTrangThai();

        // Ràng buộc nghiệp vụ: Không thể thay đổi trạng thái của đơn hàng đã hoàn thành hoặc đã hủy.
        if (trangThaiHienTai == TrangThaiDonHang.DA_GIAO || trangThaiHienTai == TrangThaiDonHang.DA_HUY) {
            throw new IllegalArgumentException("Không thể thay đổi trạng thái của đơn hàng đã hoàn thành hoặc đã hủy.");
        }

        // Kiểm tra tính hợp lệ của việc chuyển đổi trạng thái
        boolean chuyenDoiHopLe = switch (trangThaiHienTai) {
            case CHO_XAC_NHAN -> trangThaiMoi == TrangThaiDonHang.CHO_XU_LY || trangThaiMoi == TrangThaiDonHang.DANG_GIAO || trangThaiMoi == TrangThaiDonHang.DA_HUY;
            case CHO_XU_LY -> trangThaiMoi == TrangThaiDonHang.DANG_GIAO || trangThaiMoi == TrangThaiDonHang.DA_HUY;
            case DANG_GIAO -> trangThaiMoi == TrangThaiDonHang.DA_GIAO || trangThaiMoi == TrangThaiDonHang.DA_HUY;
            default -> false;
        };

        if (!chuyenDoiHopLe) {
            throw new IllegalArgumentException("Không thể chuyển đơn hàng từ trạng thái '" + trangThaiHienTai + "' sang '" + trangThaiMoi + "'.");
        }

        // Xử lý trừ kho khi đơn hàng được xác nhận và chuyển đi giao
        if (trangThaiMoi == TrangThaiDonHang.DANG_GIAO && (trangThaiHienTai == TrangThaiDonHang.CHO_XAC_NHAN || trangThaiHienTai == TrangThaiDonHang.CHO_XU_LY)) {
            for (ChiTietDonHang chiTiet : donHang.getChiTietDonHangs()) {
                SanPham sanPham = chiTiet.getSanPham();
                if (sanPham != null && sanPham.getKho() != null) {
                    Kho kho = sanPham.getKho();
                    if (kho.getSoLuong() < chiTiet.getSoLuong()) {
                        throw new IllegalStateException("Không đủ hàng trong kho cho sản phẩm: " + sanPham.getTenSanPham());
                    }
                    kho.setSoLuong(kho.getSoLuong() - chiTiet.getSoLuong());
                }
            }
        }

        // Xử lý hoàn trả hàng vào kho khi đơn hàng bị hủy
        if (trangThaiMoi == TrangThaiDonHang.DA_HUY) {
            // Chỉ hoàn kho cho các đơn hàng đã trừ kho (trạng thái đang xử lý hoặc đang giao)
            if (trangThaiHienTai == TrangThaiDonHang.CHO_XU_LY || trangThaiHienTai == TrangThaiDonHang.DANG_GIAO) {
                for (ChiTietDonHang chiTiet : donHang.getChiTietDonHangs()) {
                    // Kiểm tra sản phẩm và kho có tồn tại không để tránh NullPointerException
                    if (chiTiet.getSanPham() != null && chiTiet.getSanPham().getKho() != null) {
                        Kho kho = chiTiet.getSanPham().getKho();
                        kho.setSoLuong(kho.getSoLuong() + chiTiet.getSoLuong());
                    }
                }
            }
        }

        // Ghi nhận thời gian giao hàng khi chuyển sang trạng thái "Đã giao"
        if (trangThaiMoi == TrangThaiDonHang.DA_GIAO) {
            // Nếu chưa thanh toán, tự động chuyển sang đã thanh toán khi giao hàng thành công (COD)
            if (donHang.getTrangThaiThanhToan() == TrangThaiThanhToan.CHUA_THANH_TOAN) {
                donHang.setTrangThaiThanhToan(TrangThaiThanhToan.DA_THANH_TOAN);
                donHang.setThoiGianThanhToan(LocalDateTime.now());
            }
        }

        donHang.setTrangThai(trangThaiMoi);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<DonHangDangGiaoQuanLyResponse> getDSDonHangDangGiao(String searchField, String searchValue, Pageable pageable) {
        Specification<DonHang> spec = (root, query, cb) -> {
            // Để tránh N+1, fetch sẵn các entity liên quan
            if (Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("nhanVienGiaoHang", jakarta.persistence.criteria.JoinType.LEFT);
            }

            // Điều kiện CỐ ĐỊNH: chỉ lấy đơn hàng đang giao
            jakarta.persistence.criteria.Predicate dangGiaoPredicate = cb.equal(root.get("trangThai"), TrangThaiDonHang.DANG_GIAO);

            // Logic mới: Nếu tìm kiếm theo mã, chỉ cần kết hợp với điều kiện đang giao
            if ("maDonHang".equals(searchField) && StringUtils.hasText(searchValue)) {
                if (searchValue.matches("\\d+")) {
                    jakarta.persistence.criteria.Predicate maDonHangPredicate = cb.equal(root.get("maDonHang"), Integer.parseInt(searchValue));
                    return cb.and(dangGiaoPredicate, maDonHangPredicate);
                } else {
                    return cb.disjunction(); // Mã không hợp lệ, trả về rỗng
                }
            }

            // Điều kiện lọc động cho các trường khác
            if (StringUtils.hasText(searchField) && StringUtils.hasText(searchValue)) {
                jakarta.persistence.criteria.Predicate searchPredicate = switch (searchField) {
                    case "tenNhanVienGiaoHang" -> cb.like(cb.lower(root.get("nhanVienGiaoHang").get("hoTen")), "%" + searchValue.toLowerCase() + "%");
                    case "tenNguoiNhan" -> cb.like(cb.lower(root.get("tenKhachHang")), "%" + searchValue.toLowerCase() + "%");
                    case "sdtNhanHang" -> cb.like(root.get("sdtNhanHang"), "%" + searchValue + "%");
                    case "diaChiNhanHang" -> cb.like(cb.lower(root.get("diaChiNhanHang")), "%" + searchValue.toLowerCase() + "%");
                    default -> cb.disjunction(); // Predicate luôn false nếu searchField không hợp lệ
                };
                return cb.and(dangGiaoPredicate, searchPredicate);
            }

            return dangGiaoPredicate;
        };

        Page<DonHang> donHangPage = donHangRepository.findAll(spec, pageable);

        List<DonHang> donHangsOnPage = donHangPage.getContent();
        if (donHangsOnPage.isEmpty()) {
            return new PagedResponse<>(Collections.emptyList(), donHangPage.getNumber(), donHangPage.getSize(), donHangPage.getTotalElements(), donHangPage.getTotalPages(), null);
        }

        // Tái sử dụng logic tính tổng tiền hiệu quả
        List<Integer> donHangIds = donHangsOnPage.stream().map(DonHang::getMaDonHang).toList();
        Map<Integer, Long> tongTienTheoDonHang = chiTietDonHangRepository.findTongTienByDonHangIds(donHangIds)
                .stream()
                .collect(Collectors.toMap(TongTienDonHangProjection::getMaDonHang, TongTienDonHangProjection::getTongTien));

        // Ánh xạ sang DTO mới
        List<DonHangDangGiaoQuanLyResponse> responses = donHangsOnPage.stream()
                .map(donHang -> DonHangDangGiaoQuanLyResponse.builder()
                        .maDonHang(donHang.getMaDonHang())
                        .tenNhanVienGiaoHang(donHang.getNhanVienGiaoHang() != null ? donHang.getNhanVienGiaoHang().getHoTen() : "Chưa gán")
                        .tenNguoiNhan(donHang.getTenKhachHang())
                        .sdtNhanHang(donHang.getSdtNhanHang())
                        .diaChiNhanHang(donHang.getDiaChiNhanHang())
                        .tongTien(tongTienTheoDonHang.getOrDefault(donHang.getMaDonHang(), 0L))
                        .trangThaiThanhToan(donHang.getTrangThaiThanhToan())
                        .build())
                .collect(Collectors.toList());

        return new PagedResponse<>(responses, donHangPage.getNumber(), donHangPage.getSize(), donHangPage.getTotalElements(), donHangPage.getTotalPages(), null);
    }

    @Override
    @Transactional(readOnly = true)
    public ChiTietGiaoHangQuanLyResponse getChiTietGiaoHangQuanLy(Integer maDonHang) {
        DonHang donHang = donHangRepository.findById(maDonHang)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với mã: " + maDonHang));

        NhanVien nvGiaoHang = donHang.getNhanVienGiaoHang();

        return ChiTietGiaoHangQuanLyResponse.builder()
                .maDonHang(donHang.getMaDonHang())
                .tenNguoiNhan(donHang.getTenKhachHang())
                .diaChiNhanHang(donHang.getDiaChiNhanHang())
                .maNhanVienGiaoHang(nvGiaoHang != null ? nvGiaoHang.getMaNhanVien() : null)
                .tenNhanVienGiaoHang(nvGiaoHang != null ? nvGiaoHang.getHoTen() : null)
                .build();
    }

    @Override
    @Transactional
    public MessageResponse ganNhanVienGiaoHang(Integer maDonHang, GanGiaoHangRequest request) {
        // 1. Tìm đơn hàng
        DonHang donHang = donHangRepository.findById(maDonHang)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với mã: " + maDonHang));

        // 2. Ràng buộc trạng thái đơn hàng: Chỉ cho phép gán/đổi người giao khi đơn hàng đang chờ xử lý hoặc đang giao
        if (donHang.getTrangThai() != TrangThaiDonHang.CHO_XU_LY && donHang.getTrangThai() != TrangThaiDonHang.DANG_GIAO) {
            throw new IllegalArgumentException("Chỉ có thể gán nhân viên cho đơn hàng đang ở trạng thái 'Chờ xử lý' hoặc 'Đang giao'.");
        }

        // 3. Tìm nhân viên
        NhanVien nhanVienMoi = nhanVienRepository.findById(request.getMaNhanVienGiaoHang())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên với mã: " + request.getMaNhanVienGiaoHang()));

        // 4. Kiểm tra nghiệp vụ của nhân viên
        if (nhanVienMoi.getViTri() != ViTri.NHAN_VIEN_GIAO_HANG) {
            throw new IllegalArgumentException("Nhân viên được chọn không phải là nhân viên giao hàng.");
        }
        if (nhanVienMoi.getTaiKhoan().getTrangThaiTaiKhoan() != TrangThaiTaiKhoan.HOAT_DONG) {
            throw new IllegalArgumentException("Không thể gán đơn hàng cho nhân viên có tài khoản đang bị khóa.");
        }

        // 5. Gán nhân viên và cập nhật đơn hàng
        donHang.setNhanVienGiaoHang(nhanVienMoi);

        // Nếu đơn hàng đang ở trạng thái "Chờ xử lý", tự động chuyển sang "Đang giao" khi gán shipper
        if (donHang.getTrangThai() == TrangThaiDonHang.CHO_XU_LY) {
            donHang.setTrangThai(TrangThaiDonHang.DANG_GIAO);
        }

        return new MessageResponse("Gán nhân viên giao hàng thành công.");
    }
}
