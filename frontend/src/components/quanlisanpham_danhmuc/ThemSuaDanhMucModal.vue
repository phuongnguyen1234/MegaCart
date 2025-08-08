<template>
  <BaseModal
    :visible="visible"
    :title="title"
    :width-class="widthClass"
    @close="$emit('close')"
  >
    <form @submit.prevent="handleSubmit">
      <div class="space-y-4">
        <!-- Tên danh mục -->
        <div>
          <label for="tenDanhMuc">Tên danh mục</label>
          <input
            id="tenDanhMuc"
            type="text"
            class="input w-full mt-1"
            placeholder="Nhập tên danh mục"
          />
        </div>

        <!-- Checkbox "Đây là danh mục cha" -->
        <div class="flex items-center">
          <input
            id="isDanhMucCha"
            type="checkbox"
            v-model="isDanhMucCha"
            class="h-4 w-4 text-blue-600 border-gray-300 rounded focus:ring-blue-500"
          />
          <label for="isDanhMucCha" class="ml-2 block text-sm text-gray-900">
            Đây là danh mục cha
          </label>
        </div>

        <!-- Danh mục cha -->
        <div>
          <label for="danhMucCha">Danh mục cha</label>
          <select
            id="danhMucCha"
            class="input w-full mt-1"
            :disabled="isDanhMucCha"
          >
            <option>Chọn danh mục cha</option>
            <!-- Thêm các option khác từ props hoặc API call -->
          </select>
        </div>

        <!-- Trạng thái -->
        <div>
          <label>Trạng thái</label>
          <div class="mt-2">
            <label
              for="trangThai"
              class="relative inline-flex items-center cursor-pointer"
            >
              <input
                type="checkbox"
                v-model="trangThaiHoatDong"
                id="trangThai"
                class="sr-only peer"
              />
              <div
                class="w-11 h-6 bg-gray-200 rounded-full peer peer-focus:outline-none peer-focus:ring-2 peer-focus:ring-blue-500 peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:start-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-green-500"
              ></div>
              <span class="ms-3 text-sm font-medium">{{
                trangThaiHoatDong ? "Đang hoạt động" : "Ngừng hoạt động"
              }}</span>
            </label>
          </div>
        </div>
      </div>
    </form>
    <template #footer>
      <div class="flex justify-end gap-4">
        <button class="btn" @click="$emit('close')">Hủy</button>
        <button type="submit" class="btn btn-primary" @click="handleSubmit">
          {{ isEditMode ? "Cập nhật danh mục" : "Thêm danh mục" }}
        </button>
      </div>
    </template>
  </BaseModal>
</template>

<script setup lang="ts">
import { ref } from "vue";
import BaseModal from "@/components/base/modals/BaseModal.vue";

defineProps<{
  visible: boolean;
  title?: string;
  widthClass?: string;
  isEditMode?: boolean;
}>();
const emit = defineEmits<{
  (e: "close"): void;
}>();

const isDanhMucCha = ref(false);
const trangThaiHoatDong = ref(true);

const handleSubmit = () => {
  // TODO: Thêm logic xử lý submit form
  // Ví dụ: emit('submit', formData);
  emit("close"); // Tạm thời chỉ đóng modal
};
</script>

<style scoped>
/* 
  Thêm @reference để cung cấp ngữ cảnh của Tailwind cho khối style này.
  Đường dẫn cần trỏ đến file CSS chính của bạn.
*/
@reference "../../assets/styles/main.css";

.input {
  @apply border border-gray-300 rounded px-3 py-2;
}
.input:focus {
  @apply outline-none ring-2 ring-blue-500;
}
.input:disabled {
  @apply bg-gray-100 cursor-not-allowed;
}

.btn {
  @apply inline-flex justify-center items-center py-2 px-4 border shadow-sm text-sm font-medium rounded-md focus:outline-none focus:ring-2 focus:ring-offset-2 transition-colors;
  /* Kiểu mặc định cho nút "Hủy" */
  @apply border-gray-300 bg-white text-gray-700 hover:bg-gray-50 focus:ring-gray-400;
}

.btn-primary {
  /* Ghi đè kiểu cho nút chính */
  @apply border-transparent bg-blue-600 text-white hover:bg-blue-700 focus:ring-blue-500;
}
</style>
