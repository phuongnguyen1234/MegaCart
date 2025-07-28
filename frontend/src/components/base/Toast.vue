<template>
  <transition name="fade" @after-leave="$emit('dong')">
    <div
      v-if="visible"
      :class="[
        'flex items-center gap-3 px-4 py-3 rounded-lg shadow-lg min-w-[250px] max-w-sm',
        loai === 'thanhCong'
          ? 'bg-[#def2d6] text-[#3C763D]'
          : 'bg-[#ebc8c4] text-[#A94442]',
      ]"
    >
      <span class="text-lg">
        <slot name="icon">
          <i v-if="loai === 'thanhCong'" class="fi fi-rs-check-circle"></i>
          <i v-else class="fi fi-rr-cross-circle"></i>
        </slot>
      </span>
      <span class="flex-1 text-sm">{{ thongBao }}</span>
      <button
        class="cursor-pointer text-inherit text-xl leading-none"
        @click="dongThongBao"
      >
        <i class="fi fi-rr-cross-small"></i>
      </button>
    </div>
  </transition>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";

const props = defineProps<{
  thongBao: string;
  loai?: "thanhCong" | "loi";
  thoiLuong?: number;
}>();

const emit = defineEmits<{
  (e: "dong"): void;
}>();

const visible = ref(true);
const thoiLuong = props.thoiLuong ?? 3000;

onMounted(() => {
  setTimeout(() => {
    visible.value = false;
  }, thoiLuong);
});

function dongThongBao() {
  visible.value = false;
}
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.4s ease, transform 0.4s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(10px);
}
</style>
