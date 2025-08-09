import apiClient from "./apiClient";

// --- Interfaces for Type Safety ---

// Dữ liệu cần thiết để đăng nhập
interface LoginCredentials {
  email: string;
  password: string;
}

// Thông tin người dùng trả về từ API
interface User {
  id: string;
  email: string;
  // Giả định backend trả về vai trò
  vaiTro: "KHACH_HANG" | "ADMIN" | "NHAN_VIEN";
}

// Phản hồi từ API đăng nhập
interface LoginResponse {
  accessToken: string;
  user: User;
}

// --- Service Functions ---

/**
 * Gửi yêu cầu đăng nhập đến API.
 * @param credentials - Thông tin email và mật khẩu của người dùng.
 * @returns Promise chứa token và thông tin người dùng.
 */
export const login = (
  credentials: LoginCredentials
): Promise<LoginResponse> => {
  // Giả định endpoint của backend là /api/auth/dang-nhap
  return apiClient.post("/auth/dang-nhap", credentials);
};

// Có thể thêm các hàm khác liên quan đến tài khoản ở đây trong tương lai
// Ví dụ:
// export const register = (data) => apiClient.post('/auth/dang-ky', data);
// export const getProfile = () => apiClient.get('/tai-khoan/me');
