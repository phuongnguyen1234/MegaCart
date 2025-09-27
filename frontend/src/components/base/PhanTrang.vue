<template>
  <nav v-if="tongSoTrang > 1" class="flex justify-center items-center mt-8">
    <!-- Nút Previous -->
    <button
      @click="chuyenTrang(trangHienTai - 1)"
      :disabled="trangHienTai === 0"
      class="font-semibold cursor-pointer px-3 py-1 mx-1 rounded-full text-blue-700 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-blue-200"
    >
      <
    </button>

    <!-- Các nút số trang -->
    <button
      v-for="trang in soTrangHienThi"
      :key="trang"
      @click="chuyenTrang(trang - 1)"
      :class="[
        'px-3 py-1 mx-1 rounded-full cursor-pointer',
        trangHienTai === trang - 1
          ? 'bg-blue-600 text-white'
          : 'text-blue-700 hover:bg-blue-200 font-semibold',
      ]"
    >
      {{ trang }}
    </button>

    <!-- Nút Next -->
    <button
      @click="chuyenTrang(trangHienTai + 1)"
      :disabled="trangHienTai === tongSoTrang - 1"
      class="font-semibold cursor-pointer px-3 py-1 mx-1 rounded-full text-blue-700 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-blue-200"
    >
      >
    </button>
  </nav>
</template>

<script setup lang="ts">
import { computed } from "vue";

const {
  trangHienTai,
  tongSoTrang,
  maxVisiblePages = 5,
} = defineProps<{
  trangHienTai: number;
  tongSoTrang: number;
  maxVisiblePages?: number;
}>();

const emit = defineEmits<{
  (e: "update:trangHienTai", trang: number): void;
}>();

const chuyenTrang = (trang: number) => {
  if (trang >= 0 && trang < tongSoTrang) {
    emit("update:trangHienTai", trang);
  }
};

const trangBatDau = computed(() => {
  const mid = Math.floor(maxVisiblePages / 2);
  if (tongSoTrang <= maxVisiblePages) return 1;
  if (trangHienTai + 1 <= mid + 1) return 1;
  if (trangHienTai + 1 >= tongSoTrang - mid)
    return tongSoTrang - maxVisiblePages + 1;
  return trangHienTai - mid + 1;
});

const trangKetThuc = computed(() => {
  return Math.min(trangBatDau.value + maxVisiblePages - 1, tongSoTrang);
});

const soTrangHienThi = computed(() => {
  const pages = [];
  for (let i = trangBatDau.value; i <= trangKetThuc.value; i++) {
    pages.push(i);
  }
  return pages;
});
</script>
