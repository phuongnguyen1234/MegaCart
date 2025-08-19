<template>
  <div class="flex min-h-screen bg-gray-50">
    <!-- Side Menu (chế độ tối giản, chỉ hiển thị nút đăng xuất) -->
    <TheSideMenu variant="minimal" />

    <!-- Nội dung chính -->
    <main class="flex-1 p-4 sm:p-6 ml-[250px]">
      <h1 class="text-2xl font-bold text-gray-800 mb-4">Giao hàng</h1>

      <!-- Bộ lọc -->
      <div class="flex flex-col sm:flex-row sm:items-end sm:gap-4 gap-2 mb-4">
        <ThanhTimKiem
          class="w-full sm:w-auto"
          :ds-tieu-chi="[
            { value: 'maDonHang', label: 'Mã đơn hàng' },
            { value: 'tenKhachHang', label: 'Tên khách hàng' },
          ]"
          v-model:modelValueLoai="loaiTimKiem"
          v-model:modelValueTuKhoa="tuKhoa"
        />
      </div>

      <!-- Số lượng đơn -->
      <p class="mb-4 text-sm text-gray-600">{{ thongTinHienThi }}</p>

      <!-- Danh sách đơn hàng -->
      <div class="space-y-4">
        <div
          v-if="donHangHienThi.length === 0"
          class="text-center py-10 text-gray-500"
        >
          Không có đơn hàng nào phù hợp.
        </div>
        <CardGiaoHang
          v-for="donHang in donHangHienThi"
          :key="donHang.maDonHang"
          :don-hang="donHang"
          @xem-chi-tiet="openXemChiTietModal"
        />
      </div>

      <!-- Phân trang -->
      <div class="mt-6 flex justify-center">
        <PhanTrang
          v-model:trangHienTai="trangHienTai"
          :tong-so-trang="tongSoTrang"
        />
      </div>

      <!-- Modal xem chi tiết và cập nhật -->
      <CapNhatGiaoHangModal
        :visible="isXemChiTietModalVisible"
        :don-hang="donHangDangChon"
        @close="closeXemChiTietModal"
        @xacNhan="handleCapNhatTrangThai"
      />
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import CardGiaoHang from "@/components/giaohang/CardGiaoHang.vue";
import CapNhatGiaoHangModal from "@/components/giaohang/CapNhatGiaoHangModal.vue";
import PhanTrang from "@/components/base/PhanTrang.vue";
import ThanhTimKiem from "@/components/base/ThanhTimKiem.vue";
import TheSideMenu from "@/components/layouts/TheSideMenu.vue";
import type { DonHang } from "@/types/DonHang";

// --- State ---
const loaiTimKiem = ref("maDonHang");
const tuKhoa = ref("");
const trangHienTai = ref(0);
const soLuongMoiTrang = 10;

const isXemChiTietModalVisible = ref(false);
const donHangDangChon = ref<DonHang | null>(null);

// --- Dữ liệu mẫu ---
const allDonHang = ref<DonHang[]>(
  Array.from({ length: 55 }, (_, i) => ({
    maDonHang: `DH${1000 + i}`,
    nguoiGiao: `Shipper ${String.fromCharCode(65 + (i % 5))}`,
    tenNguoiNhan: `Nguyễn Văn ${String.fromCharCode(65 + (i % 26))}`,
    diaChiNhanHang: `${i + 1} Đường ABC, Phường XYZ, Quận 1, TP. HCM`,
    sdtNhanHang: `0909123${String(i).padStart(3, "0")}`,
    tongTien: 150000 + i * 10000,
    hinhThucThanhToan:
      i % 2 === 0 ? "Thanh toán khi nhận hàng" : "Thanh toán online",
    hinhThucNhanHang: i % 2 === 0 ? "Giao hàng tiêu chuẩn" : "Giao hàng nhanh",
    trangThaiDonHang: "Chờ xác nhận", // Trạng thái mặc định
    trangThaiThanhToan: "Chưa thanh toán",
    thoiGianDatHang: new Date(
      Date.now() - i * 24 * 60 * 60 * 1000
    ).toISOString(),
    thoiGianThanhToan: "N/A",
    chiTietDonHang: [
      {
        maSanPham: `SP00${i % 10}`,
        tenSanPham: `Sản phẩm mẫu ${i % 10}`,
        soLuong: (i % 3) + 1,
        gia: 50000,
        hinhAnh: `https://via.placeholder.com/150/0000FF/808080?Text=SP${
          i % 10
        }`,
      },
    ],
  }))
);

const danhSachNguoiGiaoHang = ref([
  "Shipper A",
  "Shipper B",
  "Shipper C",
  "Shipper D",
  "Shipper E",
  "Shipper F",
]);

// --- Computed ---
const placeholderTimKiem = computed(() => {
  if (loaiTimKiem.value === "maDonHang") {
    return "Tìm kiếm mã đơn hàng...";
  }
  return "Tìm kiếm tên khách hàng...";
});

const donHangDaLoc = computed(() =>
  allDonHang.value.filter((d) => {
    if (!tuKhoa.value.trim()) return true;
    const keyword = tuKhoa.value.toLowerCase();
    if (loaiTimKiem.value === "maDonHang") {
      return d.maDonHang.toLowerCase().includes(keyword);
    }
    if (loaiTimKiem.value === "tenKhachHang") {
      return d.tenNguoiNhan.toLowerCase().includes(keyword);
    }
    return true; // Mặc định không lọc nếu loại tìm kiếm không xác định
  })
);

// --- Phân trang ---
const tongSoTrang = computed(() =>
  Math.ceil(donHangDaLoc.value.length / soLuongMoiTrang)
);

const donHangHienThi = computed(() => {
  const batDau = trangHienTai.value * soLuongMoiTrang;
  return donHangDaLoc.value.slice(batDau, batDau + soLuongMoiTrang);
});

const thongTinHienThi = computed(() => {
  const tongSo = donHangDaLoc.value.length;
  if (tongSo === 0) {
    return "Không tìm thấy đơn hàng nào.";
  }

  const batDau = trangHienTai.value * soLuongMoiTrang + 1;
  const ketThuc = batDau + donHangHienThi.value.length - 1;

  return `Hiển thị từ ${batDau} đến ${ketThuc} trên tổng số ${tongSo} đơn hàng.`;
});

// --- Modal ---
const openXemChiTietModal = (donHang: DonHang) => {
  donHangDangChon.value = donHang;
  isXemChiTietModalVisible.value = true;
};

const closeXemChiTietModal = () => {
  isXemChiTietModalVisible.value = false;
  donHangDangChon.value = null;
};

const handleCapNhatTrangThai = (
  maDonHang: string,
  trangThaiMoi: "Đang giao" | "Đã giao" | "Hủy"
) => {
  const donHangCanCapNhat = allDonHang.value.find(
    (dh) => dh.maDonHang === maDonHang
  );
  if (donHangCanCapNhat) {
    donHangCanCapNhat.trangThaiDonHang = trangThaiMoi;
    // Ở đây bạn có thể gọi API để cập nhật trạng thái trên server
    console.log(`Đã cập nhật đơn hàng ${maDonHang} thành ${trangThaiMoi}`);
    // Hiển thị thông báo thành công (nếu cần)
  }
  closeXemChiTietModal();
};

// --- Reset về trang đầu nếu bộ lọc thay đổi ---
watch([tuKhoa, loaiTimKiem], () => {
  trangHienTai.value = 0;
});
</script>
