<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <h1 class="text-2xl font-bold text-gray-800 mb-4">Quản lí nhân viên</h1>

    <!-- Thanh công cụ: Thêm, Lọc, Tìm kiếm -->
    <div
      class="flex flex-wrap items-center justify-between gap-4 bg-white p-4 rounded-lg shadow-sm mb-4"
    >
      <!-- Nút thêm nhân viên -->
      <button
        @click="openAddModal"
        class="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded-lg shadow transition duration-300 ease-in-out"
      >
        + Thêm nhân viên
      </button>

      <div class="flex flex-wrap items-center gap-x-6 gap-y-4">
        <!-- Lọc theo vị trí -->
        <div class="flex items-center gap-2">
          <label for="vi-tri-filter" class="text-sm font-medium text-gray-700"
            >Vị trí:</label
          >
          <select
            id="vi-tri-filter"
            v-model="viTri"
            class="border border-gray-300 rounded-md bg-gray-50 px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 shadow-sm"
          >
            <option value="all">Tất cả</option>
            <option value="Quản lí đơn">Quản lí đơn</option>
            <option value="Giao hàng">Giao hàng</option>
            <option value="Quản lí kho">Quản lí kho</option>
          </select>
        </div>

        <!-- Lọc theo trạng thái -->
        <div class="flex items-center gap-2">
          <label
            for="trang-thai-filter"
            class="text-sm font-medium text-gray-700"
            >Trạng thái:</label
          >
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
        v-model:modelValueTuKhoa="tuKhoaTimKiem"
      />
    </div>

    <!-- Chú thích số lượng -->
    <div class="mb-6 text-sm text-gray-600">
      <p>Đang hiển thị {{ filteredNhanVien.length }} nhân viên.</p>
    </div>

    <!-- Bảng dữ liệu nhân viên -->
    <DataTable :headers="tableHeaders" :rows="tableRows">
      <!-- Tùy chỉnh cột Vị trí -->
      <template #cell-4="{ value: viTriValue }">
        <span
          :class="getViTriClass(viTriValue)"
          class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
        >
          {{ viTriValue }}
        </span>
      </template>

      <!-- Tùy chỉnh cột Trạng thái tài khoản -->
      <template #cell-5="{ value: trangThaiValue }">
        <span
          :class="
            trangThaiValue === 'Hoạt động'
              ? 'bg-green-100 text-green-800'
              : 'bg-red-100 text-red-800'
          "
          class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
        >
          {{ trangThaiValue }}
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
import { ref, computed } from "vue";
import ThanhTimKiem from "@/components/base/ThanhTimKiem.vue";
import DataTable from "@/components/base/DataTable.vue";
import ThemSuaNhanVienModal from "@/components/quanlitaikhoan/ThemSuaNhanVienModal.vue";
import type { NhanVien } from "@/types/QuanLiNhanVien";

// State cho việc lọc và tìm kiếm
const viTri = ref("all"); // Vị trí mặc định
const trangThaiFilter = ref("all"); // Lọc theo trạng thái
const loaiTimKiem = ref("ten"); // Loại tìm kiếm mặc định
const tuKhoaTimKiem = ref(""); // Từ khóa tìm kiếm

// State cho modal
const isModalVisible = ref(false);
const selectedNhanVien = ref<NhanVien | null>(null);

// --- Dữ liệu giả ---
const danhSachNhanVien = ref<NhanVien[]>([
  {
    maNhanVien: "NV001",
    hoTen: "Nguyễn Văn An",
    email: "an.nv@megacart.com",
    soDienThoai: "0901234567",
    viTri: "Quản lí đơn",
    trangThai: "Hoạt động",
  },
  {
    maNhanVien: "NV002",
    hoTen: "Trần Thị Bình",
    email: "binh.tt@megacart.com",
    soDienThoai: "0901234568",
    viTri: "Giao hàng",
    trangThai: "Hoạt động",
  },
  {
    maNhanVien: "NV003",
    hoTen: "Lê Văn Cường",
    email: "cuong.lv@megacart.com",
    soDienThoai: "0901234569",
    viTri: "Quản lí kho",
    trangThai: "Khóa",
  },
  {
    maNhanVien: "NV004",
    hoTen: "Phạm Thị Dung",
    email: "dung.pt@megacart.com",
    soDienThoai: "0901234570",
    viTri: "Quản lí đơn",
    trangThai: "Hoạt động",
  },
]);

// Định nghĩa các tiêu chí tìm kiếm cho component ThanhTimKiem
const dsTieuChiTimKiem = [
  { value: "ten", label: "Tên nhân viên" },
  { value: "ma", label: "Mã nhân viên" },
  { value: "sdt", label: "Số điện thoại" },
];

// --- Computed Properties ---
const filteredNhanVien = computed(() => {
  let result = danhSachNhanVien.value;

  // 1. Lọc theo vị trí
  if (viTri.value !== "all") {
    result = result.filter((nv) => nv.viTri === viTri.value);
  }

  // 2. Lọc theo trạng thái
  if (trangThaiFilter.value !== "all") {
    result = result.filter((nv) => nv.trangThai === trangThaiFilter.value);
  }

  // 3. Lọc theo từ khóa tìm kiếm
  if (tuKhoaTimKiem.value.trim()) {
    const keyword = tuKhoaTimKiem.value.trim().toLowerCase();
    result = result.filter((nv) => {
      switch (loaiTimKiem.value) {
        case "ten":
          return nv.hoTen.toLowerCase().includes(keyword);
        case "ma":
          return nv.maNhanVien.toLowerCase().includes(keyword);
        case "sdt":
          return nv.soDienThoai.includes(keyword);
        default:
          return true;
      }
    });
  }

  return result;
});
const tableHeaders = [
  "Mã nhân viên",
  "Họ tên",
  "Email",
  "Số điện thoại",
  "Vị trí",
  "Trạng thái",
  "Hành động",
];

const tableRows = computed(() => {
  return filteredNhanVien.value.map((nv) => [
    nv.maNhanVien,
    nv.hoTen,
    nv.email,
    nv.soDienThoai,
    nv.viTri,
    nv.trangThai,
    nv, // Truyền cả object nhân viên để xử lý ở cột hành động
  ]);
});

const getViTriClass = (viTri: string) => {
  switch (viTri) {
    case "Quản lí đơn":
      return "bg-blue-100 text-blue-800";
    case "Giao hàng":
      return "bg-yellow-100 text-yellow-800";
    case "Quản lí kho":
      return "bg-indigo-100 text-indigo-800";
    default:
      return "bg-gray-100 text-gray-800";
  }
};

// --- Modal Control Functions ---

const openAddModal = () => {
  selectedNhanVien.value = null; // Xóa lựa chọn cũ để thêm mới
  isModalVisible.value = true;
};

const handleEdit = (nhanVien: NhanVien) => {
  selectedNhanVien.value = nhanVien;
  isModalVisible.value = true;
};

const closeModal = () => {
  isModalVisible.value = false;
  selectedNhanVien.value = null; // Xóa lựa chọn khi đóng modal
};

const handleSave = (nhanVienData: NhanVien) => {
  if (nhanVienData.maNhanVien) {
    // Cập nhật nhân viên đã có
    const index = danhSachNhanVien.value.findIndex(
      (nv) => nv.maNhanVien === nhanVienData.maNhanVien
    );
    if (index !== -1) {
      danhSachNhanVien.value[index] = nhanVienData;
    }
  } else {
    // Thêm nhân viên mới (tạo mã tạm thời)
    const newMaNhanVien = `NV${(danhSachNhanVien.value.length + 1)
      .toString()
      .padStart(3, "0")}`;
    danhSachNhanVien.value.push({ ...nhanVienData, maNhanVien: newMaNhanVien });
  }
  closeModal();
  // Có thể thêm thông báo thành công ở đây
};
</script>
