<template>
  <Overlay :visible="visible" z-index="z-[1002]" @close="$emit('close')">
    <div
      class="bg-gray-50 max-h-[90vh] rounded-lg shadow-xl relative p-4 flex flex-col"
      :class="widthClass"
    >
      <!-- Nút đóng -->
      <button
        class="cursor-pointer absolute top-2 right-2 text-2xl"
        @click="$emit('close')"
        title="Đóng"
      >
        ×
      </button>

      <!-- Tiêu đề -->
      <h2 v-if="title" class="text-center text-lg font-semibold mb-4">
        {{ title }}
      </h2>

      <!-- Nội dung chính -->
      <div class="flex-1 overflow-y-auto pr-2">
        <slot />
      </div>

      <!-- Footer cho các nút hành động -->
      <div v-if="$slots.footer" class="mt-4 pt-4 border-t">
        <slot name="footer" />
      </div>
    </div>
  </Overlay>
</template>

<script setup lang="ts">
import Overlay from "../Overlay.vue";

withDefaults(
  defineProps<{
    visible: boolean;
    title?: string;
    widthClass?: string;
  }>(),
  {
    widthClass: "w-[500px]",
  }
);

defineEmits<{
  (e: "close"): void;
}>();
</script>
