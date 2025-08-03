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

// Import layout và các view cho trang Admin
import AdminLayout from "@/components/layouts/AdminLayout.vue";
import ThongKeView from "@/views/nhanvien/ThongKeView.vue";
import QuanLiDonHangView from "@/views/nhanvien/QuanLiDonHangView.vue";
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
    // === ADMIN ROUTES ===
    {
      path: "/admin",
      component: AdminLayout,
      // meta: { requiresAuth: true, role: 'admin' }, // Thêm để bảo vệ route
      children: [
        { path: "", redirect: "/admin/dashboard" }, // Redirect /admin to /admin/dashboard
        { path: "dashboard", name: "ThongKe", component: ThongKeView },
        { path: "don-hang", name: "DonHang", component: QuanLiDonHangView },
        // { path: 'shipping', name: 'AdminShipping', component: ShippingView },
        // { path: 'products', name: 'AdminProducts', component: ProductsView },
        // { path: 'categories', name: 'AdminCategories', component: CategoriesView },
        // { path: 'customers', name: 'AdminCustomers', component: CustomersView },
        // { path: 'staff', name: 'AdminStaff', component: StaffView },
        // ---
        // Để các route trên hoạt động, bạn cần tạo các component View tương ứng
        // Ví dụ: src/views/admin/DashboardView.vue
        // ---
      ],
    },
  ],
});

export default router;
