<!-- src/components/home/BannerSlider.vue -->
<template>
  <div class="relative w-full h-[300px] overflow-hidden">
    <!-- Slides -->
    <div
      class="flex transition-transform duration-700 ease-in-out h-full"
      :style="{ transform: `translateX(-${currentIndex * 100}%)` }"
    >
      <div
        v-for="(image, index) in images"
        :key="index"
        class="w-full flex-shrink-0 h-full"
      >
        <img :src="image" alt="Banner" class="w-full h-full object-cover" />
      </div>
    </div>

    <!-- Left arrow -->
    <button
      class="absolute top-1/2 left-4 -translate-y-1/2 w-10 h-10 rounded-full bg-white bg-opacity-50 hover:bg-opacity-80 flex items-center justify-center shadow-md"
      @click="prevSlide"
    >
      <i class="fi fi-rr-angle-left text-xl"></i>
    </button>

    <!-- Right arrow -->
    <button
      class="absolute top-1/2 right-4 -translate-y-1/2 w-10 h-10 rounded-full bg-white bg-opacity-50 hover:bg-opacity-80 flex items-center justify-center shadow-md"
      @click="nextSlide"
    >
      <i class="fi fi-rr-angle-right text-xl"></i>
    </button>
  </div>
</template>


<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const images = [
  'https://picsum.photos/id/1015/1200/300',
  'https://picsum.photos/id/1016/1200/300',
  'https://picsum.photos/id/1018/1200/300',
  'https://picsum.photos/id/1020/1200/300',
  'https://picsum.photos/id/1024/1200/300',
]

const currentIndex = ref(0)
let intervalId: ReturnType<typeof setInterval>

const nextSlide = () => {
  currentIndex.value = (currentIndex.value + 1) % images.length
}

const prevSlide = () => {
  currentIndex.value = (currentIndex.value - 1 + images.length) % images.length
}

onMounted(() => {
  intervalId = setInterval(() => {
    nextSlide()
  }, 5000)
})

onUnmounted(() => {
  clearInterval(intervalId)
})
</script>
