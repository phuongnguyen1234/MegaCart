import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { jwtDecode } from "jwt-decode";
import {
  login as loginApi,
  logout as logoutApi,
} from "@/service/taikhoan.service";
import type { LoginCredentials } from "@/service/taikhoan.service";

// Interface cho đối tượng người dùng mà chúng ta sẽ lưu trong store
interface UserState {
  id: number;
  fullName: string;
  email: string;
  role: "KHACH_HANG" | "ADMIN" | "NHAN_VIEN";
}

// Interface cho payload của JWT sau khi được giải mã
interface DecodedToken {
  sub: string;
  role: "KHACH_HANG" | "ADMIN" | "NHAN_VIEN";
  fullName: string;
  userId: number;
  iat: number;
  exp: number;
}

export const useAuthStore = defineStore("auth", () => {
  const router = useRouter();

  // --- STATE ---
  const user = ref<UserState | null>(null);
  const token = ref<string | null>(localStorage.getItem("access_token"));

  // --- GETTERS (Computed properties) ---
  const isLoggedIn = computed(() => !!user.value);
  const userName = computed(() => user.value?.fullName || "");

  /**
   * Xóa thông tin xác thực khỏi state và localStorage một cách thầm lặng.
   */
  function clearAuthData() {
    user.value = null;
    token.value = null;
    localStorage.removeItem("access_token");
  }

  // --- ACTIONS ---

  /**
   * Lưu trữ thông tin xác thực sau khi đăng nhập hoặc kiểm tra token.
   * @param newToken - JWT nhận được từ API.
   */
  function setAuthData(newToken: string) {
    const decodedToken: DecodedToken = jwtDecode(newToken);
    user.value = {
      id: decodedToken.userId,
      fullName: decodedToken.fullName,
      email: decodedToken.sub,
      role: decodedToken.role,
    };
    token.value = newToken;
    localStorage.setItem("access_token", newToken);
  }

  /**
   * Xử lý logic đăng nhập, gọi API và điều hướng.
   */
  async function login(credentials: LoginCredentials) {
    const response = await loginApi(credentials);
    setAuthData(response.token);

    // Điều hướng sau khi đăng nhập thành công
    if (user.value?.role === "ADMIN" || user.value?.role === "NHAN_VIEN") {
      router.push({ name: "ThongKe" }); // Hoặc trang dashboard của admin
    } else {
      router.push({ name: "TrangChu" });
    }
  }

  /**
   * Xóa thông tin người dùng và token, sau đó điều hướng về trang đăng nhập.
   */
  async function logout() {
    try {
      await logoutApi();
    } catch (error) {
      console.error(
        "Lỗi khi gọi API đăng xuất, nhưng vẫn tiến hành đăng xuất ở client:",
        error
      );
    } finally {
      clearAuthData();
      router.push({ name: "DangNhap" });
    }
  }

  // Logic khởi tạo: Tự động đăng nhập nếu có token hợp lệ trong localStorage.
  // Chạy một lần khi store được khởi tạo.
  if (token.value) {
    try {
      const decoded: DecodedToken = jwtDecode(token.value);
      // Kiểm tra xem token đã hết hạn chưa
      if (decoded.exp * 1000 > Date.now()) {
        setAuthData(token.value);
      } else {
        clearAuthData(); // Token hết hạn, xóa nó đi
      }
    } catch (error) {
      console.error("Token không hợp lệ, đang xóa...", error);
      clearAuthData(); // Token không hợp lệ, xóa nó đi
    }
  }

  return {
    user,
    isLoggedIn,
    userName,
    login,
    logout,
  };
});
