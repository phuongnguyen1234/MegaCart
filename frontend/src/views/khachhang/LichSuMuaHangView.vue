<template>
  <CustomerNoNav>
    <div class="max-w-7xl mx-auto grid grid-cols-1 md:grid-cols-6 gap-4 p-6">
      <!-- BỘ LỌC -->
      <div class="md:col-span-1">
        <AccordionBoLocDonHang
          v-model:tuNgay="tuNgay"
          v-model:denNgay="denNgay"
          v-model:maDonHang="maDonHang"
          @timKiem="timKiemDonHang"
        />
      </div>

      <!-- DANH SÁCH ĐƠN HÀNG -->
      <div class="md:col-span-5">
        <h2 class="text-center text-xl font-bold mb-4">LỊCH SỬ MUA HÀNG</h2>

        <!-- TAB -->
        <div class="flex border-b mb-4">
          <button
            v-for="trangThai in trangThaiList"
            :key="trangThai.value"
            class="relative px-4 py-2 -mb-px text-sm font-medium transition-colors duration-200"
            :class="[
              trangThaiDangChon === trangThai.value
                ? 'border-b-2 border-blue-600 text-blue-600'
                : 'text-gray-500 hover:text-blue-600',
            ]"
            @click="trangThaiDangChon = trangThai.value"
          >
            {{ trangThai.label }} ({{
              soLuongTheoTrangThai[trangThai.value] || 0
            }})
          </button>
        </div>

        <!-- DANH SÁCH ĐƠN -->
        <div class="space-y-4 max-h-[500px] overflow-y-auto pr-2">
          <CardDonHang
            v-for="donHang in donHangHienTai"
            :key="donHang.ma"
            :donHang="donHang"
          />
        </div>
      </div>
    </div>
  </CustomerNoNav>
</template>

<script setup>
import { ref, computed } from "vue";
import CustomerNoNav from "@/components/layouts/CustomerNoNav.vue";
import AccordionBoLocDonHang from "@/components/base/AccordionBoLocDonHang.vue";
import CardDonHang from "@/components/base/card/CardDonHang.vue";

const tuNgay = ref("2025-01-01");
const denNgay = ref("2025-01-02");
const maDonHang = ref("");

const trangThaiList = [
  { label: "Chờ xác nhận", value: "cho-xac-nhan" },
  { label: "Chờ xử lí", value: "cho-xu-li" },
  { label: "Đang giao", value: "dang-giao" },
  { label: "Đã giao", value: "da-giao" },
  { label: "Đã huỷ", value: "da-huy" },
];
const trangThaiDangChon = ref("cho-xac-nhan");

const danhSachDonHang = ref([
  {
    ma: "1234567",
    sanPhamTieuBieu: {
      ten: "Bánh mì",
      soLuong: 2,
    },
    soSanPhamConLai: 1,
    tongTien: 39000,
    thoiGianDat: "2025-06-25 10:10:00",
    trangThai: "cho-xac-nhan",
  },
]);

const donHangHienTai = computed(() =>
  danhSachDonHang.value.filter((d) => d.trangThai === trangThaiDangChon.value)
);

const soLuongTheoTrangThai = computed(() => {
  const dem = {};
  for (const dh of danhSachDonHang.value) {
    dem[dh.trangThai] = (dem[dh.trangThai] || 0) + 1;
  }
  return dem;
});

const timKiemDonHang = () => {
  console.log("Bắt đầu tìm kiếm đơn hàng với các bộ lọc:", {
    tuNgay: tuNgay.value,
    denNgay: denNgay.value,
    maDonHang: maDonHang.value,
  });
  // TODO: Gọi API với các giá trị lọc ở trên
};
</script>
