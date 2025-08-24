<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-4">Quản lí danh mục</h1>

    <!-- Chức năng -->
    <div
      class="flex flex-wrap items-end gap-4 mb-4 bg-white p-4 rounded-lg shadow"
    >
      <!-- Nút thêm danh mục -->
      <button
        @click="openAddModal"
        class="cursor-pointer bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded inline-flex items-center"
      >
        <i class="fi fi-rr-plus-small align-middle mr-1 -ml-1"></i>
        <span>Thêm danh mục</span>
      </button>

      <!-- Bộ lọc theo trạng thái -->
      <div>
        <label
          for="trang-thai-filter"
          class="block text-sm font-medium text-gray-700"
        >
          Trạng thái
        </label>
        <select
          id="trang-thai-filter"
          v-model="selectedTrangThai"
          class="cursor-pointer mt-1 block w-48 border border-gray-300 bg-white rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm px-3 py-2"
          :disabled="areOtherFiltersDisabled"
          :class="{ 'bg-gray-100 cursor-not-allowed': areOtherFiltersDisabled }"
        >
          <option value="">Tất cả</option>
          <option
            v-for="(label, key) in TrangThaiDanhMucLabel"
            :key="key"
            :value="key"
          >
            {{ label }}
          </option>
        </select>
      </div>

      <!-- Thanh tìm kiếm -->
      <ThanhTimKiem
        :ds-tieu-chi="dsTieuChiTimKiem"
        v-model:modelValueLoai="loaiTimKiem"
        v-model:modelValueTuKhoa="tuKhoa"
        @idSearchActive="areOtherFiltersDisabled = $event"
      />
    </div>

    <!-- Bảng dữ liệu danh mục -->
    <div class="bg-white shadow-md rounded-lg overflow-hidden">
      <DataTable :headers="headers" :rows="rows" :is-loading="isLoading">
        <template #cell-3="{ value: trangThai }">
          <span
            :class="[
              'px-2 inline-flex text-xs leading-5 font-semibold rounded-full',
              trangThai.value === TrangThaiDanhMucKey.HOAT_DONG
                ? 'bg-green-100 text-green-800'
                : 'bg-red-100 text-red-800',
            ]"
            >{{ trangThai.label }}</span
          >
        </template>
        <template #cell-4="{ value }">
          <div class="flex justify-center">
            <button
              @click="handleEditClick(value)"
              class="cursor-pointer p-2 rounded-full text-blue-600 hover:text-blue-800"
            >
              <i class="fi fi-rr-pencil text-base"></i>
            </button>
          </div>
        </template>
      </DataTable>
    </div>
    <!-- Phân trang -->
    <div class="mt-4 flex justify-center">
      <PhanTrang
        v-model:trangHienTai="trangHienTai"
        :tong-so-trang="tongSoTrang"
      />
    </div>

    <!-- Modal Thêm/Sửa danh mục -->
    <ThemSuaDanhMucModal
      :visible="isModalVisible"
      :title="modalTitle"
      :is-edit-mode="isEditMode"
      width-class="w-[500px]"
      @close="closeModal"
      :danh-muc-sua="selectedCategory"
      @success="handleSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import ThanhTimKiem from "@/components/base/ThanhTimKiem.vue";
import DataTable from "@/components/base/DataTable.vue";
import PhanTrang from "@/components/base/PhanTrang.vue";
import ThemSuaDanhMucModal from "@/components/quanlisanpham_danhmuc/ThemSuaDanhMucModal.vue";
import {
  getDanhSachDanhMuc,
  getChiTietDanhMuc,
} from "@/service/danhmuc.service";
import type {
  ChiTietDanhMucQuanLyResponse,
  DanhMucQuanLyResponse,
  GetDanhMucParams,
} from "@/types/danhmuc.types";
import {
  TrangThaiDanhMucKey,
  TrangThaiDanhMucLabel,
} from "@/types/danhmuc.types";
import { useToast } from "@/composables/useToast";
import { useDanhMucStore } from "@/store/danhmuc.store";

// --- State ---
const loaiTimKiem = ref<"tenDanhMuc" | "maDanhMuc">("tenDanhMuc");
const tuKhoa = ref("");
const selectedTrangThai = ref<TrangThaiDanhMucKey | "">("");
const isModalVisible = ref(false);
const isEditMode = ref(false);
const selectedCategory = ref<ChiTietDanhMucQuanLyResponse | null>(null);
const isLoading = ref(false);
const { showToast } = useToast();
const danhMucStore = useDanhMucStore();
const areOtherFiltersDisabled = ref(false);
const debounceTimer = ref<ReturnType<typeof setTimeout> | undefined>(undefined);

const dsTieuChiTimKiem = [
  { value: "tenDanhMuc", label: "Tên danh mục" },
  { value: "maDanhMuc", label: "Mã danh mục", isId: true },
];

const allDanhMuc = ref<DanhMucQuanLyResponse[]>([]);

// --- Modal Logic ---
const modalTitle = computed(() =>
  isEditMode.value ? "Sửa thông tin danh mục" : "Thêm danh mục mới"
);

const openAddModal = () => {
  isEditMode.value = false;
  selectedCategory.value = null;
  isModalVisible.value = true;
};

const openEditModal = (category: ChiTietDanhMucQuanLyResponse) => {
  isEditMode.value = true;
  selectedCategory.value = category;
  isModalVisible.value = true;
};

const handleEditClick = async (categoryId: number) => {
  try {
    const categoryDetails = await getChiTietDanhMuc(categoryId);
    openEditModal(categoryDetails);
  } catch (error) {
    console.error("Lỗi khi lấy chi tiết danh mục:", error);
    showToast({
      loai: "loi",
      thongBao: "Không thể tải dữ liệu danh mục để sửa.",
    });
  }
};

const closeModal = () => {
  isModalVisible.value = false;
  selectedCategory.value = null;
};

const handleSuccess = () => {
  fetchDanhMuc(); // Tải lại bảng danh mục hiện tại

  // Đồng thời, yêu cầu store tải lại cả hai loại dữ liệu danh mục
  // để đảm bảo tính nhất quán trên toàn bộ ứng dụng.
  danhMucStore.fetchMenuDanhMuc(true); // Dữ liệu cho menu khách hàng
  danhMucStore.fetchAdminCategoryOptions(true); // Dữ liệu cho các dropdown/filter trong trang admin
  danhMucStore.fetchAllCategoriesFlat(true); // Dữ liệu phẳng cho modal sửa danh mục
};

// --- Phân trang ---
const trangHienTai = ref(0);
const soLuongMoiTrang = ref(10);
const tongSoTrang = ref(1);

// --- Fetch dữ liệu ---
const fetchDanhMuc = async () => {
  isLoading.value = true;
  try {
    const params: GetDanhMucParams = {
      page: trangHienTai.value,
      size: soLuongMoiTrang.value,
      searchField: tuKhoa.value ? loaiTimKiem.value : undefined,
      searchValue: tuKhoa.value || undefined,
      trangThai: areOtherFiltersDisabled.value
        ? undefined
        : selectedTrangThai.value || undefined,
    };
    const response = await getDanhSachDanhMuc(params);
    allDanhMuc.value = response.content;
    tongSoTrang.value = response.totalPages;
  } catch (error) {
    console.error("Lỗi khi lấy danh sách danh mục:", error);
    showToast({
      loai: "loi",
      thongBao: "Không thể tải danh sách danh mục.",
    });
    allDanhMuc.value = [];
    tongSoTrang.value = 1;
  } finally {
    isLoading.value = false;
  }
};

// --- Cấu hình DataTable ---
const headers = [
  "Mã danh mục",
  "Tên danh mục",
  "Danh mục cha",
  "Trạng thái",
  "Hành động",
];

const rows = computed(() =>
  allDanhMuc.value.map((dm) => [
    dm.maDanhMuc,
    dm.tenDanhMuc,
    dm.tenDanhMucCha || "—", // Hiển thị fallback nếu không có
    dm.trangThai, // Truyền cả object để slot tùy chỉnh
    dm.maDanhMuc,
  ])
);

// --- Watchers ---

// 1. Watch for search keyword changes with a debounce to prevent race conditions
watch(tuKhoa, () => {
  clearTimeout(debounceTimer.value);
  debounceTimer.value = setTimeout(() => {
    if (trangHienTai.value !== 0) {
      trangHienTai.value = 0;
    } else {
      fetchDanhMuc();
    }
  }, 300); // Wait for 300ms after user stops typing
});

// 2. Watch for status filter to apply immediately
watch(selectedTrangThai, () => {
  if (trangHienTai.value !== 0) {
    trangHienTai.value = 0;
  } else {
    fetchDanhMuc();
  }
});

// 3. When the current page changes, fetch data
watch(trangHienTai, fetchDanhMuc);

// 4. When changing search type, handle cleanup logic.
watch(loaiTimKiem, (newLoai) => {
  const isIdSearch =
    dsTieuChiTimKiem.find((t) => t.value === newLoai)?.isId ?? false;
  areOtherFiltersDisabled.value = isIdSearch;
  tuKhoa.value = ""; // Clearing the keyword will trigger the debounced watcher

  if (isIdSearch) {
    selectedTrangThai.value = "";
  }
});

// 5. Initial data load when the component is created.
fetchDanhMuc();
</script>
