import { createRouter, createWebHistory } from "vue-router";
import TrangChuView from "../views/khachhang/TrangChuView.vue";
import DangNhapView from "../views/DangNhapView.vue";
import KetQuaTimKiemView from "../views/khachhang/KetQuaTimKiemView.vue";
import XemDanhMucChaView from "../views/khachhang/XemDanhMucChaView.vue";
import XemDanhMucConView from "../views/khachhang/XemDanhMucConView.vue";
import CapNhatTaiKhoanView from "../views/khachhang/CapNhatTaiKhoanView.vue";
import GioHangView from "@/views/khachhang/GioHangView.vue";
import ChiTietSanPhamView from "@/views/khachhang/ChiTietSanPhamView.vue";
import LichSuMuaHangView from "@/views/khachhang/LichSuMuaHangView.vue";
import DatLaiMatKhauView from "@/views/khachhang/DatLaiMatKhauView.vue";

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
      // Đường dẫn cho trang xem danh mục, ví dụ: /Quan-ao
      path: "/:danhMucCha",
      name: "XemDanhMucCha",
      component: XemDanhMucChaView,
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
      path: "/:danhMucCha/:danhMucCon/:id",
      name: "ChiTietSanPham",
      component: ChiTietSanPhamView,
    },
    {
      path: "/lich-su-mua-hang",
      name: "LichSuMuaHang",
      component: LichSuMuaHangView,
    },
    {
      path: "/:danhMucCha/:danhMucCon",
      name: "XemDanhMucCon",
      component: XemDanhMucConView,
    },
    {
      path: "/dat-lai-mat-khau",
      name: "DatLaiMatKhau",
      component: DatLaiMatKhauView,
    },
  ],
});

export default router;
