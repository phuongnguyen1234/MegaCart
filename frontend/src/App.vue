<template>
  <div id="app-container">
    <router-view />
    <TheToaster />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from "vue";
import { useDanhMucStore } from "@/store/danhmuc.store";
import TheToaster from "@/components/base/TheToaster.vue";

// Tải các dữ liệu toàn cục ngay khi ứng dụng khởi động
onMounted(() => {
  const danhMucStore = useDanhMucStore();
  danhMucStore.fetchMenuDanhMuc();
});
</script>
