<template>
  <BaseModal
    :visible="visible"
    :title="isEditing ? 'Cập nhật nhân viên' : 'Thêm nhân viên'"
    @close="closeModal"
  >
    <form @submit.prevent="handleSubmit">
      <div class="space-y-4">
        <div>
          <label for="ten" class="block text-sm font-medium text-gray-700"
            >Tên nhân viên</label
          >
          <input
            id="ten"
            v-model="formData.hoTen"
            type="text"
            required
            class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm px-3 py-2"
          />
        </div>

        <div>
          <label for="email" class="block text-sm font-medium text-gray-700"
            >Email</label
          >
          <input
            id="email"
            v-model="formData.email"
            type="email"
            required
            class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm px-3 py-2"
          />
        </div>

        <div>
          <label
            for="soDienThoai"
            class="block text-sm font-medium text-gray-700"
            >Số điện thoại</label
          >
          <input
            id="soDienThoai"
            v-model="formData.soDienThoai"
            type="text"
            required
            class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm px-3 py-2"
          />
        </div>

        <div>
          <label for="viTri" class="block text-sm font-medium text-gray-700"
            >Vị trí</label
          >
          <select
            id="viTri"
            v-model="formData.viTri"
            class="mt-1 block w-full border border-gray-300 bg-white rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm px-3 py-2"
          >
            <option value="Quản lí đơn">Quản lí đơn</option>
            <option value="Giao hàng">Giao hàng</option>
            <option value="Quản lí kho">Quản lí kho</option>
          </select>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700"
            >Trạng thái tài khoản</label
          >
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
                class="w-11 h-6 bg-gray-200 rounded-full peer peer-focus:outline-none peer-focus:ring-2 peer-focus:ring-indigo-500 peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:start-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-green-500"
              ></div>
              <span class="ms-3 text-sm font-medium text-gray-700">{{
                isTrangThaiHoatDong ? "Hoạt động" : "Khóa"
              }}</span>
            </label>
          </div>
        </div>
      </div>

      <div class="mt-4 flex justify-end">
        <button
          type="button"
          @click="closeModal"
          class="bg-gray-300 hover:bg-gray-400 text-gray-800 font-bold py-2 px-4 rounded-lg shadow transition duration-300 ease-in-out mr-2"
        >
          Hủy
        </button>
        <button
          type="submit"
          class="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded-lg shadow transition duration-300 ease-in-out"
        >
          {{ isEditing ? "Cập nhật" : "Thêm" }}
        </button>
      </div>
    </form>
  </BaseModal>
</template>

<script setup lang="ts">
import { ref, watch, computed } from "vue";
import BaseModal from "../base/modals/BaseModal.vue";
import type { NhanVien } from "@/types/QuanLiNhanVien";

const props = defineProps<{
  visible: boolean;
  nhanVien: NhanVien | null;
}>();

const emit = defineEmits<{
  (e: "close"): void;
  (e: "save", data: NhanVien): void;
}>();

const isEditing = ref(false);
const formData = ref<NhanVien>({
  maNhanVien: "",
  hoTen: "",
  email: "",
  soDienThoai: "",
  viTri: "Quản lí đơn",
  trangThai: "Hoạt động",
});

const isTrangThaiHoatDong = computed({
  get: () => formData.value.trangThai === "Hoạt động",
  set: (value) => {
    formData.value.trangThai = value ? "Hoạt động" : "Khóa";
  },
});

watch(
  () => props.nhanVien,
  (newVal) => {
    if (newVal) {
      isEditing.value = true;
      formData.value = { ...newVal };
    } else {
      isEditing.value = false;
      formData.value = {
        maNhanVien: "",
        hoTen: "",
        email: "",
        soDienThoai: "",
        viTri: "Quản lí đơn",
        trangThai: "Hoạt động",
      };
    }
  }
);

const closeModal = () => {
  emit("close");
};

const handleSubmit = () => {
  emit("save", formData.value);
};
</script>
