<template>
  <BaseModal
    :visible="visible"
    :title="title"
    :is-loading="isLoading"
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
            v-model="formData.tenDanhMuc"
            class="input w-full mt-1"
            placeholder="Nhập tên danh mục"
            required
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
            v-model="formData.maDanhMucCha"
            class="input w-full mt-1"
            :disabled="isDanhMucCha"
            :required="!isDanhMucCha"
          >
            <option :value="undefined">-- Chọn danh mục cha --</option>
            <option
              v-for="dmc in filteredDanhMucChaOptions"
              :key="dmc.maDanhMuc"
              :value="dmc.maDanhMuc"
            >
              {{ dmc.tenDanhMuc }}
            </option>
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
                v-model="isTrangThaiHoatDong"
                id="trangThai"
                class="sr-only peer"
              />
              <div
                class="w-11 h-6 bg-gray-200 rounded-full peer peer-focus:outline-none peer-focus:ring-2 peer-focus:ring-blue-500 peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:start-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-green-500"
              ></div>
              <span class="ms-3 text-sm font-medium">{{
                isTrangThaiHoatDong ? "Hoạt động" : "Không hoạt động"
              }}</span>
            </label>
          </div>
        </div>
      </div>
    </form>
    <template #footer>
      <div class="flex justify-end gap-4">
        <button class="btn" @click="$emit('close')">Hủy</button>
        <button
          type="submit"
          class="btn btn-primary"
          @click="handleSubmit"
          :disabled="isLoading || (isEditMode && !hasChanged)"
        >
          {{ isEditMode ? "Cập nhật danh mục" : "Thêm danh mục" }}
        </button>
      </div>
    </template>
  </BaseModal>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import { useDanhMucStore } from "@/store/danhmuc.store";
import BaseModal from "@/components/base/modals/BaseModal.vue";
import { themDanhMuc, capNhatDanhMuc } from "@/service/danhmuc.service";
import type {
  ThemDanhMucRequest,
  CapNhatDanhMucRequest, // Import the new interface
  ChiTietDanhMucQuanLyResponse,
} from "@/types/danhmuc.types";
import { TrangThaiDanhMucKey } from "@/types/danhmuc.types";
import { useToast } from "@/composables/useToast";

const props = defineProps<{
  visible: boolean;
  title?: string;
  widthClass?: string;
  isEditMode?: boolean;
  danhMucSua?: ChiTietDanhMucQuanLyResponse | null;
}>();

const emit = defineEmits<{
  (e: "close"): void;
  (e: "success"): void;
}>();

// --- State ---
const { showToast } = useToast();
const danhMucStore = useDanhMucStore();
const isLoading = ref(false);
const isDanhMucCha = ref(false);

const initialFormData: ThemDanhMucRequest = {
  tenDanhMuc: "",
  maDanhMucCha: undefined,
  trangThai: TrangThaiDanhMucKey.HOAT_DONG,
};
const formData = ref<ThemDanhMucRequest>({ ...initialFormData }); // formData vẫn dùng ThemDanhMucRequest cho việc binding
const initialEditData = ref<string>(""); // Lưu trạng thái ban đầu để so sánh
const initialIsDanhMucCha = ref(false); // Lưu trạng thái checkbox ban đầu

// --- Computed Properties ---
const isTrangThaiHoatDong = computed({
  get: () => formData.value.trangThai === TrangThaiDanhMucKey.HOAT_DONG,
  set: (value) => {
    formData.value.trangThai = value
      ? TrangThaiDanhMucKey.HOAT_DONG
      : TrangThaiDanhMucKey.KHONG_HOAT_DONG;
  },
});

const filteredDanhMucChaOptions = computed(() => {
  // Bắt đầu với danh sách phẳng tất cả các danh mục
  let options = danhMucStore.allCategoriesFlat;

  // Chỉ hiển thị các danh mục không có cha (tức là chúng là danh mục cha).
  // Dựa vào API, danh mục cha sẽ có `tenDanhMucCha` là null/undefined.
  options = options.filter((dm) => !dm.tenDanhMucCha);

  // Nếu đang ở chế độ sửa, loại bỏ chính danh mục đang sửa khỏi danh sách
  // để tránh trường hợp một danh mục tự làm cha của chính nó.
  if (props.isEditMode && props.danhMucSua) {
    options = options.filter(
      (dm) => dm.maDanhMuc !== props.danhMucSua!.maDanhMuc
    );
  }
  return options;
});

const hasChanged = computed(() => {
  if (!props.isEditMode) {
    return true; // Ở chế độ "Thêm", nút luôn được bật
  }
  // So sánh dữ liệu form và trạng thái checkbox "Đây là danh mục cha"
  const formDataChanged =
    JSON.stringify(formData.value) !== initialEditData.value;
  const isDanhMucChaChanged = isDanhMucCha.value !== initialIsDanhMucCha.value;
  return formDataChanged || isDanhMucChaChanged;
});

// --- Methods ---
const resetFormState = () => {
  formData.value = { ...initialFormData };
  isDanhMucCha.value = false;
  initialEditData.value = "";
  initialIsDanhMucCha.value = false; // Reset initial checkbox state
};

const populateFormForEdit = (danhMuc: ChiTietDanhMucQuanLyResponse) => {
  formData.value.tenDanhMuc = danhMuc.tenDanhMuc;
  formData.value.trangThai = danhMuc.trangThai.value;

  if (danhMuc.maDanhMucCha) {
    isDanhMucCha.value = false;
    formData.value.maDanhMucCha = danhMuc.maDanhMucCha;
  } else {
    isDanhMucCha.value = true;
    formData.value.maDanhMucCha = undefined;
  }

  // Lưu trạng thái ban đầu để so sánh
  initialEditData.value = JSON.stringify(formData.value);
  initialIsDanhMucCha.value = isDanhMucCha.value; // Lưu trạng thái ban đầu của checkbox
};

const handleSubmit = async () => {
  if (!formData.value.tenDanhMuc.trim()) {
    showToast({ loai: "loi", thongBao: "Tên danh mục không được để trống." });
    return;
  }
  if (!isDanhMucCha.value && !formData.value.maDanhMucCha) {
    showToast({ loai: "loi", thongBao: "Vui lòng chọn danh mục cha." });
    return;
  }

  isLoading.value = true;
  try {
    if (props.isEditMode && props.danhMucSua) {
      const initialData = JSON.parse(
        initialEditData.value
      ) as ThemDanhMucRequest;
      const updatePayload: CapNhatDanhMucRequest = {
        // Luôn gửi các trường cơ bản này
        tenDanhMuc: formData.value.tenDanhMuc,
        trangThai: formData.value.trangThai,
      };

      const isParentStatusChanged =
        isDanhMucCha.value !== initialIsDanhMucCha.value;
      const isParentIdChanged =
        formData.value.maDanhMucCha !== initialData.maDanhMucCha;

      // Chỉ thêm các trường liên quan đến cha-con vào payload NẾU chúng thực sự thay đổi.
      // Điều này đảm bảo backend nhận được tín hiệu cập nhật chính xác.
      if (isParentStatusChanged || isParentIdChanged) {
        updatePayload.isDanhMucChaUpdated = true;
        updatePayload.maDanhMucCha = isDanhMucCha.value
          ? undefined
          : formData.value.maDanhMucCha;
      }

      await capNhatDanhMuc(props.danhMucSua.maDanhMuc, updatePayload);
      showToast({
        loai: "thanhCong",
        thongBao: "Cập nhật danh mục thành công!",
      });
    } else {
      const createPayload = { ...formData.value };
      if (isDanhMucCha.value) createPayload.maDanhMucCha = undefined;
      await themDanhMuc(createPayload);
      showToast({
        loai: "thanhCong",
        thongBao: "Thêm danh mục mới thành công!",
      });
    }
    emit("success");
    emit("close");
  } catch (error) {
    console.error("Lỗi khi lưu danh mục:", error);
    // Cố gắng lấy thông báo lỗi chi tiết từ API response
    const err = error as any;
    const errorMessage =
      err.response?.data?.message || "Đã có lỗi xảy ra khi lưu danh mục.";

    showToast({ loai: "loi", thongBao: errorMessage });
  } finally {
    isLoading.value = false;
  }
};

// --- Watchers & Lifecycle ---
watch(
  () => props.visible,
  async (isVisible) => {
    if (isVisible) {
      // 1. Tải danh sách danh mục phẳng để điền vào dropdown "Danh mục cha".
      // Store sẽ tự xử lý việc cache để tránh gọi API không cần thiết.
      // Việc này đảm bảo dữ liệu luôn có sẵn khi modal được mở.
      await danhMucStore.fetchAllCategoriesFlat();

      // 2. Reset trạng thái form về mặc định
      resetFormState();
      // 3. Nếu là chế độ sửa, điền dữ liệu vào form
      if (props.isEditMode && props.danhMucSua) {
        populateFormForEdit(props.danhMucSua);
      }
    }
  }
);

watch(isDanhMucCha, (isCha) => {
  if (isCha) {
    formData.value.maDanhMucCha = undefined;
  }
});
</script>

<style scoped>
/* 
  Thêm @reference để cung cấp ngữ cảnh của Tailwind cho khối style này.
  Đường dẫn cần trỏ đến file CSS chính của bạn.
*/
@reference "../../assets/styles/main.css";

.input {
  @apply border border-gray-300 rounded px-3 py-2 bg-white;
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
