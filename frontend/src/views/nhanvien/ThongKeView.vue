<template>
  <div>
    <section>
      <h2 class="text-xl font-bold text-gray-800 mb-4">Doanh thu</h2>
      <div class="grid grid-cols-1 xl:grid-cols-3 gap-4">
        <StatCard
          tieuDe="Doanh thu hôm nay"
          giaTri="10.000.000 VND"
          icon="lucide:coins"
        />
        <StatCard
          tieuDe="Doanh thu tháng này"
          giaTri="10.000.000 VND"
          icon="lucide:wallet"
        />
        <StatCard
          tieuDe="Tăng trưởng doanh thu"
          giaTri="+50%"
          icon="lucide:arrow-up"
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
            <BieuDoDuong
              :labels="['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']"
              :data="[200, 300, 250, 400, 500, 450, 300]"
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
              :labels="['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun']"
              :data="[1000, 1500, 1200, 1700, 2000, 2500]"
            />
          </div>
          <div class="mt-auto pt-2 text-right">
            <a href="#" class="text-xs text-blue-600">Xem chi tiết</a>
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
              :tienDo="50"
              :mucTieu="20000000"
              :tienDoHienTai="10000000"
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
            <a href="#" class="text-xs text-blue-600">Xem chi tiết</a>
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
            <a href="#" class="text-xs text-blue-600">Xem thêm</a>
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
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from "vue";
import StatCard from "@/components/dashboard/StatCard.vue";
import BieuDoGauge from "@/components/dashboard/BieuDoGauge.vue";
import DataTable from "@/components/base/DataTable.vue";
import BieuDoCot from "@/components/dashboard/BieuDoCot.vue";
import BieuDoDuong from "@/components/dashboard/BieuDoDuong.vue";
import BieuDoTron from "@/components/dashboard/BieuDoTron.vue";

// --- Logic cho menu dropdown "Mục tiêu doanh thu" ---
const isMenuOpen = ref(false);
const menuRef = ref<HTMLElement | null>(null);

/**
 * Xử lý sự kiện khi người dùng click vào mục "Cập nhật mục tiêu".
 * Bạn có thể thêm logic mở modal hoặc các hành động khác tại đây.
 */
const handleUpdateTarget = () => {
  console.log("Hành động 'Cập nhật mục tiêu' được gọi từ ThongKeView.vue");
  // Ví dụ: Mở một modal
  isMenuOpen.value = false; // Đóng menu sau khi click
};

/**
 * Đóng menu khi người dùng click ra bên ngoài.
 */
const handleClickOutside = (event: MouseEvent) => {
  if (menuRef.value && !menuRef.value.contains(event.target as Node)) {
    isMenuOpen.value = false;
  }
};

onMounted(() => {
  document.addEventListener("mousedown", handleClickOutside);
});
onUnmounted(() => {
  document.removeEventListener("mousedown", handleClickOutside);
});
</script>
