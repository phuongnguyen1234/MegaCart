<template>
  <CustomerWithNav>
    <ThemVaoGioHangModal
      :visible="isAddToCartModalVisible"
      :sanPham="selectedProduct"
      @close="closeAddToCartModal"
      @add="handleAddToCart"
    />
    <div class="max-w-7xl mx-auto px-4 py-6">
      <!-- Nút Lọc cho di động -->
      <div class="lg:hidden mb-4">
        <button
          @click="isFilterVisibleOnMobile = !isFilterVisibleOnMobile"
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
        <!-- Bộ lọc -->
        <aside
          class="w-full lg:w-64 shrink-0"
          :class="isFilterVisibleOnMobile ? 'block' : 'hidden lg:block'"
        >
          <AccordionSanPham :categories="dsDanhMuc" :manufacturers="dsNSX" />
        </aside>

        <!-- Nội dung chính -->
        <main class="flex-1">
          <template v-if="productsByKeyword.length > 0">
            <h1 class="text-xl font-bold mb-2">
              Kết quả tìm kiếm cho từ khóa "<span class="text-blue-600">{{
                tuKhoa
              }}</span
              >"
            </h1>
            <p class="text-sm text-gray-600 mb-4">
              Tìm thấy {{ productsByKeyword.length }} sản phẩm.
            </p>

            <GridSanPham
              :ds-san-pham="paginatedProducts"
              @them-vao-gio-hang="showAddToCartModal"
            />
            <PhanTrang
              v-model:trangHienTai="trangHienTai"
              :tong-so-trang="tongSoTrang"
            />
          </template>

          <!-- Không có kết quả -->
          <template v-else>
            <div class="text-center mt-10">
              <img
                src="https://picsum.photos/200?grayscale"
                class="mx-auto mb-6"
              />
              <h2 class="text-xl font-semibold text-gray-700 mb-2">
                Không tìm thấy sản phẩm nào có từ khóa {{ tuKhoa }}
              </h2>
              <p class="text-gray-500 mb-4">
                Vui lòng kiểm tra lại nhập liệu hoặc thử một từ khóa khác.
              </p>
              <router-link
                to="/"
                class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
              >
                Quay về Trang chủ
              </router-link>
            </div>
          </template>
        </main>
      </div>
    </div>
  </CustomerWithNav>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { useRoute } from "vue-router";
import { useToast } from "@/composables/useToast";
import CustomerWithNav from "@/components/layouts/CustomerWithNav.vue";
import AccordionSanPham from "@/components/base/AccordionSanPham.vue";
import ThemVaoGioHangModal from "@/components/base/modals/ThemVaoGioHangModal.vue";
import GridSanPham from "@/components/base/GridSanPham.vue";
import PhanTrang from "@/components/base/PhanTrang.vue";
import type { SanPham } from "@/types/SanPham";

// Lấy từ khóa tìm kiếm từ query
const route = useRoute();
const { showToast } = useToast();
const tuKhoa = computed(() => (route.query.q as string) || "");

const isFilterVisibleOnMobile = ref(false);

// Giả lập danh sách sản phẩm
const dsSanPhamMau = ref<SanPham[]>([]);
const dsTenSanPham = [
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
const dsDanhMuc = ["Quần áo", "Phụ kiện", "Giày dép"];

// Tạo 100 sản phẩm giả để demo
for (let i = 1; i <= 1000; i++) {
  const name = dsTenSanPham[i % dsTenSanPham.length];
  dsSanPhamMau.value.push({
    maSanPham: i,
    tenSanPham: `${name} mẫu ${i}`,
    donGia: Math.floor(Math.random() * 1000) * 1000 + 100000,
    anhMinhHoa: [`https://picsum.photos/300?random=${i}`],
    nhan: i % 5 === 0 ? "Bán chạy" : i % 3 === 0 ? "Mới" : undefined,
    donVi: "Cái",
    nhaSanXuat: dsNSX[i % dsNSX.length],
    danhMucCha: dsDanhMuc[i % dsDanhMuc.length],
    danhMucCon: dsDanhMuc[i % dsDanhMuc.length],
  });
}

// Lọc sản phẩm theo từ khóa (chưa phân trang)
const productsByKeyword = computed(() => {
  if (!tuKhoa.value) {
    return dsSanPhamMau.value;
  }
  return dsSanPhamMau.value.filter((product) =>
    product.tenSanPham.toLowerCase().includes(tuKhoa.value.toLowerCase())
  );
});

// Phân trang
const soSPMoiTrang = 40; // Hiển thị 40 sản phẩm mỗi trang (tương đương 10 hàng x 4 cột)
const trangHienTai = ref(0);
const tongSoTrang = computed(() =>
  Math.ceil(productsByKeyword.value.length / soSPMoiTrang)
);

// Lấy sản phẩm cho trang hiện tại
const paginatedProducts = computed(() => {
  return productsByKeyword.value.slice(
    trangHienTai.value * soSPMoiTrang,
    (trangHienTai.value + 1) * soSPMoiTrang
  );
});

// Xử lý ThemVaoGioHang Modal
const isAddToCartModalVisible = ref(false);
const selectedProduct = ref<SanPham | null>(null);

function showAddToCartModal(product: SanPham) {
  selectedProduct.value = product;
  isAddToCartModalVisible.value = true;
}

function closeAddToCartModal() {
  isAddToCartModalVisible.value = false;
  selectedProduct.value = null;
}

function handleAddToCart(payload: { sanPham: SanPham; soLuong: number }) {
  console.log(
    "🛒 Thêm sản phẩm vào giỏ:",
    payload.sanPham,
    "Số lượng:",
    payload.soLuong
  );
  closeAddToCartModal();
  showToast({
    thongBao: `Đã thêm "${payload.soLuong} ${payload.sanPham.tenSanPham}" vào giỏ hàng!`,
    loai: "thanhCong",
  });
}
</script>
