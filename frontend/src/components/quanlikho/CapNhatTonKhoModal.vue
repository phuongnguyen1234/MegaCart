<template>
  <BaseModal
    :visible="visible"
    :title="tieuDeModal"
    @close="dongModal"
    width-class="w-[550px]"
  >
    <div class="space-y-4">
      <!-- Số lượng hiện tại -->
      <div>
        <label class="block text-sm font-medium text-gray-700">
          Số lượng hiện tại
        </label>
        <p class="mt-1 text-lg font-semibold text-gray-900">
          {{ sanPham?.soLuong ?? 0 }}
        </p>
      </div>

      <!-- Hình thức cập nhật -->
      <div>
        <label class="block text-sm font-medium text-gray-700">
          Hình thức cập nhật
        </label>
        <div class="mt-2 flex items-center gap-x-6">
          <div class="flex items-center">
            <input
              id="hinh-thuc-them"
              v-model="hinhThucCapNhat"
              type="radio"
              value="them"
              class="h-4 w-4 border-gray-300 text-indigo-600 focus:ring-indigo-600"
            />
            <label
              for="hinh-thuc-them"
              class="ml-2 block text-sm text-gray-900"
            >
              Thêm vào hiện tại
            </label>
          </div>
          <div class="flex items-center">
            <input
              id="hinh-thuc-ghi-de"
              v-model="hinhThucCapNhat"
              type="radio"
              value="ghide"
              class="h-4 w-4 border-gray-300 text-indigo-600 focus:ring-indigo-600"
            />
            <label
              for="hinh-thuc-ghi-de"
              class="ml-2 block text-sm text-gray-900"
            >
              Ghi đè
            </label>
          </div>
        </div>
      </div>

      <!-- Số lượng cập nhật -->
      <div>
        <label
          for="so-luong-cap-nhat"
          class="block text-sm font-medium text-gray-700"
        >
          Số lượng cập nhật
        </label>
        <input
          id="so-luong-cap-nhat"
          v-model.number="soLuongCapNhat"
          type="number"
          min="0"
          class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
        />
      </div>

      <!-- Nội dung cập nhật -->
      <div>
        <label
          for="noi-dung-cap-nhat"
          class="block text-sm font-medium text-gray-700"
        >
          Nội dung cập nhật
        </label>
        <textarea
          id="noi-dung-cap-nhat"
          v-model="noiDungCapNhat"
          rows="3"
          placeholder="Ví dụ: Nhập hàng từ nhà cung cấp A, điều chỉnh kiểm kê,..."
          class="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
        ></textarea>
      </div>
    </div>

    <!-- Footer với nút Lưu -->
    <template #footer>
      <div class="flex justify-end">
        <button
          @click="luuThayDoi"
          class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
        >
          Lưu thay đổi
        </button>
      </div>
    </template>
  </BaseModal>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import BaseModal from "@/components/base/modals/BaseModal.vue";
import type { DuLieuCapNhat, SanPhamTonKho } from "@/types/khohang.types";

const props = defineProps<{
  visible: boolean;
  sanPham: SanPhamTonKho | null;
}>();
const emit = defineEmits<{
  (e: "close"): void;
  (e: "save", data: DuLieuCapNhat): void;
}>();

const hinhThucCapNhat = ref<"them" | "ghide">("them");
const soLuongCapNhat = ref<number | null>(null);
const noiDungCapNhat = ref("");

const tieuDeModal = computed(
  () => `Cập nhật tồn kho - ${props.sanPham?.tenSanPham ?? "Sản phẩm"}`
);

const dongModal = () => emit("close");

const luuThayDoi = () => {
  if (
    !props.sanPham ||
    soLuongCapNhat.value === null ||
    soLuongCapNhat.value < 0
  ) {
    console.error("Dữ liệu không hợp lệ!");
    return;
  }
  const data: DuLieuCapNhat = {
    maSanPham: props.sanPham.maSanPham,
    hinhThuc: hinhThucCapNhat.value,
    soLuong: soLuongCapNhat.value,
    noiDung: noiDungCapNhat.value,
  };
  emit("save", data);
  dongModal();
};

watch(
  () => props.visible,
  (isVisible) => {
    if (isVisible) {
      hinhThucCapNhat.value = "them";
      soLuongCapNhat.value = null;
      noiDungCapNhat.value = "";
    }
  }
);
</script>
