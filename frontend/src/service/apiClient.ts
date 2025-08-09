import axios from "axios";

// Tạo instance Axios
const apiClient = axios.create({
  baseURL: "http://localhost:8080/api", // Đổi theo backend của bạn
  timeout: 10000, // 10 giây timeout
  headers: {
    "Content-Type": "application/json",
  },
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
      if (error.response.status === 401) {
        // Ví dụ: token hết hạn → logout
        localStorage.removeItem("access_token");
        window.location.href = "/login";
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
