import type { PageableParams } from "./api.types";

/**
 * Định nghĩa các tham số để lấy danh sách sản phẩm trong kho.
 * Tương ứng với các @RequestParam trong `QuanLyKhoController.getDanhSachKho`.
 */
export interface GetKhoParams extends PageableParams {
  tuKhoa?: string;
  maDanhMuc?: number;
}

/**
 * Đại diện cho thông tin tồn kho của một sản phẩm trong danh sách.
 * Tương ứng với `KhoResponse` ở backend.
 *
 * Lưu ý: Component `QuanLiKhoView.vue` đang sử dụng một kiểu dữ liệu giả
 * tên là `SanPhamTonKho`. Interface này nên được sử dụng để thay thế.
 */
export interface KhoResponse {
  maSanPham: number;
  tenSanPham: string;
  anhMinhHoaChinh: string; // Giả định có trường này để hiển thị ảnh
  danhMucCha?: string;
  danhMucCon?: string;
  soLuongTon: number;
  thoiGianCapNhatCuoi?: string; // Chuỗi ISO DateTime
  noiDungCapNhatCuoi?: string;
}

/**
 * Đại diện cho thông tin tồn kho chi tiết của một sản phẩm, dùng cho việc sửa.
 * Tương ứng với `ChiTietKhoResponse` ở backend.
 */
export interface ChiTietKhoResponse {
  maSanPham: number;
  tenSanPham: string;
  soLuongTon: number;
}

/**
 * Đại diện cho payload (dữ liệu gửi đi) khi cập nhật tồn kho.
 * Tương ứng với `CapNhatKhoRequest` ở backend.
 */
export interface CapNhatKhoRequest {
  soLuong: number;
  noiDung: string;
  /**
   * Xác định xem số lượng mới sẽ ghi đè lên số lượng hiện tại (true)
   * hay được cộng vào số lượng hiện tại (false).
   */
  ghiDe: boolean;
}

/**
 * Kiểu dữ liệu cho form cập nhật trong modal `CapNhatTonKhoModal`.
 * Đây là kiểu dữ liệu phía client và sẽ được chuyển đổi thành `CapNhatKhoRequest` trước khi gửi đi.
 */
export interface DuLieuCapNhatKhoForm {
  hinhThuc: "them" | "ghide";
  soLuong: number | null;
  noiDung: string;
}
