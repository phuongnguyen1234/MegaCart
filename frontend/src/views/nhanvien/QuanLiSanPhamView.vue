<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-4">Quản lí sản phẩm</h1>

    <!-- Chức năng -->
    <div
      class="flex justify-between items-center mb-4 bg-white p-4 rounded-lg shadow"
    >
      <!-- Nút thêm sản phẩm -->
      <button
        @click="openAddModal"
        class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded inline-flex items-center"
      >
        <i class="fi fi-rr-plus-small align-middle mr-1 -ml-1"></i>
        <span>Thêm sản phẩm</span>
      </button>

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
            :class="
              trangThai.value === 'BAN'
                ? 'bg-green-100 text-green-800'
                : 'bg-red-100 text-red-800'
            "
            class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
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
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import ThanhTimKiem from "@/components/base/ThanhTimKiem.vue";
import DataTable from "@/components/base/DataTable.vue";
import PhanTrang from "@/components/base/PhanTrang.vue";
import ThemSuaSanPhamModal from "@/components/quanlisanpham_danhmuc/ThemSuaSanPhamModal.vue";
import { getDanhSachSanPhamQuanLy } from "@/service/sanpham.service";
import type {
  SanPhamQuanLyResponse,
  GetSanPhamQuanLyParams,
} from "@/types/sanpham.types";
import { useToast } from "@/composables/useToast";

// --- State ---
const loaiTimKiem = ref("tenSanPham");
const tuKhoa = ref("");
const isModalVisible = ref(false);
const isEditMode = ref(false);
const selectedProduct = ref<SanPhamQuanLyResponse | null>(null);
const isLoading = ref(false);
const { showToast } = useToast();

const allSanPham = ref<SanPhamQuanLyResponse[]>([]);

// --- Modal Logic ---
const modalTitle = computed(() =>
  isEditMode.value ? "Sửa thông tin sản phẩm" : "Thêm sản phẩm mới"
);

const openAddModal = () => {
  isEditMode.value = false;
  selectedProduct.value = null;
  isModalVisible.value = true;
};

const openEditModal = (product: SanPhamQuanLyResponse) => {
  isEditMode.value = true;
  selectedProduct.value = product;
  isModalVisible.value = true;
};

const handleEditClick = (productId: any) => {
  const productToEdit = allSanPham.value.find((p) => p.maSanPham === productId);
  if (productToEdit) {
    openEditModal(productToEdit);
  }
};

const closeModal = () => {
  isModalVisible.value = false;
};

// --- Phân trang ---
const trangHienTai = ref(0);
const soLuongMoiTrang = ref(10);
const tongSoTrang = ref(1);

// --- Cấu hình DataTable ---
const headers = [
  "Mã sản phẩm",
  "Ảnh minh họa",
  "Tên sản phẩm",
  "Danh mục",
  "Đơn giá",
  "Tồn kho",
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
    sp.tenDanhMuc,
    formatCurrency(sp.donGia),
    sp.soLuongTon,
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

watch([trangHienTai, tuKhoa], fetchSanPham, { immediate: true });
</script>
