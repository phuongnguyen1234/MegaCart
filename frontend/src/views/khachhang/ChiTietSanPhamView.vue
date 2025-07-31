<template>
  <CustomerWithNav>
    <div class="max-w-6xl mx-auto p-4">
      <!-- Breadcrumb -->
      <Breadcrumbs :items="breadcrumbs" class="mb-4" />

      <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
        <!-- Hình ảnh + nhãn -->
        <div class="relative w-full aspect-square border overflow-hidden">
          <img
            :src="sanPham.hinhAnh"
            alt="Ảnh sản phẩm"
            class="w-full h-full object-cover"
          />
          <span
            v-if="sanPham.nhan"
            class="absolute top-2 right-2 bg-red-600 text-white text-xs font-semibold px-2 py-1 rounded"
          >
            {{ sanPham.nhan }}
          </span>
        </div>

        <!-- Thông tin chi tiết -->
        <div class="md:col-span-1 md:col-start-2 flex flex-col gap-3">
          <div class="text-2xl font-semibold">{{ sanPham.ten }}</div>
          <div class="text-xl font-bold text-red-600">
            {{ dinhDangTien(sanPham.donGia) }}
          </div>

          <div class="flex items-center gap-2">
            <i class="fi fi-rr-balance-scale"></i>
            <span>{{ sanPham.donVi }}</span>
          </div>

          <div class="flex items-center gap-2">
            <i class="fi fi-rr-factory"></i>
            <span>{{ sanPham.nhaSanXuat }}</span>
          </div>

          <div class="flex items-start gap-2">
            <i class="fi fi-rr-info"></i>
            <span>Thành phần: {{ sanPham.thanhPhan }}</span>
          </div>

          <div class="flex items-start gap-2">
            <i class="fi fi-rr-exclamation"></i>
            <span>Luôn giao thực phẩm tươi sống vào ngày giao hàng</span>
          </div>
        </div>

        <!-- Thêm vào giỏ -->
        <div class="border rounded p-4 h-fit space-y-4">
          <h2 class="text-lg font-semibold">Thêm vào giỏ hàng</h2>

          <div class="flex items-center justify-between">
            <label class="font-medium">Số lượng:</label>
            <div class="flex items-center gap-3">
              <button
                @click="giamSoLuong"
                class="w-8 h-8 rounded-full bg-gray-800 text-white text-lg hover:bg-gray-900 cursor-pointer"
              >
                −
              </button>
              <span class="min-w-[24px] text-center">{{ soLuong }}</span>
              <button
                @click="tangSoLuong"
                class="w-8 h-8 rounded-full bg-gray-800 text-white text-lg hover:bg-gray-900 cursor-pointer"
              >
                +
              </button>
            </div>
          </div>

          <div>
            <p class="font-medium">Tạm tính:</p>
            <p class="text-red-600 font-semibold">
              {{ dinhDangTien(tamTinh) }}
            </p>
          </div>

          <button
            class="w-full bg-white border border-gray-800 text-gray-800 py-2 rounded hover:bg-gray-100 font-medium flex items-center justify-center gap-2"
            @click="themVaoGio"
          >
            <i class="fi fi-rr-shopping-cart-add"></i> Thêm vào giỏ
          </button>
        </div>
      </div>
    </div>
  </CustomerWithNav>
</template>

<script setup lang="ts">
import CustomerWithNav from "@/components/layouts/CustomerWithNav.vue";
import Breadcrumbs from "@/components/base/Breadcrumbs.vue";
import { useToast } from "@/composables/useToast";
import { ref, computed } from "vue";

// Breadcrumb sử dụng 'to' thay vì 'href'
const breadcrumbs = [
  { text: "Trang chủ", to: "/" },
  { text: "Thực phẩm", to: "/thuc-pham" },
  { text: "Bánh mì", to: "/thuc-pham/banh-mi" },
  { text: "Bánh mì" }, // Không có `to` vì đây là trang hiện tại
];

// Dữ liệu sản phẩm mẫu
const sanPham = {
  ten: "Bánh mì",
  donGia: 7000,
  donVi: "Cái",
  nhaSanXuat: "Township",
  thanhPhan: "Bột mì, men, muối",
  hinhAnh: "https://via.placeholder.com/400",
  nhan: "Mới", // 👈 thêm nhãn góc phải
};

const { showToast } = useToast();

const soLuong = ref(1);

const tamTinh = computed(() => sanPham.donGia * soLuong.value);

const tangSoLuong = () => soLuong.value++;
const giamSoLuong = () => {
  if (soLuong.value > 1) soLuong.value--;
};

const dinhDangTien = (val: number) => val.toLocaleString("vi-VN") + " VND";

const themVaoGio = () => {
  showToast({
    thongBao: `Đã thêm ${soLuong.value} ${sanPham.ten} vào giỏ hàng.`,
    loai: "thanhCong",
  });
};
</script>
