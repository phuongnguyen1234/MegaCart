<template>
  <BaseModal
    :visible="visible"
    :title="tieuDeModal"
    @close="dongModal"
    width-class="w-[550px]"
    :is-loading="isLoading"
  >
    <form @submit.prevent="handleSubmit">
      <div class="space-y-4">
        <!-- Số lượng hiện tại -->
        <div>
          <label class="block text-sm font-medium text-gray-700">
            Số lượng hiện tại
          </label>
          <p class="mt-1 text-lg font-semibold text-gray-900">
            {{ chiTietSanPham?.soLuongHienTai ?? 0 }}
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
                v-model="formData.hinhThuc"
                type="radio"
                :value="HinhThucCapNhatKhoKey.THEM_VAO_HIEN_TAI"
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
                v-model="formData.hinhThuc"
                type="radio"
                :value="HinhThucCapNhatKhoKey.GHI_DE"
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
            v-model.number="formData.soLuong"
            type="number"
            min="0"
            required
            class="input mt-1"
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
            v-model="formData.noiDung"
            rows="3"
            placeholder="Ví dụ: Nhập hàng từ NCC A, điều chỉnh kiểm kê,... (không bắt buộc)"
            class="input mt-1"
          ></textarea>
        </div>
      </div>
    </form>

    <!-- Footer với nút Lưu -->
    <template #footer>
      <div class="flex justify-end gap-4">
        <button class="btn" @click="dongModal">Hủy</button>
        <button
          @click="handleSubmit"
          class="btn btn-primary"
          :disabled="isLoading"
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
import { getChiTietKho, capNhatKho } from "@/service/khohang.service";
import { useToast } from "@/composables/useToast";
import type {
  KhoResponse,
  ChiTietKhoResponse,
  CapNhatKhoRequest,
} from "@/types/khohang.types";
import { HinhThucCapNhatKhoKey } from "@/types/khohang.types";

const props = defineProps<{
  visible: boolean;
  sanPham: KhoResponse | null;
}>();
const emit = defineEmits<{
  (e: "close"): void;
  (e: "success"): void;
}>();

const isLoading = ref(false);
const { showToast } = useToast();
const chiTietSanPham = ref<ChiTietKhoResponse | null>(null);

const initialFormData = {
  hinhThuc: HinhThucCapNhatKhoKey.THEM_VAO_HIEN_TAI,
  soLuong: null as number | null,
  noiDung: "",
};
const formData = ref({ ...initialFormData });

const tieuDeModal = computed(
  () => `Cập nhật tồn kho - ${props.sanPham?.tenSanPham ?? "Sản phẩm"}`
);

const dongModal = () => emit("close");

const resetForm = () => {
  formData.value = { ...initialFormData };
  chiTietSanPham.value = null;
};

const fetchChiTietKho = async (maSanPham: number) => {
  isLoading.value = true;
  try {
    chiTietSanPham.value = await getChiTietKho(maSanPham);
  } catch (error) {
    console.error("Lỗi khi lấy chi tiết tồn kho:", error);
    showToast({
      loai: "loi",
      thongBao: "Không thể tải thông tin tồn kho hiện tại.",
    });
    dongModal();
  } finally {
    isLoading.value = false;
  }
};

const handleSubmit = async () => {
  if (
    !props.sanPham ||
    formData.value.soLuong === null ||
    formData.value.soLuong < 0
  ) {
    showToast({ loai: "loi", thongBao: "Số lượng cập nhật không hợp lệ." });
    return;
  }

  const payload: CapNhatKhoRequest = {
    hinhThuc: formData.value.hinhThuc,
    soLuong: formData.value.soLuong,
  };

  // Chỉ thêm `noiDung` vào payload nếu người dùng có nhập.
  // Nếu không, trường này sẽ là `undefined` và không được gửi đi, đúng với định nghĩa `noiDung?: string`.
  if (formData.value.noiDung?.trim()) {
    payload.noiDung = formData.value.noiDung.trim();
  }

  isLoading.value = true;
  try {
    await capNhatKho(props.sanPham.maSanPham, payload);
    showToast({
      loai: "thanhCong",
      thongBao: "Cập nhật tồn kho thành công!",
    });
    emit("success");
    dongModal();
  } catch (error: any) {
    const errorMessage =
      error.response?.data?.message || "Có lỗi xảy ra khi cập nhật tồn kho.";
    showToast({ loai: "loi", thongBao: errorMessage });
  } finally {
    isLoading.value = false;
  }
};

watch(
  () => props.visible,
  (isVisible) => {
    if (isVisible && props.sanPham) {
      resetForm();
      fetchChiTietKho(props.sanPham.maSanPham);
    }
  }
);
</script>

<style scoped>
@reference "../../assets/styles/main.css";

.input {
  @apply border border-gray-300 rounded-md px-3 py-2 bg-white w-full shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm;
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
