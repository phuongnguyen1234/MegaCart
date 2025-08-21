package com.megacart.service;

import com.megacart.dto.request.CapNhatMucTieuDoanhThuRequest;
import com.megacart.dto.response.BieuDoDuongResponse;
import com.megacart.dto.response.BieuDoTronResponse;
import com.megacart.dto.response.MessageResponse;
import com.megacart.dto.response.ChiTietSanPhamBanChayResponse;
import com.megacart.dto.response.ChiTietDonHangThangResponse;
import com.megacart.dto.response.ChiTietDoanhThuThangResponse;
import com.megacart.dto.response.MucTieuDoanhThuResponse;
import com.megacart.dto.response.PagedResponse;
import com.megacart.dto.response.SanPhamBanChayResponse;
import com.megacart.dto.response.SanPhamTonKhoResponse;
import com.megacart.dto.response.ThongKeTongQuanResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ThongKeService {
    /**
     * Lấy các chỉ số thống kê tổng quan cho dashboard.
     * @return DTO chứa thông tin về doanh thu và đơn hàng.
     */
    ThongKeTongQuanResponse getThongKeTongQuan();

    /**
     * Lấy dữ liệu doanh thu theo ngày cho biểu đồ đường.
     * @param period Số ngày cần thống kê (ví dụ: 7, 14, 30).
     * @return Dữ liệu cho biểu đồ.
     */
    BieuDoDuongResponse getDoanhThuTheoNgay(int period);

    /**
     * Lấy dữ liệu số lượng đơn hàng theo tháng cho biểu đồ đường.
     * @return Dữ liệu cho biểu đồ.
     */
    BieuDoDuongResponse getDonHangTheoThang();

    /**
     * Lấy dữ liệu mục tiêu và doanh thu thực tế của tháng hiện tại.
     * @return Dữ liệu cho biểu đồ gauge.
     */
    MucTieuDoanhThuResponse getMucTieuDoanhThuThangNay();

    /**
     * Cập nhật mục tiêu doanh thu cho tháng hiện tại.
     * @param request DTO chứa mục tiêu mới.
     */
    MessageResponse capNhatMucTieuDoanhThuThangNay(CapNhatMucTieuDoanhThuRequest request);

    /**
     * Lấy dữ liệu tỉ lệ các trạng thái đơn hàng cho biểu đồ tròn.
     * @return Danh sách các đối tượng chứa label và value.
     */
    List<BieuDoTronResponse> getTiLeDonHang();

    /**
     * Lấy danh sách các sản phẩm bán chạy nhất.
     * @param limit Giới hạn số lượng sản phẩm trả về.
     * @return Danh sách các sản phẩm bán chạy.
     */
    List<SanPhamBanChayResponse> getSanPhamBanChay(int limit);

    /**
     * Lấy danh sách các sản phẩm có số lượng tồn kho cao nhất.
     * @param limit Giới hạn số lượng sản phẩm trả về.
     * @return Danh sách các sản phẩm tồn kho cao.
     */
    List<SanPhamTonKhoResponse> getSanPhamTonKhoCao(int limit);

    /**
     * Lấy chi tiết doanh thu theo từng tháng của một năm, có phân trang.
     * @return Dữ liệu chi tiết doanh thu theo tháng đã được phân trang.
     */
    List<ChiTietDoanhThuThangResponse> getChiTietDoanhThuThang();

    /**
     * Lấy dữ liệu doanh thu theo tháng cho biểu đồ đường.
     * @return Dữ liệu cho biểu đồ.
     */
    BieuDoDuongResponse getDoanhThuTheoThang();

    /**
     * Lấy dữ liệu số lượng đơn hàng theo ngày cho biểu đồ đường.
     * @param period Số ngày cần thống kê (ví dụ: 7, 14, 30).
     * @return Dữ liệu cho biểu đồ.
     */
    BieuDoDuongResponse getDonHangTheoNgay(int period);

    /**
     * Lấy chi tiết đơn hàng theo từng tháng của một năm, có phân trang.
     * @return Dữ liệu chi tiết đơn hàng theo tháng đã được phân trang.
     */
    List<ChiTietDonHangThangResponse> getChiTietDonHangThang();

    /**
     * Lấy danh sách chi tiết các sản phẩm bán chạy nhất, có phân trang.
     * @param pageable Thông tin phân trang.
     * @return Dữ liệu chi tiết sản phẩm bán chạy đã được phân trang.
     */
    PagedResponse<ChiTietSanPhamBanChayResponse> getChiTietSanPhamBanChay(Pageable pageable);
}
