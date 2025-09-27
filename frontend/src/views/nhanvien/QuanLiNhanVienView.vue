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
        class="cursor-pointer bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded inline-flex items-center"
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
            class="cursor-pointer mt-1 block w-48 border border-gray-300 bg-white rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm px-3 py-2"
            :disabled="areOtherFiltersDisabled"
            :class="{
              'bg-gray-100 cursor-not-allowed': areOtherFiltersDisabled,
            }"
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
            id="trang-thai"
            v-model="selectedTrangThai"
            class="cursor-pointer mt-1 block w-48 border border-gray-300 bg-white rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm px-3 py-2"
            :disabled="areOtherFiltersDisabled"
            :class="{
              'bg-gray-100 cursor-not-allowed': areOtherFiltersDisabled,
            }"
          >
            <option value="">Tất cả</option>
            <option
              v-for="(label, key) in TrangThaiTaiKhoanLabel"
              :key="key"
              :value="key"
            >
              {{ label }}
            </option>
          </select>
        </div>
      </div>

      <!-- Thanh tìm kiếm -->
      <ThanhTimKiem
        :ds-tieu-chi="dsTieuChiTimKiem"
        v-model:modelValueLoai="loaiTimKiem"
        v-model:modelValueTuKhoa="tuKhoa"
        @idSearchActive="areOtherFiltersDisabled = $event"
      />
    </div>

    <!-- Chú thích số lượng -->
    <div class="mb-4">
      <p class="text-base text-gray-600">
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
          class="cursor-pointer text-blue-600 hover:text-blue-800 font-medium"
        >
          <i class="fi fi-rr-pencil text-base"></i>
        </button>
      </template>
    </DataTable>

    <!-- Phân trang -->
    <div class="mt-4 flex justify-center">
      <PhanTrang
        v-model:trangHienTai="trangHienTai"
        :tong-so-trang="tongSoTrang"
      />
    </div>

    <!-- Modal thêm/sửa nhân viên -->
    <ThemSuaNhanVienModal
      :visible="isModalVisible"
      :nhan-vien="selectedNhanVien"
      @close="closeModal"
      @success="handleSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import ThanhTimKiem from "@/components/base/ThanhTimKiem.vue";
import DataTable from "@/components/base/DataTable.vue";
import PhanTrang from "@/components/base/PhanTrang.vue";
import ThemSuaNhanVienModal from "@/components/quanlitaikhoan/ThemSuaNhanVienModal.vue";
import { ViTriLabel, ViTriKey } from "@/types/nhanvien.types";
import {
  TrangThaiTaiKhoanKey,
  TrangThaiTaiKhoanLabel,
} from "@/types/khachhang.types";
import type {
  HienThiDanhSachNhanVienResponse,
  GetNhanVienParams,
} from "@/types/nhanvien.types";
import { getDanhSachNhanVien } from "@/service/quanlinhanvien.service";

// State cho bộ lọc, tìm kiếm
const selectedViTri = ref<ViTriKey | "">("");
const selectedTrangThai = ref<TrangThaiTaiKhoanKey | "">("");
const loaiTimKiem = ref<"hoTen" | "email" | "soDienThoai" | "maNhanVien">(
  "hoTen"
);
const tuKhoa = ref("");
const isLoading = ref(false);
const areOtherFiltersDisabled = ref(false);
const debounceTimer = ref<ReturnType<typeof setTimeout> | undefined>(undefined);
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
  { value: "maNhanVien", label: "Mã nhân viên", isId: true },
];

// Fetch danh sách nhân viên từ API
const fetchNhanVien = async () => {
  isLoading.value = true;
  try {
    const params: GetNhanVienParams = {
      page: trangHienTai.value,
      size: soLuongMoiTrang.value,
      searchField: tuKhoa.value ? loaiTimKiem.value : undefined,
      searchValue: tuKhoa.value || undefined,
      viTri: areOtherFiltersDisabled.value
        ? undefined
        : selectedViTri.value || undefined,
      trangThai: areOtherFiltersDisabled.value
        ? undefined
        : selectedTrangThai.value || undefined,
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

// --- Watchers ---

// 1. Watch for search keyword changes with a debounce
watch(tuKhoa, () => {
  clearTimeout(debounceTimer.value);
  debounceTimer.value = setTimeout(() => {
    if (trangHienTai.value !== 0) {
      trangHienTai.value = 0;
    } else {
      fetchNhanVien();
    }
  }, 300);
});

// 2. Watch for other filters to apply immediately
watch([selectedViTri, selectedTrangThai], () => {
  if (trangHienTai.value !== 0) {
    trangHienTai.value = 0;
  } else {
    fetchNhanVien();
  }
});

// 3. When the current page changes, fetch data
watch(trangHienTai, fetchNhanVien);

// 4. When changing search type, handle cleanup logic
watch(loaiTimKiem, (newLoai) => {
  const isIdSearch =
    dsTieuChiTimKiem.find((t) => t.value === newLoai)?.isId ?? false;
  areOtherFiltersDisabled.value = isIdSearch;
  tuKhoa.value = ""; // This will trigger the debounced watcher

  if (isIdSearch) {
    selectedViTri.value = "";
    selectedTrangThai.value = "";
  }
});

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

// Hàm xử lý khi thêm/sửa thành công
const handleSuccess = () => {
  // Tải lại danh sách nhân viên để hiển thị dữ liệu mới nhất
  fetchNhanVien();
};

// 5. Initial data load
fetchNhanVien();
</script>
