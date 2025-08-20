<template>
  <div class="flex min-h-screen bg-gray-50">
    <!-- Side Menu (chế độ tối giản, chỉ hiển thị nút đăng xuất) -->
    <!-- TheSideMenu is likely part of a layout, but kept here as per original file -->
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
        <div v-if="isLoading" class="text-center py-10 text-gray-500">
          Đang tải dữ liệu...
        </div>
        <div
          v-else-if="danhSachDonHang.length === 0"
          class="text-center py-10 text-gray-500"
        >
          Không có đơn hàng nào phù hợp.
        </div>
        <CardGiaoHang
          v-else
          v-for="donHang in danhSachDonHang"
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
import { ref, computed, watch, onMounted } from "vue";
import CardGiaoHang from "@/components/giaohang/CardGiaoHang.vue";
import CapNhatGiaoHangModal from "@/components/giaohang/CapNhatGiaoHangModal.vue";
import PhanTrang from "@/components/base/PhanTrang.vue";
import ThanhTimKiem from "@/components/base/ThanhTimKiem.vue";
import TheSideMenu from "@/components/layouts/TheSideMenu.vue";
import { useToast } from "@/composables/useToast";
import {
  getDSDonHangCanGiao,
  getChiTietDonHangGiaoHang,
  capNhatTrangThaiGiaoHang,
} from "@/service/giaohang.service";
import type {
  DonHangGiaoHangResponse,
  GetDonHangGiaoHangParams,
  ChiTietDonHangGiaoHangResponse,
  CapNhatGiaoHangRequest,
} from "@/types/giaohang.types";

const { showToast } = useToast();

// --- State for Filters & Pagination ---
const loaiTimKiem = ref<"maDonHang" | "tenKhachHang">("maDonHang");
const tuKhoa = ref("");
const trangHienTai = ref(0);
const soLuongMoiTrang = 10;

// --- State for Data ---
const danhSachDonHang = ref<DonHangGiaoHangResponse[]>([]);
const tongSoTrang = ref(1);
const totalElements = ref(0);
const isLoading = ref(false);

// --- State for Modal ---
const isXemChiTietModalVisible = ref(false);
const donHangDangChon = ref<ChiTietDonHangGiaoHangResponse | null>(null);

// --- Data Fetching ---
const fetchDonHang = async () => {
  isLoading.value = true;
  try {
    const params: GetDonHangGiaoHangParams = {
      page: trangHienTai.value,
      size: soLuongMoiTrang,
      searchField: tuKhoa.value ? loaiTimKiem.value : undefined,
      searchValue: tuKhoa.value || undefined,
    };
    const response = await getDSDonHangCanGiao(params);
    danhSachDonHang.value = response.content;
    tongSoTrang.value = response.totalPages;
    totalElements.value = response.totalElements;
  } catch (error) {
    showToast({
      loai: "loi",
      thongBao: "Không thể tải danh sách đơn hàng.",
    });
    danhSachDonHang.value = [];
    tongSoTrang.value = 1;
    totalElements.value = 0;
  } finally {
    isLoading.value = false;
  }
};

onMounted(fetchDonHang);

// --- Watchers ---
watch([tuKhoa, loaiTimKiem], () => {
  trangHienTai.value = 0;
});

watch([trangHienTai, tuKhoa, loaiTimKiem], fetchDonHang);

// --- Computed ---
const thongTinHienThi = computed(() => {
  const total = totalElements.value;
  if (total === 0) {
    return "Không tìm thấy đơn hàng nào.";
  }

  const batDau = trangHienTai.value * soLuongMoiTrang + 1;
  const ketThuc = batDau + danhSachDonHang.value.length - 1;

  return `Hiển thị từ ${batDau} đến ${ketThuc} trên tổng số ${total} đơn hàng.`;
});

// --- Modal ---
const openXemChiTietModal = async (donHang: DonHangGiaoHangResponse) => {
  isXemChiTietModalVisible.value = true;
  donHangDangChon.value = null; // Reset before fetching
  try {
    // Show a loading state in the modal if needed
    const chiTiet = await getChiTietDonHangGiaoHang(donHang.maDonHang);
    donHangDangChon.value = chiTiet;
  } catch (error) {
    showToast({
      loai: "loi",
      thongBao: "Không thể tải chi tiết đơn hàng.",
    });
    closeXemChiTietModal();
  }
};

const closeXemChiTietModal = () => {
  isXemChiTietModalVisible.value = false;
  donHangDangChon.value = null;
};

const handleCapNhatTrangThai = async (payload: CapNhatGiaoHangRequest) => {
  if (!donHangDangChon.value) return;

  try {
    await capNhatTrangThaiGiaoHang(donHangDangChon.value.maDonHang, payload);
    showToast({
      loai: "thanhCong",
      thongBao: "Cập nhật trạng thái giao hàng thành công!",
    });
    closeXemChiTietModal();
    fetchDonHang(); // Refresh the list
  } catch (error) {
    showToast({
      loai: "loi",
      thongBao: "Có lỗi xảy ra khi cập nhật.",
    });
  }
};
</script>
