<template>
  <div>
    <section>
      <h2 class="text-xl font-bold text-gray-800 mb-4">Doanh thu</h2>
      <!-- Sử dụng dữ liệu động -->
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
          <select class="ml-2 text-sm border rounded px-2 py-1">
            <option>7 ngày</option>
            <option>14 ngày</option>
            <option>30 ngày</option>
          </select>
          <div class="relative mt-2 h-60">
            <!-- Sử dụng dữ liệu động -->
            <BieuDoDuong
              v-if="doanhThuTheoNgay"
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
            <!-- Sử dụng dữ liệu động -->
            <BieuDoDuong
              v-if="doanhThuTheoThang"
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
              :tienDo="tienDoPhanTram"
              :mucTieu="mucTieuDoanhThu?.mucTieu ?? 0"
              :tienDoHienTai="mucTieuDoanhThu?.thucTe ?? 0"
            />
          </div>
        </div>
      </div>
    </section>

    <section class="mt-8">
      <h2 class="text-xl font-bold text-gray-800 mb-4">Đơn hàng</h2>
      <div class="grid grid-cols-2 xl:grid-cols-4 gap-4 mb-4">
        <StatCard
          tieuDe="Đơn hàng hôm nay"
          giaTri="200"
          icon="lucide:file-text"
        />
        <StatCard
          tieuDe="Đơn hàng tháng này"
          giaTri="40"
          icon="lucide:calendar"
        />
        <StatCard
          tieuDe="Tăng trưởng đơn hàng"
          giaTri="+50%"
          icon="lucide:trending-up"
        />
        <StatCard tieuDe="Số đơn đang giao" giaTri="20" icon="lucide:truck" />
      </div>

      <DataTable
        :headers="[
          'Mã đơn hàng',
          'Khách hàng',
          'Thời gian đặt',
          'Trạng thái',
          'Tổng tiền',
          '',
        ]"
        :rows="[
          [
            '#1000',
            'Nguyễn Văn A',
            '10:10:00 25/06/2025',
            'Đang giao',
            '100.000 VND',
            'Xem',
          ],
          [
            '#999',
            'Nguyễn Thị B',
            '10:05:00 25/06/2025',
            'Đang giao',
            '20.000 VND',
            'Xem',
          ],
        ]"
      />

      <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4 mt-4">
        <div class="p-4 bg-white rounded-2xl shadow">
          <label class="text-sm font-semibold"
            >Biến động đơn hàng theo ngày</label
          >
          <select class="ml-2 text-sm border rounded px-2 py-1">
            <option>7 ngày</option>
            <option>14 ngày</option>
            <option>30 ngày</option>
          </select>
          <div class="relative mt-2 h-60">
            <BieuDoDuong
              :labels="['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']"
              :data="[20, 30, 25, 35, 45, 40, 30]"
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
              :labels="['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun']"
              :data="[80, 90, 100, 120, 140, 160]"
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
              :labels="[
                'Chờ xác nhận',
                'Chờ xử lí',
                'Đang giao',
                'Đã giao',
                'Đã hủy',
              ]"
              :data="[15, 5, 30, 40, 10]"
            />
          </div>
        </div>
      </div>
    </section>

    <section class="mt-8">
      <h2 class="text-xl font-bold text-gray-800 mb-4">Sản phẩm</h2>
      <div class="grid grid-cols-1 xl:grid-cols-2 gap-4">
        <div class="p-4 bg-white rounded-2xl shadow flex flex-col">
          <div>
            <label class="text-sm font-semibold">Sản phẩm bán chạy</label>
          </div>
          <div class="relative mt-2 h-60">
            <BieuDoCot
              :labels="[
                'Sản phẩm A',
                'Sản phẩm B',
                'Sản phẩm C',
                'Sản phẩm D',
                'Sản phẩm E',
                'Sản phẩm F',
                'Sản phẩm G',
                'Sản phẩm H',
                'Sản phẩm I',
                'Sản phẩm J',
              ]"
              :data="[120, 100, 140, 130, 110, 150, 160, 170, 180, 190]"
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

        <div class="p-4 bg-white rounded-2xl shadow">
          <label class="text-sm font-semibold">Sản phẩm còn tồn kho cao</label>
          <DataTable
            :headers="['Sản phẩm', 'Tồn kho', 'Đã bán']"
            :rows="[
              ['Quần jeans', '90', '10'],
              ['Parfait bơ lạc', '50', '5'],
            ]"
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
// Giả sử bạn đã tạo service và types
// import {
//   getThongKeTongQuan,
//   getDoanhThuTheoNgay,
//   getDoanhThuTheoThang,
//   getMucTieuDoanhThu,
//   updateMucTieuDoanhThu,
// } from "@/service/thongke.service";
// import type {
//   ThongKeTongQuan,
//   BieuDoData,
//   MucTieuDoanhThu,
// } from "@/types/thongke.types";

// --- State for loading indicators ---
const isLoading = reactive({
  tongQuan: false,
  doanhThuNgay: false,
  doanhThuThang: false,
  mucTieu: false,
});

// --- State for data ---
const thongKeTongQuan = ref<any>(null); // Thay 'any' bằng ThongKeTongQuan
const doanhThuTheoNgay = ref<any>(null); // Thay 'any' bằng BieuDoData
const doanhThuTheoThang = ref<any>(null); // Thay 'any' bằng BieuDoData
const mucTieuDoanhThu = ref<any>(null); // Thay 'any' bằng MucTieuDoanhThu

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

/**
 * Xử lý sự kiện khi người dùng click vào mục "Cập nhật mục tiêu".
 * Bạn có thể thêm logic mở modal hoặc các hành động khác tại đây.
 */
const handleUpdateTarget = () => {
  mucTieuMoi.value = mucTieuDoanhThu.value?.mucTieu ?? 0;
  isMucTieuModalVisible.value = true;
  isMenuOpen.value = false; // Đóng menu sau khi click
};

const closeMucTieuModal = () => {
  isMucTieuModalVisible.value = false;
};

const saveMucTieuMoi = () => {
  // await updateMucTieuDoanhThu({ mucTieuMoi: mucTieuMoi.value });
  if (mucTieuMoi.value !== null && mucTieuMoi.value > 0) {
    mucTieuDoanhThu.value.mucTieu = mucTieuMoi.value;
  }
  // fetchMucTieu(); // Tải lại dữ liệu mục tiêu sau khi lưu
  closeMucTieuModal();
};

/**
 * Đóng menu khi người dùng click ra bên ngoài.
 */
const handleClickOutside = (event: MouseEvent) => {
  if (menuRef.value && !menuRef.value.contains(event.target as Node)) {
    isMenuOpen.value = false;
  }
};

// --- Logic cho modal chi tiết doanh thu tháng ---
const isDoanhThuThangModalVisible = ref(false);

const doanhThuThangModalHeaders = ref([
  "Tháng",
  "Mục tiêu",
  "Doanh thu",
  "Đạt mục tiêu",
  "Tăng trưởng",
  "Trung bình mỗi đơn",
]);

const doanhThuThangModalRows = ref([
  ["1/2025", "20,000,000 VND", "18,500,000 VND", "92.5%", "-", "250,000 VND"],
  [
    "2/2025",
    "20,000,000 VND",
    "21,000,000 VND",
    "105%",
    "+13.5%",
    "260,000 VND",
  ],
  [
    "3/2025",
    "22,000,000 VND",
    "23,000,000 VND",
    "104.5%",
    "+9.5%",
    "275,000 VND",
  ],
  [
    "4/2025",
    "22,000,000 VND",
    "21,500,000 VND",
    "97.7%",
    "-6.5%",
    "270,000 VND",
  ],
  [
    "5/2025",
    "25,000,000 VND",
    "26,000,000 VND",
    "104%",
    "+20.9%",
    "280,000 VND",
  ],
  [
    "6/2025",
    "25,000,000 VND",
    "28,000,000 VND",
    "112%",
    "+7.7%",
    "290,000 VND",
  ],
]);

const openDoanhThuThangModal = () => {
  isDoanhThuThangModalVisible.value = true;
};
const closeDoanhThuThangModal = () => {
  isDoanhThuThangModalVisible.value = false;
};

// --- Logic cho modal chi tiết đơn hàng tháng ---
const isDonHangThangModalVisible = ref(false);

const donHangThangModalHeaders = ref([
  "Tháng",
  "Số đơn",
  "Tăng trưởng",
  "Trung bình mỗi đơn",
  "Đơn giao thành công",
  "Đơn bị hủy",
]);

const donHangThangModalRows = ref([
  ["1/2025", "80", "-", "250,000 VND", "75 (93.8%)", "5 (6.2%)"],
  ["2/2025", "90", "+12.5%", "260,000 VND", "83 (92.2%)", "7 (7.8%)"],
  ["3/2025", "100", "+11.1%", "275,000 VND", "92 (92.0%)", "8 (8.0%)"],
  ["4/2025", "120", "+20.0%", "270,000 VND", "110 (91.7%)", "10 (8.3%)"],
  ["5/2025", "140", "+16.7%", "280,000 VND", "129 (92.1%)", "11 (7.9%)"],
  ["6/2025", "160", "+14.3%", "290,000 VND", "148 (92.5%)", "12 (7.5%)"],
]);

const openDonHangThangModal = () => {
  isDonHangThangModalVisible.value = true;
};
const closeDonHangThangModal = () => {
  isDonHangThangModalVisible.value = false;
};

// --- Logic cho modal sản phẩm bán chạy ---
const isSanPhamBanChayModalVisible = ref(false);

const sanPhamBanChayModalHeaders = ref([
  "STT",
  "Mã sản phẩm",
  "Tên sản phẩm",
  "Số lượng bán ra",
  "Số lượng/đơn",
  "Số đơn đặt",
]);

const sanPhamBanChayModalRows = ref([
  ["1", "#SP001", "Sản phẩm A", "190", "1.5", "127"],
  ["2", "#SP002", "Sản phẩm B", "180", "1.2", "150"],
  ["3", "#SP003", "Sản phẩm C", "170", "1.8", "94"],
  ["4", "#SP004", "Sản phẩm D", "160", "1.1", "145"],
  ["5", "#SP005", "Sản phẩm E", "150", "2.0", "75"],
  ["6", "#SP006", "Sản phẩm F", "140", "1.4", "100"],
  ["7", "#SP007", "Sản phẩm G", "130", "1.3", "100"],
  ["8", "#SP008", "Sản phẩm H", "120", "1.0", "120"],
  ["9", "#SP009", "Sản phẩm I", "110", "1.1", "100"],
  ["10", "#SP010", "Sản phẩm J", "100", "1.0", "100"],
]);

const openSanPhamBanChayModal = () => {
  isSanPhamBanChayModalVisible.value = true;
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

onMounted(() => {
  // fetchAllData();
  document.addEventListener("mousedown", handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener("mousedown", handleClickOutside);
});

// --- Data Fetching Functions ---
// async function fetchAllData() {
//   fetchThongKeTongQuan();
//   fetchDoanhThuTheoNgay(7);
//   fetchDoanhThuTheoThang(new Date().getFullYear());
//   fetchMucTieu();
//   // ... fetch other data
// }

// async function fetchThongKeTongQuan() {
//   isLoading.tongQuan = true;
//   try {
//     thongKeTongQuan.value = await getThongKeTongQuan();
//   } finally {
//     isLoading.tongQuan = false;
//   }
// }

// async function fetchDoanhThuTheoNgay(period: number) {
//   isLoading.doanhThuNgay = true;
//   try {
//     doanhThuTheoNgay.value = await getDoanhThuTheoNgay({ period });
//   } finally {
//     isLoading.doanhThuNgay = false;
//   }
// }

// async function fetchMucTieu() {
//   isLoading.mucTieu = true;
//   try {
//     mucTieuDoanhThu.value = await getMucTieuDoanhThu();
//   } finally {
//     isLoading.mucTieu = false;
//   }
// }
</script>
