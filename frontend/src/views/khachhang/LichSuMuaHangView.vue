<template>
  <CustomerWithNav>
    <div class="max-w-7xl mx-auto grid grid-cols-1 lg:grid-cols-4 gap-6 p-6">
      <!-- BỘ LỌC -->
      <div class="lg:col-span-1">
        <AccordionBoLocDonHang
          v-model:tuNgay="tuNgay"
          v-model:denNgay="denNgay"
          v-model:maDonHang="tuKhoa"
          @timKiem="timKiemDonHang"
        />
      </div>

      <!-- DANH SÁCH ĐƠN HÀNG -->
      <div class="lg:col-span-3">
        <h2 class="text-center text-xl font-bold mb-4">LỊCH SỬ MUA HÀNG</h2>

        <!-- TAB -->
        <div
          class="flex border-b mb-4 overflow-x-auto overflow-y-hidden -mx-4 px-4"
        >
          <button
            v-for="trangThai in trangThaiList"
            :key="trangThai.value"
            class="relative px-4 py-2 -mb-px text-sm font-medium transition-colors duration-200 shrink-0"
            :class="[
              trangThaiDangChon === trangThai.value
                ? 'border-b-2 border-blue-600 text-blue-600'
                : 'text-gray-500 hover:text-blue-600',
            ]"
            @click="trangThaiDangChon = trangThai.value"
          >
            {{ trangThai.label }}
            <!-- TODO: Cần API riêng để lấy số lượng đơn hàng theo từng trạng thái -->
          </button>
        </div>

        <!-- Loading State -->
        <div v-if="isLoading" class="text-center py-10">
          <div
            class="animate-spin rounded-full h-10 w-10 border-b-2 border-blue-600 mx-auto"
          ></div>
          <p class="mt-3 text-gray-600">Đang tải đơn hàng...</p>
        </div>

        <!-- Error State -->
        <div v-else-if="apiError" class="text-center py-10">
          <p class="text-red-500">{{ apiError }}</p>
        </div>

        <!-- Empty State -->
        <div
          v-else-if="donHangList.length === 0"
          class="text-center py-10 text-gray-500"
        >
          <i class="fi fi-rr-box-open text-6xl mb-3"></i>
          <p>Không có đơn hàng nào trong mục này.</p>
        </div>

        <!-- DANH SÁCH ĐƠN -->
        <div v-else class="space-y-4 max-h-[60vh] overflow-y-auto pr-2">
          <CardDonHang
            v-for="donHang in donHangList"
            :key="donHang.maDonHang"
            :donHang="donHang"
            @xemChiTiet="xemChiTietDonHang(donHang)"
          />
        </div>

        <!-- TODO: Thêm phân trang -->
      </div>
    </div>

    <ChiTietDonHangModal
      v-if="donHangDangChon"
      :visible="isModalVisible"
      :ma-don-hang="donHangDangChon.maDonHang"
      @close="dongModal"
      @update="handleModalUpdate"
    />
  </CustomerWithNav>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from "vue";
import CustomerWithNav from "@/components/layouts/CustomerWithNav.vue";
import AccordionBoLocDonHang from "@/components/base/AccordionBoLocDonHang.vue";
import CardDonHang from "@/components/base/card/CardDonHang.vue";
import ChiTietDonHangModal from "@/components/base/modals/ChiTietDonHangModal.vue";
import { useToast } from "@/composables/useToast";
import { getLichSuMuaHang } from "@/service/donhang.service";
import type { LichSuDonHang } from "@/types/donhang.types";
import { TrangThaiDonHangFilter } from "@/types/donhang.types";
import { AxiosError } from "axios";

const { showToast } = useToast();

// --- Helper Functions for Dates ---
const getISODateString = (date: Date): string => {
  return date.toISOString().split("T")[0];
};

const getFirstDayOfMonth = (): string => {
  const now = new Date();
  const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);
  return getISODateString(firstDay);
};

// --- State for Filters ---
const tuNgay = ref<string | undefined>(getFirstDayOfMonth());
const denNgay = ref<string | undefined>(getISODateString(new Date()));
const tuKhoa = ref(""); // Dùng cho tìm kiếm theo mã đơn hàng hoặc tên sản phẩm

const trangThaiList = [
  { label: "Chờ xác nhận", value: TrangThaiDonHangFilter.CHO_XAC_NHAN },
  { label: "Chờ xử lý", value: TrangThaiDonHangFilter.CHO_XU_LY },
  { label: "Đang giao", value: TrangThaiDonHangFilter.DANG_GIAO },
  { label: "Đã giao", value: TrangThaiDonHangFilter.DA_GIAO },
  { label: "Đã huỷ", value: TrangThaiDonHangFilter.DA_HUY },
];
const trangThaiDangChon = ref<TrangThaiDonHangFilter>(
  TrangThaiDonHangFilter.CHO_XAC_NHAN
);
// --- State for Data & UI ---
const isLoading = ref(true);
const apiError = ref<string | null>(null);
const donHangList = ref<LichSuDonHang[]>([]);
const currentPage = ref(0);
const totalPages = ref(0);

const isModalVisible = ref(false);
const donHangDangChon = ref<LichSuDonHang | null>(null);
const fetchLichSuDonHang = async () => {
  isLoading.value = true;
  apiError.value = null;
  try {
    const response = await getLichSuMuaHang({
      trangThai: trangThaiDangChon.value,
      tuKhoa: tuKhoa.value || undefined,
      tuNgay: tuNgay.value,
      denNgay: denNgay.value,
      page: currentPage.value,
      size: 10, // Hoặc một giá trị khác bạn muốn
    });
    donHangList.value = response.content;
    totalPages.value = response.totalPages;
  } catch (err) {
    console.error("Lỗi khi tải lịch sử đơn hàng:", err);
    if (err instanceof AxiosError && err.response?.data?.message) {
      apiError.value = err.response.data.message;
    } else {
      apiError.value = "Không thể tải dữ liệu. Vui lòng thử lại sau.";
    }
    donHangList.value = []; // Xóa danh sách cũ nếu có lỗi
  } finally {
    isLoading.value = false;
  }
};

const xemChiTietDonHang = (donHang: LichSuDonHang) => {
  donHangDangChon.value = donHang;
  isModalVisible.value = true;
};

const dongModal = () => {
  isModalVisible.value = false;
  donHangDangChon.value = null;
};

const timKiemDonHang = () => {
  currentPage.value = 0;
  fetchLichSuDonHang();
};

watch(trangThaiDangChon, () => {
  // Khi chuyển tab, chỉ cần reset về trang đầu tiên và tìm kiếm lại
  // Các bộ lọc khác (ngày, từ khóa) sẽ được giữ nguyên.
  timKiemDonHang();
});

// Tự động tìm kiếm khi người dùng thay đổi khoảng ngày.
// Điều này mang lại trải nghiệm lọc tức thì mà không cần nhấn nút "Tìm kiếm".
watch([tuNgay, denNgay], () => {
  timKiemDonHang();
});

onMounted(() => {
  fetchLichSuDonHang();
});

const handleModalUpdate = () => {
  // Đóng modal và tải lại danh sách đơn hàng khi có cập nhật
  isModalVisible.value = false;
  donHangDangChon.value = null;
  fetchLichSuDonHang();
};
</script>
