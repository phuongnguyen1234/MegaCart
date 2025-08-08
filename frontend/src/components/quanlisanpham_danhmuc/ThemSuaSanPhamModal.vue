<template>
  <BaseModal
    :visible="visible"
    :title="title"
    :width-class="widthClass"
    @close="$emit('close')"
  >
    <form>
      <div class="grid grid-cols-2 gap-x-6 gap-y-4">
        <!-- Tên sản phẩm chiếm 2 cột -->
        <div class="col-span-2">
          <label for="tenSanPham">Tên sản phẩm</label>
          <input id="tenSanPham" type="text" class="input w-full mt-1" />
        </div>

        <!-- Danh mục -->
        <div>
          <label for="danhMucCha">Danh mục cha</label>
          <select id="danhMucCha" class="input w-full mt-1">
            <option>Chọn danh mục</option>
            <!-- Thêm các option khác -->
          </select>
        </div>

        <div>
          <label for="danhMucCon">Danh mục con</label>
          <select id="danhMucCon" class="input w-full mt-1">
            <option>Chọn danh mục</option>
          </select>
        </div>

        <!-- Mô tả chiếm 2 cột -->
        <div class="col-span-2">
          <label for="moTa">Mô tả</label>
          <textarea
            id="moTa"
            rows="3"
            class="input w-full resize-none mt-1"
          ></textarea>
        </div>

        <!-- Nhà sản xuất và Nhãn -->
        <div>
          <label for="nhaSanXuat">Nhà sản xuất</label>
          <input id="nhaSanXuat" type="text" class="input w-full mt-1" />
        </div>
        <div>
          <label for="nhan">Nhãn</label>
          <select id="nhan" class="input w-full mt-1">
            <option>Chọn nhãn</option>
          </select>
        </div>

        <!-- Đơn giá và Đơn vị -->
        <div>
          <label for="donGia">Đơn giá (VND)</label>
          <input id="donGia" type="number" class="input w-full mt-1" />
        </div>
        <div>
          <label for="donVi">Đơn vị</label>
          <input id="donVi" type="text" class="input w-full mt-1" />
        </div>

        <!-- Ghi chú chiếm 2 cột -->
        <div class="col-span-2">
          <label for="ghiChu">Ghi chú (Tùy chọn)</label>
          <textarea
            id="ghiChu"
            rows="2"
            class="input w-full resize-none mt-1"
          ></textarea>
        </div>

        <!-- Trạng thái -->
        <div>
          <label>Trạng thái</label>
          <div class="mt-2">
            <label
              for="trangThaiBan"
              class="relative inline-flex items-center cursor-pointer"
            >
              <input
                type="checkbox"
                v-model="trangThaiBan"
                id="trangThaiBan"
                class="sr-only peer"
              />
              <div
                class="w-11 h-6 bg-gray-200 rounded-full peer peer-focus:outline-none peer-focus:ring-2 peer-focus:ring-blue-500 peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:start-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-green-500"
              ></div>
              <span class="ms-3 text-sm font-medium">{{
                trangThaiBan ? "Đang bán" : "Ngừng bán"
              }}</span>
            </label>
          </div>
        </div>

        <!-- Ảnh minh họa chiếm 2 cột -->
        <div class="col-span-2">
          <label>Ảnh minh họa (cần ít nhất 1)</label>
          <div
            class="mt-1 flex justify-center items-center w-full h-24 border-2 border-dashed border-gray-300 rounded-lg cursor-pointer hover:bg-gray-50"
          >
            <div class="text-center">
              <p class="text-2xl text-gray-400">+</p>
              <p class="text-sm text-gray-500">Tải ảnh lên</p>
            </div>
          </div>
        </div>
      </div>
    </form>
    <template #footer>
      <div class="flex justify-end gap-4">
        <button class="btn" @click="$emit('close')">Hủy</button>
        <button type="submit" class="btn btn-primary">
          {{ isEditMode ? "Cập nhật sản phẩm" : "Thêm sản phẩm" }}
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
defineEmits<{
  (e: "close"): void;
}>();

const trangThaiBan = ref(true);
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
