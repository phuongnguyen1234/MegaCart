<template>
  <div class="p-4 md:p-6">
    <h1 class="text-2xl font-bold mb-4">Quản lí Nhân viên</h1>

    <!-- Thanh công cụ: Thêm, Lọc, Tìm kiếm -->
    <div
      class="flex flex-wrap items-end gap-4 mb-4 bg-white p-4 rounded-lg shadow"
    >
      <!-- Nút thêm nhân viên -->
      <button
        @click="openAddModal"
        class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded inline-flex items-center"
      >
        <i class="fi fi-rr-plus-small align-middle mr-1 -ml-1"></i>
        <span>Thêm nhân viên</span>
      </button>

      <div class="flex flex-wrap items-end gap-4">
        <!-- Lọc theo vị trí -->
        <div>
          <label for="vi-tri" class="block text-sm font-medium text-gray-700">
            Vị trí
          </label>
          <select
            id="vi-tri"
            v-model="selectedViTri"
            class="mt-1 block w-48 border border-gray-300 bg-white rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm px-3 py-2"
          >
            <option value="">Tất cả</option>
            <option v-for="(label, key) in ViTriLabel" :key="key" :value="key">
              {{ label }}
            </option>
          </select>
        </div>

        <!-- Lọc theo trạng thái -->
        <div>
          <label
            for="trang-thai"
            class="block text-sm font-medium text-gray-700"
          >
            Trạng thái tài khoản
          </label>
          <select
            id="trang-thai-filter"
            v-model="trangThaiFilter"
            class="border border-gray-300 rounded-md bg-gray-50 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 shadow-sm"
          >
            <option value="all">Tất cả</option>
            <option value="Hoạt động">Hoạt động</option>
            <option value="Khóa">Khóa</option>
          </select>
        </div>
      </div>

      <!-- Thanh tìm kiếm -->
      <ThanhTimKiem
        :ds-tieu-chi="dsTieuChiTimKiem"
        v-model:modelValueLoai="loaiTimKiem"
        v-model:modelValueTuKhoa="tuKhoa"
      />
    </div>

    <!-- Chú thích số lượng -->
    <div class="mb-4">
      <p class="text-sm text-gray-600">
        {{ thongTinHienThi }}
      </p>
    </div>

    <!-- Bảng dữ liệu nhân viên -->
    <DataTable
      :headers="tableHeaders"
      :rows="tableRows"
      :is-loading="isLoading"
    >
      <!-- Tùy chỉnh cột Vị trí -->
      <template #cell-4="{ value: viTri }">
        <span
          :class="getViTriClass(viTri.value)"
          class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
        >
          {{ viTri.label }}
        </span>
      </template>

      <!-- Tùy chỉnh cột Trạng thái tài khoản -->
      <template #cell-5="{ value: trangThai }">
        <span
          :class="
            trangThai.value === TrangThaiTaiKhoanKey.HOAT_DONG
              ? 'bg-green-100 text-green-800'
              : 'bg-red-100 text-red-800'
          "
          class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
        >
          {{ trangThai.label }}
        </span>
      </template>

      <!-- Tùy chỉnh cột Hành động -->
      <template #cell-6="{ value: nhanVien }">
        <button
          @click="handleEdit(nhanVien)"
          class="text-indigo-600 hover:text-indigo-900 font-medium"
        >
          Sửa
        </button>
      </template>
    </DataTable>

    <!-- Modal thêm/sửa nhân viên -->
    <ThemSuaNhanVienModal
      :visible="isModalVisible"
      :nhan-vien="selectedNhanVien"
      @close="closeModal"
      @save="handleSave"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import ThanhTimKiem from "@/components/base/ThanhTimKiem.vue";
import DataTable from "@/components/base/DataTable.vue";
import ThemSuaNhanVienModal from "@/components/quanlitaikhoan/ThemSuaNhanVienModal.vue";
import { ViTriLabel, ViTriKey } from "@/types/nhanvien.types";
import {
  TrangThaiTaiKhoanKey,
  TrangThaiTaiKhoanLabel,
} from "@/types/khachhang.types";
import type { HienThiDanhSachNhanVienResponse } from "@/types/nhanvien.types";
import { getDanhSachNhanVien } from "@/service/quanlinhanvien.service";

// State cho bộ lọc, tìm kiếm
const selectedViTri = ref<string | "">("");
const trangThaiFilter = ref<string>("all");
const loaiTimKiem = ref<"hoTen" | "email" | "soDienThoai">("hoTen");
const tuKhoa = ref("");
const isLoading = ref(false);

// Dữ liệu danh sách nhân viên
const danhSachNhanVien = ref<HienThiDanhSachNhanVienResponse[]>([]);
const tongSoTrang = ref(1);
const trangHienTai = ref(0);
const soLuongMoiTrang = ref(10);

// Tiêu chí tìm kiếm
const dsTieuChiTimKiem = [
  { value: "hoTen", label: "Tên nhân viên" },
  { value: "email", label: "Email" },
  { value: "soDienThoai", label: "Số điện thoại" },
];

// Fetch danh sách nhân viên từ API
const fetchNhanVien = async () => {
  isLoading.value = true;
  try {
    const params = {
      page: trangHienTai.value,
      size: soLuongMoiTrang.value,
      searchField: tuKhoa.value ? loaiTimKiem.value : undefined,
      searchValue: tuKhoa.value || undefined,
      viTri: selectedViTri.value ? selectedViTri.value : undefined,
      trangThai:
        trangThaiFilter.value === "all"
          ? undefined
          : trangThaiFilter.value === "Hoạt động"
          ? TrangThaiTaiKhoanKey.HOAT_DONG
          : TrangThaiTaiKhoanKey.KHOA,
    };
    const response = await getDanhSachNhanVien(params);
    danhSachNhanVien.value = response.content;
    tongSoTrang.value = response.totalPages;
  } catch (error) {
    danhSachNhanVien.value = [];
    tongSoTrang.value = 1;
  } finally {
    isLoading.value = false;
  }
};

// Theo dõi thay đổi bộ lọc/tìm kiếm để tự động fetch lại
watch(
  [selectedViTri, trangThaiFilter, loaiTimKiem, tuKhoa, trangHienTai],
  fetchNhanVien,
  { immediate: true }
);

// Hiển thị số lượng
const thongTinHienThi = computed(() => {
  if (danhSachNhanVien.value.length === 0)
    return "Không có nhân viên nào được tìm thấy";
  const start = trangHienTai.value * soLuongMoiTrang.value + 1;
  const end = start + danhSachNhanVien.value.length - 1;
  return `Đang hiển thị ${start} - ${end} nhân viên`;
});

// Table headers & rows
const tableHeaders = [
  "Mã nhân viên",
  "Họ tên",
  "Email",
  "Số điện thoại",
  "Vị trí",
  "Trạng thái",
  "Hành động",
];

const tableRows = computed(() =>
  danhSachNhanVien.value.map((nv) => [
    nv.maNhanVien,
    nv.tenNhanVien,
    nv.email,
    nv.soDienThoai,
    nv.viTri,
    nv.trangThaiTaiKhoan,
    nv,
  ])
);

// Helper cho class vị trí
const getViTriClass = (viTri: string) => {
  switch (viTri) {
    case ViTriKey.NHAN_VIEN_QUAN_LI_DON:
      return "bg-blue-100 text-blue-800";
    case ViTriKey.NHAN_VIEN_GIAO_HANG:
      return "bg-yellow-100 text-yellow-800";
    case ViTriKey.NHAN_VIEN_QUAN_LI_KHO:
      return "bg-indigo-100 text-indigo-800";
    default:
      return "bg-gray-100 text-gray-800";
  }
};

// --- Modal Control Functions ---
// Chỉ giữ lại logic mở modal, chưa xử lý thêm/sửa
const isModalVisible = ref(false);
const selectedNhanVien = ref<HienThiDanhSachNhanVienResponse | null>(null);

const openAddModal = () => {
  selectedNhanVien.value = null;
  isModalVisible.value = true;
};

const handleEdit = (nhanVien: HienThiDanhSachNhanVienResponse) => {
  selectedNhanVien.value = nhanVien;
  isModalVisible.value = true;
};

const closeModal = () => {
  isModalVisible.value = false;
  selectedNhanVien.value = null;
};

// handleSave sẽ để trống, xử lý sau
const handleSave = () => {};
</script>
