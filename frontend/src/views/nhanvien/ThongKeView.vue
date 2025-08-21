<template>
  <div>
    <section>
      <!--doanh thu-->
      <h2 class="text-xl font-bold text-gray-800 mb-4">Doanh thu</h2>
      <div class="grid grid-cols-1 xl:grid-cols-3 gap-4">
        <StatCard
          tieuDe="Doanh thu hôm nay"
          :giaTri="formatCurrency(thongKeTongQuan?.doanhThu.homNay ?? 0)"
          icon="lucide:coins"
          :is-loading="isLoading.tongQuan"
        />
        <StatCard
          tieuDe="Doanh thu tháng này"
          :giaTri="formatCurrency(thongKeTongQuan?.doanhThu.thangNay ?? 0)"
          icon="lucide:wallet"
          :is-loading="isLoading.tongQuan"
        />
        <StatCard
          tieuDe="Tăng trưởng doanh thu"
          :giaTri="`${
            thongKeTongQuan?.doanhThu.tangTruongSoVoiThangTruoc ?? 0
          }%`"
          icon="lucide:arrow-up"
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
          tieuDe="Đơn hàng hôm nay"
          :giaTri="thongKeTongQuan?.donHang.homNay?.toString() ?? '0'"
          icon="lucide:file-text"
          :is-loading="isLoading.tongQuan"
        />
        <StatCard
          tieuDe="Đơn hàng tháng này"
          :giaTri="thongKeTongQuan?.donHang.thangNay?.toString() ?? '0'"
          icon="lucide:calendar"
          :is-loading="isLoading.tongQuan"
        />
        <StatCard
          tieuDe="Tăng trưởng đơn hàng"
          :giaTri="`${
            thongKeTongQuan?.donHang.tangTruongSoVoiThangTruoc ?? 0
          }%`"
          icon="lucide:trending-up"
          :is-loading="isLoading.tongQuan"
        />
        <StatCard
          tieuDe="Số đơn đang giao"
          :giaTri="thongKeTongQuan?.donHang.soDonDangGiao?.toString() ?? '0'"
          icon="lucide:truck"
          :is-loading="isLoading.tongQuan"
        />
      </div>

      <!--danh sách 20 đơn hàng gần đây, đang bổ sung API để fetch-->
      <DataTable
        :headers="[
          'Mã đơn hàng',
          'Khách hàng',
          'Thời gian đặt',
          'Trạng thái',
          'Tổng tiền',
          '',
        ]"
        :rows="donHangGanDayRows"
        :is-loading="isLoading.donHangGanDay"
      />

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
        <div class="p-4 bg-white rounded-2xl shadow flex flex-col">
          <div>
            <label class="text-sm font-semibold">Sản phẩm bán chạy</label>
          </div>
          <div class="relative mt-2 h-60">
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
        <div class="p-4 bg-white rounded-2xl shadow">
          <label class="text-sm font-semibold">Sản phẩm còn tồn kho cao</label>
          <DataTable
            :headers="['Sản phẩm', 'Tồn kho', 'Đã bán']"
            :rows="sanPhamTonKhoRows"
            :is-loading="isLoading.sanPham"
          />
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

// --- State for interactivity ---
const doanhThuNgayPeriod = ref(7);
const donHangNgayPeriod = ref(7);

// --- Logic cho menu dropdown "Mục tiêu doanh thu" ---
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

const mucTieuHienTai = computed(() => mucTieuDoanhThu.value?.mucTieu ?? 0);

const sanPhamBanChayChartData = computed(() => {
  if (
    !sanPhamBanChayForChart.value ||
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
  return sanPhamTonKhoCao.value.map((item) => [
    item.tenSanPham,
    item.soLuongTon.toString(),
    item.soLuongDaBan.toString(),
  ]);
});

const donHangGanDayRows = computed(() => {
  /*return donHangGanDay.value.map((donHang) => [
    donHang.maDonHang,
    donHang.tenKhachHang,
    new Date(donHang.thoiGianDat).toLocaleString("vi-VN"),
    donHang.trangThai,
    formatCurrency(donHang.tongTien),
    "", // For action button column
  ]); */
});

const tiLeDonHangLabels = computed(
  () => tiLeDonHang.value?.map((item) => item.name) ?? []
);

const tiLeDonHangData = computed(
  () => tiLeDonHang.value?.map((item) => item.value) ?? []
);

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
    } finally {
      isLoading.mucTieu = false;
      closeMucTieuModal();
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
const doanhThuThangModalRows = ref<string[][]>([]);

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
const donHangThangModalRows = ref<string[][]>([]);

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
const sanPhamBanChayModalRows = ref<string[][]>([]);

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
    thongKeTongQuan.value = await thongKeService.getThongKeTongQuan();
  } finally {
    isLoading.tongQuan = false;
  }
}

async function fetchDoanhThuTheoNgay(period: number = 7) {
  isLoading.doanhThuNgay = true;
  try {
    doanhThuTheoNgay.value = await thongKeService.getDoanhThuTheoNgay(period);
  } finally {
    isLoading.doanhThuNgay = false;
  }
}

async function fetchDoanhThuTheoThang() {
  isLoading.doanhThuThang = true;
  try {
    doanhThuTheoThang.value = await thongKeService.getDoanhThuTheoThang();
  } finally {
    isLoading.doanhThuThang = false;
  }
}

async function fetchMucTieu() {
  isLoading.mucTieu = true;
  try {
    mucTieuDoanhThu.value = await thongKeService.getMucTieuDoanhThu();
  } finally {
    isLoading.mucTieu = false;
  }
}

async function fetchDonHangGanDay() {
  isLoading.donHangGanDay = true;
  try {
    // Giả sử service có phương thức getDonHangGanDay
    //donHangGanDay.value = await thongKeService.getDonHangGanDay(20);
  } finally {
    isLoading.donHangGanDay = false;
  }
}

async function fetchDonHangTheoNgay(period: number = 7) {
  isLoading.donHangNgay = true;
  try {
    donHangTheoNgay.value = await thongKeService.getDonHangTheoNgay(period);
  } finally {
    isLoading.donHangNgay = false;
  }
}

async function fetchDonHangTheoThang() {
  isLoading.donHangThang = true;
  try {
    donHangTheoThang.value = await thongKeService.getDonHangTheoThang();
  } finally {
    isLoading.donHangThang = false;
  }
}

async function fetchTiLeDonHang() {
  isLoading.tiLeDonHang = true;
  try {
    tiLeDonHang.value = await thongKeService.getTiLeDonHang();
  } finally {
    isLoading.tiLeDonHang = false;
  }
}

async function fetchChiTietDoanhThuThang() {
  isLoading.chiTietDoanhThuThang = true;
  try {
    chiTietDoanhThuThang.value = await thongKeService.getChiTietDoanhThuThang();
    doanhThuThangModalRows.value = chiTietDoanhThuThang.value.map((item) => [
      item.thang,
      formatCurrency(item.mucTieu),
      formatCurrency(item.doanhThu),
      `${item.tiLeDatDoanhThu}%`,
      item.tangTruong > 0 ? `+${item.tangTruong}%` : `${item.tangTruong}%`,
      formatCurrency(item.trungBinhMoiDon),
    ]);
  } finally {
    isLoading.chiTietDoanhThuThang = false;
  }
}

async function fetchChiTietDonHangThang() {
  isLoading.chiTietDonHangThang = true;
  try {
    chiTietDonHangThang.value = await thongKeService.getChiTietDonHangThang();
    donHangThangModalRows.value = chiTietDonHangThang.value.map((item) => [
      item.thang,
      item.soDonHang.toString(),
      item.tangTruong > 0 ? `+${item.tangTruong}%` : `${item.tangTruong}%`,
      formatCurrency(item.trungBinhMoiDon),
      `${item.donGiaoThanhCong.soLuong} (${item.donGiaoThanhCong.phanTram}%)`,
      `${item.donBiHuy.soLuong} (${item.donBiHuy.phanTram}%)`,
    ]);
  } finally {
    isLoading.chiTietDonHangThang = false;
  }
}

async function fetchChiTietSanPhamBanChay() {
  isLoading.chiTietSanPhamBanChay = true;
  try {
    // Lấy 20 sản phẩm bán chạy nhất cho modal chi tiết
    const banChayResponse = await thongKeService.getChiTietSanPhamBanChay(20);
    const chiTietList = banChayResponse.content;
    sanPhamBanChay.value = chiTietList;
    sanPhamBanChayModalRows.value = chiTietList.map((item, idx) => [
      (idx + 1).toString(),
      item.maSanPham.toString(),
      item.tenSanPham,
      item.soLuongBanRa.toString(),
      item.soLuongTrungBinhMoiDon.toString(),
      item.soDonDat.toString(),
    ]);
  } finally {
    isLoading.chiTietSanPhamBanChay = false;
  }
}

async function fetchSanPhamData() {
  isLoading.sanPham = true;
  try {
    // Lấy top 10 (dữ liệu đơn giản) cho biểu đồ và danh sách tồn kho cao
    const [banChay, tonKho] = await Promise.all([
      thongKeService.getSanPhamBanChay(10),
      thongKeService.getSanPhamTonKhoCao(),
    ]);

    sanPhamBanChayForChart.value = banChay;
    sanPhamTonKhoCao.value = tonKho;
  } finally {
    isLoading.sanPham = false;
  }
}

onMounted(() => {
  Promise.all([
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
