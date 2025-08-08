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
          <option value="hoatdong">Hoạt động</option>
          <option value="ngunghoatdong">Ngừng hoạt động</option>
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
      <DataTable :headers="headers" :rows="rows">
        <!-- Tùy chỉnh cột Trạng thái -->
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

        <template #cell-6="{ value }">
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
import type {
  KhachHang,
  DuLieuCapNhatKhachHang,
} from "@/types/QuanLiKhachHang";

// --- Dữ liệu giả lập ---
const allKhachHang = ref<KhachHang[]>(
  Array.from({ length: 150 }, (_, i) => ({
    maKhachHang: `KH${String(i + 1).padStart(4, "0")}`,
    tenKhachHang: `Nguyễn Văn ${String.fromCharCode(65 + (i % 26))}${i}`,
    email: `nguyenvan${i}@example.com`,
    diaChi: `${i + 1} Đường ABC, Phường XYZ, Quận 1, TP. HCM`,
    soDienThoai: `090${String(i).padStart(7, "0")}`,
    trangThai: i % 10 === 0 ? "Ngừng hoạt động" : "Hoạt động",
  }))
);

const loaiTimKiem = ref("ten");
const tuKhoa = ref("");
const selectedTrangThai = ref<"hoatdong" | "ngunghoatdong" | "">("");

const dsTieuChiTimKiem = [
  { value: "ten", label: "Tên khách hàng" },
  { value: "email", label: "Email" },
  { value: "sdt", label: "Số điện thoại" },
];

// --- Lọc dữ liệu ---
const khachHangDaLoc = computed(() => {
  return allKhachHang.value.filter((kh) => {
    const matchTrangThai = (() => {
      if (!selectedTrangThai.value) return true;
      const trangThaiNormalized =
        selectedTrangThai.value === "hoatdong"
          ? "Hoạt động"
          : "Ngừng hoạt động";
      return kh.trangThai === trangThaiNormalized;
    })();

    const matchTuKhoa = (() => {
      if (!tuKhoa.value.trim()) return true;
      const keyword = tuKhoa.value.toLowerCase();
      switch (loaiTimKiem.value) {
        case "ten":
          return kh.tenKhachHang.toLowerCase().includes(keyword);
        case "email":
          return kh.email.toLowerCase().includes(keyword);
        case "sdt":
          return kh.soDienThoai.includes(keyword);
        default:
          return true;
      }
    })();

    return matchTrangThai && matchTuKhoa;
  });
});

// --- Phân trang ---
const trangHienTai = ref(0);
const soLuongMoiTrang = ref(10);

const tongSoTrang = computed(() =>
  Math.ceil(khachHangDaLoc.value.length / soLuongMoiTrang.value)
);

const khachHangHienThi = computed(() => {
  const batDau = trangHienTai.value * soLuongMoiTrang.value;
  const ketThuc = batDau + soLuongMoiTrang.value;
  return khachHangDaLoc.value.slice(batDau, ketThuc);
});

const thongTinHienThi = computed(() => {
  const total = khachHangDaLoc.value.length;
  if (total === 0) {
    return "Không có khách hàng nào được tìm thấy";
  }
  const start = trangHienTai.value * soLuongMoiTrang.value + 1;
  const end = start + khachHangHienThi.value.length - 1;
  return `Đang hiển thị ${start} - ${end} của ${total} khách hàng`;
});

watch(khachHangDaLoc, () => {
  trangHienTai.value = 0;
});

// --- Cấu hình DataTable ---
const headers = [
  "Mã khách hàng",
  "Tên khách hàng",
  "Email",
  "Địa chỉ",
  "Số điện thoại",
  "Trạng thái",
  "Hành động",
];

const rows = computed(() =>
  khachHangHienThi.value.map((kh) => [
    kh.maKhachHang,
    kh.tenKhachHang,
    kh.email,
    kh.diaChi,
    kh.soDienThoai,
    kh.trangThai,
    kh.maKhachHang, // Truyền mã khách hàng cho slot hành động
  ])
);

// --- Xử lý hành động ---
const isModalVisible = ref(false);
const khachHangDangSua = ref<KhachHang | null>(null);

const handleEdit = (maKhachHang: string) => {
  const customerToEdit = allKhachHang.value.find(
    (kh) => kh.maKhachHang === maKhachHang
  );
  if (customerToEdit) {
    khachHangDangSua.value = customerToEdit;
    isModalVisible.value = true;
  }
};

const handleSave = (data: DuLieuCapNhatKhachHang) => {
  console.log("Lưu thông tin khách hàng:", data);
  // Logic cập nhật dữ liệu thật (thay thế dữ liệu giả lập)
  // const index = allKhachHang.value.findIndex(kh => kh.maKhachHang === data.maKhachHang);
  // if (index !== -1) { ... }
  isModalVisible.value = false;
};
</script>
