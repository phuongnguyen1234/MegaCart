import apiClient from "./apiClient";

// --- Interfaces for Type Safety ---

// Dữ liệu cần thiết để đăng nhập
export interface LoginCredentials {
  email: string;
  matKhau: string;
}

// Thông tin người dùng trả về từ API
interface User {
  id: string;
  email: string;
  // Giả định backend trả về vai trò
  vaiTro: "KHACH_HANG" | "ADMIN" | "NHAN_VIEN";
}

// Phản hồi từ API đăng nhập
export interface LoginResponse {
  token: string;
}

// --- Service Functions ---

/**
 * Gửi yêu cầu đăng nhập đến API.
 * @param credentials - Thông tin email và mật khẩu của người dùng.
 * @returns Promise chứa JWT.
 */
export const login = (
  credentials: LoginCredentials
): Promise<LoginResponse> => {
  // Kiểm tra đầu vào ở phía client trước khi gửi request
  // để tránh các lỗi validation không cần thiết từ server.
  if (!credentials.email || !credentials.matKhau) {
    return Promise.reject(new Error("Email và mật khẩu không được để trống."));
  }
  // Giả định endpoint của backend là /api/auth/dang-nhap
  return apiClient.post("/auth/dang-nhap", credentials);
};

export const logout = async () => {
  await apiClient.post("/auth/dang-xuat");
};

// Có thể thêm các hàm khác liên quan đến tài khoản ở đây trong tương lai
// Ví dụ:
// export const register = (data) => apiClient.post('/auth/dang-ky', data);
// export const getProfile = () => apiClient.get('/tai-khoan/me');
