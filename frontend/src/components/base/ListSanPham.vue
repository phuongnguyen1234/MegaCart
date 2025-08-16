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
        class="grid gap-4 grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4"
      >
        <CardSanPham
          v-for="sanPham in pagedProducts"
          :key="sanPham.maSanPham"
          :sanPham="sanPham"
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
import { ref, computed, onMounted, onBeforeUnmount, watch } from "vue";
import CardSanPham from "@/components/base/card/CardSanPham.vue";
import type { SanPhamResponse } from "@/types/sanpham.types";

const props = defineProps<{
  tieuDe: string;
  dsSanPham: SanPhamResponse[];
  linkXemThem?: string;
}>();

defineEmits<{ (e: "themVaoGioHang", product: SanPhamResponse): void }>();

const containerRef = ref<HTMLElement | null>(null);
const kichThuocTrang = ref(4);
const trangHienTai = ref(0);

const tinhKichThuocTrang = () => {
  const chieuRong = containerRef.value?.clientWidth || window.innerWidth;
  if (chieuRong >= 1024) kichThuocTrang.value = 4; // lg
  else if (chieuRong >= 768) kichThuocTrang.value = 3; // md
  else if (chieuRong >= 640) kichThuocTrang.value = 2; // sm
  else kichThuocTrang.value = 1;
};

onMounted(() => {
  tinhKichThuocTrang();
  window.addEventListener("resize", tinhKichThuocTrang);
});

onBeforeUnmount(() => {
  window.removeEventListener("resize", tinhKichThuocTrang);
});

watch(kichThuocTrang, () => {
  trangHienTai.value = 0; // reset page when screen changes
});

const totalPages = computed(() =>
  Math.ceil(props.dsSanPham.length / kichThuocTrang.value)
);

const pagedProducts = computed(() =>
  props.dsSanPham.slice(
    trangHienTai.value * kichThuocTrang.value,
    (trangHienTai.value + 1) * kichThuocTrang.value
  )
);

const canScrollLeft = computed(() => trangHienTai.value > 0);
const canScrollRight = computed(
  () => trangHienTai.value < totalPages.value - 1
);

const scrollLeft = () => {
  if (canScrollLeft.value) trangHienTai.value--;
};
const scrollRight = () => {
  if (canScrollRight.value) trangHienTai.value++;
};
</script>
