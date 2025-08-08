<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-4">Quản lí sản phẩm</h1>

    <!-- Chức năng -->
    <div class="flex justify-between items-center mb-4">
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
        :ds-tieu-chi="[
          { value: 'maSanPham', label: 'Mã sản phẩm' },
          { value: 'tenSanPham', label: 'Tên sản phẩm' },
        ]"
        v-model:modelValueLoai="loaiTimKiem"
        v-model:modelValueTuKhoa="tuKhoa"
      />
    </div>

    <!-- Bảng dữ liệu sản phẩm -->
    <div class="bg-white shadow-md rounded-lg overflow-hidden">
      <!-- 
        Lưu ý: Để các nút "Sửa" và ảnh minh họa hoạt động, component DataTable 
        cần hỗ trợ slots cho các ô (cell). Dưới đây là một ví dụ về cách sử dụng:
        - Slot `cell-1` sẽ tùy chỉnh cách hiển thị cho cột thứ 2 (Ảnh minh họa).
        - Slot `cell-7` sẽ tùy chỉnh cách hiển thị cho cột thứ 8 (Hành động).
        Component DataTable của bạn có thể cần được cập nhật để nhận các slot này.
       -->
      <DataTable :headers="headers" :rows="rows">
        <template #cell-1="{ value }">
          <img
            :src="value"
            alt="Ảnh sản phẩm"
            class="w-16 h-16 object-cover rounded"
          />
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
import type { SanPham } from "@/types/SanPham";

// --- State ---
const loaiTimKiem = ref("tenSanPham");
const tuKhoa = ref("");
const isModalVisible = ref(false);
const isEditMode = ref(false);
const selectedProduct = ref<SanPham | null>(null);

// --- Modal Logic ---
const modalTitle = computed(() =>
  isEditMode.value ? "Sửa thông tin sản phẩm" : "Thêm sản phẩm mới"
);

const openAddModal = () => {
  isEditMode.value = false;
  selectedProduct.value = null;
  isModalVisible.value = true;
};

const openEditModal = (product: SanPham) => {
  isEditMode.value = true;
  selectedProduct.value = product;
  isModalVisible.value = true;
};

const handleEditClick = (productId: any) => {
  // `value` từ slot là mã sản phẩm, chúng ta dùng nó để tìm sản phẩm đầy đủ.
  const productToEdit = allSanPham.value.find((p) => p.maSanPham === productId);
  if (productToEdit) {
    openEditModal(productToEdit);
  }
};

const closeModal = () => {
  isModalVisible.value = false;
};

// --- Dữ liệu mẫu ---
const allSanPham = ref<SanPham[]>(
  Array.from({ length: 125 }, (_, i) => ({
    maSanPham: `SP${String(i + 1).padStart(4, "0")}`,
    anhMinhHoa: [
      `https://via.placeholder.com/150/F5F5F5/333333?text=Ảnh+${i + 1}`,
    ],
    tenSanPham: `Sản phẩm mẫu ${i + 1}`,
    danhMucCha:
      i % 3 === 0 ? "Điện tử" : i % 3 === 1 ? "Thời trang" : "Gia dụng",
    danhMucCon:
      i % 3 === 0 ? "Điện thoại" : i % 3 === 1 ? "Áo thun" : "Nồi cơm",
    donGia: 50000 + i * 15000,
    donVi: "Cái",
    nhaSanXuat: `Nhà sản xuất ${String.fromCharCode(65 + (i % 10))}`,
    trangThai: i % 5 === 0 ? "Ngừng bán" : "Đang bán",
  }))
);

// --- Computed Properties ---
const sanPhamDaLoc = computed(() => {
  if (!tuKhoa.value.trim()) {
    return allSanPham.value;
  }
  const keyword = tuKhoa.value.toLowerCase();
  return allSanPham.value.filter((sp) => {
    if (loaiTimKiem.value === "maSanPham") {
      return String(sp.maSanPham).toLowerCase().includes(keyword);
    }
    // Mặc định tìm theo tên sản phẩm
    return sp.tenSanPham.toLowerCase().includes(keyword);
  });
});

// --- Phân trang ---
const trangHienTai = ref(0);
const soLuongMoiTrang = ref(10);

const tongSoTrang = computed(() =>
  Math.ceil(sanPhamDaLoc.value.length / soLuongMoiTrang.value)
);

const sanPhamHienThi = computed(() => {
  const batDau = trangHienTai.value * soLuongMoiTrang.value;
  const ketThuc = batDau + soLuongMoiTrang.value;
  return sanPhamDaLoc.value.slice(batDau, ketThuc);
});

// --- Cấu hình DataTable ---
const headers = [
  "Mã sản phẩm",
  "Ảnh minh họa",
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
  sanPhamHienThi.value.map((sp) => [
    sp.maSanPham,
    sp.anhMinhHoa[0],
    sp.tenSanPham,
    sp.danhMucCha,
    sp.danhMucCon,
    formatCurrency(sp.donGia),
    sp.trangThai,
    sp.maSanPham, // Chỉ truyền mã sản phẩm (string) để tuân thủ kiểu dữ liệu
  ])
);

// --- Watchers ---
watch(sanPhamDaLoc, () => {
  trangHienTai.value = 0;
});
</script>
