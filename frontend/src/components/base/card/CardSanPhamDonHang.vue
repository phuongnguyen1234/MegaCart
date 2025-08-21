<template>
  <div class="flex items-start gap-4 rounded-lg border bg-white p-3 shadow-sm">
    <!-- Hình ảnh -->
    <component
      :is="isManagementMode ? 'div' : 'router-link'"
      :to="isManagementMode ? undefined : `/san-pham/${sanPham.maSanPham}`"
      class="shrink-0"
    >
      <img
        :src="sanPham.anhMinhHoa"
        alt="Ảnh sản phẩm"
        class="h-20 w-20 rounded-md object-cover"
      />
    </component>

    <!-- Thông tin sản phẩm -->
    <div class="flex-grow text-sm">
      <component
        :is="isManagementMode ? 'span' : 'router-link'"
        :to="isManagementMode ? undefined : `/san-pham/${sanPham.maSanPham}`"
        class="font-semibold text-gray-800"
        :class="{ 'hover:text-blue-600 hover:underline': !isManagementMode }"
      >
        {{ sanPham.tenSanPham }}
      </component>
      <div class="text-gray-600">{{ formatPrice(sanPham.donGia) }}</div>
      <div class="text-gray-500">Số lượng: {{ sanPham.soLuong }}</div>

      <!-- Trạng thái: Ngừng kinh doanh (ưu tiên cao nhất) -->
      <div
        v-if="sanPham.trangThaiSanPham?.value === TrangThaiSanPhamKey.KHONG_BAN"
        class="mt-1 text-xs font-semibold text-red-600"
      >
        <i class="fi fi-rr-ban mr-1"></i>{{ sanPham.trangThaiSanPham.label }}
      </div>
      <!-- Trạng thái: Hết hàng -->
      <div
        v-else-if="
          sanPham.trangThaiTonKho?.value === TrangThaiTonKhoKey.HET_HANG
        "
        class="mt-1 text-xs font-semibold text-orange-500"
      >
        <i class="fi fi-rr-box-open mr-1"></i
        >{{ sanPham.trangThaiTonKho.label }}
      </div>
    </div>

    <!-- Thành tiền -->
    <div class="shrink-0 text-right font-bold text-red-600">
      {{ formatPrice(sanPham.donGia * sanPham.soLuong) }}
    </div>
  </div>
</template>

<script setup lang="ts">
import type { ChiTietDonHangItem } from "@/types/donhang.types";
import { TrangThaiSanPhamKey, TrangThaiTonKhoKey } from "@/types/sanpham.types";

withDefaults(
  defineProps<{
    sanPham: ChiTietDonHangItem;
    isManagementMode?: boolean;
  }>(),
  {
    isManagementMode: false,
  }
);

const formatPrice = (price: number) => {
  return price.toLocaleString("vi-VN") + " VND";
};
</script>
