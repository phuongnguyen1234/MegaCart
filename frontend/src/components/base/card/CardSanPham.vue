<template>
  <div
    class="relative bg-white border-none rounded-xl shadow-sm hover:shadow-md transition overflow-hidden flex flex-col"
  >
    <!-- Nhãn sản phẩm (Mới, Bán chạy, v.v.) -->
    <span
      v-if="sanPham.nhan"
      class="absolute top-2 left-2 bg-red-500 text-white text-xs font-semibold px-2 py-0.5 rounded z-10"
    >
      {{ sanPham.nhan }}
    </span>

    <!-- Ảnh sản phẩm -->
    <div class="relative group overflow-hidden">
      <a :href="`/san-pham/${sanPham.maSanPham}`">
        <img
          :src="sanPham.anhMinhHoa[0]"
          :alt="sanPham.tenSanPham"
          class="w-full h-60 object-cover transition-transform duration-300 ease-in-out group-hover:scale-105"
        />
      </a>
      <!-- Nút Thêm -->
      <button
        @click="onAdd"
        class="absolute bottom-2 right-2 bg-blue-600 text-white px-3 py-1.5 text-xs font-semibold rounded-full flex items-center shadow hover:bg-blue-700 transition cursor-pointer"
      >
        <span class="select-none mr-1 text-sm font-bold">+</span> Thêm
      </button>
    </div>

    <!-- Thông tin -->
    <div class="p-3 flex-grow flex flex-col justify-between">
      <a
        :href="`/san-pham/${sanPham.maSanPham}`"
        class="font-semibold text-gray-800 text-sm truncate hover:underline"
      >
        {{ sanPham.tenSanPham }}
      </a>
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
import type { SanPham } from "@/types/SanPham";

const props = defineProps<{
  sanPham: SanPham;
}>();

const emit = defineEmits<{
  (e: "themVaoGioHang", product: SanPham): void;
}>();

const onAdd = () => emit("themVaoGioHang", props.sanPham);
</script>
