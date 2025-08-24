<template>
  <div class="bg-gray-100">
    <!-- Bọc TheSideMenu trong transition để có hiệu ứng mượt mà -->
    <transition name="slide-menu">
      <TheSideMenu v-if="isMenuVisible" />
    </transition>

    <!-- Nội dung chính, có margin-left thay đổi theo trạng thái menu -->
    <main
      class="flex-grow transition-all duration-300 ease-in-out"
      :class="{ 'ml-[250px]': isMenuVisible }"
    >
      <!-- Header chứa nút burger -->
      <header
        class="sticky top-0 z-20 bg-white shadow-sm flex items-center p-3"
      >
        <button
          @click="isMenuVisible = !isMenuVisible"
          class="cursor-pointer p-2 rounded-full hover:bg-gray-200 focus:outline-none"
        >
          <Icon icon="lucide:menu" class="w-6 h-6 text-gray-700" />
        </button>
        <!-- Bạn có thể thêm các thành phần khác cho header ở đây -->
      </header>

      <!-- Khu vực hiển thị nội dung của các trang -->
      <div class="p-5">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { Icon } from "@iconify/vue";
import TheSideMenu from "@/components/layouts/TheSideMenu.vue";

const isMenuVisible = ref(true);
</script>
