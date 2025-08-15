import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { jwtDecode } from "jwt-decode";
import * as taiKhoanService from "@/service/taikhoan.service";
import type { LoginCredentials } from "@/service/taikhoan.service";
import router from "@/router";
import { useCartStore } from "./giohang.store";

interface JwtPayload {
  sub: string; // User's email
  name: string; // User's full name
  roles: string[]; // User's roles
  iat: number;
  exp: number;
}

export const useAuthStore = defineStore("auth", () => {
  const token = ref<string | null>(localStorage.getItem("access_token"));
  const userName = ref<string>("");
  const userEmail = ref<string>("");

  // Không khởi tạo cartStore ở đây để tránh dependency cycle

  const isLoggedIn = computed(() => !!token.value);

  const _clearState = () => {
    token.value = null;
    userName.value = "";
    userEmail.value = "";
    localStorage.removeItem("access_token");
    useCartStore().clearCartCount(); // Xóa số lượng giỏ hàng khi logout
  };

  const _decodeAndSetState = (jwt: string) => {
    try {
      const decoded = jwtDecode<JwtPayload>(jwt);
      // Kiểm tra token hết hạn
      if (decoded.exp * 1000 < Date.now()) {
        _clearState();
        return;
      }
      token.value = jwt;
      userName.value = decoded.name;
      userEmail.value = decoded.sub;
      localStorage.setItem("access_token", jwt);
    } catch (error) {
      console.error("Failed to decode token:", error);
      _clearState();
    }
  };

  // Khởi tạo state từ token trong localStorage khi store được tạo
  if (token.value) {
    _decodeAndSetState(token.value);
  }

  const fetchUser = async () => {
    if (!isLoggedIn.value) return; // Chỉ fetch khi đã đăng nhập
    try {
      // Gọi API để lấy thông tin mới nhất và cập nhật vào store
      const userData = await taiKhoanService.layThongTinTaiKhoan();
      userName.value = userData.tenKhachHang;
      userEmail.value = userData.email;
    } catch (error) {
      console.error("Không thể lấy thông tin người dùng cho header:", error);
      // Interceptor trong apiClient sẽ xử lý lỗi 401 và tự động logout.
    }
  };

  const login = async (credentials: LoginCredentials) => {
    const response = await taiKhoanService.login(credentials);
    _decodeAndSetState(response.token);
    // Lấy thông tin người dùng và giỏ hàng song song để tăng tốc
    await Promise.all([fetchUser(), useCartStore().fetchCartCount()]);
  };

  const logout = async () => {
    try {
      await taiKhoanService.logout();
    } catch (error) {
      console.error(
        "Logout API call failed, clearing client state anyway.",
        error
      );
    } finally {
      _clearState();
      await router.push({ name: "DangNhap" });
    }
  };

  const updateUserName = (newName: string) => {
    userName.value = newName;
  };

  return {
    isLoggedIn,
    userName,
    userEmail,
    login,
    logout,
    updateUserName,
    fetchUser,
  };
});
