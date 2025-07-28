<template>
  <div class="w-full border rounded text-sm overflow-hidden bg-white">
    <!-- Danh mục -->
    <div class="border-b">
      <button
        class="cursor-pointer w-full text-left px-4 py-2 bg-blue-200 font-semibold flex justify-between items-center"
        @click="toggle('danhMucCon')"
      >
        Danh mục
        <i
          :class="
            isOpen.danhMucCon
              ? 'fi-rr-angle-small-up'
              : 'fi-rr-angle-small-down'
          "
          class="text-xl flex items-center"
        ></i>
      </button>
      <div v-if="isOpen.danhMucCon" class="px-4 py-2 space-y-2">
        <label
          v-for="item in danhMucOption"
          :key="item"
          class="flex items-center gap-2"
        >
          <input
            type="radio"
            name="category"
            :value="item"
            v-model="selected.danhMucCon"
          />
          {{ item }}
        </label>
      </div>
    </div>

    <!-- Đơn giá -->
    <div class="border-b">
      <button
        class="cursor-pointer w-full text-left px-4 py-2 bg-blue-200 font-semibold flex justify-between items-center"
        @click="toggle('donGia')"
      >
        Đơn giá
        <i
          :class="
            isOpen.donGia ? 'fi-rr-angle-small-up' : 'fi-rr-angle-small-down'
          "
          class="text-xl flex items-center"
        ></i>
      </button>
      <div v-if="isOpen.donGia" class="px-4 py-2 space-y-3">
        <p>
          Đến <strong>{{ maxDonGia.toLocaleString() }} VND</strong>
        </p>
        <input
          type="range"
          v-model="maxDonGia"
          min="0"
          :max="100000"
          step="1000"
          class="w-full accent-blue-500"
        />
        <div class="flex items-center gap-4">
          <label class="flex items-center gap-1">
            <input type="radio" value="asc" v-model="sapXep" />
            Tăng dần
          </label>
          <label class="flex items-center gap-1">
            <input type="radio" value="desc" v-model="sapXep" />
            Giảm dần
          </label>
        </div>
      </div>
    </div>

    <!-- Nhà sản xuất -->
    <div>
      <button
        class="cursor-pointer w-full text-left px-4 py-2 bg-blue-200 font-semibold flex justify-between items-center"
        @click="toggle('nhaSanXuat')"
      >
        Nhà sản xuất
        <i
          :class="
            isOpen.nhaSanXuat
              ? 'fi-rr-angle-small-up'
              : 'fi-rr-angle-small-down'
          "
          class="text-xl flex items-center"
        ></i>
      </button>
      <div v-if="isOpen.nhaSanXuat" class="px-4 py-2 space-y-2">
        <label
          v-for="item in nhaSanXuatOption"
          :key="item"
          class="flex items-center gap-2"
        >
          <input
            type="radio"
            name="manufacturer"
            :value="item"
            v-model="selected.nhaSanXuat"
          />
          {{ item }}
        </label>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";

const props = defineProps<{
  danhMucCon?: string[];
  nhaSanXuat?: string[];
}>();

// Dữ liệu mock nếu chưa có
const danhMucOption = computed(() => props.danhMucCon ?? ["Tất cả", "Quần áo"]);
const nhaSanXuatOption = computed(
  () => props.nhaSanXuat ?? ["Tất cả", "Township", "HayDay"]
);

const isOpen = ref({
  danhMucCon: true,
  donGia: false,
  nhaSanXuat: false,
});

const selected = ref({
  danhMucCon: "Tất cả",
  nhaSanXuat: "Tất cả",
});

const maxDonGia = ref(100000);
const sapXep = ref<"asc" | "desc">("asc");

function toggle(section: keyof typeof isOpen.value) {
  isOpen.value[section] = !isOpen.value[section];
}
</script>

<style scoped>
input[type="range"] {
  accent-color: #3b82f6; /* Tailwind blue-500 */
}
</style>
