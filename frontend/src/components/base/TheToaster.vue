<template>
  <div class="fixed top-5 right-5 z-[5000] flex flex-col gap-3">
    <Toast
      v-for="toast in toasts"
      :key="toast.id"
      :thongBao="toast.thongBao"
      :loai="toast.loai"
      :thoiLuong="toast.thoiLuong"
      @remove="dongToast(toast.id)"
    />
  </div>
</template>

<script setup lang="ts">
import { useToast } from "@/composables/useToast";
import Toast from "@/components/base/Toast.vue";

const { mangToast: toasts, removeToast: dongToast } = useToast();
</script>
