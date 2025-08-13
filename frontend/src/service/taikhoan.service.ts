import apiClient from "./apiClient";

// --- Interfaces for Type Safety ---

// Dữ liệu cần thiết để đăng nhập
export interface LoginCredentials {
  email: string;
  matKhau: string;
}

// Dữ liệu cần thiết để đăng ký
export interface RegisterCredentials {
  email: string;
  matKhau: string;
}

// Thông tin người dùng trả về từ API
export interface User {
  tenKhachHang: string;
  email: string;
  diaChi: string;
  soDienThoai: string;
}

// Phản hồi từ API đăng nhập hoặc các hành động xác thực khác
export interface AuthResponse {
  token: string;
}

/**
 * @deprecated Sử dụng AuthResponse để nhất quán hơn.
 */
export type LoginResponse = AuthResponse;

// Phản hồi từ API cập nhật hồ sơ
export interface CapNhatHoSoResponse {
  message: string;
  emailChangeInitiated: boolean;
}

// --- Service Functions ---

/**
 * Gửi yêu cầu đăng nhập đến API.
 * @param credentials - Thông tin email và mật khẩu của người dùng.
 * @returns Promise chứa JWT.
 */
export const login = (credentials: LoginCredentials): Promise<AuthResponse> => {
  // Kiểm tra đầu vào ở phía client trước khi gửi request
  // để tránh các lỗi validation không cần thiết từ server.
  if (!credentials.email || !credentials.matKhau) {
    return Promise.reject(new Error("Email và mật khẩu không được để trống."));
  }
  // Giả định endpoint của backend là /api/auth/dang-nhap
  return apiClient.post("/auth/dang-nhap", credentials);
};

/**
 * Gửi yêu cầu đăng ký đến API.
 * @param credentials - Thông tin đăng ký của người dùng (email, mật khẩu).
 * @returns Promise<void>
 */
export const register = (credentials: RegisterCredentials): Promise<void> => {
  if (!credentials.email || !credentials.matKhau) {
    return Promise.reject(new Error("Email và mật khẩu không được để trống."));
  }
  // Giả định endpoint của backend là /api/auth/dang-ky
  return apiClient.post("/auth/dang-ky", credentials);
};

export const logout = async () => {
  await apiClient.post("/auth/dang-xuat");
};

// Gửi email quên mật khẩu
export const guiEmailQuenMatKhau = (email: string) => {
  return apiClient.post("/auth/quen-mat-khau", { email });
};

// xac thuc otp
export const xacThucOtp = (email: string, otp: string) => {
  return apiClient.post("/auth/xac-thuc-otp", { email, otp });
};

// Đặt lại mật khẩu
export const datLaiMatKhau = (
  email: string,
  otp: string,
  matKhauMoi: string
) => {
  return apiClient.post("/auth/dat-lai-mat-khau", { email, otp, matKhauMoi });
};

//lay thong tin tai khoan khach hang
export const layThongTinTaiKhoan = (): Promise<User> => {
  return apiClient.get("/khach-hang/thong-tin");
};

//cap nhat tai khoan
export const capNhatTaiKhoan = (
  data: Partial<User>
): Promise<CapNhatHoSoResponse> => {
  return apiClient.patch("/khach-hang/cap-nhat-thong-tin", data);
};

// Xác nhận thay đổi email bằng OTP
export const xacNhanDoiEmail = (otp: string): Promise<AuthResponse> => {
  return apiClient.post("/khach-hang/email/xac-nhan", { otp });
};
