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
        <label class="block text-sm font-medium text-gray-700">
          Tên khách hàng
        </label>
        <p class="mt-1 text-sm text-gray-900">{{ formData.tenKhachHang }}</p>
      </div>

      <!-- Email -->
      <div>
        <label class="block text-sm font-medium text-gray-700">Email</label>
        <p class="mt-1 text-sm text-gray-900">{{ formData.email }}</p>
      </div>

      <!-- Số điện thoại -->
      <div>
        <label class="block text-sm font-medium text-gray-700">
          Số điện thoại
        </label>
        <p class="mt-1 text-sm text-gray-900">{{ formData.soDienThoai }}</p>
      </div>

      <!-- Địa chỉ -->
      <div>
        <label class="block text-sm font-medium text-gray-700">Địa chỉ</label>
        <p class="mt-1 text-sm text-gray-900">{{ formData.diaChi }}</p>
      </div>

      <!-- Trạng thái tài khoản -->
      <div>
        <label class="block text-sm font-medium text-gray-700">
          Trạng thái tài khoản
        </label>
        <div class="mt-2">
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
              class="w-11 h-6 bg-gray-200 rounded-full peer peer-focus:ring-4 peer-focus:ring-blue-300 peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-0.5 after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"
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

const dongModal = () => emit("close");

const luuThayDoi = () => {
  if (!props.khachHang) return;
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
      formData.value.tenKhachHang = props.khachHang.tenKhachHang;
      formData.value.email = props.khachHang.email;
      formData.value.diaChi = props.khachHang.diaChi;
      formData.value.soDienThoai = props.khachHang.soDienThoai;
      formData.value.trangThaiTaiKhoan =
        props.khachHang.trangThaiTaiKhoan.value;
    }
  }
);
</script>
