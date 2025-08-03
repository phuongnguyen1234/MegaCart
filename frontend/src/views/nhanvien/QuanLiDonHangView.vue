<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-4">Đơn hàng</h1>

    <!-- Bộ lọc -->
    <div class="flex flex-wrap items-end gap-4 mb-4">
      <!-- Ngày đặt -->
      <div>
        <label for="ngay-dat" class="block text-sm font-medium text-gray-700">
          Thời gian đặt hàng
        </label>
        <input
          id="ngay-dat"
          type="date"
          v-model="ngayDat"
          class="mt-1 block w-48 border border-gray-300 rounded px-2 py-1 shadow-sm"
        />
      </div>

      <!-- Mã đơn hàng -->
      <div>
        <label class="block text-sm font-medium text-gray-700">
          Mã đơn hàng
        </label>
        <div class="flex items-center border border-gray-300 rounded mt-1 px-2">
          <select class="border-none bg-transparent focus:outline-none">
            <option>Mã đơn hàng</option>
          </select>
          <input
            type="text"
            v-model="tuKhoa"
            placeholder="Tìm kiếm mã đơn hàng..."
            class="ml-2 w-60 py-1 border-none focus:outline-none"
          />
        </div>
      </div>

      <!-- Trạng thái -->
      <div>
        <label for="trang-thai" class="block text-sm font-medium text-gray-700">
          Trạng thái
        </label>
        <select
          id="trang-thai"
          v-model="trangThai"
          class="mt-1 block w-40 border border-gray-300 rounded px-2 py-1 shadow-sm"
        >
          <option v-for="tt in dsTrangThai" :key="tt.value" :value="tt.value">
            {{ tt.text }}
          </option>
        </select>
      </div>
    </div>

    <!-- Số lượng đơn -->
    <p class="mb-2 text-sm text-gray-600">
      Đang hiển thị
      <span class="font-semibold text-gray-800">{{ donHangDaLoc.length }}</span>
      đơn hàng
    </p>

    <!-- Bảng -->
    <DataTable :headers="headers" :rows="rowsHienThi" />

    <!-- Phân trang -->
    <div class="mt-4 flex justify-center">
      <PhanTrang
        v-model:trangHienTai="trangHienTai"
        :tong-so-trang="tongSoTrang"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import DataTable from "@/components/base/DataTable.vue";
import PhanTrang from "@/components/base/PhanTrang.vue";
import { ref, computed, watch } from "vue";

// --- Bộ lọc ---
const ngayDat = ref("2025-06-25");
const tuKhoa = ref("");
const trangThai = ref("Đang giao");

const dsTrangThai = [
  { value: "", text: "Tất cả" },
  { value: "Chờ xác nhận", text: "Chờ xác nhận" },
  { value: "Chờ xử lí", text: "Chờ xử lí" },
  { value: "Đang giao", text: "Đang giao" },
  { value: "Đã giao", text: "Đã giao" },
  { value: "Đã hủy", text: "Đã hủy" },
];

// --- Danh sách đơn hàng mẫu ---
interface DonHang {
  ma: string;
  khach: string;
  thoiGian: string;
  trangThai: string;
  tongTien: string;
}

const allDonHang = ref<DonHang[]>(
  Array.from({ length: 150 }, (_, i) => {
    const id = 1000 - i;
    const status = dsTrangThai[(i % (dsTrangThai.length - 1)) + 1].value;
    return {
      ma: `#${id}`,
      khach: `Nguyễn Văn ${String.fromCharCode(65 + (i % 26))}`,
      thoiGian: `10:${String(i).padStart(2, "0")}:00 25/06/2025`,
      trangThai: status,
      tongTien: `${((i + 1) * 10000).toLocaleString("vi-VN")} VND`,
    };
  })
);

// --- Header và hàng ---
const headers = [
  "Mã đơn hàng",
  "Khách hàng",
  "Thời gian đặt",
  "Trạng thái",
  "Tổng tiền",
  "",
];

const donHangDaLoc = computed(() =>
  allDonHang.value.filter((d) => {
    const matchNgay = d.thoiGian.includes(ngayDat.value);
    const matchTuKhoa = d.ma.toLowerCase().includes(tuKhoa.value.toLowerCase());
    const matchTrangThai = !trangThai.value || d.trangThai === trangThai.value;
    return matchNgay && matchTuKhoa && matchTrangThai;
  })
);

// --- Phân trang ---
const trangHienTai = ref(0);
const soLuongMoiTrang = 50;
const tongSoTrang = computed(() =>
  Math.ceil(donHangDaLoc.value.length / soLuongMoiTrang)
);

const rowsHienThi = computed(() => {
  const batDau = trangHienTai.value * soLuongMoiTrang;
  return donHangDaLoc.value
    .slice(batDau, batDau + soLuongMoiTrang)
    .map((d) => [
      d.ma,
      d.khach,
      d.thoiGian,
      d.trangThai,
      d.tongTien,
      `<a href="#" class="text-blue-600 hover:underline">Sửa</a>`,
    ]);
});

// --- Reset về trang đầu nếu bộ lọc thay đổi ---
watch(donHangDaLoc, () => {
  trangHienTai.value = 0;
});
</script>

<style scoped>
select,
input[type="text"],
input[type="date"] {
  min-width: 180px;
}
</style>
