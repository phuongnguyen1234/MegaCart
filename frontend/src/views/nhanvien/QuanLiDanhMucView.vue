<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-4">Quản lí danh mục</h1>

    <!-- Chức năng -->
    <div class="flex justify-between items-center mb-4">
      <!-- Nút thêm danh mục -->
      <button
        @click="openAddModal"
        class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded inline-flex items-center"
      >
        <i class="fi fi-rr-plus-small align-middle mr-1 -ml-1"></i>
        <span>Thêm danh mục</span>
      </button>

      <!-- Thanh tìm kiếm -->
      <ThanhTimKiem
        :ds-tieu-chi="[
          { value: 'maDanhMuc', label: 'Mã danh mục' },
          { value: 'tenDanhMuc', label: 'Tên danh mục' },
        ]"
        v-model:modelValueLoai="loaiTimKiem"
        v-model:modelValueTuKhoa="tuKhoa"
      />
    </div>

    <!-- Bảng dữ liệu danh mục -->
    <div class="bg-white shadow-md rounded-lg overflow-hidden">
      <DataTable :headers="headers" :rows="rows">
        <template #cell-4="{ value }">
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

    <!-- Modal Thêm/Sửa danh mục -->
    <ThemSuaDanhMucModal
      :visible="isModalVisible"
      :title="modalTitle"
      :is-edit-mode="isEditMode"
      width-class="w-[500px]"
      @close="closeModal"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import ThanhTimKiem from "@/components/base/ThanhTimKiem.vue";
import DataTable from "@/components/base/DataTable.vue";
import PhanTrang from "@/components/base/PhanTrang.vue";
import ThemSuaDanhMucModal from "@/components/quanlisanpham_danhmuc/ThemSuaDanhMucModal.vue";
import type { DanhMuc } from "@/types/DanhMuc";

// --- State ---
const loaiTimKiem = ref("tenDanhMuc");
const tuKhoa = ref("");
const isModalVisible = ref(false);
const isEditMode = ref(false);
const selectedCategory = ref<DanhMuc | null>(null);

// --- Modal Logic ---
const modalTitle = computed(() =>
  isEditMode.value ? "Sửa thông tin danh mục" : "Thêm danh mục mới"
);

const openAddModal = () => {
  isEditMode.value = false;
  selectedCategory.value = null;
  isModalVisible.value = true;
};

const openEditModal = (category: DanhMuc) => {
  isEditMode.value = true;
  selectedCategory.value = category;
  isModalVisible.value = true;
};

const handleEditClick = (categoryId: any) => {
  const categoryToEdit = allDanhMuc.value.find(
    (p) => p.maDanhMuc === categoryId
  );
  if (categoryToEdit) {
    openEditModal(categoryToEdit);
  }
};

const closeModal = () => {
  isModalVisible.value = false;
};

// --- Dữ liệu mẫu ---
const allDanhMuc = ref<DanhMuc[]>([
  {
    maDanhMuc: "DT",
    tenDanhMuc: "Điện tử",
    danhMucCha: null,
    trangThai: "Đang hoạt động",
  },
  {
    maDanhMuc: "DT_DIENTHOAI",
    tenDanhMuc: "Điện thoại",
    danhMucCha: "Điện tử",
    trangThai: "Đang hoạt động",
  },
  {
    maDanhMuc: "DT_LAPTOP",
    tenDanhMuc: "Laptop",
    danhMucCha: "Điện tử",
    trangThai: "Đang hoạt động",
  },
  {
    maDanhMuc: "DT_TABLET",
    tenDanhMuc: "Máy tính bảng",
    danhMucCha: "Điện tử",
    trangThai: "Ngừng hoạt động",
  },
  {
    maDanhMuc: "TT",
    tenDanhMuc: "Thời trang",
    danhMucCha: null,
    trangThai: "Đang hoạt động",
  },
  {
    maDanhMuc: "TT_NAM",
    tenDanhMuc: "Thời trang nam",
    danhMucCha: "Thời trang",
    trangThai: "Đang hoạt động",
  },
  {
    maDanhMuc: "TT_NU",
    tenDanhMuc: "Thời trang nữ",
    danhMucCha: "Thời trang",
    trangThai: "Đang hoạt động",
  },
]);

// --- Computed Properties ---
const danhMucDaLoc = computed(() => {
  if (!tuKhoa.value.trim()) {
    return allDanhMuc.value;
  }
  const keyword = tuKhoa.value.toLowerCase();
  return allDanhMuc.value.filter((dm) => {
    if (loaiTimKiem.value === "maDanhMuc") {
      return String(dm.maDanhMuc).toLowerCase().includes(keyword);
    }
    return dm.tenDanhMuc.toLowerCase().includes(keyword);
  });
});

// --- Phân trang ---
const trangHienTai = ref(0);
const soLuongMoiTrang = ref(10);

const tongSoTrang = computed(() =>
  Math.ceil(danhMucDaLoc.value.length / soLuongMoiTrang.value)
);

const danhMucHienThi = computed(() => {
  const batDau = trangHienTai.value * soLuongMoiTrang.value;
  const ketThuc = batDau + soLuongMoiTrang.value;
  return danhMucDaLoc.value.slice(batDau, ketThuc);
});

// --- Cấu hình DataTable ---
const headers = [
  "Mã danh mục",
  "Tên danh mục",
  "Danh mục cha",
  "Trạng thái",
  "Hành động",
];

const rows = computed(() =>
  danhMucHienThi.value.map((dm) => [
    dm.maDanhMuc,
    dm.tenDanhMuc,
    dm.danhMucCha || "—",
    dm.trangThai,
    dm.maDanhMuc,
  ])
);

// --- Watchers ---
watch(danhMucDaLoc, () => {
  trangHienTai.value = 0;
});
</script>
