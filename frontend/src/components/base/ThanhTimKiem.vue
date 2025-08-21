<template>
  <div>
    <label for="loai-tim-kiem" class="block text-sm font-medium text-gray-700">
      Tìm kiếm theo
    </label>
    <div
      class="flex items-center border border-gray-300 rounded mt-1 shadow-sm"
    >
      <select
        id="loai-tim-kiem"
        v-model="localLoaiTimKiem"
        class="border-r border-gray-300 bg-gray-50 px-2 py-1 focus:outline-none rounded-l"
      >
        <option
          v-for="tieuChi in dsTieuChi"
          :key="tieuChi.value"
          :value="tieuChi.value"
        >
          {{ tieuChi.label }}
        </option>
      </select>
      <input
        type="text"
        v-model="localTuKhoa"
        :placeholder="placeholder"
        class="w-60 py-1 px-2 border-none focus:outline-none rounded-r"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, defineProps, defineEmits, watch, ref } from "vue";

const props = defineProps<{
  dsTieuChi: { value: string; label: string; isId?: boolean }[];
  modelValueLoai: string;
  modelValueTuKhoa: string;
}>();

const emit = defineEmits<{
  (e: "update:modelValueLoai", value: string): void;
  (e: "update:modelValueTuKhoa", value: string): void;
  (e: "idSearchActive", isActive: boolean): void;
}>();

// Local copies để v-model hoạt động
const localLoaiTimKiem = ref(props.modelValueLoai);
const localTuKhoa = ref(props.modelValueTuKhoa);

// Đồng bộ props với local
watch(
  () => props.modelValueLoai,
  (newVal) => (localLoaiTimKiem.value = newVal)
);
watch(
  () => props.modelValueTuKhoa,
  (newVal) => (localTuKhoa.value = newVal)
);

// Emit khi local thay đổi
watch(localLoaiTimKiem, (val) => emit("update:modelValueLoai", val));
watch(localTuKhoa, (val) => emit("update:modelValueTuKhoa", val));

// Logic để xác định xem tiêu chí tìm kiếm theo ID có được chọn hay không
const isIdSearchSelected = computed(() => {
  const selectedOption = props.dsTieuChi.find(
    (t) => t.value === localLoaiTimKiem.value
  );
  // Kích hoạt ngay khi tiêu chí là ID được chọn
  return !!selectedOption?.isId;
});

// Gửi trạng thái ra ngoài component cha khi nó thay đổi
watch(
  isIdSearchSelected,
  (isActive) => {
    emit("idSearchActive", isActive);
  },
  { immediate: true }
);

// Placeholder động
const placeholder = computed(() => {
  const chon = props.dsTieuChi.find((t) => t.value === localLoaiTimKiem.value);
  return chon ? `Tìm kiếm ${chon.label.toLowerCase()}...` : "Nhập từ khóa...";
});
</script>
