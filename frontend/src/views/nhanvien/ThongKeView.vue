<template>
  <div>
    <section>
      <!--doanh thu-->
      <h2 class="text-xl font-bold text-gray-800 mb-4">Doanh thu</h2>
      <div class="grid grid-cols-1 xl:grid-cols-3 gap-4">
        <StatCard
          :tieuDe="`Doanh thu hôm nay (${formattedToday})`"
          :giaTri="formatCurrency(thongKeTongQuan?.doanhThu?.homNay ?? 0)"
          icon="lucide:coins"
          :is-loading="isLoading.tongQuan"
        />
        <StatCard
          :tieuDe="`Doanh thu tháng này (${formattedThisMonth})`"
          :giaTri="formatCurrency(thongKeTongQuan?.doanhThu?.thangNay ?? 0)"
          icon="lucide:wallet"
          :is-loading="isLoading.tongQuan"
        />
        <StatCard
          tieuDe="Tăng trưởng doanh thu"
          :giaTri="`${tangTruongDoanhThu}%`"
          :icon="
            tangTruongDoanhThu > 0 ? 'lucide:arrow-up' : 'lucide:arrow-down'
          "
          :giaTriClass="
            tangTruongDoanhThu > 0 ? 'text-green-600' : 'text-red-600'
          "
          :is-loading="isLoading.tongQuan"
        />
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4 mt-4">
        <div class="p-4 bg-white rounded-2xl shadow">
          <label class="text-sm font-semibold"
            >Biến động doanh thu theo ngày</label
          >
          <select
            v-model="doanhThuNgayPeriod"
            @change="fetchDoanhThuTheoNgay(doanhThuNgayPeriod)"
            class="ml-2 text-sm border rounded px-2 py-1"
          >
            <option :value="7">7 ngày</option>
            <option :value="14">14 ngày</option>
            <option :value="30">30 ngày</option>
          </select>
          <div class="relative mt-2 h-60">
            <BieuDoDuong
              v-if="!isLoading.doanhThuNgay && doanhThuTheoNgay"
              :labels="doanhThuTheoNgay.labels"
              :data="doanhThuTheoNgay.data"
            />
          </div>
        </div>

        <div class="p-4 bg-white rounded-2xl shadow flex flex-col">
          <div>
            <label class="text-sm font-semibold"
              >Biến động doanh thu theo tháng</label
            >
          </div>
          <div class="relative mt-2 h-60">
            <BieuDoDuong
              v-if="!isLoading.doanhThuThang && doanhThuTheoThang"
              :labels="doanhThuTheoThang.labels"
              :data="doanhThuTheoThang.data"
            />
          </div>
          <div class="mt-auto pt-2 text-right">
            <button
              @click="openDoanhThuThangModal"
              class="text-xs text-blue-600 hover:underline"
            >
              Xem chi tiết
            </button>
          </div>
        </div>

        <div class="p-4 bg-white rounded-2xl shadow relative">
          <div class="flex justify-between items-center">
            <span class="text-sm font-semibold">Mục tiêu doanh thu</span>
          </div>
          <!-- Menu ba chấm (dropdown) đã được chuyển về đây -->
          <div ref="menuRef" class="absolute top-2 right-2 z-10">
            <button
              @click="isMenuOpen = !isMenuOpen"
              class="p-2 text-gray-500 rounded-full hover:bg-gray-100 hover:text-gray-700 focus:outline-none"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="w-5 h-5"
                viewBox="0 0 20 20"
                fill="currentColor"
              >
                <path
                  d="M6 10a2 2 0 11-4 0 2 2 0 014 0zm6 0a2 2 0 11-4 0 2 2 0 014 0zm6 0a2 2 0 11-4 0 2 2 0 014 0z"
                />
              </svg>
            </button>
            <!-- Nội dung Dropdown -->
            <div
              v-show="isMenuOpen"
              class="absolute right-0 mt-2 w-40 bg-white rounded-md shadow-lg ring-1 ring-black ring-opacity-5"
            >
              <button
                @click="handleUpdateTarget"
                class="w-full text-left px-4 py-2 text-sm text-gray-700 hover:bg-gray-100"
              >
                Cập nhật mục tiêu
              </button>
            </div>
          </div>
          <div class="relative mt-2 h-60">
            <BieuDoGauge
              v-if="!isLoading.mucTieu && mucTieuDoanhThu"
              :tienDo="tienDoPhanTram"
              :mucTieu="mucTieuDoanhThu?.mucTieu ?? 0"
              :tienDoHienTai="mucTieuDoanhThu?.thucTe ?? 0"
            />
          </div>
        </div>
      </div>
    </section>

    <!--đơn hàng-->
    <section class="mt-8">
      <h2 class="text-xl font-bold text-gray-800 mb-4">Đơn hàng</h2>
      <div class="grid grid-cols-2 xl:grid-cols-4 gap-4 mb-4">
        <StatCard
          :tieuDe="`Đơn hàng hôm nay (${formattedToday})`"
          :giaTri="thongKeTongQuan?.donHang?.homNay?.toString() ?? '0'"
          icon="lucide:file-text"
          :is-loading="isLoading.tongQuan"
        />
        <StatCard
          :tieuDe="`Đơn hàng tháng này (${formattedThisMonth})`"
          :giaTri="thongKeTongQuan?.donHang?.thangNay?.toString() ?? '0'"
          icon="lucide:calendar"
          :is-loading="isLoading.tongQuan"
        />
        <StatCard
          tieuDe="Tăng trưởng đơn hàng"
          :giaTri="`${tangTruongDonHang}%`"
          :icon="
            tangTruongDonHang > 0
              ? 'lucide:trending-up'
              : 'lucide:trending-down'
          "
          :giaTriClass="
            tangTruongDonHang > 0 ? 'text-green-600' : 'text-red-600'
          "
          :is-loading="isLoading.tongQuan"
        />
        <StatCard
          tieuDe="Số đơn đang giao"
          :giaTri="thongKeTongQuan?.donHang?.soDonDangGiao?.toString() ?? '0'"
          icon="lucide:truck"
          :is-loading="isLoading.tongQuan"
        />
      </div>

      <!--danh sách 20 đơn hàng gần đây, đang bổ sung API để fetch-->
      <div class="h-96 overflow-y-auto bg-white rounded-2xl shadow">
        <DataTable
          :headers="[
            'Mã đơn hàng',
            'Khách hàng',
            'Thời gian đặt',
            'Trạng thái',
            'Tổng tiền',
            'Hành động',
          ]"
          :rows="donHangGanDayRows"
          :is-loading="isLoading.donHangGanDay"
        >
          <!-- Sử dụng slot để tùy chỉnh cột "Trạng thái" (index 3) -->
          <template #cell-3="{ value }">
            <span
              :class="[
                'px-2 inline-flex text-xs leading-5 font-semibold rounded-full',
                getTrangThaiClass(value.value),
              ]"
            >
              {{ value.label }}
            </span>
          </template>
          <!-- Sử dụng slot để tùy chỉnh cột "Hành động" (index 5) -->
          <template #cell-5="{ row }">
            <button
              @click="openChiTietDonHangModal(parseInt(row[0]))"
              class="text-sm font-medium text-blue-600 hover:underline"
            >
              Xem
            </button>
          </template>
        </DataTable>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4 mt-4">
        <div class="p-4 bg-white rounded-2xl shadow">
          <label class="text-sm font-semibold"
            >Biến động đơn hàng theo ngày</label
          >
          <select
            v-model="donHangNgayPeriod"
            @change="fetchDonHangTheoNgay(donHangNgayPeriod)"
            class="ml-2 text-sm border rounded px-2 py-1"
          >
            <option :value="7">7 ngày</option>
            <option :value="14">14 ngày</option>
            <option :value="30">30 ngày</option>
          </select>
          <div class="relative mt-2 h-60">
            <BieuDoDuong
              v-if="!isLoading.donHangNgay && donHangTheoNgay"
              :labels="donHangTheoNgay.labels"
              :data="donHangTheoNgay.data"
            />
          </div>
        </div>

        <div class="p-4 bg-white rounded-2xl shadow flex flex-col">
          <div>
            <label class="text-sm font-semibold"
              >Biến động đơn hàng theo tháng</label
            >
          </div>
          <div class="relative mt-2 h-60">
            <BieuDoDuong
              v-if="!isLoading.donHangThang && donHangTheoThang"
              :labels="donHangTheoThang.labels"
              :data="donHangTheoThang.data"
            />
          </div>
          <div class="mt-auto pt-2 text-right">
            <button
              @click="openDonHangThangModal"
              class="text-xs text-blue-600 hover:underline"
            >
              Xem chi tiết
            </button>
          </div>
        </div>

        <div class="p-4 bg-white rounded-2xl shadow">
          <label class="text-sm font-semibold">Tỉ lệ đơn hàng</label>
          <div class="relative mt-2 h-60">
            <BieuDoTron
              v-if="!isLoading.tiLeDonHang && tiLeDonHang"
              :labels="tiLeDonHangLabels"
              :data="tiLeDonHangData"
            />
          </div>
        </div>
      </div>
    </section>

    <!--sản phẩm-->
    <section class="mt-8">
      <h2 class="text-xl font-bold text-gray-800 mb-4">Sản phẩm</h2>

      <!-- Biểu đồ cột top 10 sản phẩm bán chạy -->
      <div class="grid grid-cols-1 xl:grid-cols-2 gap-4">
        <div class="p-4 bg-white rounded-2xl shadow flex flex-col h-100">
          <div>
            <label class="text-sm font-semibold"
              >Top 10 sản phẩm bán chạy 30 ngày qua</label
            >
          </div>
          <div class="relative mt-2 h-full">
            <BieuDoCot
              v-if="!isLoading.sanPham && sanPhamBanChayChartData"
              :labels="sanPhamBanChayChartData.labels"
              :data="sanPhamBanChayChartData.data"
            />
          </div>
          <div class="mt-auto pt-2 text-right">
            <button
              @click="openSanPhamBanChayModal"
              class="text-xs text-blue-600 hover:underline"
            >
              Xem thêm
            </button>
          </div>
        </div>

        <!--danh sách sản phẩm tồn kho cao-->
        <div class="p-4 bg-white rounded-2xl shadow flex flex-col h-100">
          <div>
            <label class="text-sm font-semibold"
              >Sản phẩm còn tồn kho cao</label
            >
          </div>
          <div class="mt-2 flex-1 overflow-y-auto">
            <DataTable
              :headers="['STT', 'Sản phẩm', 'Tồn kho', 'Đã bán']"
              :rows="sanPhamTonKhoRows"
              :is-loading="isLoading.sanPham"
            />
          </div>
        </div>
      </div>
    </section>

    <!-- Modal Chi tiết Doanh thu theo tháng -->
    <BaseModal
      :visible="isDoanhThuThangModalVisible"
      title="Chi tiết doanh thu theo tháng"
      @close="closeDoanhThuThangModal"
      width-class="w-[800px]"
    >
      <DataTable
        :headers="doanhThuThangModalHeaders"
        :rows="doanhThuThangModalRows"
        :is-loading="isLoading.chiTietDoanhThuThang"
      />
    </BaseModal>

    <!-- Modal Chi tiết Đơn hàng theo tháng -->
    <BaseModal
      :visible="isDonHangThangModalVisible"
      title="Chi tiết đơn hàng theo tháng"
      @close="closeDonHangThangModal"
      width-class="w-[800px]"
    >
      <DataTable
        :headers="donHangThangModalHeaders"
        :rows="donHangThangModalRows"
        :is-loading="isLoading.chiTietDonHangThang"
      />
    </BaseModal>

    <!-- Modal Chi tiết Sản phẩm bán chạy -->
    <BaseModal
      :visible="isSanPhamBanChayModalVisible"
      title="Chi tiết sản phẩm bán chạy"
      @close="closeSanPhamBanChayModal"
      width-class="w-[800px]"
    >
      <DataTable
        :headers="sanPhamBanChayModalHeaders"
        :rows="sanPhamBanChayModalRows"
        :is-loading="isLoading.chiTietSanPhamBanChay"
      />
    </BaseModal>

    <!-- Modal Cập nhật mục tiêu -->
    <BaseModal
      :visible="isMucTieuModalVisible"
      title="Cập nhật mục tiêu doanh thu"
      @close="closeMucTieuModal"
    >
      <div class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700"
            >Mục tiêu hiện tại</label
          >
          <p class="mt-1 text-lg font-semibold text-gray-900">
            {{ formatCurrency(mucTieuHienTai) }}
          </p>
        </div>
        <div>
          <label
            for="muc-tieu-moi"
            class="block text-sm font-medium text-gray-700"
            >Mục tiêu mới</label
          >
          <input
            type="number"
            id="muc-tieu-moi"
            v-model.number="mucTieuMoi"
            class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-blue-500 focus:border-blue-500"
            placeholder="Nhập mục tiêu mới"
          />
        </div>
      </div>
      <template #footer>
        <div class="flex justify-end">
          <button
            @click="saveMucTieuMoi"
            class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          >
            Lưu thay đổi
          </button>
        </div>
      </template>
    </BaseModal>

    <!-- Modal Chi tiết Đơn hàng (Read-only) -->
    <ChiTietDonHangModal
      v-if="selectedDonHangId"
      :visible="isChiTietDonHangModalVisible"
      :ma-don-hang="selectedDonHangId"
      @close="closeChiTietDonHangModal"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, reactive } from "vue";
import StatCard from "@/components/dashboard/StatCard.vue";
import BieuDoGauge from "@/components/dashboard/BieuDoGauge.vue";
import DataTable from "@/components/base/DataTable.vue";
import BieuDoCot from "@/components/dashboard/BieuDoCot.vue";
import BieuDoDuong from "@/components/dashboard/BieuDoDuong.vue";
import BieuDoTron from "@/components/dashboard/BieuDoTron.vue";
import BaseModal from "@/components/base/modals/BaseModal.vue";
import ChiTietDonHangModal from "@/components/dashboard/ChiTietDonHangModal.vue";

import { thongKeService } from "@/service/thongke.service";
import type {
  ThongKeTongQuanResponse,
  BieuDoDuongResponse,
  BieuDoTronResponse,
  MucTieuDoanhThuResponse,
  SanPhamBanChayResponse,
  SanPhamTonKhoResponse,
  ChiTietDoanhThuThangResponse,
  ChiTietDonHangThangResponse,
  ChiTietSanPhamBanChayResponse,
} from "@/types/thongke.types";
import { TrangThaiDonHangKey } from "@/types/donhang.types";
import type { DonHangGanDayResponse } from "@/types/thongke.types";

// --- Helpers ---
const formattedToday = new Intl.DateTimeFormat("vi-VN", {
  day: "2-digit",
  month: "2-digit",
  year: "numeric",
}).format(new Date());

const formattedThisMonth = new Intl.DateTimeFormat("vi-VN", {
  month: "numeric",
  year: "numeric",
}).format(new Date());

// --- State for loading indicators ---
const isLoading = reactive({
  tongQuan: false,
  doanhThuNgay: false,
  doanhThuThang: false,
  mucTieu: false,
  donHangGanDay: false,
  donHangNgay: false,
  donHangThang: false,
  tiLeDonHang: false,
  sanPham: false,
  chiTietDoanhThuThang: false,
  chiTietDonHangThang: false,
  chiTietSanPhamBanChay: false,
});

// --- State for data ---
const thongKeTongQuan = ref<ThongKeTongQuanResponse | null>(null);
const doanhThuTheoNgay = ref<BieuDoDuongResponse | null>(null);
const doanhThuTheoThang = ref<BieuDoDuongResponse | null>(null);
const mucTieuDoanhThu = ref<MucTieuDoanhThuResponse | null>(null);
//const donHangGanDay = ref<DonHangGanDayResponse[]>([]);
const donHangTheoNgay = ref<BieuDoDuongResponse | null>(null);
const donHangTheoThang = ref<BieuDoDuongResponse | null>(null);
const tiLeDonHang = ref<BieuDoTronResponse[] | null>(null);
const sanPhamBanChayForChart = ref<SanPhamBanChayResponse[]>([]); // Top 10 cho biểu đồ
const sanPhamBanChay = ref<ChiTietSanPhamBanChayResponse[]>([]); // Top 20 cho modal
const sanPhamTonKhoCao = ref<SanPhamTonKhoResponse[]>([]);
const chiTietDoanhThuThang = ref<ChiTietDoanhThuThangResponse[]>([]);
const chiTietDonHangThang = ref<ChiTietDonHangThangResponse[]>([]);
const donHangGanDay = ref<DonHangGanDayResponse[]>([]);

// --- State for interactivity ---
const doanhThuNgayPeriod = ref(7);
const donHangNgayPeriod = ref(7);

// --- Logic cho menu dropdown "Mục tiêu doanh thu" ---
const isChiTietDonHangModalVisible = ref(false);
const selectedDonHangId = ref<number | null>(null);

const openChiTietDonHangModal = (maDonHang: number) => {
  selectedDonHangId.value = maDonHang;
  isChiTietDonHangModalVisible.value = true;
};

const closeChiTietDonHangModal = () => {
  isChiTietDonHangModalVisible.value = false;
  selectedDonHangId.value = null; // Reset để modal có thể được tạo lại
};

const isMenuOpen = ref(false);
const menuRef = ref<HTMLElement | null>(null);
const isMucTieuModalVisible = ref(false);
const mucTieuMoi = ref<number | null>(null);

const tienDoPhanTram = computed(() => {
  if (!mucTieuDoanhThu.value || mucTieuDoanhThu.value.mucTieu === 0) {
    return 0;
  }
  return (mucTieuDoanhThu.value.thucTe / mucTieuDoanhThu.value.mucTieu) * 100;
});

const tangTruongDoanhThu = computed(
  () => thongKeTongQuan.value?.doanhThu?.tangTruongSoVoiThangTruoc ?? 0
);

const tangTruongDonHang = computed(
  () => thongKeTongQuan.value?.donHang?.tangTruongSoVoiThangTruoc ?? 0
);

const mucTieuHienTai = computed(() => mucTieuDoanhThu.value?.mucTieu ?? 0);

const sanPhamBanChayChartData = computed(() => {
  if (
    !Array.isArray(sanPhamBanChayForChart.value) ||
    sanPhamBanChayForChart.value.length === 0
  ) {
    return null;
  }
  return {
    labels: sanPhamBanChayForChart.value.map((p) => p.tenSanPham),
    data: sanPhamBanChayForChart.value.map((p) => p.soLuongDaBan),
  };
});

const sanPhamTonKhoRows = computed(() => {
  return Array.isArray(sanPhamTonKhoCao.value)
    ? sanPhamTonKhoCao.value.map((item, index) => [
        (index + 1).toString(),
        item.tenSanPham,
        item.soLuongTon.toString(),
        item.soLuongDaBan.toString(),
      ])
    : [];
});

const donHangGanDayRows = computed(() => {
  return Array.isArray(donHangGanDay.value)
    ? donHangGanDay.value.map((donHang) => [
        donHang.maDonHang.toString(),
        donHang.tenKhachHang,
        new Date(donHang.thoiGianDatHang).toLocaleString("vi-VN"),
        donHang.trangThai, // Truyền cả object trạng thái để slot có thể sử dụng
        formatCurrency(donHang.tongTien),
        "", // Placeholder cho cột hành động, sẽ được thay thế bởi slot
      ])
    : [];
});

const tiLeDonHangLabels = computed(() =>
  Array.isArray(tiLeDonHang.value)
    ? tiLeDonHang.value.map((item) => item.label)
    : []
);

const tiLeDonHangData = computed(() =>
  Array.isArray(tiLeDonHang.value)
    ? tiLeDonHang.value.map((item) => item.value)
    : []
);

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

const handleUpdateTarget = () => {
  mucTieuMoi.value = mucTieuDoanhThu.value?.mucTieu ?? 0;
  isMucTieuModalVisible.value = true;
  isMenuOpen.value = false;
};

const closeMucTieuModal = () => {
  isMucTieuModalVisible.value = false;
};

const saveMucTieuMoi = async () => {
  if (mucTieuMoi.value !== null && mucTieuMoi.value > 0) {
    isLoading.mucTieu = true;
    try {
      await thongKeService.capNhatMucTieuDoanhThu({
        mucTieuMoi: mucTieuMoi.value,
      });
      await fetchMucTieu();
      closeMucTieuModal();
    } catch (error) {
      // Bạn có thể thêm thông báo lỗi cho người dùng ở đây
    } finally {
      isLoading.mucTieu = false;
    }
  }
};

const handleClickOutside = (event: MouseEvent) => {
  if (menuRef.value && !menuRef.value.contains(event.target as Node)) {
    isMenuOpen.value = false;
  }
};

const isDoanhThuThangModalVisible = ref(false);
const doanhThuThangModalHeaders = ref([
  "Tháng",
  "Mục tiêu",
  "Doanh thu",
  "Đạt mục tiêu",
  "Tăng trưởng",
  "Trung bình mỗi đơn",
]);
const doanhThuThangModalRows = computed(() => {
  return Array.isArray(chiTietDoanhThuThang.value)
    ? chiTietDoanhThuThang.value.map((item) => [
        item.thang,
        formatCurrency(item.mucTieu ?? 0),
        formatCurrency(item.doanhThu ?? 0),
        `${(item.tiLeDatMucTieu ?? 0).toFixed(2)}%`,
        `${(item.tangTruong ?? 0).toFixed(2)}%`,
        formatCurrency(item.trungBinhMoiDon ?? 0),
      ])
    : [];
});
const openDoanhThuThangModal = async () => {
  isDoanhThuThangModalVisible.value = true;
  await fetchChiTietDoanhThuThang();
};
const closeDoanhThuThangModal = () => {
  isDoanhThuThangModalVisible.value = false;
};

const isDonHangThangModalVisible = ref(false);
const donHangThangModalHeaders = ref([
  "Tháng",
  "Số đơn",
  "Tăng trưởng",
  "Trung bình mỗi đơn",
  "Đơn giao thành công",
  "Đơn bị hủy",
]);
const donHangThangModalRows = computed(() => {
  return Array.isArray(chiTietDonHangThang.value)
    ? chiTietDonHangThang.value.map((item) => [
        item.thang,
        item.soDonHang.toString(),
        `${(item.tangTruong ?? 0).toFixed(2)}%`,
        formatCurrency(item.trungBinhMoiDon),
        `${item.donGiaoThanhCong.soLuong} (${(
          item.donGiaoThanhCong.phanTram ?? 0
        ).toFixed(2)}%)`,
        `${item.donBiHuy.soLuong} (${(item.donBiHuy.phanTram ?? 0).toFixed(
          2
        )}%)`,
      ])
    : [];
});
const openDonHangThangModal = async () => {
  isDonHangThangModalVisible.value = true;
  await fetchChiTietDonHangThang();
};
const closeDonHangThangModal = () => {
  isDonHangThangModalVisible.value = false;
};

const isSanPhamBanChayModalVisible = ref(false);
const sanPhamBanChayModalHeaders = ref([
  "STT",
  "Mã sản phẩm",
  "Tên sản phẩm",
  "Số lượng bán ra",
  "Số lượng/đơn",
  "Số đơn đặt",
]);
const sanPhamBanChayModalRows = computed(() => {
  return Array.isArray(sanPhamBanChay.value)
    ? sanPhamBanChay.value.map((item, index) => [
        (index + 1).toString(),
        item.maSanPham.toString(),
        item.tenSanPham,
        item.soLuongBanRa.toString(),
        (item.soLuongTrungBinhMoiDon ?? 0).toFixed(2),
        item.soDonDat.toString(),
      ])
    : [];
});
const openSanPhamBanChayModal = async () => {
  isSanPhamBanChayModalVisible.value = true;
  // Chỉ fetch dữ liệu chi tiết khi modal được mở lần đầu
  if (sanPhamBanChay.value.length === 0) {
    await fetchChiTietSanPhamBanChay();
  }
};
const closeSanPhamBanChayModal = () => {
  isSanPhamBanChayModalVisible.value = false;
};

const formatCurrency = (value: number): string => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(value);
};

// --- Data Fetching Functions ---
async function fetchThongKeTongQuan() {
  isLoading.tongQuan = true;
  try {
    const responseData = await thongKeService.getThongKeTongQuan();
    if (responseData && responseData.doanhThu && responseData.donHang) {
      thongKeTongQuan.value = responseData;
    } else {
      // Dữ liệu không hợp lệ, có thể hiển thị thông báo lỗi
    }
  } catch (error: any) {
    // Xử lý lỗi
  } finally {
    isLoading.tongQuan = false;
  }
}

async function fetchDoanhThuTheoNgay(period: number = 7) {
  isLoading.doanhThuNgay = true;
  try {
    const data = await thongKeService.getDoanhThuTheoNgay(period);
    doanhThuTheoNgay.value = data;
  } catch (error) {
    // Xử lý lỗi
  } finally {
    isLoading.doanhThuNgay = false;
  }
}

async function fetchDoanhThuTheoThang() {
  isLoading.doanhThuThang = true;
  try {
    const data = await thongKeService.getDoanhThuTheoThang();
    doanhThuTheoThang.value = data;
  } catch (error) {
    // Xử lý lỗi
  } finally {
    isLoading.doanhThuThang = false;
  }
}

async function fetchMucTieu() {
  isLoading.mucTieu = true;
  try {
    const data = await thongKeService.getMucTieuDoanhThu();
    mucTieuDoanhThu.value = data;
  } catch (error) {
    // Xử lý lỗi
  } finally {
    isLoading.mucTieu = false;
  }
}

async function fetchDonHangGanDay() {
  isLoading.donHangGanDay = true;
  try {
    // Gọi service với tham số `limit` là 20, theo đúng API backend
    donHangGanDay.value = await thongKeService.getDonHangGanDay(20);
  } catch (error) {
    // Xử lý lỗi
  } finally {
    isLoading.donHangGanDay = false;
  }
}

async function fetchSanPhamData() {
  isLoading.sanPham = true;
  try {
    const [banChay, tonKho] = await Promise.all([
      thongKeService.getSanPhamBanChay(10),
      thongKeService.getSanPhamTonKhoCao(),
    ]);
    sanPhamBanChayForChart.value = banChay;
    sanPhamTonKhoCao.value = tonKho;
  } catch (error) {
    // Xử lý lỗi
  } finally {
    isLoading.sanPham = false;
  }
}

async function fetchDonHangTheoNgay(period: number = 7) {
  isLoading.donHangNgay = true;
  try {
    const data = await thongKeService.getDonHangTheoNgay(period);
    donHangTheoNgay.value = data;
  } catch (error) {
    // Xử lý lỗi
  } finally {
    isLoading.donHangNgay = false;
  }
}

async function fetchDonHangTheoThang() {
  isLoading.donHangThang = true;
  try {
    const data = await thongKeService.getDonHangTheoThang();
    donHangTheoThang.value = data;
  } catch (error) {
    // Xử lý lỗi
  } finally {
    isLoading.donHangThang = false;
  }
}

async function fetchTiLeDonHang() {
  isLoading.tiLeDonHang = true;
  try {
    const data = await thongKeService.getTiLeDonHang();
    tiLeDonHang.value = data;
  } catch (error) {
    // Xử lý lỗi
  } finally {
    isLoading.tiLeDonHang = false;
  }
}

async function fetchChiTietDoanhThuThang() {
  isLoading.chiTietDoanhThuThang = true;
  try {
    const data = await thongKeService.getChiTietDoanhThuThang();
    chiTietDoanhThuThang.value = data;
  } catch (error) {
    // Xử lý lỗi
  } finally {
    isLoading.chiTietDoanhThuThang = false;
  }
}

async function fetchChiTietDonHangThang() {
  isLoading.chiTietDonHangThang = true;
  try {
    const data = await thongKeService.getChiTietDonHangThang();
    chiTietDonHangThang.value = data;
  } catch (error) {
    // Xử lý lỗi
  } finally {
    isLoading.chiTietDonHangThang = false;
  }
}

async function fetchChiTietSanPhamBanChay(page = 0, size = 20) {
  isLoading.chiTietSanPhamBanChay = true;
  try {
    const data = await thongKeService.getChiTietSanPhamBanChay(page, size);
    sanPhamBanChay.value = data.content;
  } catch (error) {
    // Xử lý lỗi
  } finally {
    isLoading.chiTietSanPhamBanChay = false;
  }
}

onMounted(() => {
  // Sử dụng Promise.allSettled để đảm bảo tất cả các yêu cầu API được thực thi
  // ngay cả khi một trong số chúng thất bại. Điều này giúp giao diện người dùng
  // hiển thị được những phần dữ liệu tải thành công.
  Promise.allSettled([
    fetchThongKeTongQuan(),
    fetchDoanhThuTheoNgay(doanhThuNgayPeriod.value),
    fetchDoanhThuTheoThang(),
    fetchMucTieu(),
    fetchDonHangGanDay(),
    fetchDonHangTheoNgay(donHangNgayPeriod.value),
    fetchDonHangTheoThang(),
    fetchTiLeDonHang(),
    fetchSanPhamData(),
  ]);
  document.addEventListener("mousedown", handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener("mousedown", handleClickOutside);
});
</script>
