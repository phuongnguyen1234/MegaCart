<template>
  <div
    class="w-full border border-gray-200 rounded-lg text-sm overflow-hidden bg-white shadow-sm"
  >
    <!-- Danh mục -->
    <div class="border-b border-gray-200">
      <button
        class="cursor-pointer w-full text-left px-4 py-3 bg-gray-50 hover:bg-gray-100 font-semibold text-gray-700 flex justify-between items-center transition-colors"
        @click="toggle('danhMuc')"
      >
        Danh mục
        <i
          :class="
            isOpen.danhMuc ? 'fi-rr-angle-small-up' : 'fi-rr-angle-small-down'
          "
          class="text-xl flex items-center"
        ></i>
      </button>
      <transition name="slide-fade">
        <div v-if="isOpen.danhMuc" class="px-4 py-3 space-y-2 bg-white">
          <label class="flex items-center gap-2 cursor-pointer">
            <input
              type="radio"
              name="category"
              :value="null"
              v-model="modelDanhMuc"
              class="text-blue-600 focus:ring-blue-500"
            />
            Tất cả
          </label>
          <label
            v-for="danhMuc in danhMucOption"
            :key="danhMuc.maDanhMuc"
            class="flex items-center gap-2 cursor-pointer"
          >
            <input
              type="radio"
              name="category"
              :value="danhMuc.maDanhMuc"
              v-model="modelDanhMuc"
              class="text-blue-600 focus:ring-blue-500"
            />
            {{ danhMuc.tenDanhMuc }}
          </label>
        </div>
      </transition>
    </div>

    <!-- Đơn giá -->
    <div class="border-b border-gray-200">
      <button
        class="cursor-pointer w-full text-left px-4 py-3 bg-gray-50 hover:bg-gray-100 font-semibold text-gray-700 flex justify-between items-center transition-colors"
        @click="toggle('donGia')"
      >
        Đơn giá
        <i
          :class="
            isOpen.donGia ? 'fi-rr-angle-small-up' : 'fi-rr-angle-small-down'
          "
          class="text-xl flex items-center"
        ></i>
      </button>
      <transition name="slide-fade">
        <div v-if="isOpen.donGia" class="px-4 py-3 space-y-3 bg-white">
          <p class="text-gray-700" v-if="khoangGia && khoangGia.max > 0">
            Đến
            <strong class="font-semibold text-blue-600"
              >{{ modelDonGia.toLocaleString() }} VND</strong
            >
          </p>
          <input
            v-if="khoangGia && khoangGia.max > 0"
            type="range"
            v-model="modelDonGia"
            :min="khoangGia.min"
            :max="khoangGia.max"
            :step="(khoangGia.max - khoangGia.min) / 200"
            class="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-blue-600"
          />
          <div class="flex items-center gap-4 pt-2">
            <label class="flex items-center gap-1.5 cursor-pointer">
              <input
                type="radio"
                value="asc"
                v-model="modelSapXep"
                class="text-blue-600 focus:ring-blue-500"
              />
              Tăng dần
            </label>
            <label class="flex items-center gap-1.5 cursor-pointer">
              <input
                type="radio"
                value="desc"
                v-model="modelSapXep"
                class="text-blue-600 focus:ring-blue-500"
              />
              Giảm dần
            </label>
          </div>
        </div>
      </transition>
    </div>

    <!-- Nhà sản xuất -->
    <div>
      <button
        class="cursor-pointer w-full text-left px-4 py-3 bg-gray-50 hover:bg-gray-100 font-semibold text-gray-700 flex justify-between items-center transition-colors"
        @click="toggle('nhaSanXuat')"
      >
        Nhà sản xuất
        <i
          :class="
            isOpen.nhaSanXuat
              ? 'fi-rr-angle-small-up'
              : 'fi-rr-angle-small-down'
          "
          class="text-xl flex items-center"
        ></i>
      </button>
      <transition name="slide-fade">
        <div v-if="isOpen.nhaSanXuat" class="px-4 py-3 space-y-2 bg-white">
          <label class="flex items-center gap-2 cursor-pointer">
            <input
              type="radio"
              name="manufacturer"
              :value="null"
              v-model="modelNhaSanXuat"
              class="text-blue-600 focus:ring-blue-500"
            />
            Tất cả
          </label>
          <label
            v-for="tenNhaSanXuat in nhaSanXuatOption"
            :key="tenNhaSanXuat"
            class="flex items-center gap-2 cursor-pointer"
          >
            <input
              type="radio"
              name="manufacturer"
              :value="tenNhaSanXuat"
              v-model="modelNhaSanXuat"
              class="text-blue-600 focus:ring-blue-500"
            />
            {{ tenNhaSanXuat }}
          </label>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import type { FilterOption, KhoangGia } from "@/types/filter.types";

defineProps<{
  danhMucOption: FilterOption[];
  nhaSanXuatOption: string[];
  khoangGia?: KhoangGia;
}>();

// Sử dụng defineModel để tạo v-model cho component
const modelDanhMuc = defineModel<number | null>("danhMuc"); // number cho ID, null cho "Tất cả"
const modelNhaSanXuat = defineModel<string | null>("nhaSanXuat");
const modelDonGia = defineModel<number>("donGia", { default: 0 });
const modelSapXep = defineModel<"asc" | "desc">("sapXep");

const isOpen = ref({
  danhMuc: true,
  donGia: true,
  nhaSanXuat: true,
});

function toggle(section: keyof typeof isOpen.value) {
  isOpen.value[section] = !isOpen.value[section];
}
</script>

<style scoped>
.slide-fade-enter-active {
  transition: all 0.2s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.2s cubic-bezier(1, 0.5, 0.8, 1);
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateY(-10px);
  opacity: 0;
}
</style>
