<template>
  <CustomerWithNav>
    <div class="max-w-7xl mx-auto p-4 md:p-6">
      <!-- Loading State -->
      <div v-if="isLoading" class="text-center py-20">
        <div
          class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"
        ></div>
        <p class="mt-4 text-gray-600">Đang tải dữ liệu sản phẩm...</p>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="text-center py-20">
        <p class="text-xl text-red-500 font-semibold">{{ error }}</p>
        <p v-if="isRedirecting" class="mt-2 text-gray-600">
          Sẽ tự động chuyển về trang chủ sau 3 giây...
        </p>
        <router-link
          v-else
          to="/"
          class="mt-4 inline-block px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
        >
          Quay về Trang chủ
        </router-link>
      </div>

      <!-- Content -->
      <div v-else-if="sanPham" class="space-y-6">
        <!-- Breadcrumb -->
        <Breadcrumbs :items="sanPham.breadcrumbs" />

        <div class="grid grid-cols-1 lg:grid-cols-10 gap-8">
          <!-- Image Gallery (Left - 3/10) -->
          <div class="lg:col-span-3">
            <div
              class="relative w-full aspect-square rounded-lg overflow-hidden shadow-sm mb-3"
            >
              <img
                :src="selectedImage"
                :alt="sanPham.tenSanPham"
                class="w-full h-full object-cover transition-opacity duration-300"
                :key="selectedImage"
              />
              <!-- Labels container -->
              <div
                class="absolute top-3 right-3 flex flex-col items-end gap-y-2"
              >
                <span
                  v-if="sanPham.nhan"
                  class="bg-red-500 text-white text-xs font-bold px-2.5 py-1 rounded-full shadow-md"
                >
                  {{ sanPham.nhan.label }}
                </span>
                <span
                  v-if="sanPham.banChay"
                  class="bg-orange-400 text-white text-xs font-bold px-2.5 py-1 rounded-full shadow-md"
                >
                  Bán chạy
                </span>
              </div>
            </div>
            <div class="flex space-x-2 overflow-x-auto overflow-y-hidden p-2">
              <img
                v-for="anh in sanPham.anhMinhHoas"
                :key="anh.duongDan"
                :src="anh.duongDan"
                :alt="`Thumbnail ${sanPham.tenSanPham}`"
                @click="selectedImage = anh.duongDan"
                class="w-20 h-20 object-cover rounded-md cursor-pointer border-2 transition-all"
                :class="
                  selectedImage === anh.duongDan
                    ? 'border-blue-500 scale-105'
                    : 'border-transparent hover:border-gray-300'
                "
              />
            </div>
          </div>

          <!-- Product Info (Middle - 4/10) -->
          <div class="lg:col-span-4 flex flex-col">
            <h1 class="text-3xl font-bold text-gray-800">
              {{ sanPham.tenSanPham }}
            </h1>
            <p class="text-2xl font-bold text-red-600 mt-2">
              {{ dinhDangTien(sanPham.donGia) }}
            </p>

            <div class="mt-4 pt-4 border-t space-y-3 text-gray-600">
              <div class="flex items-center gap-3">
                <i
                  class="fi fi-rr-balance-scale text-blue-500 w-5 text-center"
                ></i>
                <span
                  >Đơn vị:
                  <span class="font-semibold text-gray-800">{{
                    sanPham.donVi
                  }}</span></span
                >
              </div>
              <div class="flex items-center gap-3">
                <i class="fi fi-rr-factory text-blue-500 w-5 text-center"></i>
                <span
                  >Nhà sản xuất:
                  <span class="font-semibold text-gray-800">{{
                    sanPham.nhaSanXuat
                  }}</span></span
                >
              </div>
              <div class="flex items-start gap-3">
                <i class="fi fi-rr-info text-blue-500 w-5 text-center mt-1"></i>
                <p>
                  Mô tả: <span class="text-gray-800">{{ sanPham.moTa }}</span>
                </p>
              </div>
              <div v-if="sanPham.ghiChu" class="flex items-start gap-3">
                <i
                  class="fi fi-rr-exclamation text-orange-500 w-5 text-center mt-1"
                ></i>
                <p>
                  Ghi chú:
                  <span class="text-gray-800">{{ sanPham.ghiChu }}</span>
                </p>
              </div>
            </div>
          </div>

          <!-- Thêm vào giỏ -->
          <div
            class="lg:col-span-3 rounded-lg bg-blue-50 p-5 h-fit shadow-sm space-y-5"
          >
            <h2 class="text-lg font-semibold border-b pb-3">
              🛒 Thêm vào giỏ hàng
            </h2>

            <!-- Số lượng -->
            <div class="flex items-center justify-between">
              <label class="font-medium text-gray-700">Số lượng:</label>
              <div class="flex items-center gap-3">
                <button
                  @click="giamSoLuong"
                  :disabled="isOutOfStock"
                  class="w-9 h-9 flex items-center justify-center rounded-full bg-blue-200 text-blue-800 text-lg hover:bg-blue-300 disabled:opacity-50 disabled:cursor-not-allowed transition cursor-pointer"
                >
                  −
                </button>
                <span
                  class="min-w-[32px] text-center font-semibold text-gray-800"
                  >{{ soLuong }}</span
                >
                <button
                  @click="tangSoLuong"
                  :disabled="isOutOfStock"
                  class="w-9 h-9 flex items-center justify-center rounded-full bg-blue-200 text-blue-800 text-lg hover:bg-blue-300 disabled:opacity-50 disabled:cursor-not-allowed transition cursor-pointer"
                >
                  +
                </button>
              </div>
            </div>

            <!-- Tạm tính -->
            <div class="border-t pt-3">
              <p class="font-medium text-gray-700">Tạm tính:</p>
              <p class="text-2xl font-bold text-red-600">
                {{ dinhDangTien(tamTinh) }}
              </p>
            </div>

            <!-- Nút thêm vào giỏ -->
            <button
              class="cursor-pointer w-full bg-[linear-gradient(135deg,_#1E88E5,_#1565C0)] hover:bg-[linear-gradient(135deg,_#42A5F5,_#1E88E5)] text-white py-3 rounded-lg font-medium flex items-center justify-center gap-2 transition disabled:bg-gray-400 disabled:cursor-not-allowed"
              @click="themVaoGio"
              :disabled="isOutOfStock"
            >
              <i class="fi fi-rr-shopping-cart-add"></i>
              <span v-if="!isOutOfStock">Thêm vào giỏ</span>
              <span v-else>Hết hàng</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </CustomerWithNav>
</template>

<script setup lang="ts">
import CustomerWithNav from "@/components/layouts/CustomerWithNav.vue";
import Breadcrumbs from "@/components/base/Breadcrumbs.vue";
import { useToast } from "@/composables/useToast";
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { getChiTietSanPham } from "@/service/sanpham.service";
import { themVaoGioHang } from "@/service/giohang.service";
import { useCartStore } from "@/store/giohang.store";
import { useAuthStore } from "@/store/auth.store";
import {
  TrangThaiTonKhoKey,
  type ChiTietSanPhamResponse,
} from "@/types/sanpham.types";
import { AxiosError } from "axios";

const route = useRoute();
const router = useRouter();
const { showToast } = useToast();
const cartStore = useCartStore();
const authStore = useAuthStore();

const sanPham = ref<ChiTietSanPhamResponse | null>(null);
const isLoading = ref(true);
const error = ref<string | null>(null);
const isRedirecting = ref(false);

const selectedImage = ref("");

const isOutOfStock = computed(
  () => sanPham.value?.trangThaiTonKho.value === TrangThaiTonKhoKey.HET_HANG
);

const dinhDangTien = (val: number) => val.toLocaleString("vi-VN") + " VND";

const fetchSanPham = async () => {
  const maSanPham = Number(route.params.maSanPham);
  if (isNaN(maSanPham)) {
    error.value = "Mã sản phẩm không hợp lệ.";
    isLoading.value = false;
    return;
  }

  try {
    const data = await getChiTietSanPham(maSanPham);

    sanPham.value = data;
    // Set ảnh chính làm ảnh được chọn ban đầu
    selectedImage.value =
      data.anhMinhHoas.find((a) => a.laAnhChinh)?.duongDan ||
      data.anhMinhHoas[0]?.duongDan ||
      "";
  } catch (err) {
    if (err instanceof AxiosError && err.response?.status === 404) {
      error.value = "Sản phẩm không tồn tại hoặc đã bị xóa.";
      isRedirecting.value = true;
      setTimeout(() => {
        router.push("/");
      }, 3000);
    } else {
      console.error("Lỗi khi tải chi tiết sản phẩm:", err);
      error.value = "Không thể tải dữ liệu sản phẩm. Vui lòng thử lại.";
    }
  } finally {
    isLoading.value = false;
  }
};

const soLuong = ref(1);

const tamTinh = computed(() =>
  sanPham.value ? sanPham.value.donGia * soLuong.value : 0
);

const tangSoLuong = () => {
  if (!isOutOfStock.value) soLuong.value++;
};
const giamSoLuong = () => {
  if (soLuong.value > 1 && !isOutOfStock.value) soLuong.value--;
};

const themVaoGio = async () => {
  if (isOutOfStock.value) {
    showToast({ thongBao: "Sản phẩm đã hết hàng!", loai: "loi" });
    return;
  }
  if (!sanPham.value) return;

  // Kiểm tra nếu người dùng chưa đăng nhập
  if (!authStore.isLoggedIn) {
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

  try {
    const response = await themVaoGioHang({
      maSanPham: sanPham.value.maSanPham,
      soLuong: soLuong.value,
    });
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

onMounted(fetchSanPham);
</script>
