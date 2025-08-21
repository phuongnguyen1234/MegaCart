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
          :disabled="areOtherFiltersDisabled"
          :class="{ 'bg-gray-100 cursor-not-allowed': areOtherFiltersDisabled }"
        />
      </div>

      <!-- Tìm kiếm -->
      <ThanhTimKiem
        :ds-tieu-chi="dsTieuChiTimKiem"
        v-model:modelValueLoai="loaiTimKiem"
        v-model:modelValueTuKhoa="tuKhoa"
        @idSearchActive="areOtherFiltersDisabled = $event"
      />

      <!-- Trạng thái -->
      <div>
        <label for="trang-thai" class="block text-sm font-medium text-gray-700">
          Trạng thái
        </label>
        <select
          id="trang-thai"
          v-model="trangThai"
          class="mt-1 block w-48 border border-gray-300 rounded px-2 py-1 shadow-sm"
          :disabled="areOtherFiltersDisabled"
          :class="{ 'bg-gray-100 cursor-not-allowed': areOtherFiltersDisabled }"
        >
          <option v-for="tt in dsTrangThai" :key="tt.value" :value="tt.value">
            {{ tt.text }}
          </option>
        </select>
      </div>
    </div>

    <!-- Số lượng đơn -->
    <p class="mb-2 text-base text-gray-600">{{ thongTinHienThi }}</p>

    <!-- Bảng -->
    <div class="overflow-x-auto shadow-md sm:rounded-lg">
      <table class="w-full text-base text-left text-gray-500">
        <thead class="text-xs text-gray-700 uppercase bg-gray-50">
          <tr>
            <th
              v-for="header in headers"
              :key="header"
              scope="col"
              class="px-6 py-3"
            >
              {{ header }}
            </th>
            <th scope="col" class="px-6 py-3 text-right">Hành động</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="donHangHienThi.length === 0">
            <td :colspan="headers.length + 1" class="text-center py-10">
              Không có đơn hàng nào phù hợp.
            </td>
          </tr>
          <tr
            v-for="donHang in donHangHienThi"
            :key="donHang.maDonHang"
            class="bg-white border-b hover:bg-gray-50"
          >
            <td class="px-6 py-4 font-medium text-gray-900">
              #{{ donHang.maDonHang }}
            </td>
            <td class="px-6 py-4">{{ donHang.tenKhachHang }}</td>
            <td class="px-6 py-4">
              {{ formatDateTimeLocal(donHang.thoiGianDatHang) }}
            </td>
            <td class="px-6 py-4">
              <span
                :class="[
                  'px-2 inline-flex text-xs leading-5 font-semibold rounded-full',
                  getTrangThaiClass(donHang.trangThai.value),
                ]"
              >
                {{ donHang.trangThai.label }}
              </span>
            </td>
            <td class="px-6 py-4">{{ formatCurrency(donHang.tongTien) }}</td>
            <td class="px-6 py-4 text-right">
              <button
                @click="openModal(donHang)"
                class="text-gray-500 hover:text-blue-600 transition-colors duration-200"
                title="Xem chi tiết"
              >
                <i class="fi fi-rr-eye text-lg align-middle"></i>
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Phân trang -->
    <div class="mt-4 flex justify-center">
      <PhanTrang
        v-model:trangHienTai="trangHienTai"
        :tong-so-trang="tongSoTrang"
      />
    </div>

    <!-- Modal Chi Tiết Đơn Hàng -->
    <ChiTietDonHangQLyModal
      :visible="isModalVisible"
      :don-hang="selectedDonHang"
      :la-che-do-chinh-sua="isEditMode"
      :danh-sach-san-pham="sanPhamForModal"
      :tong-tien="selectedDonHang?.tongTien || 0"
      @close="closeModal"
      @luu="handleLuuThayDoi"
    />
  </div>
</template>

<script setup lang="ts">
import PhanTrang from "@/components/base/PhanTrang.vue";
import ChiTietDonHangQLyModal from "@/components/quanlidonhang/ChiTietDonHangQLyModal.vue";
import ThanhTimKiem from "@/components/base/ThanhTimKiem.vue";
import { ref, computed, watch } from "vue";
import {
  DonHangQuanLyResponse,
  ChiTietDonHangQuanLyResponse,
  GetDonHangQuanLyParams,
  TrangThaiDonHangKey,
  CapNhatDonHangRequest,
  TrangThaiThanhToanKey,
  type ChiTietDonHangItem,
} from "@/types/donhang.types";
import {
  getDanhSachDonHangQuanLy,
  getChiTietDonHangQuanLy,
  capNhatDonHang,
} from "@/service/donhang.service";
import { useToast } from "@/composables/useToast";
import { formatCurrency, formatDateTimeLocal } from "@/utils/formatters";

// Helper function to get today's date in YYYY-MM-DD format
const getTodayISOString = (): string => {
  const today = new Date();
  const year = today.getFullYear();
  const month = String(today.getMonth() + 1).padStart(2, "0");
  const day = String(today.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
};

// --- Bộ lọc ---
const ngayDat = ref(getTodayISOString()); // Mặc định là ngày hôm nay
const loaiTimKiem = ref<"maDonHang" | "tenKhachHang">("tenKhachHang");
const tuKhoa = ref("");
const trangThai = ref(""); // TrangThaiDonHangKey hoặc ""
const trangHienTai = ref(0);
const soLuongMoiTrang = ref(20);
const { showToast } = useToast();
const areOtherFiltersDisabled = ref(false);

// Helper để lấy thông báo lỗi chi tiết
const getErrorMessage = (error: any): string => {
  if (error && error.response && error.response.data) {
    // Backend có thể trả về lỗi trong 'message' hoặc 'error'
    return (
      error.response.data.message ||
      error.response.data.error ||
      "Lỗi từ server nhưng không có message cụ thể."
    );
  }
  if (error instanceof Error) {
    return error.message;
  }
  return "Đã có lỗi không mong muốn xảy ra.";
};

const dsTieuChiTimKiem = [
  { value: "tenKhachHang", label: "Tên người nhận" },
  { value: "maDonHang", label: "Mã đơn hàng", isId: true },
];

const dsTrangThai = [
  { value: "", text: "Tất cả" },
  { value: TrangThaiDonHangKey.CHO_XAC_NHAN, text: "Chờ xác nhận" },
  { value: TrangThaiDonHangKey.CHO_XU_LY, text: "Chờ xử lí" },
  { value: TrangThaiDonHangKey.DANG_GIAO, text: "Đang giao" },
  { value: TrangThaiDonHangKey.DA_GIAO, text: "Đã giao" },
  { value: TrangThaiDonHangKey.DA_HUY, text: "Đã hủy" },
];

// --- Danh sách đơn hàng ---
const danhSachDonHang = ref<DonHangQuanLyResponse[]>([]);
const tongSoTrang = ref(1);
const totalElements = ref(0);
const isLoading = ref(false);

// --- Fetch đơn hàng từ API ---
const fetchDonHang = async () => {
  isLoading.value = true;
  try {
    const params: GetDonHangQuanLyParams = {
      page: trangHienTai.value,
      size: soLuongMoiTrang.value,
      searchField: tuKhoa.value ? loaiTimKiem.value : undefined,
      searchValue: tuKhoa.value || undefined,
      // Chỉ thêm các bộ lọc khác nếu chúng không bị vô hiệu hóa
      trangThai: areOtherFiltersDisabled.value
        ? undefined
        : trangThai.value
        ? (trangThai.value as TrangThaiDonHangKey)
        : undefined,
      ngayDat: areOtherFiltersDisabled.value
        ? undefined
        : ngayDat.value || undefined,
    };
    const res = await getDanhSachDonHangQuanLy(params);
    danhSachDonHang.value = res?.content ?? [];
    tongSoTrang.value = res?.totalPages ?? 1;
    totalElements.value = res?.totalElements ?? 0;
  } catch (error) {
    danhSachDonHang.value = [];
    tongSoTrang.value = 1;
    totalElements.value = 0;
    console.error("Lỗi khi tải danh sách đơn hàng:", error);
    showToast({ loai: "loi", thongBao: getErrorMessage(error) });
  } finally {
    isLoading.value = false;
  }
};

// --- Watchers (Cấu trúc lại để tránh race condition) ---

// 1. Khi các bộ lọc thay đổi, reset về trang đầu tiên.
// Việc gán trangHienTai = 0 sẽ kích hoạt watcher số 2 để fetch dữ liệu.
// Nếu đã ở trang 0, thì fetch trực tiếp.
watch([ngayDat, tuKhoa, trangThai], () => {
  if (trangHienTai.value !== 0) {
    trangHienTai.value = 0;
  } else {
    fetchDonHang();
  }
});

// 2. Khi trang hiện tại thay đổi (do người dùng click phân trang hoặc do watcher 1 reset), fetch dữ liệu.
watch(trangHienTai, fetchDonHang);

// 3. Khi thay đổi loại tìm kiếm, xử lý logic một cách tập trung để tránh race condition
watch(loaiTimKiem, (newLoai) => {
  const isIdSearch =
    dsTieuChiTimKiem.find((t) => t.value === newLoai)?.isId ?? false;
  areOtherFiltersDisabled.value = isIdSearch;
  tuKhoa.value = "";

  if (isIdSearch) {
    ngayDat.value = "";
    trangThai.value = "";
  } else {
    ngayDat.value = getTodayISOString();
  }
});

// 4. Tải dữ liệu lần đầu tiên khi component được tạo.
fetchDonHang();

// --- Header bảng ---
const headers = [
  "Mã đơn hàng",
  "Tên người nhận",
  "Thời gian đặt",
  "Trạng thái",
  "Tổng tiền",
];

// --- Định dạng dữ liệu ---
const getTrangThaiClass = (trangThaiKey: TrangThaiDonHangKey): string => {
  switch (trangThaiKey) {
    case TrangThaiDonHangKey.CHO_XAC_NHAN:
      return "bg-yellow-100 text-yellow-800";
    case TrangThaiDonHangKey.CHO_XU_LY:
      return "bg-blue-100 text-blue-800";
    case TrangThaiDonHangKey.DANG_GIAO:
      return "bg-indigo-100 text-indigo-800";
    case TrangThaiDonHangKey.DA_GIAO:
      return "bg-green-100 text-green-800";
    case TrangThaiDonHangKey.DA_HUY:
      return "bg-red-100 text-red-800";
    default:
      return "bg-gray-100 text-gray-800";
  }
};

// --- Hiển thị số lượng ---
const thongTinHienThi = computed(() => {
  const total = totalElements.value;
  if (total === 0) return "Không tìm thấy đơn hàng nào.";
  const batDau = trangHienTai.value * soLuongMoiTrang.value + 1;
  const ketThuc = batDau + danhSachDonHang.value.length - 1;
  return `Hiển thị từ ${batDau} đến ${ketThuc} trên tổng số ${total} đơn hàng.`;
});

// --- Dữ liệu hiển thị bảng ---
const donHangHienThi = computed(() => danhSachDonHang.value ?? []);

// --- Modal hiển thị chi tiết đơn hàng ---
const isModalVisible = ref(false);
const selectedDonHang = ref<ChiTietDonHangQuanLyResponse | null>(null);
const isEditMode = ref(false);
const sanPhamForModal = ref<ChiTietDonHangItem[]>([]); // Danh sách sản phẩm cho modal

const openModal = async (donHang: DonHangQuanLyResponse) => {
  isModalVisible.value = true;
  // Mặc định là chế độ xem, sẽ cập nhật sau khi lấy chi tiết
  isEditMode.value = false;
  try {
    const chiTiet = await getChiTietDonHangQuanLy(donHang.maDonHang);
    selectedDonHang.value = chiTiet;
    sanPhamForModal.value = chiTiet.items;

    // Điều kiện để cho phép sửa:
    // 1. Trạng thái đơn hàng là "Chờ xử lý", "Đang giao" hoặc "Đã giao"
    // 2. VÀ Trạng thái thanh toán là "Chưa thanh toán"
    const coTheSuaTheoTrangThaiDon =
      chiTiet.trangThai.value === TrangThaiDonHangKey.CHO_XU_LY ||
      chiTiet.trangThai.value === TrangThaiDonHangKey.DANG_GIAO ||
      chiTiet.trangThai.value === TrangThaiDonHangKey.DA_GIAO;
    const chuaThanhToan =
      chiTiet.trangThaiThanhToan.value ===
      TrangThaiThanhToanKey.CHUA_THANH_TOAN;

    isEditMode.value = coTheSuaTheoTrangThaiDon && chuaThanhToan;
  } catch (error) {
    console.error("Lỗi khi lấy chi tiết đơn hàng:", error);
    showToast({ loai: "loi", thongBao: getErrorMessage(error) });
    selectedDonHang.value = null;
    sanPhamForModal.value = [];
    closeModal();
  }
};

const closeModal = () => {
  isModalVisible.value = false;
  selectedDonHang.value = null;
  sanPhamForModal.value = [];
};

const handleLuuThayDoi = async (data: CapNhatDonHangRequest) => {
  if (!selectedDonHang.value) return;
  try {
    // Ở đây bạn có thể thêm một loading state cho modal
    await capNhatDonHang(selectedDonHang.value.maDonHang, data);
    // Cập nhật lại danh sách hoặc chỉ item đã thay đổi
    fetchDonHang();
    closeModal();
    showToast({ loai: "thanhCong", thongBao: "Cập nhật thành công!" });
  } catch (error) {
    console.error("Lỗi khi cập nhật đơn hàng:", error);
    showToast({ loai: "loi", thongBao: getErrorMessage(error) });
  }
};
</script>
