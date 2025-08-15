<template>
  <div class="flex items-center border rounded p-3 gap-3 relative">
    <input
      type="checkbox"
      :checked="isChecked"
      @change="$emit('chon', ($event.target as HTMLInputElement).checked)"
      class="cursor-pointer"
    />
    <router-link
      :to="{
        name: 'ChiTietSanPham',
        params: {
          maSanPham: sanPham.maSanPham,
        },
      }"
      class="shrink-0"
    >
      <div class="w-16 h-16 bg-gray-200 rounded"></div>
    </router-link>

    <div class="flex-1">
      <h3 class="text-blue-700 font-semibold">
        <router-link
          :to="{
            name: 'ChiTietSanPham',
            params: {
              maSanPham: sanPham.maSanPham,
            },
          }"
          class="hover:underline"
          >{{ sanPham.tenSanPham }}</router-link
        >
      </h3>
      <p class="text-sm">
        {{ formatPrice(sanPham.donGia) }} VND/{{ sanPham.donVi }}
      </p>

      <div class="flex items-center mt-1 gap-2">
        <button
          @click="thayDoiSoLuong(-1)"
          class="w-6 h-6 bg-blue-600 text-white rounded-full text-center cursor-pointer hover:bg-blue-700"
        >
          −
        </button>
        <span>{{ sanPham.soLuong }}</span>
        <button
          @click="thayDoiSoLuong(1)"
          class="w-6 h-6 bg-blue-600 text-white rounded-full text-center cursor-pointer hover:bg-blue-700"
        >
          +
        </button>
      </div>
    </div>

    <div class="text-right font-semibold">
      {{ formatPrice(sanPham.donGia * sanPham.soLuong) }} VND
    </div>

    <!-- Nút xóa -->
    <button
      @click="$emit('xoa')"
      class="absolute top-2 right-2 w-6 h-6 bg-red-600 text-white rounded-full cursor-pointer hover:bg-red-700"
    >
      −
    </button>
  </div>
</template>

<script setup lang="ts">
import type { PropType } from "vue";
import type { GioHangItem } from "@/types/giohang.types";

const props = defineProps({
  sanPham: {
    type: Object as PropType<GioHangItem>,
    required: true,
  },
  isChecked: {
    type: Boolean,
    required: true,
  },
});
const emit = defineEmits(["chon", "thayDoiSoLuong", "xoa"]);

const thayDoiSoLuong = (delta: number) => {
  const newSL = props.sanPham.soLuong + delta;
  if (newSL >= 1) {
    emit("thayDoiSoLuong", newSL);
  }
};

const formatPrice = (vnd: number) => {
  if (typeof vnd !== "number") return 0;
  return vnd.toLocaleString("vi-VN");
};
</script>
