import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { jwtDecode } from "jwt-decode";
import * as taiKhoanService from "@/service/taikhoan.service";
import type { LoginCredentials } from "@/service/taikhoan.service";
import router from "@/router";
import { useCartStore } from "./giohang.store";
import type { JwtPayload } from "@/types/api.types";

export const useAuthStore = defineStore("auth", () => {
  const token = ref<string | null>(localStorage.getItem("access_token"));
  const userName = ref<string>("");
  const userEmail = ref<string>("");
  const role = ref<string>("");

  // Không khởi tạo cartStore ở đây để tránh dependency cycle

  const isLoggedIn = computed(() => !!token.value);
  const isAdmin = computed(() => role.value === "ADMIN");
  const isKhachHang = computed(() => role.value === "KHACH_HANG");

  const _clearState = () => {
    token.value = null;
    userName.value = "";
    userEmail.value = "";
    role.value = "";
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
      role.value = decoded.role || "";
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
    if (!isLoggedIn.value || !isKhachHang.value) return; // Chỉ fetch khi đã đăng nhập và là khách hàng
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

    const fetchPromises = [];

    // Đối với khách hàng, chúng ta cần lấy thông tin tài khoản chi tiết (để cập nhật)
    // và số lượng giỏ hàng.
    // Đối với Admin/Nhân viên, thông tin tên từ JWT là đủ, và họ không có giỏ hàng.
    if (role.value === "KHACH_HANG") {
      fetchPromises.push(fetchUser());
      fetchPromises.push(useCartStore().fetchCartCount());
    }

    if (fetchPromises.length > 0) {
      await Promise.all(fetchPromises);
    }
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
    role,
    isAdmin,
    isKhachHang,
    login,
    logout,
    updateUserName,
    fetchUser,
  };
});
