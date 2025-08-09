import { createRouter, createWebHistory } from "vue-router";
import { decodeJwtPayload } from "@/utils/jwt";

// --- General & Customer Views ---
import DangNhapView from "@/views/DangNhapView.vue";
import TrangChuView from "@/views/khachhang/TrangChuView.vue";
import KetQuaTimKiemView from "@/views/khachhang/KetQuaTimKiemView.vue";
import XemDanhMucView from "@/views/khachhang/XemDanhMucView.vue";
import ChiTietSanPhamView from "@/views/khachhang/ChiTietSanPhamView.vue";
import GioHangView from "@/views/khachhang/GioHangView.vue";
import CapNhatTaiKhoanView from "@/views/khachhang/CapNhatTaiKhoanView.vue";
import LichSuMuaHangView from "@/views/khachhang/LichSuMuaHangView.vue";
import DatLaiMatKhauView from "@/views/khachhang/DatLaiMatKhauView.vue";

// --- Admin/Employee Layout & Views ---
import AdminLayout from "@/components/layouts/AdminLayout.vue";
import ThongKeView from "@/views/nhanvien/ThongKeView.vue";
import QuanLiDonHangView from "@/views/nhanvien/QuanLiDonHangView.vue";
import QuanLiGiaoHangView from "@/views/nhanvien/QuanLiGiaoHangView.vue";
import QuanLiSanPhamView from "@/views/nhanvien/QuanLiSanPhamView.vue";
import QuanLiDanhMucView from "@/views/nhanvien/QuanLiDanhMucView.vue";
import QuanLiKhoView from "@/views/nhanvien/QuanLiKhoView.vue";
import QuanLiKhachHangView from "@/views/nhanvien/QuanLiKhachHangView.vue";
import QuanLiNhanVienView from "@/views/nhanvien/QuanLiNhanVienView.vue";

// --- Standalone Employee Views ---
import GiaoHangView from "@/views/nhanvien/GiaoHangView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      // Chuyển hướng từ đường dẫn gốc "/" đến trang chủ
      path: "/",
      redirect: "/trang-chu",
    },
    {
      path: "/dang-nhap",
      name: "DangNhap",
      component: DangNhapView,
      meta: { guest: true }, // Route chỉ dành cho khách (chưa đăng nhập)
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
      path: "/tai-khoan",
      name: "TaiKhoan",
      component: CapNhatTaiKhoanView,
      meta: { requiresAuth: true, roles: ["KHACH_HANG"] }, // Chỉ cho phép KHACH_HANG
    },
    {
      path: "/gio-hang",
      name: "GioHang",
      component: GioHangView,
      meta: { requiresAuth: true, roles: ["KHACH_HANG"] }, // Chỉ cho phép KHACH_HANG
    },
    {
      path: "/lich-su-mua-hang",
      name: "LichSuMuaHang",
      component: LichSuMuaHangView,
      meta: { requiresAuth: true, roles: ["KHACH_HANG"] }, // Chỉ cho phép KHACH_HANG
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
      meta: { requiresAuth: true, roles: ["ADMIN", "NHAN_VIEN"] }, // Chỉ cho phép ADMIN và NHAN_VIEN
      children: [
        { path: "", redirect: "/admin/dashboard" }, // Redirect /admin to /admin/dashboard
        { path: "dashboard", name: "ThongKe", component: ThongKeView },
        { path: "don-hang", name: "DonHang", component: QuanLiDonHangView },
        {
          path: "giao-hang",
          name: "QuanLiGiaoHang",
          component: QuanLiGiaoHangView,
        },
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
    // --- DYNAMIC ROUTES (PRODUCT/CATEGORY) ---
    // Phải được đặt ở cuối để không ghi đè các route tĩnh ở trên.
    // Sắp xếp từ cụ thể nhất đến chung chung nhất.
    {
      path: "/:danhMucCha/:danhMucCon/:maSanPham",
      name: "ChiTietSanPham",
      component: ChiTietSanPhamView,
    },
    {
      path: "/:danhMucCha/:danhMucCon",
      name: "DanhMucCon",
      component: XemDanhMucView,
    },
    {
      // Route này sẽ bắt các URL như /thoi-trang-nam, /dien-tu, v.v.
      // Nó phải là route động cuối cùng để không bắt các route như /gio-hang, /admin.
      path: "/:danhMucCha",
      name: "DanhMucCha",
      component: XemDanhMucView,
    },
  ],
});

/**
 * Canh gác điều hướng toàn cục (Global Navigation Guard)
 *
 * Hàm này sẽ được thực thi trước mỗi lần chuyển route.
 * Nó được dùng ở đây để bảo vệ các route yêu cầu xác thực.
 */
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem("access_token");
  const payload = token ? decodeJwtPayload(token) : null;
  if (payload) {
    // Log payload đã được giải mã để kiểm tra
    console.log("Decoded JWT Payload in Router Guard:", payload);
  }
  const userRole = payload?.role;

  const isGuestRoute = to.matched.some((record) => record.meta.guest);
  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth);
  const requiredRoles = to.matched.flatMap(
    (record) => record.meta.roles || []
  ) as string[];

  // Luồng 1: Đã đăng nhập nhưng vào trang guest (vd: /dang-nhap)
  // Chuyển hướng về trang chủ/dashboard tương ứng.
  if (token && isGuestRoute) {
    if (userRole === "ADMIN" || userRole === "NHAN_VIEN") {
      return next({ path: "/admin/dashboard" });
    }
    return next({ name: "TrangChu" });
  }

  // Luồng 2: Chưa đăng nhập nhưng vào trang cần xác thực
  if (!token && requiresAuth) {
    return next({ name: "DangNhap" });
  }

  // Luồng 3: Đã đăng nhập, kiểm tra vai trò cho các trang yêu cầu vai trò cụ thể
  if (token && requiredRoles.length > 0) {
    if (!userRole || !requiredRoles.includes(userRole)) {
      // Không có quyền -> chuyển hướng về trang phù hợp với vai trò
      console.warn(
        `Truy cập bị từ chối: Route ${
          to.path
        } yêu cầu vai trò ${requiredRoles.join(
          ", "
        )}, nhưng người dùng có vai trò ${userRole || "không xác định"}.`
      );
      if (userRole === "ADMIN" || userRole === "NHAN_VIEN") {
        return next({ path: "/admin/dashboard" });
      }
      return next({ name: "TrangChu" });
    }
  }

  // Luồng 4: Các trường hợp còn lại (trang công khai, hoặc đã đăng nhập và có quyền)
  return next();
});

export default router;
