<template>
  <CustomerWithNav>
    <ThemVaoGioHangModal
      :visible="isAddToCartModalVisible"
      :sanPham="selectedProduct"
      @close="closeAddToCartModal"
      @add="handleAddToCart"
    />

    <div class="max-w-7xl mx-auto px-4 py-6">
      <!-- Breadcrumbs -->
      <Breadcrumbs :items="breadcrumbItems" class="mb-4" />

      <!-- Mobile Filter Toggle -->
      <div class="lg:hidden mb-4">
        <button
          @click="toggleMobileFilter"
          class="w-full px-4 py-2 bg-white border rounded-md flex justify-between items-center"
        >
          <span class="font-semibold">Bộ lọc</span>
          <i
            :class="
              isFilterVisibleOnMobile
                ? 'fi-rr-angle-small-up'
                : 'fi-rr-angle-small-down'
            "
            class="text-xl flex items-center"
          ></i>
        </button>
      </div>

      <div class="flex flex-col lg:flex-row gap-6">
        <!-- Filters -->
        <aside
          class="w-full lg:w-64 shrink-0"
          :class="isFilterVisibleOnMobile ? 'block' : 'hidden lg:block'"
        >
          <AccordionSanPham :danhMucCon="dsDanhMucCon" :nhaSanXuat="dsNSX" />
        </aside>

        <!-- Product Grid -->
        <main class="flex-1">
          <header class="mb-4">
            <h1 class="text-xl font-bold">Danh mục: {{ danhMucCon }}</h1>
            <p class="text-sm text-gray-600">
              Tìm thấy {{ productsInCategory.length }} sản phẩm.
            </p>
          </header>

          <GridSanPham
            :dsSanPham="paginatedProducts"
            :trangHienTai="currentPage"
            :tongSoTrang="totalPages"
            :trangBatDau="startPage"
            :trangKetThuc="endPage"
            :soTrangHienThi="pagesToShow"
            @them-vao-gio-hang="showAddToCartModal"
            @chuyen-trang="(trang:number) => (currentPage = trang)"
          />
        </main>
      </div>
    </div>
  </CustomerWithNav>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { useRoute } from "vue-router";
import CustomerWithNav from "@/components/layouts/CustomerWithNav.vue";
import AccordionSanPham from "@/components/base/AccordionSanPham.vue";
import GridSanPham from "@/components/base/GridSanPham.vue";
import Breadcrumbs from "@/components/base/Breadcrumbs.vue";
import ThemVaoGioHangModal from "@/components/xemsanpham/ThemVaoGioHangModal.vue";
import { useBreadcrumbs } from "@/composables/useBreadcrumbs";
import { useToast } from "@/composables/useToast";
import type { SanPham } from "@/types/SanPham";

const route = useRoute();
const danhMucCha = computed(() => route.params.danhMucCha as string);
const danhMucCon = computed(() => route.params.danhMucCon as string);
const { showToast } = useToast();

const isFilterVisibleOnMobile = ref(false);
const toggleMobileFilter = () => {
  isFilterVisibleOnMobile.value = !isFilterVisibleOnMobile.value;
};

const dsSanPhamMau = ref<SanPham[]>([]);
const dsTenSPMau = [
  "Áo Thun",
  "Quần Jean",
  "Váy Đầm",
  "Giày Sneaker",
  "Túi Xách",
  "Đồng Hồ",
  "Kính Mát",
  "Mũ Lưỡi Trai",
];
const dsNSX = ["Nike", "Adidas", "Puma", "Gucci", "LV", "Zara", "H&M"];
const dsDanhMucCha = ["Thời trang", "Phụ kiện", "Giày dép"];
const dsDanhMucCon = ["Áo", "Quần", "Giày", "Túi", "Đồng hồ", "Kính"];

for (let i = 1; i <= 1000; i++) {
  const name = dsTenSPMau[i % dsTenSPMau.length];
  dsSanPhamMau.value.push({
    maSanPham: i,
    tenSanPham: `${name} mẫu ${i}`,
    donGia: Math.floor(Math.random() * 1000) * 1000 + 100000,
    anhMinhHoa: [
      `https://picsum.photos/300?random=${i}`,
      `https://picsum.photos/300?random=${i + 1000}`,
    ],
    nhan: i % 5 === 0 ? "Bán chạy" : i % 3 === 0 ? "Mới" : undefined,
    donVi: "Cái",
    nhaSanXuat: dsNSX[i % dsNSX.length],
    danhMucCha: dsDanhMucCha[i % dsDanhMucCha.length],
    danhMucCon: dsDanhMucCon[i % dsDanhMucCon.length],
    trangThai: "Đang bán",
  });
}

const productsInCategory = computed(() => {
  return dsSanPhamMau.value.filter(
    (p) =>
      p.danhMucCha.toLowerCase() === danhMucCha.value.toLowerCase() &&
      p.danhMucCon.toLowerCase() === danhMucCon.value.toLowerCase()
  );
});

const pageSize = 40;
const currentPage = ref(0);
const totalPages = computed(() =>
  Math.ceil(productsInCategory.value.length / pageSize)
);
const maxVisiblePages = 5;

const startPage = computed(() => {
  const mid = Math.floor(maxVisiblePages / 2);
  if (totalPages.value <= maxVisiblePages) return 1;
  if (currentPage.value + 1 <= mid + 1) return 1;
  if (currentPage.value + 1 >= totalPages.value - mid)
    return totalPages.value - maxVisiblePages + 1;
  return currentPage.value - mid + 1;
});

const endPage = computed(() => {
  return Math.min(startPage.value + maxVisiblePages - 1, totalPages.value);
});

const pagesToShow = computed(() => {
  const pages = [];
  for (let i = startPage.value; i <= endPage.value; i++) {
    pages.push(i);
  }
  return pages;
});

const paginatedProducts = computed(() => {
  return productsInCategory.value.slice(
    currentPage.value * pageSize,
    (currentPage.value + 1) * pageSize
  );
});

const isAddToCartModalVisible = ref(false);
const selectedProduct = ref<SanPham | null>(null);

function showAddToCartModal(sanPham: SanPham) {
  selectedProduct.value = sanPham;
  isAddToCartModalVisible.value = true;
}

function closeAddToCartModal() {
  isAddToCartModalVisible.value = false;
  selectedProduct.value = null;
}

function handleAddToCart(payload: { sanPham: SanPham; soLuong: number }) {
  closeAddToCartModal();
  showToast({
    thongBao: `Đã thêm "${payload.soLuong} ${payload.sanPham.tenSanPham}" vào giỏ hàng!`,
    loai: "thanhCong",
  });
}

const tailBreadcrumbs = computed(() => [
  { text: danhMucCha.value, to: `/${danhMucCha.value}` },
  { text: danhMucCon.value },
]);
const breadcrumbItems = useBreadcrumbs(tailBreadcrumbs);
</script>
