<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-4">Quản lí sản phẩm</h1>

    <!-- Chức năng -->
    <div
      class="flex flex-wrap items-end gap-4 mb-4 bg-white p-4 rounded-lg shadow"
    >
      <!-- Nút thêm sản phẩm -->
      <button
        @click="openAddModal"
        class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded inline-flex items-center"
      >
        <i class="fi fi-rr-plus-small align-middle mr-1 -ml-1"></i>
        <span>Thêm sản phẩm</span>
      </button>

      <!-- Bộ lọc danh mục -->
      <BoLocDanhMuc
        v-model:parent="selectedDanhMucCha"
        v-model:child="selectedDanhMucCon"
      />

      <!-- Bộ lọc theo trạng thái -->
      <div>
        <label
          for="trang-thai-filter"
          class="block text-sm font-medium text-gray-700"
        >
          Trạng thái
        </label>
        <select
          id="trang-thai-filter"
          v-model="selectedTrangThai"
          class="mt-1 block w-48 border border-gray-300 bg-white rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm px-3 py-2"
        >
          <option value="">Tất cả</option>
          <option
            v-for="(label, key) in TrangThaiSanPhamLabel"
            :key="key"
            :value="key"
          >
            {{ label }}
          </option>
        </select>
      </div>

      <!-- Thanh tìm kiếm -->
      <ThanhTimKiem
        :ds-tieu-chi="[{ value: 'tenSanPham', label: 'Tên sản phẩm' }]"
        v-model:modelValueLoai="loaiTimKiem"
        v-model:modelValueTuKhoa="tuKhoa"
      />
    </div>

    <!-- Bảng dữ liệu sản phẩm -->
    <div class="bg-white shadow-md rounded-lg overflow-hidden">
      <DataTable :headers="headers" :rows="rows" :is-loading="isLoading">
        <template #cell-1="{ value }">
          <img
            :src="value"
            alt="Ảnh sản phẩm"
            class="w-16 h-16 object-cover rounded border"
          />
        </template>
        <template #cell-6="{ value: trangThai }">
          <span
            :class="[
              'px-2 inline-flex text-xs leading-5 font-semibold rounded-full',
              trangThai.value === TrangThaiSanPhamKey.BAN
                ? 'bg-green-100 text-green-800'
                : 'bg-red-100 text-red-800',
            ]"
            >{{ trangThai.label }}</span
          >
        </template>
        <template #cell-7="{ value }">
          <button
            @click="handleEditClick(value)"
            class="text-blue-600 hover:text-blue-800 font-semibold"
          >
            Sửa
          </button>
        </template>
      </DataTable>
    </div>
    <!-- Phân trang -->
    <div class="mt-4 flex justify-center">
      <PhanTrang
        v-model:trangHienTai="trangHienTai"
        :tong-so-trang="tongSoTrang"
      />
    </div>

    <!-- Modal Thêm/Sửa sản phẩm -->
    <ThemSuaSanPhamModal
      :visible="isModalVisible"
      :title="modalTitle"
      :is-edit-mode="isEditMode"
      width-class="w-[800px]"
      @close="closeModal"
      :san-pham-sua="selectedProduct"
      @success="handleSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import ThanhTimKiem from "@/components/base/ThanhTimKiem.vue";
import DataTable from "@/components/base/DataTable.vue";
import PhanTrang from "@/components/base/PhanTrang.vue";
import BoLocDanhMuc from "@/components/base/BoLocDanhMuc.vue";
import ThemSuaSanPhamModal from "@/components/quanlisanpham_danhmuc/ThemSuaSanPhamModal.vue";
import {
  getDanhSachSanPhamQuanLy,
  getChiTietSanPhamQuanLy,
} from "@/service/sanpham.service";
import {
  TrangThaiSanPhamKey,
  TrangThaiSanPhamLabel,
} from "@/types/sanpham.types";
import type {
  SanPhamQuanLyResponse,
  GetSanPhamQuanLyParams,
  ChiTietSanPhamQuanLyResponse,
} from "@/types/sanpham.types";
import { useToast } from "@/composables/useToast";

// --- State ---
const loaiTimKiem = ref("tenSanPham");
const selectedDanhMucCha = ref<number | "">("");
const selectedDanhMucCon = ref<number | "">("");
const selectedTrangThai = ref<TrangThaiSanPhamKey | "">("");
const tuKhoa = ref("");
const isModalVisible = ref(false);
const isEditMode = ref(false);
const selectedProduct = ref<ChiTietSanPhamQuanLyResponse | null>(null);
const isLoading = ref(false);
const { showToast } = useToast();

const allSanPham = ref<SanPhamQuanLyResponse[]>([]);

// Computed property để xác định mã danh mục cần lọc
const activeMaDanhMuc = computed(() => {
  // Ưu tiên lọc theo danh mục con nếu được chọn
  return selectedDanhMucCon.value || selectedDanhMucCha.value || undefined;
});

// --- Modal Logic ---
const modalTitle = computed(() =>
  isEditMode.value ? "Sửa thông tin sản phẩm" : "Thêm sản phẩm mới"
);

const openAddModal = () => {
  isEditMode.value = false;
  selectedProduct.value = null;
  isModalVisible.value = true;
};

const openEditModal = (product: ChiTietSanPhamQuanLyResponse) => {
  isEditMode.value = true;
  selectedProduct.value = product;
  isModalVisible.value = true;
};

const handleEditClick = async (productId: number) => {
  try {
    // Gọi API để lấy dữ liệu chi tiết của sản phẩm
    const productDetails = await getChiTietSanPhamQuanLy(productId);
    openEditModal(productDetails);
  } catch (error) {
    console.error("Lỗi khi lấy chi tiết sản phẩm:", error);
    showToast({
      loai: "loi",
      thongBao: "Không thể tải dữ liệu sản phẩm để sửa.",
    });
  }
};

const closeModal = () => {
  isModalVisible.value = false;
  selectedProduct.value = null; // Xóa dữ liệu sản phẩm khi đóng modal
};

// --- Phân trang ---
const trangHienTai = ref(0);
const soLuongMoiTrang = ref(10);
const tongSoTrang = ref(1);

// --- Cấu hình DataTable ---
const headers = [
  "Mã sản phẩm",
  "Ảnh sản phẩm",
  "Tên sản phẩm",
  "Danh mục cha",
  "Danh mục con",
  "Đơn giá",
  "Trạng thái",
  "Hành động",
];

const formatCurrency = (value: number): string => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(value);
};

const rows = computed(() =>
  allSanPham.value.map((sp) => [
    sp.maSanPham,
    sp.anhMinhHoaChinh,
    sp.tenSanPham,
    sp.danhMucCha || "—", // Hiển thị fallback nếu không có danh mục cha
    sp.danhMucCon || "—", // Hiển thị fallback nếu không có danh mục con
    formatCurrency(sp.donGia),
    sp.trangThai, // Truyền cả object để slot tùy chỉnh
    sp.maSanPham,
  ])
);

// --- Fetch dữ liệu ---
const fetchSanPham = async () => {
  isLoading.value = true;
  try {
    const params: GetSanPhamQuanLyParams = {
      page: trangHienTai.value,
      size: soLuongMoiTrang.value,
      tuKhoa: tuKhoa.value || undefined,
      maDanhMuc: activeMaDanhMuc.value,
      trangThai: selectedTrangThai.value || undefined,
    };
    const response = await getDanhSachSanPhamQuanLy(params);
    allSanPham.value = response.content;
    tongSoTrang.value = response.totalPages;
  } catch (error) {
    console.error("Lỗi khi lấy danh sách sản phẩm:", error);
    showToast({
      loai: "loi",
      thongBao: "Không thể tải danh sách sản phẩm.",
    });
    allSanPham.value = [];
    tongSoTrang.value = 1;
  } finally {
    isLoading.value = false;
  }
};

// Hàm xử lý khi thêm/sửa thành công
const handleSuccess = () => {
  fetchSanPham(); // Tải lại danh sách sản phẩm
};

watch(
  [trangHienTai, tuKhoa, activeMaDanhMuc, selectedTrangThai],
  fetchSanPham,
  {
    immediate: true,
  }
);

// Reset về trang đầu tiên khi người dùng thay đổi bộ lọc
watch(
  [tuKhoa, activeMaDanhMuc, selectedTrangThai],
  () => (trangHienTai.value = 0)
);
</script>
