import { createRouter, createWebHistory } from "vue-router";
import { decodeJwtPayload } from "@/utils/jwt";
import type { JwtPayload, NhanVienJwtPayload } from "@/types/api.types";
import { VaiTroKey, ViTriNhanVienKey } from "@/types/taikhoan.types";
// --- General & Customer Views ---
import DangNhapView from "@/views/DangNhapView.vue";
import TrangChuView from "@/views/khachhang/TrangChuView.vue";
import XemDanhSachSanPhamView from "@/views/khachhang/XemDanhSachSanPhamView.vue";
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
      meta: { customerOnly: true },
    },
    {
      // Đường dẫn cho trang kết quả tìm kiếm, ví dụ: /tim-kiem?q=laptop
      path: "/tim-kiem",
      name: "KetQuaTimKiem",
      component: XemDanhSachSanPhamView,
      meta: { customerOnly: true },
    },
    {
      path: "/tai-khoan",
      name: "TaiKhoan",
      component: CapNhatTaiKhoanView,
      meta: { requiresAuth: true, roles: [VaiTroKey.KHACH_HANG] },
    },
    {
      path: "/gio-hang",
      name: "GioHang",
      component: GioHangView,
      meta: { requiresAuth: true, roles: [VaiTroKey.KHACH_HANG] },
    },
    {
      path: "/lich-su-mua-hang",
      name: "LichSuMuaHang",
      component: LichSuMuaHangView,
      meta: { requiresAuth: true, roles: [VaiTroKey.KHACH_HANG] },
    },
    {
      path: "/dat-lai-mat-khau",
      name: "DatLaiMatKhau",
      component: DatLaiMatKhauView,
    },
    {
      // Đường dẫn chi tiết sản phẩm, ví dụ: /san-pham/123
      path: "/san-pham/:maSanPham",
      name: "ChiTietSanPham",
      component: ChiTietSanPhamView,
      meta: { customerOnly: true },
    },
    {
      // Đường dẫn cho các sản phẩm có nhãn "Mới"
      path: "/san-pham-moi",
      name: "SanPhamMoi",
      component: XemDanhSachSanPhamView,
      meta: { customerOnly: true },
    },
    {
      // Đường dẫn cho các sản phẩm bán chạy nhất
      path: "/ban-chay",
      name: "BanChay",
      component: XemDanhSachSanPhamView,
      meta: { customerOnly: true },
    },
    // === MANAGEMENT ROUTES ===
    {
      path: "/quan-ly",
      component: AdminLayout,
      // Chỉ cho phép người dùng có vai trò NHAN_VIEN (bao gồm cả ADMIN và các vị trí khác)
      // truy cập vào các route trong layout này.
      meta: {
        requiresAuth: true,
        roles: [VaiTroKey.NHAN_VIEN, VaiTroKey.ADMIN],
      },
      children: [
        {
          path: "",
          redirect: () => {
            // Điều hướng động dựa trên vai trò và vị trí của nhân viên
            const token = localStorage.getItem("access_token");
            const payload = token
              ? (decodeJwtPayload(token) as JwtPayload | null)
              : null;

            if (payload?.role === VaiTroKey.ADMIN) {
              return { path: "/quan-ly/dashboard" };
            }

            if (payload?.role === VaiTroKey.NHAN_VIEN) {
              const nhanVienPayload = payload as NhanVienJwtPayload;
              switch (nhanVienPayload.viTri) {
                case ViTriNhanVienKey.NHAN_VIEN_QUAN_LI_KHO:
                  return { path: "/quan-ly/kho-hang" };
                case ViTriNhanVienKey.NHAN_VIEN_BAN_HANG:
                  return { path: "/quan-ly/don-hang" };
                // Nhân viên giao hàng đã được xử lý ở beforeEach, không nên vào /quan-ly.
                // Đây là một fallback an toàn.
                case ViTriNhanVienKey.NHAN_VIEN_GIAO_HANG:
                  return { name: "GiaoHang" };
                default:
                  // Fallback cho các vị trí nhân viên khác (nếu có)
                  return { path: "/quan-ly/don-hang" };
              }
            }

            // Fallback cuối cùng nếu không xác định được vai trò -> về trang đăng nhập
            return { name: "DangNhap" };
          },
        },
        {
          path: "dashboard",
          name: "ThongKe",
          component: ThongKeView,
          meta: { roles: [VaiTroKey.ADMIN] }, // Chỉ Admin mới được xem Thống kê
        },
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
      component: AdminLayout,
      meta: {
        requiresAuth: true,
        roles: [VaiTroKey.NHAN_VIEN], // Chỉ cho phép nhân viên
        // Yêu cầu vị trí cụ thể là nhân viên giao hàng
        requiredPosition: [ViTriNhanVienKey.NHAN_VIEN_GIAO_HANG],
        // Cung cấp thông tin cho AdminLayout để hiển thị menu tối giản
        menuVariant: "minimal",
      },
      children: [
        {
          path: "",
          name: "GiaoHang",
          component: GiaoHangView,
        },
      ],
    },
    // --- DYNAMIC ROUTES (PRODUCT/CATEGORY) ---
    // Phải được đặt ở cuối để không ghi đè các route tĩnh ở trên.
    // Sắp xếp từ cụ thể nhất đến chung chung nhất.
    {
      // Route này sẽ bắt các URL như /thoi-trang-nam, /dien-tu, v.v.
      // Nó phải là route động cuối cùng để không bắt các route như /gio-hang, /admin.
      path: "/danh-muc/:danhMucCha/:danhMucCon?", // danhMucCon là optional
      name: "XemDanhMuc",
      component: XemDanhSachSanPhamView,
      meta: { customerOnly: true },
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
  const payload = token ? (decodeJwtPayload(token) as JwtPayload | null) : null;
  if (payload) {
    // console.log("Decoded JWT Payload in Router Guard:", payload); // Bật khi cần debug
  }
  const userRole = payload?.role;

  const isGuestRoute = to.matched.some((record) => record.meta.guest);
  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth);
  const requiredRoles = to.matched.flatMap(
    (record) => record.meta.roles || []
  ) as VaiTroKey[];
  const requiredPositions = to.matched.flatMap(
    (record) => (record.meta.requiredPosition as ViTriNhanVienKey[]) || []
  );
  const isCustomerOnlyRoute = to.matched.some(
    (record) => record.meta.customerOnly
  );

  // Tạo biến cờ để code dễ đọc hơn
  // "Staff" bao gồm cả Nhân viên và Admin.
  const isStaff =
    userRole === VaiTroKey.NHAN_VIEN || userRole === VaiTroKey.ADMIN;

  // Luồng 1: Đã đăng nhập nhưng vào trang guest (vd: /dang-nhap)
  // Chuyển hướng về trang chủ/dashboard tương ứng.
  if (token && isGuestRoute) {
    if (isStaff) {
      return next({ path: "/quan-ly" }); // Để /admin tự điều hướng
    }
    return next({ name: "TrangChu" });
  }

  // Luồng 1.5 (Mới): Admin/Nhân viên đã đăng nhập và cố gắng truy cập trang chỉ dành cho khách hàng.
  // Chuyển hướng họ về trang dashboard.
  if (token && isStaff && isCustomerOnlyRoute) {
    return next({ path: "/quan-ly" }); // Để /admin tự điều hướng
  }

  // Luồng 2: Chưa đăng nhập nhưng vào trang cần xác thực
  if (!token && requiresAuth) {
    // Cải tiến: Lưu lại trang đích để chuyển hướng sau khi đăng nhập thành công
    return next({ name: "DangNhap", query: { redirect: to.fullPath } });
  }

  // Luồng 3: Đã đăng nhập, kiểm tra quyền truy cập (vai trò và vị trí)
  if (token && requiresAuth) {
    // A. Kiểm tra vai trò (Role Check)
    // Người dùng phải có vai trò được phép cho MỌI cấp route (từ cha đến con).
    // Sử dụng .every() để đảm bảo tất cả các điều kiện đều đúng.
    const isRoleAuthorized = to.matched.every((record) => {
      const roles = record.meta.roles as VaiTroKey[] | undefined;
      // Nếu route không định nghĩa vai trò, cho qua ở cấp này
      if (!roles || roles.length === 0) {
        return true;
      }
      // Nếu có định nghĩa, vai trò người dùng phải nằm trong danh sách
      return userRole ? roles.includes(userRole) : false;
    });

    if (!isRoleAuthorized) {
      // Không có quyền truy cập dựa trên vai trò
      console.warn(
        `Truy cập bị từ chối: Route ${
          to.path
        } yêu cầu quyền mà người dùng (vai trò: ${
          userRole || "không xác định"
        }) không có.`
      );
      // Chuyển hướng về trang an toàn
      if (isStaff) {
        return next({ path: "/quan-ly" }); // Để /quan-ly tự điều hướng
      }
      return next({ name: "TrangChu" });
    }

    // B. Vai trò đã hợp lệ, kiểm tra các điều kiện phụ (Vị trí, etc.)
    const userPosition = (payload as NhanVienJwtPayload)?.viTri;

    // LUỒNG 3.1: Xử lý trường hợp đặc biệt cho nhân viên giao hàng.
    // Họ không được phép truy cập vào bất kỳ trang nào trong khu vực /admin.
    if (
      userPosition === ViTriNhanVienKey.NHAN_VIEN_GIAO_HANG &&
      to.path.startsWith("/quan-ly")
    ) {
      return next({ name: "GiaoHang" });
    }

    // LUỒNG 3.2: Kiểm tra vị trí cụ thể nếu route yêu cầu.
    // Áp dụng cho các route như /giao-hang.
    if (requiredPositions.length > 0 && userRole === VaiTroKey.NHAN_VIEN) {
      const hasRequiredPosition = userPosition
        ? requiredPositions.includes(userPosition)
        : false;

      if (!hasRequiredPosition) {
        // Có vai trò Nhân viên nhưng không đúng vị trí -> về trang an toàn
        return next({ path: "/quan-ly" }); // Để /quan-ly tự điều hướng
      }
    }
  }

  // Luồng 4: Các trường hợp còn lại (trang công khai, hoặc đã đăng nhập và có quyền)
  return next();
});

export default router;
