<template>
  <div
    class="relative bg-white border rounded-xl shadow-sm hover:shadow-md transition-shadow overflow-hidden flex flex-col cursor-pointer group"
    @click="navigateToDetail"
  >
    <!-- Nhãn sản phẩm (Mới, Bán chạy, v.v.) -->
    <div class="absolute top-2 left-2 z-10 flex flex-col items-start gap-y-1.5">
      <span
        v-if="sanPham.nhan"
        class="bg-red-500 text-white text-xs font-semibold px-2 py-0.5 rounded"
      >
        {{ sanPham.nhan }}
      </span>
      <span
        v-if="sanPham.banChay"
        class="bg-orange-400 text-white text-xs font-semibold px-2 py-0.5 rounded"
      >
        Bán chạy
      </span>
    </div>

    <!-- Ảnh sản phẩm -->
    <div class="relative overflow-hidden">
      <img
        :src="sanPham.anhMinhHoaChinh"
        :alt="sanPham.tenSanPham"
        class="w-full h-60 object-cover transition-transform duration-300 ease-in-out group-hover:scale-105"
      />
      <!-- Overlay khi hết hàng -->
      <div
        v-if="isOutOfStock"
        class="absolute inset-0 bg-black/40 flex items-center justify-center"
      >
        <span class="text-white font-bold text-lg">Hết hàng</span>
      </div>

      <!-- Nút Thêm -->
      <button
        v-if="!isOutOfStock"
        @click.stop="onAdd"
        class="absolute bottom-2 right-2 bg-blue-600 text-white px-3 py-1.5 text-xs font-semibold rounded-full flex items-center shadow-md hover:bg-blue-700 transition-opacity opacity-0 group-hover:opacity-100"
      >
        <span class="select-none mr-1 text-sm font-bold">+</span> Thêm
      </button>
    </div>

    <!-- Thông tin -->
    <div class="p-3 flex-grow flex flex-col justify-between">
      <div class="font-semibold text-gray-800 text-sm truncate">
        {{ sanPham.tenSanPham }}
      </div>
      <p class="text-blue-700 font-bold text-base mt-1">
        {{ sanPham.donGia.toLocaleString() }} VND
      </p>
      <div class="flex items-center text-xs text-gray-500 mt-2 space-x-2">
        <span>⚖️ {{ sanPham.donVi }}</span>
        <span>🏭 {{ sanPham.nhaSanXuat }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { useRouter } from "vue-router";
import { TrangThaiTonKho, type SanPhamResponse } from "@/types/sanpham.types";

const props = defineProps<{
  sanPham: SanPhamResponse;
}>();

const router = useRouter();

const emit = defineEmits<{
  (e: "mo-modal-them", product: SanPhamResponse): void;
}>();

const isOutOfStock = computed(
  () => props.sanPham.trangThaiTonKho === TrangThaiTonKho.HET_HANG
);

const onAdd = () => emit("mo-modal-them", props.sanPham);

const navigateToDetail = () => {
  router.push(`/san-pham/${props.sanPham.maSanPham}`);
};
</script>
