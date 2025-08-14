<!-- src/components/base/GridSanPham.vue -->
<template>
  <div v-if="dsSanPham.length > 0">
    <div class="grid gap-4 grid-cols-2 md:grid-cols-4">
      <CardSanPham
        v-for="sanPham in dsSanPham"
        :key="sanPham.maSanPham"
        :sanPham="sanPham"
        @mo-modal-them="$emit('themVaoGioHang', sanPham)"
      />
    </div>

    <!-- Phân trang -->
    <div class="flex justify-center mt-6 space-x-1" v-if="tongSoTrang > 1">
      <button
        v-if="trangHienTai > 0"
        @click="$emit('chuyenTrang', 0)"
        class="w-9 h-9 flex items-center justify-center rounded-full text-sm font-medium transition text-blue-700 hover:bg-blue-600 hover:text-white"
      >
        «
      </button>
      <button
        v-if="trangHienTai > 0"
        @click="$emit('chuyenTrang', trangHienTai - 1)"
        class="w-9 h-9 flex items-center justify-center rounded-full text-sm font-medium transition text-blue-700 hover:bg-blue-600 hover:text-white"
      >
        ‹
      </button>
      <span
        v-if="trangBatDau > 1"
        class="w-9 h-9 flex items-center justify-center text-sm text-gray-400"
      >
        ...
      </span>
      <button
        v-for="n in soTrangHienThi"
        :key="n"
        @click="$emit('chuyenTrang', n - 1)"
        class="w-9 h-9 flex items-center justify-center rounded-full text-sm font-medium transition duration-200"
        :class="[
          trangHienTai === n - 1
            ? 'bg-blue-600 text-white'
            : 'text-blue-700 bg-transparent hover:bg-blue-600 hover:text-white',
        ]"
      >
        {{ n }}
      </button>
      <span
        v-if="trangKetThuc < tongSoTrang"
        class="w-9 h-9 flex items-center justify-center text-sm text-gray-400"
      >
        ...
      </span>
      <button
        v-if="trangHienTai < tongSoTrang - 1"
        @click="$emit('chuyenTrang', trangHienTai + 1)"
        class="w-9 h-9 flex items-center justify-center rounded-full text-sm font-medium transition text-blue-700 hover:bg-blue-600 hover:text-white"
      >
        ›
      </button>
      <button
        v-if="trangHienTai < tongSoTrang - 1"
        @click="$emit('chuyenTrang', tongSoTrang - 1)"
        class="w-9 h-9 flex items-center justify-center rounded-full text-sm font-medium transition text-blue-700 hover:bg-blue-600 hover:text-white"
      >
        »
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import CardSanPham from "@/components/base/card/CardSanPham.vue";
import type { SanPhamResponse } from "@/types/sanpham.types";

const props = withDefaults(
  defineProps<{
    dsSanPham: SanPhamResponse[];
    trangHienTai: number;
    tongSoTrang: number;
    maxVisiblePages?: number;
  }>(),
  {
    maxVisiblePages: 5,
  }
);

defineEmits<{
  (e: "themVaoGioHang", sanPham: SanPhamResponse): void;
  (e: "chuyenTrang", trang: number): void;
}>();

// --- Logic phân trang ---
// Logic này được chuyển vào đây từ component cha để GridSanPham trở nên độc lập hơn.

const trangBatDau = computed(() => {
  // Tính toán trang bắt đầu cho dải phân trang
  const mid = Math.floor(props.maxVisiblePages / 2);
  if (props.tongSoTrang <= props.maxVisiblePages) return 1;
  if (props.trangHienTai + 1 <= mid + 1) return 1;
  if (props.trangHienTai + 1 >= props.tongSoTrang - mid)
    return props.tongSoTrang - props.maxVisiblePages + 1;
  return props.trangHienTai - mid + 1;
});

const trangKetThuc = computed(() => {
  // Tính toán trang kết thúc cho dải phân trang
  return Math.min(
    trangBatDau.value + props.maxVisiblePages - 1,
    props.tongSoTrang
  );
});

const soTrangHienThi = computed(() => {
  // Tạo mảng các số trang sẽ được hiển thị
  const pages = [];
  for (let i = trangBatDau.value; i <= trangKetThuc.value; i++) {
    pages.push(i);
  }
  return pages;
});
</script>
