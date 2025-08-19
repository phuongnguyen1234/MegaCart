import { defineStore } from "pinia";
import { ref, computed } from "vue";
import { jwtDecode } from "jwt-decode";
import * as taiKhoanService from "@/service/taikhoan.service";
import type { LoginRequest } from "@/types/taikhoan.types";
import router from "@/router";
import { useCartStore } from "./giohang.store";
import type { JwtPayload } from "@/types/api.types";
import { VaiTroKey, ViTriNhanVienKey } from "@/types/taikhoan.types";
import type { NhanVienJwtPayload } from "@/types/api.types";

export const useAuthStore = defineStore("auth", () => {
  // --- State ---
  const token = ref<string | null>(localStorage.getItem("access_token"));
  // Lưu trữ toàn bộ payload đã giải mã để dễ dàng truy cập các trường như `userId`, `viTri`
  const payload = ref<JwtPayload | null>(null);

  // --- Internal Functions ---
  const _decodeAndSetState = (jwt: string) => {
    try {
      const decoded = jwtDecode<JwtPayload>(jwt);
      // Kiểm tra token hết hạn
      if (decoded.exp * 1000 < Date.now()) {
        console.warn("Token đã hết hạn, đang xóa trạng thái.");
        _clearState();
        return;
      }
      token.value = jwt;
      payload.value = decoded;
      localStorage.setItem("access_token", jwt);
    } catch (error) {
      console.error("Không thể giải mã token:", error);
      _clearState();
    }
  };

  const _clearState = () => {
    token.value = null;
    payload.value = null;
    localStorage.removeItem("access_token");
    // Gọi useCartStore() bên trong hàm để tránh lỗi dependency cycle
    useCartStore().clearCartCount();
  };

  // Khởi tạo state từ token trong localStorage khi store được tạo
  if (token.value) {
    _decodeAndSetState(token.value);
  }

  // --- Computed Properties (Getters) ---
  const isLoggedIn = computed(() => !!token.value && !!payload.value);
  const userName = computed(() => payload.value?.fullName || "");
  const userEmail = computed(() => payload.value?.sub || "");
  const role = computed(() => payload.value?.role || null);
  const isAdmin = computed(() => payload.value?.role === VaiTroKey.ADMIN);
  const isKhachHang = computed(
    () => payload.value?.role === VaiTroKey.KHACH_HANG
  );
  const isNhanVien = computed(
    () => payload.value?.role === VaiTroKey.NHAN_VIEN
  );

  // --- Actions ---
  const fetchUser = async () => {
    if (!isKhachHang.value) return; // Chỉ fetch khi là khách hàng
    try {
      // Gọi API để lấy thông tin mới nhất và cập nhật vào store
      const userData = await taiKhoanService.layThongTinTaiKhoan();
      if (payload.value) {
        payload.value.fullName = userData.tenKhachHang;
      }
    } catch (error) {
      console.error("Không thể lấy thông tin người dùng cho header:", error);
      // Interceptor trong apiClient sẽ xử lý lỗi 401 và tự động logout.
    }
  };

  const login = async (credentials: LoginRequest) => {
    const response = await taiKhoanService.login(credentials);
    _decodeAndSetState(response.token);

    // Sau khi giải mã, payload và các computed property (isKhachHang, isNhanVien, etc.) sẽ được cập nhật.
    // Dựa vào vai trò và vị trí, chuyển hướng người dùng đến trang phù hợp.
    const redirectPath = router.currentRoute.value.query.redirect as
      | string
      | undefined;

    if (isKhachHang.value) {
      // Chạy song song để tối ưu tốc độ
      await Promise.all([fetchUser(), useCartStore().fetchCartCount()]);
      // Chuyển hướng đến trang họ muốn vào hoặc trang chủ
      await router.push(redirectPath || { name: "TrangChu" });
    } else if (
      isNhanVien.value &&
      payload.value?.role === VaiTroKey.NHAN_VIEN
    ) {
      const nhanVienPayload = payload.value as NhanVienJwtPayload;
      // Nếu là nhân viên giao hàng, chuyển đến trang giao hàng riêng
      if (nhanVienPayload.viTri === ViTriNhanVienKey.NHAN_VIEN_GIAO_HANG) {
        await router.push({ name: "GiaoHang" });
      } else {
        // Các nhân viên khác (kho, bán hàng) sẽ luôn được chuyển đến /quan-ly
        // để router xử lý điều hướng động, bất kể redirectPath là gì.
        await router.push({ path: "/quan-ly" });
      }
    } else if (isAdmin.value) {
      await router.push(redirectPath || { path: "/quan-ly/dashboard" });
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
    if (payload.value) {
      payload.value.fullName = newName;
    }
  };

  return {
    // State & Getters
    payload,
    isLoggedIn,
    userName,
    userEmail,
    role,
    isAdmin,
    isKhachHang,
    isNhanVien,
    // Actions
    login,
    logout,
    updateUserName,
    fetchUser,
  };
});
