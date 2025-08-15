<template>
  <div class="flex items-center border p-2 rounded gap-3">
    <!-- Hình ảnh -->
    <img
      :src="sanPham.hinhAnh"
      alt="Ảnh sản phẩm"
      class="w-14 h-14 object-cover rounded"
    />

    <div class="flex-1 text-sm">
      <div class="font-semibold text-blue-600">{{ sanPham.tenSanPham }}</div>
      <div>
        {{ sanPham.donGia.toLocaleString() }} VND/{{ sanPham.donVi ?? "" }}
      </div>
      <div class="flex items-center gap-1">
        <i class="fi fi-rr-shopping-bag"></i>
        <span>Số lượng: {{ sanPham.soLuong }}</span>
      </div>
    </div>

    <div class="font-semibold text-sm whitespace-nowrap">
      {{ sanPham.thanhTien.toLocaleString() }} VND
    </div>
  </div>
</template>

<script setup lang="ts">
import type { GioHangItem } from "@/types/giohang.types";

defineProps<{
  sanPham: GioHangItem;
}>();
</script>
