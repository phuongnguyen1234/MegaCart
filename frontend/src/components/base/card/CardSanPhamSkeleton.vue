<template>
  <div
    class="relative bg-white rounded-xl shadow-sm overflow-hidden flex flex-col"
  >
    <!-- Ảnh sản phẩm (Skeleton) -->
    <div class="relative overflow-hidden">
      <div class="w-full h-60 bg-gray-200 animate-pulse"></div>
    </div>

    <!-- Thông tin (Skeleton) -->
    <div class="p-3 flex-grow flex flex-col justify-between">
      <div>
        <div class="h-5 bg-gray-200 rounded w-3/4 animate-pulse"></div>
      </div>
      <div class="mt-1">
        <div class="h-6 bg-gray-200 rounded w-1/2 animate-pulse"></div>
      </div>
      <div class="flex items-center mt-2 space-x-2">
        <div class="h-4 bg-gray-200 rounded w-1/4 animate-pulse"></div>
        <div class="h-4 bg-gray-200 rounded w-1/3 animate-pulse"></div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// Component này không cần logic, chỉ để hiển thị UI.
</script>
