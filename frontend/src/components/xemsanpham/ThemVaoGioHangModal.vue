<template>
  <Overlay :visible="visible" z-index="z-[1001]" @close="emit('dong')">
    <div
      class="bg-white p-6 rounded-lg w-[300px] relative shadow-lg animate-fade-in"
    >
      <!-- Nút đóng dùng flaticon icon -->
      <button
        class="absolute top-2 right-2 text-xl text-gray-600 hover:text-black cursor-pointer"
        @click="emit('dong')"
      >
        <i class="fi-rr-cross-small"></i>
      </button>

      <!-- Tiêu đề -->
      <h2 class="text-lg font-semibold mb-4 text-center">Thêm vào giỏ hàng</h2>

      <!-- Nội dung -->
      <div class="flex gap-4 items-center">
        <img
          v-if="sanPham?.anhMinhHoaChinh"
          :src="sanPham.anhMinhHoaChinh"
          :alt="sanPham.tenSanPham"
          class="w-20 h-20 object-cover rounded-md flex-shrink-0"
        />
        <div class="flex-1">
          <div class="font-semibold">{{ sanPham?.tenSanPham }}</div>
          <div v-if="sanPham" class="text-m text-gray-500">
            {{ sanPham.donGia.toLocaleString() }} VND/{{ sanPham.donVi }}
          </div>

          <!-- Bộ chọn số lượng -->
          <div class="flex items-center mt-2 gap-3">
            <button
              class="cursor-pointer w-6 h-6 rounded-full bg-blue-600 hover:bg-blue-700 text-white"
              @click="giamSoLuong"
            >
              −
            </button>
            <span class="w-6 text-center font-semibold">{{ soLuong }}</span>
            <button
              class="cursor-pointer w-6 h-6 rounded-full bg-blue-600 hover:bg-blue-700 text-white"
              @click="tangSoLuong"
            >
              +
            </button>
          </div>
        </div>
      </div>

      <!-- Tạm tính -->
      <div class="mt-4 text-left font-semibold text-xl">
        Tạm tính: {{ tamTinh.toLocaleString() }} VND
      </div>

      <!-- Nút thêm vào giỏ -->
      <div class="text-center mt-5">
        <button
          class="cursor-pointer bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
          @click="themVaoGioHang"
        >
          Thêm vào giỏ
        </button>
      </div>
    </div>
  </Overlay>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import Overlay from "@/components/base/Overlay.vue";
import type { SanPhamResponse } from "@/types/sanpham.types";

const props = withDefaults(
  defineProps<{
    visible: boolean;
    sanPham: SanPhamResponse | null;
  }>(),
  { sanPham: null }
);

const emit = defineEmits(["dong", "them"]);

const soLuong = ref(1);
const donGia = computed(() => props.sanPham?.donGia || 0);

const tamTinh = computed(() => soLuong.value * donGia.value);

const tangSoLuong = () => {
  soLuong.value++;
};

const giamSoLuong = () => {
  if (soLuong.value > 1) soLuong.value--;
};

const themVaoGioHang = () => {
  // Sửa tên sự kiện thành 'them' cho nhất quán
  emit("them", { sanPham: props.sanPham, soLuong: soLuong.value });
  emit("dong");
};

// Theo dõi prop 'visible' để reset số lượng khi modal được mở
watch(
  () => props.visible,
  (isVisible) => {
    if (isVisible) {
      soLuong.value = 1;
    }
  }
);
</script>

<style scoped>
@keyframes fade-in {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}
.animate-fade-in {
  animation: fade-in 0.2s ease-out;
}
</style>
