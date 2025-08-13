/**
 * Dữ liệu gửi lên khi thêm sản phẩm vào giỏ hàng.
 */
export interface ThemVaoGioHangRequest {
  maSanPham: number;
  soLuong: number;
}

/**
 * Phản hồi từ API sau khi thêm sản phẩm vào giỏ.
 */
export interface ThemVaoGioHangResponse {
  message: string;
  tongSoLuongSanPham: number; // Tổng số loại sản phẩm khác nhau trong giỏ
  tongSoLuongDonVi: number; // Tổng số lượng của tất cả sản phẩm
}

/**
 * Phản hồi từ API khi lấy thông tin giỏ hàng.
 * Backend trả về một đối tượng lớn cho cả trang thanh toán.
 */
export interface ThongTinThanhToanResponse {
  items: any[]; // Chi tiết các sản phẩm, có thể định nghĩa kỹ hơn nếu cần
  tongSoLuongSanPham: number;
  tongTien: number;
}
