<template>
  <div class="p-4 md:p-6">
    <!-- Header -->
    <h1 class="text-2xl font-bold mb-4">Quản lí Kho hàng</h1>

    <!-- Bộ lọc -->
    <div
      class="flex flex-wrap items-end gap-4 mb-4 bg-white p-4 rounded-lg shadow"
    >
      <!-- Lọc theo danh mục cha -->
      <div>
        <label
          for="danh-muc-cha"
          class="block text-sm font-medium text-gray-700"
        >
          Danh mục cha
        </label>
        <select
          id="danh-muc-cha"
          v-model="selectedDanhMucCha"
          class="mt-1 block w-48 border border-gray-300 bg-white rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm px-3 py-2"
        >
          <option value="">Tất cả</option>
          <option v-for="dmc in danhMucCha" :key="dmc.id" :value="dmc.id">
            {{ dmc.ten }}
          </option>
        </select>
      </div>

      <!-- Lọc theo danh mục con -->
      <div>
        <label
          for="danh-muc-con"
          class="block text-sm font-medium text-gray-700"
        >
          Danh mục con
        </label>
        <select
          id="danh-muc-con"
          v-model="selectedDanhMucCon"
          :disabled="!selectedDanhMucCha"
          class="mt-1 block w-48 border border-gray-300 bg-white rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm px-3 py-2 disabled:bg-gray-100"
        >
          <option value="">Tất cả</option>
          <option
            v-for="dmc in danhMucConFiltered"
            :key="dmc.id"
            :value="dmc.id"
          >
            {{ dmc.ten }}
          </option>
        </select>
      </div>

      <!-- Thanh tìm kiếm -->
      <ThanhTimKiem
        :ds-tieu-chi="dsTieuChiTimKiem"
        v-model:modelValueLoai="loaiTimKiem"
        v-model:modelValueTuKhoa="tuKhoa"
      />

      <button
        @click="apDungBoLoc"
        class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
      >
        Áp dụng
      </button>
    </div>

    <!-- Thông tin hiển thị -->
    <div class="mb-4">
      <p class="text-sm text-gray-600">
        {{ thongTinHienThi }}
      </p>
    </div>

    <!-- Bảng dữ liệu tồn kho -->
    <div class="bg-white shadow-md rounded-lg overflow-hidden">
      <DataTable :headers="headers" :rows="rows">
        <!-- Tùy chỉnh cột Hành động -->
        <template #cell-7="{ value }">
          <button
            @click="handleEdit(value)"
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

    <!-- Modal cập nhật tồn kho -->
    <CapNhatTonKhoModal
      :visible="isModalVisible"
      :san-pham="sanPhamDangSua"
      @close="isModalVisible = false"
      @save="handleSave"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import DataTable from "@/components/base/DataTable.vue";
import PhanTrang from "@/components/base/PhanTrang.vue";
import ThanhTimKiem from "@/components/base/ThanhTimKiem.vue";
import CapNhatTonKhoModal from "@/components/quanlikho/CapNhatTonKhoModal.vue";
import type { SanPhamTonKho, DuLieuCapNhat } from "@/types/QuanLiKho";

// --- Dữ liệu giả lập ---
const danhMucCha = ref([
  { id: 1, ten: "Điện tử" },
  { id: 2, ten: "Quần áo" },
]);
const danhMucCon = ref([
  { id: 101, idCha: 1, ten: "Điện thoại" },
  { id: 102, idCha: 1, ten: "Laptop" },
  { id: 201, idCha: 2, ten: "Áo nam" },
  { id: 202, idCha: 2, ten: "Quần nam" },
]);
const allTonKho = ref<SanPhamTonKho[]>(
  Array.from({ length: 125 }, (_, i) => {
    const dmc = i % 2 === 0 ? danhMucCha.value[0] : danhMucCha.value[1];
    const dm_con =
      dmc.id === 1 ? danhMucCon.value[i % 2] : danhMucCon.value[2 + (i % 2)];
    return {
      maSanPham: `SP${String(i + 1).padStart(4, "0")}`,
      tenSanPham: `Sản phẩm tồn kho ${i + 1}`,
      idDanhMucCha: dmc.id,
      danhMucCha: dmc.ten,
      idDanhMucCon: dm_con.id,
      danhMucCon: dm_con.ten,
      soLuong: Math.floor(Math.random() * 200),
      thoiGianCapNhat: new Date(Date.now() - i * 3600000),
      noiDungCapNhat: i % 5 === 0 ? "Nhập hàng" : "Cập nhật sau bán",
    };
  })
);

const dsTieuChiTimKiem = [
  { value: "ten", label: "Tên sản phẩm" },
  { value: "ma", label: "Mã sản phẩm" },
];

// --- State cho bộ lọc ---
const selectedDanhMucCha = ref<number | "">("");
const selectedDanhMucCon = ref<number | "">("");
const loaiTimKiem = ref("ten");
const tuKhoa = ref("");
const activeFilters = ref({
  danhMucCha: "" as number | "",
  danhMucCon: "" as number | "",
  loaiTimKiem: "ten",
  tuKhoa: "",
});

const danhMucConFiltered = computed(() =>
  !selectedDanhMucCha.value
    ? []
    : danhMucCon.value.filter((dmc) => dmc.idCha === selectedDanhMucCha.value)
);

watch(selectedDanhMucCha, () => {
  selectedDanhMucCon.value = "";
});

const apDungBoLoc = () => {
  activeFilters.value = {
    danhMucCha: selectedDanhMucCha.value,
    danhMucCon: selectedDanhMucCon.value,
    loaiTimKiem: loaiTimKiem.value,
    tuKhoa: tuKhoa.value,
  };
};

// --- Lọc dữ liệu ---
const tonKhoDaLoc = computed(() => {
  return allTonKho.value.filter((item) => {
    const matchDanhMucCha =
      !activeFilters.value.danhMucCha ||
      item.idDanhMucCha === activeFilters.value.danhMucCha;

    const matchDanhMucCon =
      !activeFilters.value.danhMucCon ||
      item.idDanhMucCon === activeFilters.value.danhMucCon;

    const matchTuKhoa = (() => {
      if (!activeFilters.value.tuKhoa.trim()) return true;
      const keyword = activeFilters.value.tuKhoa.toLowerCase();
      if (activeFilters.value.loaiTimKiem === "ma") {
        return item.maSanPham.toLowerCase().includes(keyword);
      }
      return item.tenSanPham.toLowerCase().includes(keyword);
    })();

    return matchDanhMucCha && matchDanhMucCon && matchTuKhoa;
  });
});

// --- Phân trang ---
const trangHienTai = ref(0);
const soLuongMoiTrang = ref(10);

const tongSoTrang = computed(() =>
  Math.ceil(tonKhoDaLoc.value.length / soLuongMoiTrang.value)
);

const tonKhoHienThi = computed(() => {
  const batDau = trangHienTai.value * soLuongMoiTrang.value;
  const ketThuc = batDau + soLuongMoiTrang.value;
  return tonKhoDaLoc.value.slice(batDau, ketThuc);
});

const thongTinHienThi = computed(() => {
  const total = tonKhoDaLoc.value.length;
  if (total === 0) {
    return "Không có sản phẩm nào được tìm thấy";
  }
  const start = trangHienTai.value * soLuongMoiTrang.value + 1;
  const end = start + tonKhoHienThi.value.length - 1;
  return `Đang hiển thị ${start} - ${end} của ${total} sản phẩm`;
});

watch(tonKhoDaLoc, () => {
  trangHienTai.value = 0;
});

// --- Cấu hình DataTable ---
const headers = [
  "Mã sản phẩm",
  "Tên sản phẩm",
  "Danh mục cha",
  "Danh mục con",
  "Số lượng",
  "Thời gian cập nhật",
  "Nội dung cập nhật",
  "Hành động",
];

const formatDateTime = (date: Date) => date.toLocaleString("vi-VN");

const rows = computed(() =>
  tonKhoHienThi.value.map((item) => [
    item.maSanPham,
    item.tenSanPham,
    item.danhMucCha,
    item.danhMucCon,
    item.soLuong,
    formatDateTime(item.thoiGianCapNhat),
    item.noiDungCapNhat,
    item.maSanPham, // Truyền mã sản phẩm cho slot hành động
  ])
);

// --- Xử lý hành động ---
const isModalVisible = ref(false);
const sanPhamDangSua = ref<SanPhamTonKho | null>(null);

const handleEdit = (maSanPham: string) => {
  const productToEdit = allTonKho.value.find((p) => p.maSanPham === maSanPham);
  if (productToEdit) {
    sanPhamDangSua.value = productToEdit;
    isModalVisible.value = true;
  }
};

const handleSave = (data: DuLieuCapNhat) => {
  console.log("Lưu dữ liệu tồn kho:", data);
  const productIndex = allTonKho.value.findIndex(
    (p) => p.maSanPham === data.maSanPham
  );
  if (productIndex !== -1) {
    // Cập nhật lại dữ liệu trong mảng (bạn sẽ thay bằng gọi API)
    // ...
  }
  isModalVisible.value = false;
};
</script>
