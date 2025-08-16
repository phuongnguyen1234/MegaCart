<template>
  <div class="flex items-start border p-2 rounded gap-3">
    <!-- Hình ảnh -->
    <router-link :to="`/san-pham/${sanPham.maSanPham}`">
      <img
        :src="sanPham.anhMinhHoa"
        alt="Ảnh sản phẩm"
        class="w-14 h-14 object-cover rounded bg-gray-100"
      />
    </router-link>

    <div class="flex-1 text-sm">
      <router-link
        :to="`/san-pham/${sanPham.maSanPham}`"
        class="font-semibold text-blue-600 hover:underline"
      >
        {{ sanPham.tenSanPham }}
      </router-link>
      <div>{{ sanPham.donGia.toLocaleString() }} VND</div>
      <div class="flex items-center gap-1">
        <i class="fi fi-rr-shopping-bag"></i>
        <span>Số lượng: {{ sanPham.soLuong }}</span>
      </div>
      <div
        v-if="sanPham.trangThaiSanPham?.value === TrangThaiSanPhamKey.NGUNG_BAN"
        class="text-xs font-semibold text-red-600 mt-1"
      >
        <i class="fi fi-rr-info mr-1"></i>{{ sanPham.trangThaiSanPham.label }}
      </div>
    </div>

    <div class="font-semibold text-sm whitespace-nowrap">
      {{ (sanPham.donGia * sanPham.soLuong).toLocaleString() }} VND
    </div>
  </div>
</template>

<script setup lang="ts">
import type { ChiTietDonHangItem } from "@/types/donhang.types";
import { TrangThaiSanPhamKey } from "@/types/sanpham.types";

defineProps<{
  sanPham: ChiTietDonHangItem;
}>();
</script>
