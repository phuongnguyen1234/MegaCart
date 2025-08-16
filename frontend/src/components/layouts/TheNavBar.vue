<template>
  <nav
    class="sticky top-0 z-40 flex h-[51px] text-white bg-[linear-gradient(135deg,_#1E88E5,_#1565C0)]"
  >
    <!-- Loading Skeleton -->
    <div v-if="isLoading" class="flex w-full animate-pulse">
      <div v-for="i in 6" :key="i" class="flex-1 h-[51px] bg-blue-400/50"></div>
    </div>

    <!-- Error State -->
    <div
      v-else-if="error"
      class="w-full text-center text-yellow-300 text-sm px-4"
    >
      {{ error }}
    </div>

    <!-- Menu Items -->
    <template v-else>
      <!-- Tất cả danh mục -->
      <div class="flex-1 relative group">
        <div
          class="select-none h-full flex items-center justify-center px-[20px] text-white no-underline whitespace-nowrap hover:bg-[linear-gradient(135deg,_#42A5F5,_#1E88E5)] hover:text-[#E3F2FD] cursor-pointer"
        >
          Tất cả danh mục
        </div>
        <div
          v-if="menuItems.length > 0"
          class="absolute left-0 top-full w-full hidden group-hover:flex flex-col bg-[#1976D2] shadow-md z-50"
        >
          <router-link
            v-for="menuItem in menuItems"
            :key="menuItem.slug"
            :to="`/danh-muc/${menuItem.slug}`"
            class="select-none whitespace-nowrap px-4 py-2 text-white no-underline text-left hover:bg-[linear-gradient(135deg,_#90CAF9,_#42A5F5)]"
          >
            {{ menuItem.tenDanhMuc }}
          </router-link>
        </div>
      </div>

      <!-- Dynamic Categories -->
      <div
        v-for="menuItem in featuredMenuItems"
        :key="menuItem.slug"
        class="flex-1 relative group"
      >
        <router-link
          :to="`/danh-muc/${menuItem.slug}`"
          class="select-none h-full flex items-center justify-center px-[20px] text-white no-underline whitespace-nowrap hover:bg-[linear-gradient(135deg,_#42A5F5,_#1E88E5)] hover:text-[#E3F2FD]"
        >
          {{ menuItem.tenDanhMuc }}
        </router-link>

        <div
          v-if="menuItem.danhMucCons && menuItem.danhMucCons.length > 0"
          class="absolute left-0 top-full w-full hidden group-hover:flex flex-col bg-[#1976D2] shadow-md z-50"
        >
          <!-- Grid 2 columns -->
          <div class="grid grid-cols-2">
            <router-link
              v-for="child in menuItem.danhMucCons.slice(0, 12)"
              :key="child.slug"
              :to="`/danh-muc/${menuItem.slug}/${child.slug}`"
              class="select-none whitespace-nowrap px-3 py-2 text-white no-underline text-left hover:bg-[linear-gradient(135deg,_#90CAF9,_#42A5F5)]"
            >
              {{ child.tenDanhMuc }}
            </router-link>
          </div>
          <!-- Xem thêm -->
          <router-link
            v-if="menuItem.danhMucCons.length > 12"
            :to="`/danh-muc/${menuItem.slug}`"
            class="block px-4 py-2 text-center font-semibold text-[#BBDEFB] hover:text-white hover:bg-[linear-gradient(135deg,_#42A5F5,_#1E88E5)] border-t border-blue-500/50"
          >
            Xem tất cả
          </router-link>
        </div>
      </div>
    </template>
  </nav>
</template>

<script setup lang="ts">
import { onMounted, computed } from "vue";
import { useDanhMucStore } from "@/store/danhmuc.store";
import { storeToRefs } from "pinia";

const danhMucStore = useDanhMucStore();
const { menuItems, isLoading, error } = storeToRefs(danhMucStore);

// Lấy 4 danh mục đầu tiên để hiển thị nổi bật
const featuredMenuItems = computed(() => menuItems.value.slice(0, 4));

onMounted(() => {
  // Gọi action từ store để tải dữ liệu
  danhMucStore.fetchMenuDanhMuc();
});
</script>
