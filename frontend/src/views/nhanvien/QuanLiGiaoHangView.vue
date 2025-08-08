<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-4">Quản lí giao hàng</h1>

    <!-- Bộ lọc -->
    <div class="flex flex-wrap items-end gap-4 mb-4">
      <!-- Tìm kiếm -->
      <ThanhTimKiem
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
      <CardDonHangGiaoHang
        v-for="donHang in donHangHienThi"
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
          <span class="font-semibold">{{ donHangDangChon.diaChi }}</span>
        </p>
        <div>
          <label
            for="nguoi-giao-moi"
            class="block text-sm font-medium text-gray-700"
          >
            Người giao
          </label>
          <select
            id="nguoi-giao-moi"
            v-model="nguoiGiaoMoi"
            class="mt-1 block w-full border border-gray-300 rounded px-2 py-1 shadow-sm"
          >
            <option
              v-for="shipper in danhSachNguoiGiaoHang"
              :key="shipper"
              :value="shipper"
            >
              {{ shipper }}
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
            :disabled="!nguoiGiaoMoi"
          >
            Lưu
          </button>
        </div>
      </template>
    </BaseModal>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import CardDonHangGiaoHang from "@/components/giaohang/CardDonHangGiaoHang.vue";
import PhanTrang from "@/components/base/PhanTrang.vue";
import BaseModal from "@/components/base/modals/BaseModal.vue";
import ThanhTimKiem from "@/components/base/ThanhTimKiem.vue";

// --- Types ---
interface DonHangGiao {
  maDonHang: string;
  nguoiGiao: string;
  tenNguoiNhan: string;
  diaChi: string;
  sdtNguoiNhan: string;
  tongTien: number;
  trangThaiThanhToan: "Đã thanh toán" | "Chưa thanh toán";
}

// --- State ---
const loaiTimKiem = ref("maDonHang");
const tuKhoa = ref("");
const trangHienTai = ref(0);
const soLuongMoiTrang = 10;

const isDoiNguoiGiaoModalVisible = ref(false);
const donHangDangChon = ref<DonHangGiao | null>(null);
const nguoiGiaoMoi = ref("");

// --- Dữ liệu mẫu ---
const allDonHang = ref<DonHangGiao[]>(
  Array.from({ length: 55 }, (_, i) => ({
    maDonHang: `DH${1000 + i}`,
    nguoiGiao: `Shipper ${String.fromCharCode(65 + (i % 5))}`,
    tenNguoiNhan: `Nguyễn Văn ${String.fromCharCode(65 + (i % 26))}`,
    diaChi: `${i + 1} Đường ABC, Phường XYZ, Quận 1, TP. HCM`,
    sdtNguoiNhan: `0909123${String(i).padStart(3, "0")}`,
    tongTien: 50000 + i * 1000,
    trangThaiThanhToan: i % 3 === 0 ? "Đã thanh toán" : "Chưa thanh toán",
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
    // Mặc định tìm theo tên khách hàng
    return d.tenNguoiNhan.toLowerCase().includes(keyword);
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
const openDoiNguoiGiaoModal = (donHang: DonHangGiao) => {
  donHangDangChon.value = donHang;
  nguoiGiaoMoi.value = donHang.nguoiGiao; // Chọn sẵn người giao hiện tại
  isDoiNguoiGiaoModalVisible.value = true;
};

const closeDoiNguoiGiaoModal = () => {
  isDoiNguoiGiaoModalVisible.value = false;
  donHangDangChon.value = null;
};

const luuNguoiGiaoMoi = () => {
  if (donHangDangChon.value && nguoiGiaoMoi.value) {
    const index = allDonHang.value.findIndex(
      (d) => d.maDonHang === donHangDangChon.value!.maDonHang
    );
    if (index !== -1) {
      allDonHang.value[index].nguoiGiao = nguoiGiaoMoi.value;
    }
  }
  closeDoiNguoiGiaoModal();
};

// --- Reset về trang đầu nếu bộ lọc thay đổi ---
watch([tuKhoa, loaiTimKiem], () => {
  trangHienTai.value = 0;
});
</script>
