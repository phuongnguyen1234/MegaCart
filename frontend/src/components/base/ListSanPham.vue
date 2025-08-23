<template>
  <section class="px-4 py-6 max-w-7xl mx-auto">
    <div class="flex justify-between items-center mb-4">
      <h2 class="text-2xl font-bold tracking-tight text-blue-600">
        {{ tieuDe }}
      </h2>
      <a
        v-if="linkXemThem"
        :href="linkXemThem"
        class="text-sm text-blue-600 hover:underline"
        >Xem tất cả</a
      >
    </div>

    <div class="relative">
      <!-- Nút trái -->
      <button
        v-if="canScrollLeft"
        @click="scrollLeft"
        class="cursor-pointer absolute -left-5 top-1/2 z-10 -translate-y-1/2 flex h-10 w-10 items-center justify-center rounded-full bg-white/80 text-gray-700 shadow-md backdrop-blur-sm transition-colors hover:bg-white"
      >
        <i class="fi fi-rr-angle-left text-xl leading-none"></i>
      </button>

      <!-- Nút phải -->
      <button
        v-if="canScrollRight"
        @click="scrollRight"
        class="cursor-pointer absolute -right-5 top-1/2 z-10 -translate-y-1/2 flex h-10 w-10 items-center justify-center rounded-full bg-white/80 text-gray-700 shadow-md backdrop-blur-sm transition-colors hover:bg-white"
      >
        <i
          class="fi fi-rr-angle-right text-xl leading-none text-blue-600 font-bold"
        ></i>
      </button>

      <!-- Danh sách sản phẩm -->
      <div
        ref="containerRef"
        class="grid grid-flow-col auto-cols-[100%] sm:auto-cols-[calc(50%-0.5rem)] md:auto-cols-[calc(33.333333%-0.666667rem)] lg:auto-cols-[calc(25%-0.75rem)] gap-4 overflow-x-auto snap-x snap-mandatory scroll-smooth [scrollbar-width:none] [-ms-overflow-style:none] [-webkit-scrollbar]:hidden"
        @scroll.passive="updateScrollState"
      >
        <CardSanPham
          v-for="sanPham in dsSanPham"
          :key="sanPham.maSanPham"
          :sanPham="sanPham"
          class="snap-start"
          @mo-modal-them="$emit('themVaoGioHang', sanPham)"
        />
      </div>
    </div>

    <div v-if="!dsSanPham.length" class="text-gray-500 italic text-sm mt-4">
      Không có sản phẩm nào.
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from "vue";
import CardSanPham from "@/components/base/card/CardSanPham.vue";
import type { SanPhamResponse } from "@/types/sanpham.types";

const props = defineProps<{
  tieuDe: string;
  dsSanPham: SanPhamResponse[];
  linkXemThem?: string;
}>();

defineEmits<{ (e: "themVaoGioHang", product: SanPhamResponse): void }>();

const containerRef = ref<HTMLElement | null>(null);
const canScrollLeft = ref(false);
const canScrollRight = ref(false);

const scrollLeft = () => {
  if (containerRef.value) {
    containerRef.value.scrollBy({
      left: -containerRef.value.clientWidth,
      behavior: "smooth",
    });
  }
};
const scrollRight = () => {
  if (containerRef.value) {
    containerRef.value.scrollBy({
      left: containerRef.value.clientWidth,
      behavior: "smooth",
    });
  }
};

const updateScrollState = () => {
  if (!containerRef.value) return;
  const { scrollLeft, scrollWidth, clientWidth } = containerRef.value;
  // Thêm một khoảng sai số nhỏ để xử lý các giá trị thập phân
  const tolerance = 1;
  canScrollLeft.value = scrollLeft > tolerance;
  canScrollRight.value = scrollLeft < scrollWidth - clientWidth - tolerance;
};

let resizeObserver: ResizeObserver;

onMounted(() => {
  const container = containerRef.value;
  if (container) {
    updateScrollState();
    container.addEventListener("scroll", updateScrollState, { passive: true });
    resizeObserver = new ResizeObserver(updateScrollState);
    resizeObserver.observe(container);
  }
});

onBeforeUnmount(() => {
  const container = containerRef.value;
  if (container) {
    container.removeEventListener("scroll", updateScrollState);
  }
  if (resizeObserver) {
    resizeObserver.disconnect();
  }
});
</script>
