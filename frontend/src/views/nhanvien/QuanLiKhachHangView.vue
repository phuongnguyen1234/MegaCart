<template>
  <div class="p-4 md:p-6">
    <h1 class="text-2xl font-bold mb-4">Quản lí Khách hàng</h1>

    <!-- Chức năng -->
    <div
      class="flex flex-wrap items-end gap-4 mb-4 bg-white p-4 rounded-lg shadow"
    >
      <!-- Lọc theo trạng thái -->
      <div>
        <label for="trang-thai" class="block text-sm font-medium text-gray-700">
          Trạng thái tài khoản
        </label>
        <select
          id="trang-thai"
          v-model="selectedTrangThai"
          class="mt-1 block w-48 border border-gray-300 bg-white rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm px-3 py-2"
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

    <!-- Bảng dữ liệu khách hàng -->
    <div class="bg-white shadow-md rounded-lg overflow-hidden">
      <DataTable :headers="headers" :rows="rows" :is-loading="isLoading">
        <!-- Tùy chỉnh cột Trạng thái -->
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

        <template #cell-6="{ value: maKhachHang }">
          <button
            @click="handleEdit(maKhachHang)"
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

    <!-- Modal cập nhật khách hàng -->
    <CapNhatKhachHangModal
      :visible="isModalVisible"
      :khach-hang="khachHangDangSua"
      @close="isModalVisible = false"
      @save="handleSave"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import ThanhTimKiem from "@/components/base/ThanhTimKiem.vue";
import DataTable from "@/components/base/DataTable.vue";
import PhanTrang from "@/components/base/PhanTrang.vue";
import CapNhatKhachHangModal from "@/components/quanlitaikhoan/CapNhatKhachHangModal.vue";
import {
  getDanhSachKhachHang,
  capNhatTrangThaiKhachHang,
} from "@/service/quanlikhachhang.service";
import type {
  HienThiDanhSachKhachHangResponse,
  GetKhachHangParams,
  CapNhatTrangThaiTaiKhoanRequest,
} from "@/types/khachhang.types";
import {
  TrangThaiTaiKhoanKey,
  TrangThaiTaiKhoanLabel,
} from "@/types/khachhang.types";
import { useToast } from "@/composables/useToast";

const selectedTrangThai = ref<TrangThaiTaiKhoanKey | "">("");
const loaiTimKiem = ref<"tenKhachHang" | "email" | "soDienThoai">(
  "tenKhachHang"
);
const tuKhoa = ref("");
const isLoading = ref(false);
const { showToast } = useToast();

const dsTieuChiTimKiem = [
  { value: "tenKhachHang", label: "Tên khách hàng" },
  { value: "email", label: "Email" },
  { value: "soDienThoai", label: "Số điện thoại" },
];
const allKhachHang = ref<HienThiDanhSachKhachHangResponse[]>([]);

// --- Cấu hình DataTable ---
const rows = computed(() =>
  allKhachHang.value.map((kh: HienThiDanhSachKhachHangResponse) => [
    kh.maKhachHang,
    kh.tenKhachHang,
    kh.email,
    kh.diaChi,
    kh.soDienThoai,
    kh.trangThaiTaiKhoan, // Truyền cả object để slot tùy chỉnh
    kh.maKhachHang,
  ])
);

// --- Phân trang ---
const trangHienTai = ref(0);
const soLuongMoiTrang = ref(10);
const tongSoTrang = ref(1);
const totalElements = ref(0);

const thongTinHienThi = computed(() => {
  const total = totalElements.value;
  if (total === 0) {
    return "Không có khách hàng nào được tìm thấy";
  }
  const start = trangHienTai.value * soLuongMoiTrang.value + 1;
  const end = start + allKhachHang.value.length - 1;
  return `Đang hiển thị ${start} - ${end} của ${total} khách hàng`;
});

// --- Xử lý hành động ---
const isModalVisible = ref(false);
const khachHangDangSua = ref<HienThiDanhSachKhachHangResponse | null>(null);

const handleEdit = (maKhachHang: number) => {
  const customerToEdit = allKhachHang.value.find(
    (kh) => kh.maKhachHang === maKhachHang
  );
  if (customerToEdit) {
    khachHangDangSua.value = customerToEdit;
    isModalVisible.value = true;
  }
};

const handleSave = async (payload: {
  maKhachHang: number;
  data: CapNhatTrangThaiTaiKhoanRequest;
}) => {
  try {
    const updatedKhachHang = await capNhatTrangThaiKhachHang(
      payload.maKhachHang,
      payload.data
    );

    // Cập nhật lại danh sách khách hàng trên UI để phản ánh thay đổi ngay lập tức
    const index = allKhachHang.value.findIndex(
      (kh) => kh.maKhachHang === payload.maKhachHang
    );
    if (index !== -1) {
      allKhachHang.value[index] = updatedKhachHang;
    }
    showToast({
      thongBao: "Cập nhật trạng thái khách hàng thành công!",
      loai: "thanhCong",
    });
  } catch (error) {
    console.error("Lỗi khi cập nhật trạng thái khách hàng:", error);
    showToast({
      thongBao: "Có lỗi xảy ra, không thể cập nhật trạng thái khách hàng.",
      loai: "loi",
    });
  }
};

// --- Fetch dữ liệu từ API ---
const fetchKhachHang = async () => {
  isLoading.value = true;
  try {
    const params: GetKhachHangParams = {
      searchField: tuKhoa.value ? loaiTimKiem.value : undefined,
      searchValue: tuKhoa.value || undefined,
      trangThai: selectedTrangThai.value || undefined,
      page: trangHienTai.value,
      size: soLuongMoiTrang.value,
    };
    const response = await getDanhSachKhachHang(params);
    allKhachHang.value = response.content;
    tongSoTrang.value = response.totalPages;
    totalElements.value = response.totalElements;
  } catch (error) {
    console.error("Lỗi khi lấy danh sách khách hàng:", error);
    allKhachHang.value = [];
    tongSoTrang.value = 1;
    totalElements.value = 0;
  } finally {
    isLoading.value = false;
  }
};

// Tự động fetch lại dữ liệu khi bộ lọc hoặc trang thay đổi
// Reset về trang đầu tiên khi người dùng thay đổi bộ lọc
watch([selectedTrangThai, tuKhoa], () => {
  trangHienTai.value = 0;
});

watch([trangHienTai, selectedTrangThai, tuKhoa], fetchKhachHang, {
  immediate: true,
});

const headers = [
  "Mã khách hàng",
  "Tên khách hàng",
  "Email",
  "Địa chỉ",
  "Số điện thoại",
  "Trạng thái",
  "Hành động",
];
</script>
