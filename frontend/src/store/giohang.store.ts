import { defineStore } from "pinia";
import { ref } from "vue";
import { getThongTinThanhToan } from "@/service/giohang.service";

export const useCartStore = defineStore("cart", () => {
  const soLuongSanPham = ref(0);

  const fetchCartCount = async () => {
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
