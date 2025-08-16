package com.megacart.service.impl;

import com.megacart.dto.request.DatHangRequest;
import com.megacart.dto.response.DatHangResponse;
import com.megacart.enumeration.TrangThaiSanPham;
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
    public DatHangResponse taoDonHang(DatHangRequest request, TaiKhoan taiKhoan) {
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

        // 2. Kiểm tra tồn kho và đếm số lượng sản phẩm hết hàng
        int outOfStockCount = 0;
        for (DatHangRequest.SanPhamDatHang item : requestedItems) {
            SanPham sanPham = sanPhamMap.get(item.getMaSanPham());
            // Coi sản phẩm không còn bán hoặc hết hàng là một trường hợp không đủ hàng
            if (sanPham.getTrangThai() != TrangThaiSanPham.BAN || sanPham.getKho() == null || sanPham.getKho().getSoLuong() < item.getSoLuong()) {
                outOfStockCount++;
            }
        }

        // 3. Xác định trạng thái đơn hàng và thông báo dựa trên tình trạng tồn kho
        TrangThaiDonHang trangThaiDonHang;
        String thongBao;
        String ghiChuHeThong = null;
        boolean isSufficientStock = (outOfStockCount == 0);
        boolean isCompletelyOutOfStock = (outOfStockCount == requestedItems.size());

        if (isCompletelyOutOfStock) {
            // Trường hợp tất cả sản phẩm đều hết hàng -> Tự động hủy
            trangThaiDonHang = TrangThaiDonHang.DA_HUY;
            thongBao = "Đặt hàng không thành công. Tất cả sản phẩm trong đơn đã hết hàng.";
            ghiChuHeThong = "Hệ thống tự động hủy do tất cả sản phẩm đã hết hàng tại thời điểm đặt.";
        } else if (!isSufficientStock) {
            // Trường hợp một vài sản phẩm hết hàng -> Chờ xác nhận
            trangThaiDonHang = TrangThaiDonHang.CHO_XAC_NHAN;
            thongBao = "Một hoặc nhiều sản phẩm không đủ số lượng. Đơn hàng của bạn đang chờ nhân viên xác nhận.";
        } else {
            // Trường hợp tất cả sản phẩm đều đủ hàng -> Đang giao
            trangThaiDonHang = TrangThaiDonHang.DANG_GIAO;
            thongBao = "Đặt hàng thành công! Đơn hàng của bạn đang được giao.";
        }

        // 4. Tạo đối tượng DonHang
        LocalDateTime thoiGianDatHang = LocalDateTime.now();
        DonHang.DonHangBuilder donHangBuilder = DonHang.builder()
                .khachHang(khachHang)
                .tenKhachHang(request.getTenNguoiNhan())
                .diaChiNhanHang(request.getDiaChiNhanHang())
                .sdtNhanHang(request.getSdtNhanHang())
                .hinhThucNhanHang(request.getHinhThucNhanHang())
                .hinhThucThanhToan(request.getHinhThucThanhToan())
                .thoiGianDatHang(thoiGianDatHang)
                .trangThai(trangThaiDonHang)
                .ghiChu(ghiChuHeThong) // Thêm ghi chú nếu có
                .trangThaiXuLi(TrangThaiXuLi.CHO_XU_LY)
                .trangThaiThanhToan(TrangThaiThanhToan.CHUA_THANH_TOAN); // Mặc định là chưa thanh toán

        // Chỉ tính ngày giao hàng dự kiến khi đơn hàng được xác nhận đủ hàng ngay lập tức
        if (isSufficientStock) {
            donHangBuilder.duKienGiaoHang(ThoiGianGiaoHangUtils.tinhThoiGianGiaoHangDuKien(thoiGianDatHang));
        }
        DonHang donHang = donHangBuilder.build();
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

        // 7. Xóa các sản phẩm đã đặt khỏi giỏ hàng (chỉ khi đơn hàng không bị hủy ngay lập tức)
        if (!isCompletelyOutOfStock) {
            List<ChiTietGioHangId> cartItemIdsToRemove = maSanPhams.stream()
                    .map(maSP -> new ChiTietGioHangId(maSP, khachHang.getMaKhachHang()))
                    .toList();
            chiTietGioHangRepository.deleteAllByIdInBatch(cartItemIdsToRemove);
        }

        // 8. Tạo và trả về response
        return DatHangResponse.builder()
                .maDonHang(savedDonHang.getMaDonHang())
                .trangThai(savedDonHang.getTrangThai())
                .thongBao(thongBao)
                .build();
    }
}