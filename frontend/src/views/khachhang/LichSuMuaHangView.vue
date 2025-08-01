<template>
  <CustomerWithNav>
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
            @xemChiTiet="xemChiTietDonHang(donHang)"
          />
        </div>
      </div>
    </div>

    <ChiTietDonHangModal
      v-if="donHangDangChon"
      :visible="isModalVisible"
      :danh-sach-san-pham="donHangDangChon.danhSachSanPham"
      :thong-tin="donHangDangChon.thongTin"
      :ma-don-hang="donHangDangChon.ma"
      :tong-tien="donHangDangChon.tongTien"
      @close="dongModal"
      @huyDon="huyDonHang"
      @giaoPhanConLai="giaoPhanConLai"
    />
  </CustomerWithNav>
</template>

<script setup>
import { ref, computed } from "vue";
import CustomerWithNav from "@/components/layouts/CustomerWithNav.vue";
import AccordionBoLocDonHang from "@/components/base/AccordionBoLocDonHang.vue";
import CardDonHang from "@/components/base/card/CardDonHang.vue";
import ChiTietDonHangModal from "@/components/base/modals/ChiTietDonHangModal.vue";

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
      ten: "Laptop Pro Max",
      soLuong: 2,
    },
    soSanPhamConLai: 1,
    tongTien: 50500000,
    thoiGianDat: "2025-06-25 10:10:00",
    trangThai: "cho-xac-nhan",
    danhSachSanPham: [
      {
        id: 1,
        ten: "Laptop Pro Max",
        donGia: 25000000,
        soLuong: 2,
        hinhAnh: "",
      },
      {
        id: 2,
        ten: "Chuột không dây",
        donGia: 500000,
        soLuong: 1,
        hinhAnh: "",
      },
    ],
    thongTin: {
      ten: "Nguyễn Văn A",
      soDienThoai: "0987654321",
      diaChi: "123 Đường ABC, Phường XYZ, Quận 1, TP. Hồ Chí Minh",
      trangThai: "Chờ xác nhận",
      giaoHang: "Giao hàng nhanh",
      thanhToan: "Thanh toán khi nhận hàng (COD)",
      thoiGianDat: "2025-06-25 10:10:00",
      duKienGiao: "2025-06-26",
      ghiChu: "Giao giờ hành chính",
    },
  },
  {
    ma: "8765432",
    sanPhamTieuBieu: {
      ten: "Điện thoại XYZ",
      soLuong: 1,
    },
    soSanPhamConLai: 0,
    tongTien: 12000000,
    thoiGianDat: "2025-06-24 15:30:00",
    trangThai: "dang-giao",
    danhSachSanPham: [
      {
        id: 3,
        ten: "Điện thoại XYZ",
        donGia: 12000000,
        soLuong: 1,
        hinhAnh: "",
      },
    ],
    thongTin: {
      ten: "Trần Thị B",
      soDienThoai: "0123456789",
      diaChi: "456 Đường DEF, Phường UVW, Quận 2, TP. Hồ Chí Minh",
      trangThai: "Đang giao",
      giaoHang: "Giao hàng nhanh",
      thanhToan: "Thanh toán khi nhận hàng (COD)",
      thoiGianDat: "2025-06-24 15:30:00",
      duKienGiao: "2025-06-27",
      ghiChu: "Giao giờ hành chính",
    },
  },
  {
    ma: "9988776",
    sanPhamTieuBieu: {
      ten: "Sách Lập trình Vue.js",
      soLuong: 1,
    },
    soSanPhamConLai: 2,
    tongTien: 750000,
    thoiGianDat: "2025-06-23 09:00:00",
    trangThai: "da-giao",
    danhSachSanPham: [
      {
        id: 4,
        ten: "Sách Lập trình Vue.js",
        donGia: 300000,
        soLuong: 1,
        hinhAnh: "",
      },
      { id: 5, ten: "Bàn phím cơ", donGia: 400000, soLuong: 1, hinhAnh: "" },
      { id: 6, ten: "Lót chuột", donGia: 50000, soLuong: 1, hinhAnh: "" },
    ],
    thongTin: {
      ten: "Lê Văn C",
      soDienThoai: "0912345678",
      diaChi: "789 Đường GHI, Phường KLM, Quận 3, TP. Hồ Chí Minh",
      trangThai: "Đã giao",
      giaoHang: "Giao hàng nhanh",
      thanhToan: "Thanh toán khi nhận hàng (COD)",
      thoiGianDat: "2025-06-23 09:00:00",
      duKienGiao: "2025-06-24",
      ghiChu: "Cảm ơn shop",
    },
  },
  {
    ma: "1122334",
    sanPhamTieuBieu: {
      ten: "Tai nghe Bluetooth",
      soLuong: 1,
    },
    soSanPhamConLai: 0,
    tongTien: 800000,
    thoiGianDat: "2025-06-22 20:00:00",
    trangThai: "da-huy",
    danhSachSanPham: [
      {
        id: 7,
        ten: "Tai nghe Bluetooth",
        donGia: 800000,
        soLuong: 1,
        hinhAnh: "",
      },
    ],
    thongTin: {
      ten: "Phạm Thị D",
      soDienThoai: "0909090909",
      diaChi: "101 Đường MNO, Phường PQR, Quận 4, TP. Hồ Chí Minh",
      trangThai: "Đã huỷ",
      giaoHang: "Giao hàng nhanh",
      thanhToan: "Thanh toán khi nhận hàng (COD)",
      thoiGianDat: "2025-06-22 20:00:00",
      duKienGiao: "2025-06-23",
      ghiChu: "Đổi ý, không mua nữa",
    },
  },
]);

const isModalVisible = ref(false);
const donHangDangChon = ref(null);

const xemChiTietDonHang = (donHang) => {
  donHangDangChon.value = donHang;
  isModalVisible.value = true;
};

const dongModal = () => {
  isModalVisible.value = false;
  donHangDangChon.value = null;
};

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

const huyDonHang = (payload) => {
  // Đảm bảo rằng donHangDangChon vẫn tồn tại
  if (!donHangDangChon.value) return;

  console.log(`Yêu cầu hủy đơn hàng: #${donHangDangChon.value.ma}`);
  console.log(`Lý do: ${payload.lyDo}`);
  if (payload.ghiChu) {
    console.log(`Ghi chú: ${payload.ghiChu}`);
  }
  // TODO: Thêm logic gọi API hủy đơn với `payload`
  dongModal();
};

const giaoPhanConLai = () => {
  console.log("Giao phần còn lại cho đơn hàng:", donHangDangChon.value.ma);
  // TODO: Thêm logic gọi API
  dongModal();
};
</script>
