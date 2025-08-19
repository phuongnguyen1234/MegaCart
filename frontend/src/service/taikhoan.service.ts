import apiClient from "./apiClient";
import type {
  AuthResponse,
  CapNhatHoSoResponse,
  CapNhatTaiKhoanRequest,
  DatLaiMatKhauRequest,
  GuiEmailQuenMatKhauRequest,
  LoginRequest,
  RegisterRequest,
  ThongTinTaiKhoanKhachHang,
  XacNhanDoiEmailRequest,
  XacThucOtpRequest,
} from "@/types/taikhoan.types";

// --- Service Functions ---

/**
 * Gửi yêu cầu đăng nhập đến API.
 * @param data - Thông tin email và mật khẩu của người dùng.
 * @returns Promise chứa JWT.
 */
export const login = (data: LoginRequest): Promise<AuthResponse> => {
  // Kiểm tra đầu vào ở phía client trước khi gửi request
  // để tránh các lỗi validation không cần thiết từ server.
  if (!data.email || !data.matKhau) {
    return Promise.reject(new Error("Email và mật khẩu không được để trống."));
  }
  // Giả định endpoint của backend là /api/auth/dang-nhap
  return apiClient.post("/auth/dang-nhap", data);
};

/**
 * Gửi yêu cầu đăng ký đến API.
 * @param data - Thông tin đăng ký của người dùng (email, mật khẩu).
 * @returns Promise<void>
 */
export const register = (data: RegisterRequest): Promise<void> => {
  if (!data.email || !data.matKhau) {
    return Promise.reject(new Error("Email và mật khẩu không được để trống."));
  }
  // Giả định endpoint của backend là /api/auth/dang-ky
  return apiClient.post("/auth/dang-ky", data);
};

/**
 * Gửi yêu cầu đăng xuất đến API.
 * @returns Promise<void>
 */
export const logout = (): Promise<void> => {
  return apiClient.post("/auth/dang-xuat");
};

/**
 * Gửi email yêu cầu đặt lại mật khẩu.
 * @param data - Dữ liệu chứa email của người dùng.
 * @returns Promise<void>
 */
export const guiEmailQuenMatKhau = (
  data: GuiEmailQuenMatKhauRequest
): Promise<void> => {
  return apiClient.post("/auth/quen-mat-khau", data);
};

/**
 * Xác thực mã OTP.
 * @param data - Dữ liệu chứa email và mã OTP.
 * @returns Promise<void>
 */
export const xacThucOtp = (data: XacThucOtpRequest): Promise<void> => {
  return apiClient.post("/auth/xac-thuc-otp", data);
};

/**
 * Đặt lại mật khẩu mới sau khi đã xác thực OTP.
 * @param data - Dữ liệu chứa email, OTP và mật khẩu mới.
 * @returns Promise<void>
 */
export const datLaiMatKhau = (data: DatLaiMatKhauRequest): Promise<void> => {
  return apiClient.post("/auth/dat-lai-mat-khau", data);
};

/**
 * Lấy thông tin tài khoản của khách hàng đang đăng nhập.
 * @returns Promise chứa thông tin chi tiết của tài khoản.
 */
export const layThongTinTaiKhoan = (): Promise<ThongTinTaiKhoanKhachHang> => {
  return apiClient.get("/khach-hang/thong-tin");
};

/**
 * Cập nhật thông tin tài khoản của khách hàng.
 * @param data - Dữ liệu cần cập nhật (tên, địa chỉ, sđt).
 * @returns Promise chứa thông báo và trạng thái thay đổi email.
 */
export const capNhatTaiKhoan = (
  data: CapNhatTaiKhoanRequest
): Promise<CapNhatHoSoResponse> => {
  return apiClient.patch("/khach-hang/cap-nhat-thong-tin", data);
};

/**
 * Xác nhận thay đổi email bằng OTP.
 * @param data - Dữ liệu chứa mã OTP.
 * @returns Promise chứa token mới nếu email được cập nhật thành công.
 */
export const xacNhanDoiEmail = (
  data: XacNhanDoiEmailRequest
): Promise<AuthResponse> => {
  return apiClient.post("/khach-hang/email/xac-nhan", data);
};
