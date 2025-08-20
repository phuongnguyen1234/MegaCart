<template>
  <div class="p-4 md:p-6">
    <!-- Header -->
    <h1 class="text-2xl font-bold mb-4">Quản lí Kho hàng</h1>

    <!-- Bộ lọc -->
    <div
      class="flex flex-wrap items-end gap-4 mb-4 bg-white p-4 rounded-lg shadow"
    >
      <BoLocDanhMuc
        v-model:parent="selectedDanhMucCha"
        v-model:child="selectedDanhMucCon"
      />

      <!-- Thanh tìm kiếm -->
      <ThanhTimKiem
        :ds-tieu-chi="dsTieuChiTimKiem"
        v-model:modelValueLoai="loaiTimKiem"
        v-model:modelValueTuKhoa="tuKhoa"
      />
    </div>

    <!-- Thông tin hiển thị -->
    <div class="mb-4">
      <p class="text-sm text-gray-600">
        {{ thongTinHienThi }}
      </p>
    </div>

    <!-- Bảng dữ liệu tồn kho -->
    <div class="bg-white shadow-md rounded-lg overflow-hidden">
      <DataTable :headers="headers" :rows="rows" :is-loading="isLoading">
        <!-- Tùy chỉnh cột Ảnh sản phẩm (cột thứ 2, index 1) -->
        <template #cell-1="{ value }">
          <img
            :src="value"
            alt="Ảnh sản phẩm"
            class="w-16 h-16 object-cover rounded border"
          />
        </template>
        <!-- Tùy chỉnh cột Số lượng tồn (cột thứ 6, index 5) -->
        <template #cell-5="{ value }">
          <span
            :class="[
              'px-2 inline-flex text-xs leading-5 font-semibold rounded-full',
              value < 10
                ? 'bg-red-100 text-red-800'
                : 'bg-green-100 text-green-800',
            ]"
          >
            {{ value }}
          </span>
        </template>
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
      @success="handleUpdateSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import DataTable from "@/components/base/DataTable.vue";
import PhanTrang from "@/components/base/PhanTrang.vue";
import ThanhTimKiem from "@/components/base/ThanhTimKiem.vue";
import BoLocDanhMuc from "@/components/base/BoLocDanhMuc.vue";
import CapNhatTonKhoModal from "@/components/quanlikho/CapNhatTonKhoModal.vue";
import { getDanhSachKho } from "@/service/khohang.service";
import type {
  KhoResponse,
  GetKhoParams,
  CapNhatKhoRequest,
} from "@/types/khohang.types";
import { useToast } from "@/composables/useToast";

const dsTieuChiTimKiem = [
  { value: "maSanPham", label: "Mã sản phẩm" },
  { value: "tenSanPham", label: "Tên sản phẩm" },
];

// --- State cho bộ lọc ---
const selectedDanhMucCha = ref<number | "">("");
const selectedDanhMucCon = ref<number | "">("");
const loaiTimKiem = ref("tenSanPham");
const tuKhoa = ref("");
const isLoading = ref(false);
const { showToast } = useToast();

const danhSachTonKho = ref<KhoResponse[]>([]);

// Computed property để xác định mã danh mục cần lọc
const activeMaDanhMuc = computed(() => {
  // Ưu tiên lọc theo danh mục con nếu được chọn
  return selectedDanhMucCon.value || selectedDanhMucCha.value || undefined;
});

// --- Phân trang ---
const trangHienTai = ref(0);
const soLuongMoiTrang = ref(10);
const tongSoTrang = ref(1);
const totalElements = ref(0);

const thongTinHienThi = computed(() => {
  const total = totalElements.value;
  if (total === 0) {
    return "Không có sản phẩm nào được tìm thấy";
  }
  const start = trangHienTai.value * soLuongMoiTrang.value + 1;
  const end = start + danhSachTonKho.value.length - 1;
  return `Đang hiển thị ${start} - ${end} của ${total} sản phẩm`;
});

// --- Fetch dữ liệu từ API ---
const fetchTonKho = async () => {
  isLoading.value = true;
  try {
    const params: GetKhoParams = {
      page: trangHienTai.value,
      size: soLuongMoiTrang.value,
      tuKhoa: tuKhoa.value || undefined,
      maDanhMuc: activeMaDanhMuc.value,
    };
    const response = await getDanhSachKho(params);
    danhSachTonKho.value = response.content;
    tongSoTrang.value = response.totalPages;
    totalElements.value = response.totalElements;
  } catch (error) {
    console.error("Lỗi khi lấy danh sách tồn kho:", error);
    showToast({
      loai: "loi",
      thongBao: "Không thể tải danh sách tồn kho.",
    });
    danhSachTonKho.value = [];
    tongSoTrang.value = 1;
    totalElements.value = 0;
  } finally {
    isLoading.value = false;
  }
};

// Tự động fetch lại dữ liệu khi bộ lọc hoặc trang thay đổi
watch([trangHienTai, tuKhoa, activeMaDanhMuc], fetchTonKho, {
  immediate: true,
});

// Reset về trang đầu tiên khi người dùng thay đổi bộ lọc
watch([tuKhoa, activeMaDanhMuc], () => {
  trangHienTai.value = 0;
});

// --- Cấu hình DataTable ---
const headers = [
  "Mã sản phẩm",
  "Ảnh sản phẩm",
  "Tên sản phẩm",
  "Danh mục cha",
  "Danh mục con",
  "Số lượng tồn",
  "Nội dung cập nhật",
  "Hành động",
];

const rows = computed(() =>
  danhSachTonKho.value.map((item) => [
    item.maSanPham,
    item.anhMinhHoaChinh,
    item.tenSanPham,
    item.danhMucCha || "—",
    item.danhMucCon || "—",
    item.soLuong,
    item.noiDungCapNhat || "—",
    item.maSanPham, // Truyền mã sản phẩm cho slot hành động
  ])
);

// --- Xử lý hành động ---
const isModalVisible = ref(false);
const sanPhamDangSua = ref<KhoResponse | null>(null);

const handleEdit = (maSanPham: number) => {
  const productToEdit = danhSachTonKho.value.find(
    (p) => p.maSanPham === maSanPham
  );
  if (productToEdit) {
    sanPhamDangSua.value = productToEdit;
    isModalVisible.value = true;
  }
};

const handleUpdateSuccess = () => {
  isModalVisible.value = false;
  fetchTonKho(); // Tải lại dữ liệu bảng để hiển thị thông tin mới nhất
};
</script>
