<template>
  <BaseModal
    :visible="visible"
    :title="isEditing ? 'Cập nhật thông tin nhân viên' : 'Thêm nhân viên mới'"
    @close="closeModal"
    width-class="w-[600px]"
  >
    <form @submit.prevent="handleSubmit">
      <div class="space-y-4">
        <!-- Tên nhân viên -->
        <div>
          <label
            for="ten"
            class="flex items-center gap-2 text-sm font-medium text-gray-700"
          >
            <i class="fi fi-rr-user text-base text-gray-400"></i>
            <span>Tên nhân viên</span></label
          >
          <input
            id="ten"
            v-model="formData.hoTen"
            type="text"
            required
            :disabled="isLoading"
            class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm px-3 py-2"
          />
        </div>

        <!-- Email -->
        <div>
          <label
            for="email"
            class="flex items-center gap-2 text-sm font-medium text-gray-700"
          >
            <i class="fi fi-rr-envelope text-base text-gray-400"></i>
            <span>Email</span></label
          >
          <input
            id="email"
            v-model="formData.email"
            type="email"
            required
            :disabled="isLoading"
            class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm px-3 py-2"
          />
        </div>

        <!-- Số điện thoại -->
        <div>
          <label
            for="soDienThoai"
            class="flex items-center gap-2 text-sm font-medium text-gray-700"
          >
            <i class="fi fi-rr-phone-call text-base text-gray-400"></i>
            <span>Số điện thoại</span></label
          >
          <input
            id="soDienThoai"
            v-model="formData.soDienThoai"
            type="text"
            required
            :disabled="isLoading"
            class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm px-3 py-2"
          />
        </div>

        <!-- Vị trí -->
        <div>
          <label
            for="viTri"
            class="flex items-center gap-2 text-sm font-medium text-gray-700"
          >
            <i class="fi fi-rr-briefcase text-base text-gray-400"></i>
            <span>Vị trí</span></label
          >
          <select
            id="viTri"
            v-model="formData.viTri"
            required
            :disabled="isLoading"
            class="cursor-pointer mt-1 block w-full border border-gray-300 bg-white rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm px-3 py-2"
          >
            <option v-for="(label, key) in ViTriLabel" :key="key" :value="key">
              {{ label }}
            </option>
          </select>
        </div>

        <!-- Trạng thái (chỉ khi sửa) -->
        <div v-if="isEditing">
          <label
            class="flex items-center gap-2 text-sm font-medium text-gray-700"
          >
            <i class="fi fi-rr-user-gear text-base text-gray-400"></i>
            <span>Trạng thái tài khoản</span></label
          >
          <div class="mt-2 pl-6">
            <label
              for="trangThai"
              class="relative inline-flex items-center cursor-pointer"
            >
              <input
                type="checkbox"
                v-model="isTrangThaiHoatDong"
                id="trangThai"
                :disabled="isLoading"
                class="sr-only peer"
              />
              <div
                class="w-11 h-6 bg-gray-200 rounded-full peer peer-focus:outline-none peer-focus:ring-2 peer-focus:ring-indigo-500 peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:start-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-green-500"
              ></div>
              <span class="ms-3 text-sm font-medium text-gray-700">{{
                isTrangThaiHoatDong ? "Hoạt động" : "Khóa"
              }}</span>
            </label>
          </div>
        </div>
      </div>

      <!-- Footer: Nút bấm -->
      <div class="mt-6 flex justify-end gap-3">
        <button
          type="button"
          @click="closeModal"
          :disabled="isLoading"
          class="cursor-pointer px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 disabled:bg-gray-100 disabled:cursor-not-allowed"
        >
          Hủy
        </button>
        <button
          type="submit"
          :disabled="isLoading || (isEditing && !hasChanged)"
          class="cursor-pointer px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:bg-gray-400 disabled:cursor-not-allowed"
        >
          <span v-if="isLoading">Đang xử lý...</span>
          <span v-else>{{ isEditing ? "Cập nhật" : "Thêm" }}</span>
        </button>
      </div>
    </form>
  </BaseModal>
</template>

<script setup lang="ts">
import { ref, watch, computed, reactive } from "vue";
import BaseModal from "@/components/base/modals/BaseModal.vue";
import { useToast } from "@/composables/useToast";
import {
  themNhanVien,
  capNhatNhanVien,
} from "@/service/quanlinhanvien.service";
import type {
  HienThiDanhSachNhanVienResponse,
  ThemNhanVienRequest,
  CapNhatNhanVienRequest,
} from "@/types/nhanvien.types";
import { ViTriKey, ViTriLabel } from "@/types/nhanvien.types";
import { TrangThaiTaiKhoanKey } from "@/types/khachhang.types";

const props = defineProps<{
  visible: boolean;
  nhanVien: HienThiDanhSachNhanVienResponse | null;
}>();

const emit = defineEmits<{
  (e: "close"): void;
  (e: "success"): void;
}>();

const { showToast } = useToast();
const isLoading = ref(false);

const isEditing = computed(() => !!props.nhanVien);

const createDefaultFormData = () => ({
  hoTen: "",
  email: "",
  soDienThoai: "",
  matKhau: "",
  viTri: ViTriKey.NHAN_VIEN_QUAN_LI_DON,
  trangThai: TrangThaiTaiKhoanKey.HOAT_DONG,
});

const formData = reactive(createDefaultFormData());
const initialFormData = ref<string>("");

const isTrangThaiHoatDong = computed({
  get: () => formData.trangThai === TrangThaiTaiKhoanKey.HOAT_DONG,
  set: (value) => {
    formData.trangThai = value
      ? TrangThaiTaiKhoanKey.HOAT_DONG
      : TrangThaiTaiKhoanKey.KHOA;
  },
});

const hasChanged = computed(() => {
  if (!isEditing.value) return true;
  return JSON.stringify(formData) !== initialFormData.value;
});

watch(
  () => props.visible,
  (isVisible) => {
    if (!isVisible) return;

    if (props.nhanVien) {
      // Chế độ sửa
      formData.hoTen = props.nhanVien.tenNhanVien;
      formData.email = props.nhanVien.email;
      formData.soDienThoai = props.nhanVien.soDienThoai;
      formData.viTri = props.nhanVien.viTri.value;
      formData.trangThai = props.nhanVien.trangThaiTaiKhoan.value;
      formData.matKhau = ""; // Không cần mật khẩu khi sửa

      // Lưu trạng thái ban đầu để so sánh
      initialFormData.value = JSON.stringify(formData);
    } else {
      // Chế độ thêm mới
      Object.assign(formData, createDefaultFormData());
      initialFormData.value = "";
    }
  }
);

const closeModal = () => {
  if (isLoading.value) return;
  emit("close");
};

const handleSubmit = async () => {
  if (isLoading.value) return;
  isLoading.value = true;
  let isSuccess = false;

  try {
    if (isEditing.value && props.nhanVien) {
      const payload: CapNhatNhanVienRequest = {
        hoTen: formData.hoTen,
        email: formData.email,
        soDienThoai: formData.soDienThoai,
        viTri: formData.viTri,
        trangThai: formData.trangThai,
      };
      await capNhatNhanVien(props.nhanVien.maNhanVien, payload);
      showToast({
        loai: "thanhCong",
        thongBao: "Cập nhật nhân viên thành công.",
      });
    } else {
      const payload: ThemNhanVienRequest = { ...formData };
      await themNhanVien(payload);
      showToast({ loai: "thanhCong", thongBao: "Thêm nhân viên thành công." });
    }
    isSuccess = true;
  } catch (error: any) {
    const message = error.response?.data?.message || "Có lỗi xảy ra.";
    showToast({ loai: "loi", thongBao: message });
  } finally {
    isLoading.value = false;
  }

  if (isSuccess) {
    emit("success");
    closeModal();
  }
};
</script>
