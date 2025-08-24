<template>
  <BaseModal
    :visible="visible"
    :title="tieuDeModal"
    @close="dongModal"
    width-class="w-[600px]"
  >
    <div class="space-y-4">
      <!-- Tên khách hàng -->
      <div>
        <label
          class="flex items-center gap-2 text-sm font-medium text-gray-700"
        >
          <i class="fi fi-rr-user text-base text-gray-400"></i>
          <span>Tên khách hàng</span>
        </label>
        <p class="mt-1 pl-6 text-sm text-gray-900">
          {{ formData.tenKhachHang }}
        </p>
      </div>

      <!-- Email -->
      <div>
        <label
          class="flex items-center gap-2 text-sm font-medium text-gray-700"
        >
          <i class="fi fi-rr-envelope text-base text-gray-400"></i>
          <span>Email</span>
        </label>
        <p class="mt-1 pl-6 text-sm text-gray-900">{{ formData.email }}</p>
      </div>

      <!-- Số điện thoại -->
      <div>
        <label
          class="flex items-center gap-2 text-sm font-medium text-gray-700"
        >
          <i class="fi fi-rr-phone-call text-base text-gray-400"></i>
          <span>Số điện thoại</span>
        </label>
        <p class="mt-1 pl-6 text-sm text-gray-900">
          {{ formData.soDienThoai }}
        </p>
      </div>

      <!-- Địa chỉ -->
      <div>
        <label
          class="flex items-center gap-2 text-sm font-medium text-gray-700"
        >
          <i class="fi fi-rr-marker text-base text-gray-400"></i>
          <span>Địa chỉ</span>
        </label>
        <p class="mt-1 pl-6 text-sm text-gray-900">{{ formData.diaChi }}</p>
      </div>

      <!-- Trạng thái tài khoản -->
      <div>
        <label
          class="flex items-center gap-2 text-sm font-medium text-gray-700"
        >
          <i class="fi fi-rr-user-gear text-base text-gray-400"></i>
          <span>Trạng thái tài khoản</span>
        </label>
        <div class="mt-2 pl-6">
          <label
            for="trang-thai-toggle"
            class="relative inline-flex items-center cursor-pointer"
          >
            <input
              type="checkbox"
              id="trang-thai-toggle"
              class="sr-only peer"
              v-model="trangThaiHoatDong"
            />
            <div
              class="w-11 h-6 bg-gray-200 rounded-full peer peer-focus:ring-4 peer-focus:ring-blue-300 peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-0.5 after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-green-500"
            ></div>
            <span class="ml-3 text-sm font-medium text-gray-900">{{
              trangThaiLabel
            }}</span>
          </label>
        </div>
      </div>
    </div>

    <!-- Footer -->
    <template #footer>
      <div class="flex justify-end">
        <button
          @click="luuThayDoi"
          :disabled="!hasChanged"
          class="cursor-pointer px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:bg-gray-400 disabled:cursor-not-allowed"
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
import type {
  HienThiDanhSachKhachHangResponse,
  CapNhatTrangThaiTaiKhoanRequest,
} from "@/types/khachhang.types";
import {
  TrangThaiTaiKhoanKey,
  TrangThaiTaiKhoanLabel,
} from "@/types/khachhang.types";

const props = defineProps<{
  visible: boolean;
  khachHang: HienThiDanhSachKhachHangResponse | null;
}>();
const emit = defineEmits<{
  (e: "close"): void;
  (
    e: "save",
    payload: {
      maKhachHang: number;
      data: CapNhatTrangThaiTaiKhoanRequest;
    }
  ): void;
}>();

const formData = ref({
  tenKhachHang: "",
  email: "",
  diaChi: "",
  soDienThoai: "",
  trangThaiTaiKhoan: TrangThaiTaiKhoanKey.HOAT_DONG,
});

// This ref will hold the state of the customer when the modal was opened.
const initialTrangThai = ref<TrangThaiTaiKhoanKey | null>(null);

const tieuDeModal = computed(
  () => `Cập nhật thông tin - ${props.khachHang?.tenKhachHang ?? "Khách hàng"}`
);

const trangThaiHoatDong = computed({
  get: () =>
    formData.value.trangThaiTaiKhoan === TrangThaiTaiKhoanKey.HOAT_DONG,
  set: (value) => {
    formData.value.trangThaiTaiKhoan = value
      ? TrangThaiTaiKhoanKey.HOAT_DONG
      : TrangThaiTaiKhoanKey.KHOA;
  },
});

const trangThaiLabel = computed(() => {
  return TrangThaiTaiKhoanLabel[formData.value.trangThaiTaiKhoan];
});

const hasChanged = computed(() => {
  if (!props.khachHang) {
    return false;
  }
  // Compare the current form status with the initial status
  return formData.value.trangThaiTaiKhoan !== initialTrangThai.value;
});

const dongModal = () => emit("close");

const luuThayDoi = () => {
  if (!props.khachHang || !hasChanged.value) return;
  const data: CapNhatTrangThaiTaiKhoanRequest = {
    trangThai: formData.value.trangThaiTaiKhoan,
  };
  emit("save", { maKhachHang: props.khachHang.maKhachHang, data });
  dongModal();
};

watch(
  () => props.visible,
  (isVisible) => {
    if (isVisible && props.khachHang) {
      // Populate form with current customer data
      formData.value.tenKhachHang = props.khachHang.tenKhachHang;
      formData.value.email = props.khachHang.email;
      formData.value.diaChi = props.khachHang.diaChi;
      formData.value.soDienThoai = props.khachHang.soDienThoai;
      formData.value.trangThaiTaiKhoan =
        props.khachHang.trangThaiTaiKhoan.value;

      // Store the initial state for comparison
      initialTrangThai.value = props.khachHang.trangThaiTaiKhoan.value;
    }
  }
);
</script>
