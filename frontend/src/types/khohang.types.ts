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
 * Đại diện cho thông tin tồn kho của một sản phẩm trong danh sách quản lý kho.
 * Tương ứng với `KhoResponse` ở backend.
 *
 * NOTE: This interface should replace the mock type `SanPhamTonKho` in `QuanLiKhoView.vue`.
 * The view will need to be updated to use these field names.
 */
export interface KhoResponse {
  maSanPham: number;
  anhMinhHoaChinh: string;
  tenSanPham: string;
  danhMucCha?: string;
  danhMucCon?: string;
  soLuong: number;
  noiDungCapNhat?: string;
}

/**
 * Đại diện cho thông tin tồn kho chi tiết của một sản phẩm, dùng cho việc sửa trong modal.
 * Tương ứng với `ChiTietKhoResponse` ở backend.
 */
export interface ChiTietKhoResponse {
  maSanPham: number;
  tenSanPham: string;
  soLuongHienTai: number;
}

/**
 * Các key của hình thức cập nhật kho, dùng để gửi lên API.
 * Giá trị của enum này phải TRÙNG KHỚP với tên của enum constant trong `HinhThucCapNhatKho.java` ở backend.
 */
export enum HinhThucCapNhatKhoKey {
  THEM_VAO_HIEN_TAI = "THEM_VAO_HIEN_TAI", // Cộng dồn vào số lượng hiện tại
  GHI_DE = "GHI_DE", // Ghi đè số lượng hiện tại
}

/**
 * Đại diện cho payload (dữ liệu gửi đi) khi cập nhật tồn kho.
 * Tương ứng với `CapNhatKhoRequest` ở backend.
 */
export interface CapNhatKhoRequest {
  hinhThuc: HinhThucCapNhatKhoKey;
  soLuong: number;
  noiDung?: string;
}
