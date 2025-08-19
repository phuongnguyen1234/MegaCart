import { defineStore } from "pinia";
import { ref } from "vue";
import { getThongTinThanhToan } from "@/service/giohang.service";
import { useAuthStore } from "./auth.store";

export const useCartStore = defineStore("cart", () => {
  const soLuongSanPham = ref(0);

  const fetchCartCount = async () => {
    // Lấy auth store để kiểm tra vai trò người dùng.
    // Phải gọi useAuthStore() bên trong action để tránh vòng lặp dependency vô hạn
    // khi các store import lẫn nhau ở cấp module.
    const authStore = useAuthStore();

    // Chỉ gọi API nếu người dùng là khách hàng. Admin/Nhân viên không có giỏ hàng.
    if (!authStore.isKhachHang) {
      soLuongSanPham.value = 0; // Đảm bảo số lượng là 0 và thoát
      return;
    }
    try {
      const cartData = await getThongTinThanhToan();
      soLuongSanPham.value = cartData.tongSoLuongSanPham;
    } catch (error) {
      console.error("Không thể lấy số lượng giỏ hàng:", error);
      // Nếu lỗi (ví dụ: chưa đăng nhập), đặt số lượng về 0
      soLuongSanPham.value = 0;
    }
  };

  const setCartCount = (count: number) => {
    soLuongSanPham.value = count;
  };

  const clearCartCount = () => {
    soLuongSanPham.value = 0;
  };

  return {
    soLuongSanPham,
    fetchCartCount,
    setCartCount,
    clearCartCount,
  };
});
