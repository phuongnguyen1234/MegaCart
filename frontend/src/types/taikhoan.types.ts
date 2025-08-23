/**
 * Represents the status of an employee account.
 */
export enum TrangThaiTaiKhoan {
  HOAT_DONG = "Hoạt động",
  KHOA = "Khóa",
}

/**
 * Defines the canonical role keys used in the system, especially in JWTs.
 * Must match the values from the backend.
 */
export enum VaiTroKey {
  KHACH_HANG = "KHACH_HANG",
  NHAN_VIEN = "NHAN_VIEN",
  ADMIN = "ADMIN",
}

/**
 * Defines the canonical keys for employee positions (chức vụ/vị trí).
 * Must match the values from the backend.
 */
export enum ViTriNhanVienKey {
  NHAN_VIEN_QUAN_LI_KHO = "NHAN_VIEN_QUAN_LI_KHO",
  NHAN_VIEN_BAN_HANG = "NHAN_VIEN_BAN_HANG", // Ví dụ cho người quản lý đơn hàng
  NHAN_VIEN_GIAO_HANG = "NHAN_VIEN_GIAO_HANG", // Ví dụ cho người quản lý giao hàng
}

export interface TaiKhoan {
  maTaiKhoan: string;
  email: string;
  soDienThoai: string;
  trangThaiTaiKhoan: TrangThaiTaiKhoan;
  vaiTro: string;
}

// --- Types for Authentication and Account Management ---

/**
 * Dữ liệu cần thiết để đăng nhập.
 * Tương ứng với `LoginRequest.java` ở backend.
 */
export interface LoginRequest {
  email: string;
  matKhau: string;
}

/**
 * Dữ liệu cần thiết để đăng ký.
 * Tương ứng với `RegisterRequest.java` ở backend.
 */
export interface RegisterRequest {
  email: string;
  matKhau: string;
  // Có thể thêm các trường khác như hoTen, soDienThoai nếu backend yêu cầu
}

/**
 * Phản hồi từ API đăng nhập hoặc các hành động xác thực khác trả về token.
 * Tương ứng với `AuthResponse.java` ở backend.
 */
export interface AuthResponse {
  token: string;
}

/**
 * Thông tin tài khoản của khách hàng (dùng cho trang "Tài khoản của tôi").
 * Tương ứng với `ThongTinTaiKhoanResponse.java` ở backend.
 */
export interface ThongTinTaiKhoanKhachHang {
  tenKhachHang: string;
  email: string;
  diaChi: string;
  soDienThoai: string;
}

/**
 * Phản hồi từ API cập nhật hồ sơ.
 */
export interface CapNhatHoSoResponse {
  message: string;
  emailChangeInitiated: boolean;
  thongTinCapNhat: ThongTinTaiKhoanKhachHang;
}

/**
 * Dữ liệu gửi lên khi yêu cầu reset mật khẩu.
 */
export interface GuiEmailQuenMatKhauRequest {
  email: string;
}

/**
 * Dữ liệu gửi lên để xác thực OTP.
 */
export interface XacThucOtpRequest {
  email: string;
  otp: string;
}

/**
 * Dữ liệu gửi lên để đặt lại mật khẩu mới.
 */
export interface DatLaiMatKhauRequest extends XacThucOtpRequest {
  matKhauMoi: string;
}

/**
 * Dữ liệu gửi lên để xác nhận thay đổi email.
 */
export interface XacNhanDoiEmailRequest {
  otp: string;
}

export interface CapNhatHoSoRequest {
  tenKhachHang: string;
  soDienThoai: string;
  diaChi: string;
  emailMoi: string;
}
