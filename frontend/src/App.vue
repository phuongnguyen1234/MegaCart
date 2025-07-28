<template>
  <div>
    <router-view />
    <TheToaster />
  </div>
</template>

<script setup lang="ts">
import TheToaster from '@/components/base/TheToaster.vue'
</script>
