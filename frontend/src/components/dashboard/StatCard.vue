<template>
  <div class="flex items-center justify-between p-4 bg-white rounded-lg shadow">
    <div>
      <p class="text-gray-500 text-sm">{{ tieuDe }}</p>
      <p class="text-xl font-bold" :class="giaTriClass">{{ giaTri }}</p>
    </div>
    <!-- Sử dụng component Icon từ @iconify/vue để hiển thị icon -->
    <Icon v-if="icon" :icon="icon" class="text-2xl text-gray-600" />
  </div>
</template>

<script lang="ts" setup>
import { Icon } from "@iconify/vue";

defineProps<{
  tieuDe: string;
  giaTri: string | number;
  icon?: string;
  giaTriClass?: string;
}>();
</script>
