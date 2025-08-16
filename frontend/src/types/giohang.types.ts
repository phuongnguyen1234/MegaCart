import type { TrangThaiSanPhamObject } from "./sanpham.types";
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
 * Dữ liệu cho một sản phẩm trong giỏ hàng.
 */
export interface GioHangItem {
  maSanPham: number;
  tenSanPham: string;
  anhMinhHoa: string; // Đồng bộ tên trường với backend (anhMinhHoa)
  donGia: number;
  soLuong: number;
  thanhTien: number; // Đơn giá * số lượng
  donVi: string;
  trangThai: TrangThaiSanPhamObject; // Thêm trạng thái sản phẩm
}

/**
 * Thông tin giao hàng mặc định của khách hàng.
 */
export interface ThongTinGiaoHangMacDinh {
  tenKhachHang: string;
  soDienThoai: string;
  diaChi: string;
}

/**
 * Phản hồi từ API khi lấy thông tin giỏ hàng.
 * Backend trả về một đối tượng lớn cho cả trang thanh toán.
 * Tương ứng với endpoint GET /api/gio-hang
 */
export interface ThongTinThanhToanResponse {
  items: GioHangItem[];
  tongSoLuongSanPham: number;
  tongTien: number;
  thongTinGiaoHangMacDinh: ThongTinGiaoHangMacDinh;
}

/**
 * Dữ liệu gửi lên khi cập nhật số lượng sản phẩm.
 * Tương ứng với endpoint PATCH /api/gio-hang/cap-nhat/{maSanPham}
 */
export interface CapNhatSoLuongRequest {
  soLuong: number;
}

/**
 * Phản hồi chung cho các thao tác cập nhật giỏ hàng, ví dụ như cập nhật số lượng.
 * Tương ứng với response của endpoint PATCH /api/gio-hang/cap-nhat/{maSanPham}
 */
export interface GioHangResponse {
  items: GioHangItem[];
  tongSoLuongSanPham: number;
  tongTien: number;
}

/**
 * Phản hồi từ API sau khi xóa sản phẩm khỏi giỏ.
 * Tương ứng với endpoint DELETE /api/gio-hang/xoa/{maSanPham}
 */
export interface XoaKhoiGioHangResponse {
  message: string;
  tongSoLuongSanPham: number;
  tongTien: number;
}
