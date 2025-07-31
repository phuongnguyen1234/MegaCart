import { createRouter, createWebHistory } from "vue-router";
import TrangChuView from "../views/khachhang/TrangChuView.vue";
import DangNhapView from "../views/DangNhapView.vue";
import KetQuaTimKiemView from "../views/khachhang/KetQuaTimKiemView.vue";
import XemDanhMucView from "../views/khachhang/XemDanhMucView.vue";
import CapNhatTaiKhoanView from "../views/khachhang/CapNhatTaiKhoanView.vue";
import GioHangView from "@/views/khachhang/GioHangView.vue";
import ChiTietSanPhamView from "@/views/khachhang/ChiTietSanPhamView.vue";
import LichSuMuaHangView from "@/views/khachhang/LichSuMuaHangView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "DangNhap",
      component: DangNhapView,
    },
    {
      path: "/trang-chu",
      name: "TrangChu",
      component: TrangChuView,
    },
    {
      // Đường dẫn cho trang kết quả tìm kiếm, ví dụ: /tim-kiem?q=laptop
      path: "/tim-kiem",
      name: "KetQuaTimKiem",
      component: KetQuaTimKiemView,
    },
    {
      // Đường dẫn cho trang xem danh mục, ví dụ: /danh-muc/Quan-ao
      path: "/:categoryName",
      name: "XemDanhMuc",
      component: XemDanhMucView,
    },
    {
      path: "/tai-khoan",
      name: "TaiKhoan",
      component: CapNhatTaiKhoanView,
    },
    {
      path: "/gio-hang",
      name: "GioHang",
      component: GioHangView,
    },
    {
      // Đường dẫn cho trang chi tiết sản phẩm, ví dụ: /san-pham/123
      path: "/san-pham/:id",
      name: "ChiTietSanPham",
      component: ChiTietSanPhamView,
    },
    {
      path: "/lich-su-mua-hang",
      name: "LichSuMuaHang",
      component: LichSuMuaHangView,
    },
  ],
});

export default router;
