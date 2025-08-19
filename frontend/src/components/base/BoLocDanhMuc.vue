<template>
  <div class="flex flex-wrap items-end gap-4">
    <!-- Lọc theo danh mục cha -->
    <div>
      <label
        for="danh-muc-cha-filter"
        class="block text-sm font-medium text-gray-700"
      >
        Danh mục cha
      </label>
      <select
        id="danh-muc-cha-filter"
        v-model="selectedParent"
        class="mt-1 block w-48 border border-gray-300 bg-white rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm px-3 py-2"
      >
        <option value="">Tất cả</option>
        <option
          v-for="dmc in danhMucChaOptions"
          :key="dmc.maDanhMuc"
          :value="dmc.maDanhMuc"
        >
          {{ dmc.tenDanhMuc }}
        </option>
      </select>
    </div>

    <!-- Lọc theo danh mục con -->
    <div>
      <label
        for="danh-muc-con-filter"
        class="block text-sm font-medium text-gray-700"
      >
        Danh mục con
      </label>
      <select
        id="danh-muc-con-filter"
        v-model="selectedChild"
        :disabled="!selectedParent"
        class="mt-1 block w-48 border border-gray-300 bg-white rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm px-3 py-2 disabled:bg-gray-100"
      >
        <option value="">Tất cả</option>
        <option
          v-for="dmc in danhMucConFiltered"
          :key="dmc.maDanhMuc"
          :value="dmc.maDanhMuc"
        >
          {{ dmc.tenDanhMuc }}
        </option>
      </select>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, watch } from "vue";
import { useDanhMucStore } from "@/store/danhmuc.store";
import type { DanhMucConMenuItem } from "@/types/danhmuc.types";

// Sử dụng v-model để component cha có thể nhận giá trị
const props = defineProps<{
  parent: number | "";
  child: number | "";
}>();

const emit = defineEmits<{
  (e: "update:parent", value: number | ""): void;
  (e: "update:child", value: number | ""): void;
}>();

// --- Store ---
const danhMucStore = useDanhMucStore();
const danhMucChaOptions = computed(() => danhMucStore.menuItems);

// Computed properties để kết nối props với v-model
const selectedParent = computed({
  get: () => props.parent,
  set: (value) => emit("update:parent", value),
});

const selectedChild = computed({
  get: () => props.child,
  set: (value) => emit("update:child", value),
});

// Lọc danh mục con dựa trên danh mục cha đã chọn
const danhMucConFiltered = computed<DanhMucConMenuItem[]>(() => {
  if (!selectedParent.value) return [];
  const parent = danhMucChaOptions.value.find(
    (p) => p.maDanhMuc === selectedParent.value
  );
  return parent ? parent.danhMucCons : [];
});

// Tự động reset danh mục con khi danh mục cha thay đổi
watch(selectedParent, () => {
  selectedChild.value = "";
});
</script>
