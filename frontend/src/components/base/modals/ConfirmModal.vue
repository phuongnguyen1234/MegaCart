<template>
  <Overlay :visible="hienThi" z-index="z-[1002]" @close="huy">
    <div class="bg-white rounded-lg shadow-xl p-6 w-full max-w-sm mx-4">
      <h3 class="text-lg font-bold text-gray-800 mb-4">{{ tieuDe }}</h3>
      <p class="text-gray-600 mb-6">{{ noiDung }}</p>
      <div class="flex justify-end space-x-4">
        <button
          @click="huy"
          :disabled="dangTai"
          class="cursor-pointer px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 transition disabled:opacity-50 disabled:cursor-not-allowed"
        >
          Không
        </button>
        <button
          @click="xacNhan"
          :disabled="dangTai"
          class="cursor-pointer px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 transition disabled:bg-red-400 disabled:cursor-not-allowed flex items-center justify-center min-w-[100px]"
        >
          <span
            v-if="dangTai"
            class="animate-spin rounded-full h-5 w-5 border-b-2 border-white"
          ></span>
          <span v-else> Đồng ý </span>
        </button>
      </div>
    </div>
  </Overlay>
</template>

<script setup lang="ts">
import Overlay from "@/components/base/Overlay.vue";
defineProps<{
  hienThi: boolean;
  tieuDe: string;
  noiDung: string;
  dangTai?: boolean;
}>();

const emit = defineEmits(["xacNhan", "huy"]);

const xacNhan = () => {
  emit("xacNhan");
};

const huy = () => {
  emit("huy");
};
</script>
