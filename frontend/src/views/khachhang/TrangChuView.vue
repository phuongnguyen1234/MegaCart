<template>
  <CustomerWithNav>
    <ThemVaoGioHangModal
      :visible="isThemVaoGioHangModalVisible"
      :sanPham="sanPhamDuocChon"
      @dong="dongThemVaoGioHangModal"
      @them="handleThemVaoGioHang"
    />
    <BannerSlider />

    <ListSanPham
      tieuDe="Sản phẩm mới"
      :dsSanPham="dsSanPhamMoi"
      linkXemThem="/san-pham-moi"
      @themVaoGioHang="hienThiThemVaoGioHangModal"
    />

    <ListSanPham
      tieuDe="Bán chạy nhất"
      :dsSanPham="dsBanChay"
      linkXemThem="/ban-chay"
      @themVaoGioHang="hienThiThemVaoGioHangModal"
    />
  </CustomerWithNav>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import CustomerWithNav from "@/components/layouts/CustomerWithNav.vue";
import BannerSlider from "@/components/trangchu/BannerSlider.vue";
import { useToast } from "@/composables/useToast";
import ListSanPham from "@/components/base/ListSanPham.vue";
import ThemVaoGioHangModal from "@/components/xemsanpham/ThemVaoGioHangModal.vue";
import {
  getSanPhamTheoNhan,
  getSanPhamBanChay,
  NhanSanPhamKey,
} from "@/service/sanpham.service";
import type { SanPhamResponse } from "@/types/sanpham.types";
import { themVaoGioHang } from "@/service/giohang.service";
import { useCartStore } from "@/store/giohang.store";
import { useAuthStore } from "@/store/auth.store";
import { useRouter } from "vue-router";

const { showToast } = useToast();
const cartStore = useCartStore();
const authStore = useAuthStore();
const router = useRouter();

const dsSanPhamMoi = ref<SanPhamResponse[]>([]);
const dsBanChay = ref<SanPhamResponse[]>([]);

onMounted(async () => {
  try {
    // Lấy 10 sản phẩm mới nhất
    const [resMoi, resBanChay] = await Promise.all([
      getSanPhamTheoNhan(NhanSanPhamKey.MOI, {}, { size: 10 }),
      getSanPhamBanChay({}, { size: 10 }),
    ]);
    dsSanPhamMoi.value = resMoi.content;
    dsBanChay.value = resBanChay.content;
  } catch (error) {
    console.error("Lỗi khi tải sản phẩm trang chủ:", error);
    showToast({
      thongBao: "Không thể tải sản phẩm. Vui lòng thử lại.",
      loai: "loi",
    });
  }
});

const isThemVaoGioHangModalVisible = ref(false);
const sanPhamDuocChon = ref<SanPhamResponse | null>(null);

const hienThiThemVaoGioHangModal = (product: SanPhamResponse) => {
  sanPhamDuocChon.value = product;
  isThemVaoGioHangModalVisible.value = true;
};

const dongThemVaoGioHangModal = () => {
  isThemVaoGioHangModalVisible.value = false;
  sanPhamDuocChon.value = null;
};

const handleThemVaoGioHang = async (payload: {
  sanPham: SanPhamResponse;
  soLuong: number;
}) => {
  // Kiểm tra nếu người dùng chưa đăng nhập
  if (!authStore.isLoggedIn) {
    dongThemVaoGioHangModal(); // Đóng modal trước khi chuyển hướng
    showToast({
      thongBao: "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng.",
      loai: "loi",
    });
    // Chuyển hướng đến trang đăng nhập và lưu lại trang hiện tại để quay về
    router.push({
      name: "DangNhap",
      query: { redirect: router.currentRoute.value.fullPath },
    });
    return; // Dừng hàm tại đây
  }
  dongThemVaoGioHangModal();
  try {
    const response = await themVaoGioHang({
      maSanPham: payload.sanPham.maSanPham,
      soLuong: payload.soLuong,
    });
    // Cập nhật số lượng trên header
    cartStore.setCartCount(response.tongSoLuongSanPham);
    showToast({
      thongBao: response.message,
      loai: "thanhCong",
    });
  } catch (error: any) {
    const message =
      error.response?.data?.message || "Thêm vào giỏ hàng thất bại!";
    showToast({ thongBao: message, loai: "loi" });
  }
};
</script>
