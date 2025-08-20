<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-4">Quản lí giao hàng</h1>

    <!-- Bộ lọc -->
    <div class="flex flex-wrap items-end gap-4 mb-4">
      <!-- Tìm kiếm -->
      <ThanhTimKiem
        :ds-tieu-chi="[
          { value: 'maDonHang', label: 'Mã đơn hàng' },
          { value: 'tenNguoiNhan', label: 'Tên người nhận' },
          { value: 'tenNhanVienGiaoHang', label: 'Tên nhân viên giao' },
          { value: 'sdtNhanHang', label: 'SĐT người nhận' },
          { value: 'diaChiNhanHang', label: 'Địa chỉ nhận' },
        ]"
        v-model:modelValueLoai="loaiTimKiem"
        v-model:modelValueTuKhoa="tuKhoa"
      />
    </div>

    <!-- Số lượng đơn -->
    <p class="mb-4 text-base text-gray-600">{{ thongTinHienThi }}</p>

    <!-- Bảng dữ liệu -->
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
      <CardDonHangGiaoHang
        v-else
        v-for="donHang in danhSachDonHang"
        :key="donHang.maDonHang"
        :don-hang="donHang"
        @doiNguoiGiao="openDoiNguoiGiaoModal"
      />
    </div>

    <!-- Phân trang -->
    <div class="mt-4 flex justify-center">
      <PhanTrang
        v-model:trangHienTai="trangHienTai"
        :tong-so-trang="tongSoTrang"
      />
    </div>

    <!-- Modal Đổi người giao -->
    <BaseModal
      :visible="isDoiNguoiGiaoModalVisible"
      title="Cập nhật thông tin giao hàng"
      @close="closeDoiNguoiGiaoModal"
    >
      <div v-if="isLoadingModal">Đang tải thông tin...</div>
      <div v-if="donHangDangChon" class="space-y-4">
        <p>
          Mã đơn hàng:
          <span class="font-semibold">#{{ donHangDangChon.maDonHang }}</span>
        </p>
        <p>
          Người nhận:
          <span class="font-semibold">{{ donHangDangChon.tenNguoiNhan }}</span>
        </p>
        <p>
          Địa chỉ:
          <span class="font-semibold">{{
            donHangDangChon.diaChiNhanHang
          }}</span>
        </p>
        <div>
          <label
            for="nguoi-giao-moi"
            class="block text-sm font-medium text-gray-700"
            >Người giao</label
          >
          <select
            id="nguoi-giao-moi"
            v-model="nguoiGiaoMoiId"
            class="mt-1 block w-full border border-gray-300 rounded px-2 py-1 shadow-sm"
          >
            <option
              v-for="shipper in danhSachNhanVienGiaoHang"
              :key="shipper.maNhanVien"
              :value="shipper.maNhanVien"
            >
              {{ shipper.tenNhanVien }}
            </option>
          </select>
        </div>
      </div>
      <template #footer>
        <div class="flex justify-end gap-2">
          <button
            @click="closeDoiNguoiGiaoModal"
            class="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300"
          >
            Hủy
          </button>
          <button
            @click="luuNguoiGiaoMoi"
            class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
            :disabled="!nguoiGiaoMoiId || isSaving"
          >
            {{ isSaving ? "Đang lưu..." : "Lưu" }}
          </button>
        </div>
      </template>
    </BaseModal>
  </div>
</template>
<script setup lang="ts">
import { ref, computed, watch, onMounted } from "vue";
import CardDonHangGiaoHang from "@/components/giaohang/CardDonHangGiaoHang.vue";
import PhanTrang from "@/components/base/PhanTrang.vue";
import BaseModal from "@/components/base/modals/BaseModal.vue";
import ThanhTimKiem from "@/components/base/ThanhTimKiem.vue";
import { useToast } from "@/composables/useToast";
import {
  getDSDonHangDangGiao,
  getChiTietGiaoHangQuanLy,
  ganNhanVienGiaoHang,
} from "@/service/quanligiaohang.service";
import { getDSNhanVienGiaoHang } from "@/service/quanlinhanvien.service";
import type {
  DonHangDangGiaoQuanLyResponse,
  GetDonHangDangGiaoParams,
  ChiTietGiaoHangQuanLyResponse,
} from "@/types/giaohang.types";
import type { NhanVienOptionResponse } from "@/types/nhanvien.types";

const { showToast } = useToast();

// --- State for Filters & Pagination ---
const loaiTimKiem = ref<
  | "maDonHang"
  | "tenNguoiNhan"
  | "tenNhanVienGiaoHang"
  | "sdtNhanHang"
  | "diaChiNhanHang"
>("maDonHang");
const tuKhoa = ref("");
const trangHienTai = ref(0);
const soLuongMoiTrang = 10;

// --- State for Data ---
const danhSachDonHang = ref<DonHangDangGiaoQuanLyResponse[]>([]);
const tongSoTrang = ref(1);
const totalElements = ref(0);
const isLoading = ref(false);
const danhSachNhanVienGiaoHang = ref<NhanVienOptionResponse[]>([]);

// --- State for Modal ---
const isDoiNguoiGiaoModalVisible = ref(false);
const donHangDangChon = ref<ChiTietGiaoHangQuanLyResponse | null>(null);
const nguoiGiaoMoiId = ref<number | null>(null);
const isLoadingModal = ref(false);
const isSaving = ref(false);

// --- Data Fetching ---
const fetchDonHang = async () => {
  isLoading.value = true;
  try {
    const params: GetDonHangDangGiaoParams = {
      page: trangHienTai.value,
      size: soLuongMoiTrang,
      searchField: tuKhoa.value ? loaiTimKiem.value : undefined,
      searchValue: tuKhoa.value || undefined,
    };
    const response = await getDSDonHangDangGiao(params);
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

const fetchNhanVienGiaoHang = async () => {
  try {
    danhSachNhanVienGiaoHang.value = await getDSNhanVienGiaoHang();
  } catch (error) {
    showToast({
      loai: "loi",
      thongBao: "Không thể tải danh sách nhân viên giao hàng.",
    });
  }
};

onMounted(() => {
  fetchDonHang();
  fetchNhanVienGiaoHang();
});

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
const openDoiNguoiGiaoModal = async (
  donHang: DonHangDangGiaoQuanLyResponse
) => {
  isDoiNguoiGiaoModalVisible.value = true;
  isLoadingModal.value = true;
  donHangDangChon.value = null;
  nguoiGiaoMoiId.value = null;

  try {
    const chiTiet = await getChiTietGiaoHangQuanLy(donHang.maDonHang);
    donHangDangChon.value = chiTiet;
    // Set the current delivery person in the dropdown
    if (chiTiet.maNhanVienGiaoHang) {
      nguoiGiaoMoiId.value = chiTiet.maNhanVienGiaoHang;
    }
  } catch (error) {
    showToast({
      loai: "loi",
      thongBao: "Không thể tải chi tiết giao hàng.",
    });
    closeDoiNguoiGiaoModal();
  } finally {
    isLoadingModal.value = false;
  }
};

const closeDoiNguoiGiaoModal = () => {
  isDoiNguoiGiaoModalVisible.value = false;
  donHangDangChon.value = null;
  nguoiGiaoMoiId.value = null;
};

const luuNguoiGiaoMoi = async () => {
  if (!donHangDangChon.value || !nguoiGiaoMoiId.value) {
    showToast({ loai: "loi", thongBao: "Vui lòng chọn một người giao." });
    return;
  }

  isSaving.value = true;
  try {
    await ganNhanVienGiaoHang(donHangDangChon.value.maDonHang, {
      maNhanVienGiaoHang: nguoiGiaoMoiId.value,
    });
    showToast({
      loai: "thanhCong",
      thongBao: "Cập nhật người giao hàng thành công!",
    });
    closeDoiNguoiGiaoModal();
    fetchDonHang(); // Refresh the list
  } catch (error) {
    showToast({
      loai: "loi",
      thongBao: "Có lỗi xảy ra khi cập nhật.",
    });
  } finally {
    isSaving.value = false;
  }
};
</script>
