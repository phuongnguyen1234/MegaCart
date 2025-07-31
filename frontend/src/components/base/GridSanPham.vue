<!-- src/components/base/GridSanPham.vue -->
<template>
  <div>
    <div class="grid gap-4 grid-cols-2 md:grid-cols-4">
      <CardSanPham
        v-for="sanPham in dsSanPham"
        :key="sanPham.maSanPham"
        :sanPham="sanPham"
        @add-to-cart="$emit('themVaoGioHang', sanPham)"
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
import CardSanPham from "@/components/base/card/CardSanPham.vue";
import type { SanPham } from "@/types/SanPham";

defineProps<{
  dsSanPham: SanPham[];
  trangHienTai: number;
  tongSoTrang: number;
  trangBatDau: number;
  trangKetThuc: number;
  soTrangHienThi: number[];
}>();

defineEmits<{
  (e: "themVaoGioHang", sanPham: SanPham): void;
  (e: "chuyenTrang", trang: number): void;
}>();
</script>
