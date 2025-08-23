<template>
  <transition
    enter-active-class="transition-opacity duration-500 ease-in-out"
    leave-active-class="transition-opacity duration-500 ease-in-out"
    enter-from-class="opacity-0"
    leave-to-class="opacity-0"
  >
    <button
      v-if="isVisible"
      @click="scrollToTop"
      class="cursor-pointer fixed bottom-8 right-8 z-50 flex h-12 w-12 items-center justify-center rounded-full bg-blue-600 text-white shadow-lg transition-all duration-300 ease-in-out hover:bg-blue-700 hover:-translate-y-1 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
      aria-label="Cuộn lên đầu trang"
      title="Cuộn lên đầu trang"
    >
      <i class="fi fi-rr-arrow-small-up text-2xl"></i>
    </button>
  </transition>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from "vue";

// --- State ---
// Biến reactive để điều khiển việc hiển thị của nút
const isVisible = ref(false);
// Ngưỡng cuộn (bằng pixel) để hiển thị nút
const scrollThreshold = 200;

// --- Methods ---
/**
 * Cuộn trang lên đầu một cách mượt mà.
 */
const scrollToTop = () => {
  window.scrollTo({
    top: 0,
    behavior: "smooth",
  });
};

/**
 * Xử lý sự kiện cuộn của cửa sổ.
 * Hiển thị hoặc ẩn nút dựa trên vị trí cuộn.
 */
const handleScroll = () => {
  isVisible.value = window.scrollY > scrollThreshold;
};

// --- Lifecycle Hooks ---
// Thêm event listener 'scroll' khi component được mounted
onMounted(() => {
  window.addEventListener("scroll", handleScroll);
});

// Gỡ bỏ event listener 'scroll' khi component bị unmounted để tránh rò rỉ bộ nhớ
onUnmounted(() => {
  window.removeEventListener("scroll", handleScroll);
});
</script>
