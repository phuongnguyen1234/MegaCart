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
        :disabled="disabled"
        class="cursor-pointer mt-1 block w-48 border border-gray-300 bg-white rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm px-3 py-2 disabled:bg-gray-100 disabled:cursor-not-allowed"
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
        :disabled="!selectedParent || disabled"
        class="cursor-pointer mt-1 block w-48 border border-gray-300 bg-white rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm px-3 py-2 disabled:bg-gray-100 disabled:cursor-not-allowed"
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
import { computed, watch, onMounted } from "vue";
import { useDanhMucStore } from "@/store/danhmuc.store";
import type { DanhMucQuanLyResponse } from "@/types/danhmuc.types";

// Sử dụng v-model để component cha có thể nhận giá trị
const props = defineProps<{
  parent: number | "";
  child: number | "";
  disabled?: boolean;
}>();

const emit = defineEmits<{
  (e: "update:parent", value: number | ""): void;
  (e: "update:child", value: number | ""): void;
}>();

// --- Store ---
const danhMucStore = useDanhMucStore();

// Tải dữ liệu khi component được tạo. Store sẽ cache lại để tránh gọi lại.
onMounted(() => {
  danhMucStore.fetchAllCategoriesFlat();
});

const allCategories = computed(() => danhMucStore.allCategoriesFlat);

const danhMucChaOptions = computed(() => {
  // Lọc ra các danh mục cha từ danh sách phẳng (những danh mục không có tenDanhMucCha)
  return allCategories.value.filter((dm) => !dm.tenDanhMucCha);
});

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
const danhMucConFiltered = computed<DanhMucQuanLyResponse[]>(() => {
  if (!selectedParent.value) return [];
  // Tìm đối tượng danh mục cha đã chọn để lấy tên của nó
  const parent = danhMucChaOptions.value.find(
    (p) => p.maDanhMuc === selectedParent.value
  );
  if (!parent) return [];
  // Lọc ra các danh mục con từ danh sách phẳng dựa vào tenDanhMucCha
  return allCategories.value.filter(
    (dm) => dm.tenDanhMucCha === parent.tenDanhMuc
  );
});

// Tự động reset danh mục con khi danh mục cha thay đổi
watch(selectedParent, () => {
  selectedChild.value = "";
});
</script>
