import { createRouter, createWebHistory } from "vue-router";
import TrangChuView from "../views/TrangChuView.vue";
import DangNhapView from "../views/DangNhapView.vue";
import KetQuaTimKiemView from "../views/KetQuaTimKiemView.vue";
import XemDanhMucView from "../views/XemDanhMucView.vue";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/dang-nhap",
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
      path: "/danh-muc/:categoryName",
      name: "XemDanhMuc",
      component: XemDanhMucView,
    },
  ],
});

export default router;
