<template>
  <div
    class="relative flex items-center gap-4 rounded-lg border bg-white p-4 shadow-sm transition-all"
    :class="{ 'opacity-60 bg-gray-50': disabled }"
  >
    <input
      type="checkbox"
      :checked="isChecked"
      :disabled="disabled"
      @change="$emit('chon', ($event.target as HTMLInputElement).checked)"
      class="h-5 w-5 shrink-0 cursor-pointer rounded border-gray-300 text-blue-600 focus:ring-blue-500 disabled:cursor-not-allowed"
    />
    <router-link
      :to="{
        name: 'ChiTietSanPham',
        params: {
          maSanPham: sanPham.maSanPham,
        },
      }"
      class="shrink-0"
      :class="{ 'pointer-events-none': disabled }"
    >
      <img
        :src="sanPham.anhMinhHoa"
        :alt="sanPham.tenSanPham"
        class="h-20 w-20 rounded-md object-cover"
        :class="{ grayscale: disabled }"
    /></router-link>

    <!-- Info & Quantity -->
    <div class="flex-grow">
      <router-link
        :to="{
          name: 'ChiTietSanPham',
          params: { maSanPham: sanPham.maSanPham },
        }"
        class="font-semibold text-gray-800 hover:text-blue-600 hover:underline"
        :class="{ 'pointer-events-none !text-gray-500 no-underline': disabled }"
      >
        {{ sanPham.tenSanPham }}
      </router-link>
      <p class="text-sm text-gray-500">
        {{ formatPrice(sanPham.donGia) }} / {{ sanPham.donVi }}
      </p>

      <!-- Message for disabled product -->
      <p v-if="disabled" class="mt-1 text-sm font-semibold text-red-500">
        Ngừng kinh doanh
      </p>

      <!-- Quantity Control (only if not disabled) -->
      <div v-else class="mt-2 flex items-center gap-2">
        <button
          @click="giamSoLuong"
          class="flex h-7 w-7 items-center justify-center rounded-full bg-blue-600 text-lg text-white hover:bg-blue-700"
        >
          −
        </button>
        <span class="min-w-[24px] text-center font-semibold">{{
          localSoLuong
        }}</span>
        <button
          @click="tangSoLuong"
          class="flex h-7 w-7 items-center justify-center rounded-full bg-blue-600 text-lg text-white hover:bg-blue-700"
        >
          +
        </button>
      </div>
    </div>

    <!-- Total Price -->
    <div class="shrink-0 text-right">
      <p class="font-bold text-red-600">
        {{ formatPrice(sanPham.thanhTien) }}
      </p>
    </div>

    <!-- Nút xóa -->
    <button
      @click="$emit('xoa')"
      class="absolute top-2 right-2 flex h-7 w-7 items-center justify-center rounded-full bg-white text-gray-500 shadow-sm ring-1 ring-inset ring-gray-200 hover:bg-gray-100 hover:text-red-600"
      aria-label="Xóa sản phẩm"
    >
      <i class="fi fi-rr-trash text-sm"></i>
    </button>
  </div>
</template>

<script setup lang="ts">
import type { PropType } from "vue";
import { ref, watch } from "vue";

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
  disabled: {
    type: Boolean,
    default: false,
  },
});
const emit = defineEmits<{
  (e: "chon", isChecked: boolean): void;
  (e: "thayDoiSoLuong", soLuong: number): void;
  (e: "xoa"): void;
}>();

// Sử dụng state cục bộ để cập nhật UI ngay lập tức (optimistic update)
const localSoLuong = ref(props.sanPham.soLuong);

// Đồng bộ state cục bộ nếu prop từ cha thay đổi (ví dụ: sau khi API call thành công)
watch(
  () => props.sanPham.soLuong,
  (newVal) => {
    localSoLuong.value = newVal;
  }
);

const giamSoLuong = () => {
  // Nếu số lượng lớn hơn 1, giảm bình thường
  if (localSoLuong.value > 1) {
    localSoLuong.value--;
    emit("thayDoiSoLuong", localSoLuong.value);
  } else {
    // Nếu số lượng là 1, thì phát sự kiện xóa
    emit("xoa");
  }
};

const tangSoLuong = () => {
  localSoLuong.value++;
  emit("thayDoiSoLuong", localSoLuong.value);
};

const formatPrice = (vnd: number) => {
  if (typeof vnd !== "number") return 0;
  return vnd.toLocaleString("vi-VN") + " VND";
};
</script>
