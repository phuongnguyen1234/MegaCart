<template>
  <Overlay :visible="visible" z-index="z-[2000]">
    <div class="w-12 h-12 border-4 border-white border-t-transparent rounded-full animate-spin"></div>
  </Overlay>
</template>

<script setup lang="ts">
import Overlay from './Overlay.vue'

defineProps<{
  visible: boolean
}>()
</script>
