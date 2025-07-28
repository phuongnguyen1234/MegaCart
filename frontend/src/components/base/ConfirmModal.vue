<template>
  <Overlay :visible="visible" z-index="z-[1002]" @close="huy">
    <div class="bg-white rounded-lg shadow-xl p-6 w-full max-w-sm mx-4">
      <h3 class="text-lg font-bold text-gray-800 mb-4">{{ title }}</h3>
      <p class="text-gray-600 mb-6">{{ message }}</p>
      <div class="flex justify-end space-x-4">
        <button
          @click="huy"
          class="cursor-pointer px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 transition"
        >
          Không
        </button>
        <button
          @click="xacNhan"
          class="cursor-pointer px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 transition"
        >
          Đồng ý
        </button>
      </div>
    </div>
  </Overlay>
</template>

<script setup lang="ts">
import Overlay from "./Overlay.vue";
defineProps<{
  visible: boolean;
  title: string;
  message: string;
}>();

const emit = defineEmits(["xacNhan", "huy"]);

const xacNhan = () => {
  emit("xacNhan");
};

const huy = () => {
  emit("huy");
};
</script>
