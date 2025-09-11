import axios from "axios";

// Tạo instance Axios
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL, // Đổi theo backend của bạn
  timeout: 10000, // 10 giây timeout
});

// Interceptor: thêm token vào header nếu có
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("access_token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor: xử lý lỗi trả về
apiClient.interceptors.response.use(
  (response) => response.data, // Chỉ lấy phần data
  (error) => {
    if (error.response) {
      // Backend trả lỗi
      console.error("API Error:", error.response.status, error.response.data);
      // Lấy URL của request gây ra lỗi
      const requestUrl = error.config.url;

      // Chỉ chuyển hướng về trang login nếu lỗi 401 không phải từ API đăng nhập
      // Điều này để tránh vòng lặp chuyển hướng khi người dùng nhập sai mật khẩu.
      if (error.response.status === 401 && requestUrl !== "/auth/dang-nhap") {
        // Token hết hạn hoặc không hợp lệ, thực hiện logout
        localStorage.removeItem("access_token");
        window.location.href = "/dang-nhap"; // Sửa thành route đăng nhập chính xác
      }
    } else if (error.request) {
      // Không nhận được phản hồi
      console.error("No response from server");
    } else {
      console.error("Request setup error:", error.message);
    }
    return Promise.reject(error);
  }
);

export default apiClient;
