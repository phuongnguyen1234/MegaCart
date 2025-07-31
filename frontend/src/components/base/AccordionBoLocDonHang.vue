<template>
  <div class="w-full border rounded text-sm overflow-hidden bg-white">
    <!-- Accordion: Thời gian -->
    <div class="border-b">
      <button
        class="cursor-pointer w-full text-left px-4 py-2 bg-blue-200 font-semibold flex justify-between items-center"
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

      <div v-if="isOpen.thoiGian" class="px-4 py-2 space-y-2">
        <div>
          <label class="block text-sm mb-1">Từ ngày</label>
          <input
            type="date"
            v-model="modelTuNgay"
            class="w-full border rounded px-2 py-1"
          />
        </div>
        <div>
          <label class="block text-sm mb-1">Đến ngày</label>
          <input
            type="date"
            v-model="modelDenNgay"
            class="w-full border rounded px-2 py-1"
          />
        </div>
      </div>
    </div>

    <!-- Accordion: Tìm kiếm -->
    <div>
      <button
        class="cursor-pointer w-full text-left px-4 py-2 bg-blue-200 font-semibold flex justify-between items-center"
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

      <div v-if="isOpen.timKiem" class="px-4 py-2 space-y-2">
        <div>
          <label class="block text-sm mb-1">Mã đơn hàng</label>
          <input
            type="text"
            v-model="modelMaDonHang"
            placeholder="Nhập mã đơn hàng"
            class="w-full border rounded px-2 py-1"
          />
        </div>

        <button
          class="w-full bg-gray-700 text-white py-1 rounded hover:bg-gray-800 mt-2"
          @click="emit('timKiem')"
        >
          Tìm kiếm
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";

const emit = defineEmits(["timKiem"]);

const modelTuNgay = defineModel<string>("tuNgay");
const modelDenNgay = defineModel<string>("denNgay");
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
input[type="date"],
input[type="text"] {
  accent-color: #3b82f6;
}
</style>
