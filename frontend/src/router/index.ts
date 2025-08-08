import { createRouter, createWebHistory } from "vue-router";
import TrangChuView from "../views/khachhang/TrangChuView.vue";
import DangNhapView from "../views/DangNhapView.vue";
import KetQuaTimKiemView from "../views/khachhang/KetQuaTimKiemView.vue";
import XemDanhMucView from "@/views/khachhang/XemDanhMucView.vue";
import CapNhatTaiKhoanView from "../views/khachhang/CapNhatTaiKhoanView.vue";
import GioHangView from "@/views/khachhang/GioHangView.vue";
import ChiTietSanPhamView from "@/views/khachhang/ChiTietSanPhamView.vue";
import LichSuMuaHangView from "@/views/khachhang/LichSuMuaHangView.vue";
import DatLaiMatKhauView from "@/views/khachhang/DatLaiMatKhauView.vue";

// Import layout và các view cho trang Admin
import AdminLayout from "@/components/layouts/AdminLayout.vue";
import ThongKeView from "@/views/nhanvien/ThongKeView.vue";
import QuanLiDonHangView from "@/views/nhanvien/QuanLiDonHangView.vue";
import QuanLiGiaoHangView from "@/views/nhanvien/QuanLiGiaoHangView.vue";
import QuanLiSanPhamView from "@/views/nhanvien/QuanLiSanPhamView.vue";
import QuanLiDanhMucView from "@/views/nhanvien/QuanLiDanhMucView.vue";
import QuanLiKhachHangView from "@/views/nhanvien/QuanLiKhachHangView.vue";
import QuanLiKhoView from "@/views/nhanvien/QuanLiKhoView.vue";
import QuanLiNhanVienView from "@/views/nhanvien/QuanLiNhanVienView.vue";

import GiaoHangView from "@/views/nhanvien/GiaoHangView.vue";
// import OrdersView from "@/views/admin/OrdersView.vue";

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
      path: "/:danhMucCha",
      name: "DanhMucCha",
      component: XemDanhMucView,
    },
    {
      path: "/:danhMucCha/:danhMucCon",
      name: "DanhMucCon",
      component: XemDanhMucView,
    },
    {
      path: "/:danhMucCha/:danhMucCon/:maSanPham",
      name: "ChiTietSanPham",
      component: ChiTietSanPhamView,
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
      path: "/lich-su-mua-hang",
      name: "LichSuMuaHang",
      component: LichSuMuaHangView,
    },
    {
      path: "/dat-lai-mat-khau",
      name: "DatLaiMatKhau",
      component: DatLaiMatKhauView,
    },
    // === ADMIN ROUTES ===
    {
      path: "/admin",
      component: AdminLayout,
      // meta: { requiresAuth: true, role: 'admin' }, // Thêm để bảo vệ route
      children: [
        { path: "", redirect: "/admin/dashboard" }, // Redirect /admin to /admin/dashboard
        { path: "dashboard", name: "ThongKe", component: ThongKeView },
        { path: "don-hang", name: "DonHang", component: QuanLiDonHangView },
        { path: "giao-hang", name: "GiaoHang", component: QuanLiGiaoHangView },
        { path: "san-pham", name: "SanPham", component: QuanLiSanPhamView },
        { path: "danh-muc", name: "DanhMuc", component: QuanLiDanhMucView },
        { path: "kho-hang", name: "KhoHang", component: QuanLiKhoView },
        {
          path: "khach-hang",
          name: "KhachHang",
          component: QuanLiKhachHangView,
        },
        {
          path: "nhan-vien",
          name: "NhanVien",
          component: QuanLiNhanVienView,
        },
      ],
    },
    {
      path: "/giao-hang",
      name: "GiaoHang",
      component: GiaoHangView,
    },
  ],
});

export default router;
