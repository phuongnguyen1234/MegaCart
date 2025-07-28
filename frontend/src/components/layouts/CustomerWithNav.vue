<template>
  <div class="flex flex-col min-h-screen bg-gray-100">
    <TheHeader />
    <main class="flex-grow pt-[70px]">
      <TheNavBar />
      <slot />
    </main>
    <TheFooter />
  </div>
</template>

<script setup lang="ts">
import TheHeader from '@/components/layouts/TheHeader.vue'
import TheNavBar from '@/components/layouts/TheNavBar.vue'
import TheFooter from '@/components/layouts/TheFooter.vue'
</script>
