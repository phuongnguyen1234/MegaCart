<template>
  <div
    class="w-full border border-gray-200 rounded-lg text-sm overflow-hidden bg-white shadow-sm"
  >
    <!-- Accordion: Thời gian -->
    <div class="border-b border-gray-200">
      <button
        class="cursor-pointer w-full text-left px-4 py-3 bg-gray-50 hover:bg-gray-100 font-semibold text-gray-700 flex justify-between items-center transition-colors"
        @click="toggle('thoiGian')"
      >
        Thời gian
        <i
          :class="
            isOpen.thoiGian ? 'fi-rr-angle-small-up' : 'fi-rr-angle-small-down'
          "
          class="text-xl flex items-center"
        ></i>
      </button>

      <transition name="slide-fade">
        <div v-if="isOpen.thoiGian" class="px-4 py-3 space-y-3 bg-white">
          <div>
            <label
              for="tuNgay"
              class="block text-sm mb-1 font-medium text-gray-600"
              >Từ ngày</label
            >
            <input
              id="tuNgay"
              type="date"
              v-model="modelTuNgay"
              class="w-full border border-gray-300 rounded-md px-2 py-1.5 text-sm focus:ring-blue-500 focus:border-blue-500 transition"
            />
          </div>
          <div>
            <label
              for="denNgay"
              class="block text-sm mb-1 font-medium text-gray-600"
              >Đến ngày</label
            >
            <input
              id="denNgay"
              type="date"
              v-model="modelDenNgay"
              class="w-full border border-gray-300 rounded-md px-2 py-1.5 text-sm focus:ring-blue-500 focus:border-blue-500 transition"
            />
          </div>
        </div>
      </transition>
    </div>

    <!-- Accordion: Tìm kiếm -->
    <div>
      <button
        class="cursor-pointer w-full text-left px-4 py-3 bg-gray-50 hover:bg-gray-100 font-semibold text-gray-700 flex justify-between items-center transition-colors"
        @click="toggle('timKiem')"
      >
        Tìm kiếm
        <i
          :class="
            isOpen.timKiem ? 'fi-rr-angle-small-up' : 'fi-rr-angle-small-down'
          "
          class="text-xl flex items-center"
        ></i>
      </button>

      <transition name="slide-fade">
        <div v-if="isOpen.timKiem" class="px-4 py-3 space-y-3 bg-white">
          <div>
            <label
              for="maDonHang"
              class="block text-sm mb-1 font-medium text-gray-600"
              >Mã đơn hàng</label
            >
            <input
              id="maDonHang"
              type="text"
              v-model="modelMaDonHang"
              placeholder="Nhập mã đơn hàng"
              class="w-full border border-gray-300 rounded-md px-2 py-1.5 text-sm focus:ring-blue-500 focus:border-blue-500 transition"
            />
          </div>
          <button
            class="w-full bg-blue-600 text-white py-2 rounded-md hover:bg-blue-700 transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 mt-2"
            @click="emit('timKiem')"
          >
            Tìm kiếm
          </button>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";

const emit = defineEmits(["timKiem"]);

const modelTuNgay = defineModel<string | undefined>("tuNgay");
const modelDenNgay = defineModel<string | undefined>("denNgay");
const modelMaDonHang = defineModel<string>("maDonHang");

const isOpen = ref({
  thoiGian: true,
  timKiem: true,
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
