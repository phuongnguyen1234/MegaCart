import type { EnumObject, PageableParams } from "./api.types";

/**
 * Các key của trạng thái tài khoản, dùng để giao tiếp với API.
 * Giá trị của enum này phải TRÙNG KHỚP với tên của enum constant trong `TrangThaiTaiKhoan.java` ở backend.
 * Dựa vào `CapNhatTrangThaiTaiKhoanRequest` được dùng chung cho cả khách hàng và nhân viên.
 */
export enum TrangThaiTaiKhoanKey {
  HOAT_DONG = "HOAT_DONG",
  KHOA = "KHOA",
}

/**
 * Ánh xạ từ key của trạng thái tài khoản sang tên hiển thị (label).
 */
export const TrangThaiTaiKhoanLabel: Record<TrangThaiTaiKhoanKey, string> = {
  [TrangThaiTaiKhoanKey.HOAT_DONG]: "Hoạt động",
  [TrangThaiTaiKhoanKey.KHOA]: "Khóa", // Hoặc "Ngừng hoạt động" tùy vào ngữ cảnh hiển thị
};

/**
 * Cấu trúc đối tượng trạng thái tài khoản nhận về từ API.
 */
export type TrangThaiTaiKhoanObject = EnumObject<TrangThaiTaiKhoanKey>;

/**
 * Dữ liệu hiển thị cho một khách hàng trong danh sách quản lý.
 * Tương ứng với `HienThiDanhSachKhachHangResponse.java` trên backend.
 */
export interface HienThiDanhSachKhachHangResponse {
  maKhachHang: number;
  tenKhachHang: string;
  email: string;
  soDienThoai: string;
  diaChi: string;
  trangThaiTaiKhoan: TrangThaiTaiKhoanObject;
}

/**
 * Dữ liệu gửi lên khi cập nhật trạng thái tài khoản của khách hàng.
 * Tương ứng với `CapNhatTrangThaiTaiKhoanRequest.java` trên backend.
 */
export interface CapNhatTrangThaiTaiKhoanRequest {
  trangThai: TrangThaiTaiKhoanKey;
}

/**
 * Các tham số để lọc và tìm kiếm khách hàng trong trang quản lý.
 * Tương ứng với các @RequestParam trong `QuanLyKhachHangController.getDSKhachHang`.
 */
export interface GetKhachHangParams extends PageableParams {
  searchField?: "tenKhachHang" | "email" | "soDienThoai";
  searchValue?: string;
  hienThiTaiKhoanBiKhoa?: boolean;
}
