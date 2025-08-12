package com.megacart.service.impl;

import com.megacart.dto.request.DatHangRequest;
import com.megacart.dto.response.DatHangResponse;
import com.megacart.enumeration.TrangThaiDonHang;
import com.megacart.enumeration.TrangThaiThanhToan;
import com.megacart.enumeration.TrangThaiXuLi;
import com.megacart.exception.ResourceNotFoundException;
import com.megacart.model.*;
import com.megacart.repository.ChiTietGioHangRepository;
import com.megacart.repository.DonHangRepository;
import com.megacart.repository.GioHangRepository;
import com.megacart.repository.KhachHangRepository;
import com.megacart.repository.KhoRepository;
import com.megacart.repository.SanPhamRepository;
import com.megacart.service.DatHangService;
import com.megacart.utils.ImageUtils;
import com.megacart.utils.ThoiGianGiaoHangUtils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DatHangServiceImpl implements DatHangService {

    private final DonHangRepository donHangRepository;
    private final KhachHangRepository khachHangRepository;
    private final SanPhamRepository sanPhamRepository;
    private final ChiTietGioHangRepository chiTietGioHangRepository;

    @Override
    @Transactional
    public DatHangResponse datHang(DatHangRequest request, TaiKhoan taiKhoan) {
        KhachHang khachHang = khachHangRepository.findById(taiKhoan.getMaTaiKhoan())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông tin khách hàng."));

        List<DatHangRequest.SanPhamDatHang> requestedItems = request.getItems();

        // 1. Lấy thông tin tất cả sản phẩm cần đặt trong 1 query
        List<Integer> maSanPhams = requestedItems.stream().map(DatHangRequest.SanPhamDatHang::getMaSanPham).toList();
        List<SanPham> sanPhamsInDB = sanPhamRepository.findAllById(maSanPhams);

        if (sanPhamsInDB.size() != maSanPhams.size()) {
            throw new ResourceNotFoundException("Một hoặc nhiều sản phẩm trong giỏ hàng không tồn tại.");
        }

        Map<Integer, SanPham> sanPhamMap = sanPhamsInDB.stream().collect(Collectors.toMap(SanPham::getMaSanPham, sp -> sp));

        // 2. Kiểm tra tồn kho cho tất cả sản phẩm
        boolean isSufficientStock = true;
        for (DatHangRequest.SanPhamDatHang item : requestedItems) {
            SanPham sanPham = sanPhamMap.get(item.getMaSanPham());
            if (sanPham.getKho() == null || sanPham.getKho().getSoLuong() < item.getSoLuong()) {
                isSufficientStock = false;
                break; // Nếu một sản phẩm hết hàng, không cần kiểm tra tiếp
            }
        }

        // 3. Xác định trạng thái đơn hàng dựa trên tình trạng tồn kho
        TrangThaiDonHang trangThaiDonHang = isSufficientStock ? TrangThaiDonHang.CHO_XU_LY : TrangThaiDonHang.CHO_XAC_NHAN;

        // 4. Tạo đối tượng DonHang
        LocalDateTime thoiGianDatHang = LocalDateTime.now();
        DonHang donHang = DonHang.builder()
                .khachHang(khachHang)
                .tenKhachHang(request.getTenNguoiNhan())
                .diaChiNhanHang(request.getDiaChiNhanHang())
                .sdtNhanHang(request.getSdtNhanHang())
                .hinhThucNhanHang(request.getHinhThucNhanHang())
                .hinhThucThanhToan(request.getHinhThucThanhToan())
                .thoiGianDatHang(thoiGianDatHang)
                .trangThai(trangThaiDonHang)
                .trangThaiXuLi(TrangThaiXuLi.KHONG)
                .trangThaiThanhToan(TrangThaiThanhToan.CHUA_THANH_TOAN) // Mặc định là chưa thanh toán
                .duKienGiaoHang(ThoiGianGiaoHangUtils.tinhThoiGianGiaoHangDuKien(thoiGianDatHang))
                .build();

        // 5. Tạo ChiTietDonHang và cập nhật số lượng tồn kho (nếu đủ hàng)
        for (DatHangRequest.SanPhamDatHang item : requestedItems) {
            SanPham sanPham = sanPhamMap.get(item.getMaSanPham());
            ChiTietDonHang chiTiet = ChiTietDonHang.builder()
                    .sanPham(sanPham)
                    .tenSanPham(sanPham.getTenSanPham())
                    .anhMinhHoa(ImageUtils.getAnhMinhHoaChinhUrl(sanPham.getAnhMinhHoas()))
                    .donGia(sanPham.getDonGia())
                    .donVi(sanPham.getDonVi())
                    .soLuong(item.getSoLuong())
                    .build();
            donHang.addChiTietDonHang(chiTiet);

            // Chỉ trừ kho nếu đơn hàng được xác nhận là đủ hàng ngay từ đầu
            if (isSufficientStock) {
                Kho kho = sanPham.getKho();
                kho.setSoLuong(kho.getSoLuong() - item.getSoLuong());
            }
        }

        // 6. Lưu đơn hàng vào CSDL
        DonHang savedDonHang = donHangRepository.save(donHang);

        // 7. Xóa các sản phẩm đã đặt khỏi giỏ hàng
        List<ChiTietGioHangId> cartItemIdsToRemove = maSanPhams.stream()
                .map(maSP -> new ChiTietGioHangId(maSP, khachHang.getMaKhachHang()))
                .toList();
        chiTietGioHangRepository.deleteAllByIdInBatch(cartItemIdsToRemove);

        // 8. Tạo và trả về response
        String thongBao = isSufficientStock
                ? "Đặt hàng thành công! Đơn hàng của bạn đang chờ xử lý."
                : "Một hoặc nhiều sản phẩm không đủ số lượng. Đơn hàng của bạn đang chờ nhân viên xác nhận.";

        return DatHangResponse.builder()
                .maDonHang(savedDonHang.getMaDonHang())
                .trangThai(savedDonHang.getTrangThai())
                .thongBao(thongBao)
                .build();
    }
}