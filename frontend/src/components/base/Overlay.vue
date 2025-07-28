<template>
  <div
    v-if="visible"
    class="fixed inset-0 flex items-center justify-center bg-black/40"
    :class="zIndex"
    @click.self="handleOverlayClick"
  >
    <slot />
  </div>
</template>

<script setup lang="ts">
defineProps<{
  visible: boolean;
  zIndex?: string;
}>();

const emit = defineEmits(["dong"]);

const handleOverlayClick = () => {
  emit("dong");
};
</script>
