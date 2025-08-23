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
    <PhanTrang
      :trang-hien-tai="trangHienTai"
      :tong-so-trang="tongSoTrang"
      :max-visible-pages="maxVisiblePages"
      @update:trang-hien-tai="$emit('chuyenTrang', $event)"
    />
  </div>
</template>

<script setup lang="ts">
import CardSanPham from "@/components/base/card/CardSanPham.vue";
import PhanTrang from "@/components/base/PhanTrang.vue";
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
</script>
