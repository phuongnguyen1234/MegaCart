<template>
  <nav v-if="tongSoTrang > 1" class="flex justify-center items-center mt-8">
    <!-- Nút Previous -->
    <button
      @click="chuyenTrang(trangHienTai - 1)"
      :disabled="trangHienTai === 0"
      class="px-3 py-1 mx-1 rounded-md text-gray-700 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-200"
    >
      <
    </button>

    <!-- Các nút số trang -->
    <button
      v-for="trang in soTrangHienThi"
      :key="trang"
      @click="chuyenTrang(trang - 1)"
      :class="[
        'px-3 py-1 mx-1 rounded-md',
        trangHienTai === trang - 1
          ? 'bg-blue-600 text-white'
          : 'text-gray-700 hover:bg-gray-200',
      ]"
    >
      {{ trang }}
    </button>

    <!-- Nút Next -->
    <button
      @click="chuyenTrang(trangHienTai + 1)"
      :disabled="trangHienTai === tongSoTrang - 1"
      class="px-3 py-1 mx-1 rounded-md text-gray-700 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-200"
    >
      >
    </button>
  </nav>
</template>

<script setup lang="ts">
import { computed } from "vue";

const props = withDefaults(
  defineProps<{
    trangHienTai: number;
    tongSoTrang: number;
    maxVisiblePages?: number;
  }>(),
  {
    maxVisiblePages: 5,
  }
);

const emit = defineEmits<{
  (e: "update:trangHienTai", trang: number): void;
}>();

const chuyenTrang = (trang: number) => {
  if (trang >= 0 && trang < props.tongSoTrang) {
    emit("update:trangHienTai", trang);
  }
};

const trangBatDau = computed(() => {
  const mid = Math.floor(props.maxVisiblePages / 2);
  if (props.tongSoTrang <= props.maxVisiblePages) return 1;
  if (props.trangHienTai + 1 <= mid + 1) return 1;
  if (props.trangHienTai + 1 >= props.tongSoTrang - mid)
    return props.tongSoTrang - props.maxVisiblePages + 1;
  return props.trangHienTai - mid + 1;
});

const trangKetThuc = computed(() => {
  return Math.min(
    trangBatDau.value + props.maxVisiblePages - 1,
    props.tongSoTrang
  );
});

const soTrangHienThi = computed(() => {
  const pages = [];
  for (let i = trangBatDau.value; i <= trangKetThuc.value; i++) {
    pages.push(i);
  }
  return pages;
});
</script>
