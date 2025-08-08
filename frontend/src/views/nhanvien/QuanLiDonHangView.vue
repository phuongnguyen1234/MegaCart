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
        />
      </div>

      <!-- Tìm kiếm -->
      <ThanhTimKiem
        :ds-tieu-chi="[
          { value: 'maDonHang', label: 'Mã đơn hàng' },
          { value: 'tenKhachHang', label: 'Tên khách hàng' },
        ]"
        v-model:modelValueLoai="loaiTimKiem"
        v-model:modelValueTuKhoa="tuKhoa"
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
        >
          <option v-for="tt in dsTrangThai" :key="tt.value" :value="tt.value">
            {{ tt.text }}
          </option>
        </select>
      </div>
    </div>

    <!-- Số lượng đơn -->
    <p class="mb-2 text-sm text-gray-600">{{ thongTinHienThi }}</p>

    <!-- Bảng -->
    <div class="overflow-x-auto shadow-md sm:rounded-lg">
      <table class="w-full text-sm text-left text-gray-500">
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
            <td class="px-6 py-4">{{ donHang.tenNguoiNhan }}</td>
            <td class="px-6 py-4">
              {{ formatDateTime(donHang.thoiGianDatHang) }}
            </td>
            <td class="px-6 py-4">{{ donHang.trangThai }}</td>
            <td class="px-6 py-4">{{ formatCurrency(donHang.tongTien) }}</td>
            <td class="px-6 py-4 text-right">
              <button
                @click="openModal(donHang)"
                class="text-gray-500 hover:text-blue-600 transition-colors duration-200"
                :title="
                  donHang.trangThai === 'Đang giao'
                    ? 'Sửa đơn hàng'
                    : 'Xem chi tiết'
                "
              >
                <i
                  v-if="donHang.trangThai === 'Đang giao'"
                  class="fi fi-rr-pencil text-lg align-middle"
                ></i>
                <i v-else class="fi fi-rr-eye text-lg align-middle"></i>
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
import type { DonHang } from "@/types/DonHang";
import { ref, computed, watch } from "vue";

// --- Bộ lọc ---
const ngayDat = ref("2025-06-25");
const loaiTimKiem = ref("maDonHang"); // 'maDonHang' hoặc 'tenKhachHang'
const tuKhoa = ref("");
const trangThai = ref("Đang giao");

const placeholderTimKiem = computed(() => {
  if (loaiTimKiem.value === "maDonHang") {
    return "Tìm kiếm mã đơn hàng...";
  }
  return "Tìm kiếm tên khách hàng...";
});

const dsTrangThai = [
  { value: "", text: "Tất cả" },
  { value: "Chờ xác nhận", text: "Chờ xác nhận" },
  { value: "Chờ xử lí", text: "Chờ xử lí" },
  { value: "Đang giao", text: "Đang giao" },
  { value: "Đã giao", text: "Đã giao" },
  { value: "Đã hủy", text: "Đã hủy" },
];

// --- Trạng thái và xử lý Modal ---
const isModalVisible = ref(false);
const selectedDonHang = ref<DonHang | null>(null);
const isEditMode = ref(false);

const openModal = (donHang: DonHang) => {
  selectedDonHang.value = donHang;
  isEditMode.value = donHang.trangThai === "Đang giao";
  isModalVisible.value = true;
};

const closeModal = () => {
  isModalVisible.value = false;
};

const handleLuuThayDoi = (formData: Partial<DonHang>) => {
  if (selectedDonHang.value) {
    const index = allDonHang.value.findIndex(
      (d) => d.maDonHang === selectedDonHang.value!.maDonHang
    );
    if (index !== -1) {
      Object.assign(allDonHang.value[index], formData);
    }
  }
  closeModal();
};

const sanPhamForModal = computed(() => {
  if (!selectedDonHang.value || !selectedDonHang.value.chiTietDonHang)
    return [];
  return selectedDonHang.value.chiTietDonHang.map((item) => ({
    id: item.maChiTietDonHang,
    ten: item.tenSanPham,
    donGia: item.donGia,
    soLuong: item.soLuong,
    hinhAnh: item.anhMinhHoa,
  }));
});

// --- Danh sách đơn hàng mẫu ---
const allDonHang = ref<DonHang[]>(
  Array.from({ length: 150 }, (_, i) => {
    const id = 1000 - i;
    const statusList = [
      "Chờ xác nhận",
      "Chờ xử lí",
      "Đang giao",
      "Đã giao",
      "Đã hủy",
    ] as const;
    const status = statusList[i % statusList.length];
    const date = new Date(
      "2025-06-25T10:" + String(i % 60).padStart(2, "0") + ":00"
    );
    const chiTiet = [
      {
        maDonHang: id,
        maChiTietDonHang: 101 + i,
        tenSanPham: "Sản phẩm Mẫu " + (i % 5),
        donGia: 50000 * ((i % 5) + 1),
        soLuong: (i % 3) + 1,
        anhMinhHoa: "https://via.placeholder.com/100",
        nhaSanXuat: "Nhà sản xuất A",
        donVi: "Cái",
      },
      {
        maDonHang: id,
        maChiTietDonHang: 201 + i,
        tenSanPham: "Phụ kiện " + ((i % 2) + 1),
        donGia: 25000,
        soLuong: 1,
        anhMinhHoa: "https://via.placeholder.com/100",
        nhaSanXuat: "Nhà sản xuất B",
        donVi: "Bộ",
      },
    ];

    return {
      maDonHang: id,
      tenNguoiNhan: `Nguyễn Văn ${String.fromCharCode(65 + (i % 26))}`,
      thoiGianDatHang: date,
      trangThai: status,
      diaChiNhanHang: `${i + 1} Đường ABC, Phường XYZ, Quận 1, TP. HCM`,
      sdtNhanHang: `0909123${String(i).padStart(3, "0")}`,
      hinhThucNhanHang: "Giao hàng tận nơi",
      hinhThucThanhToan: "Thanh toán khi nhận hàng",
      trangThaiThanhToan: "Chưa thanh toán",
      thoiGianThanhToan: date,
      duKienGiaoHang: new Date(date.getTime() + 3 * 24 * 60 * 60 * 1000),
      trangThaiXuLi: "Chưa xử lí", // Thêm thuộc tính còn thiếu để khớp với type DonHang
      ghiChu: i % 10 === 0 ? `Ghi chú đặc biệt cho đơn hàng #${id}` : "",
      chiTietDonHang: chiTiet,
      tongTien: chiTiet.reduce(
        (sum, item) => sum + item.donGia * item.soLuong,
        0
      ),
    };
  })
);

// --- Header và hàng ---
const headers = [
  "Mã đơn hàng",
  "Khách hàng",
  "Thời gian đặt",
  "Trạng thái",
  "Tổng tiền",
];

const donHangDaLoc = computed(() =>
  allDonHang.value.filter((d) => {
    const thoiGian = d.thoiGianDatHang;
    const matchNgay =
      !ngayDat.value ||
      (thoiGian instanceof Date &&
        !isNaN(thoiGian.getTime()) &&
        thoiGian.toISOString().split("T")[0] === ngayDat.value);

    const matchTuKhoa = (() => {
      if (!tuKhoa.value.trim()) return true;
      const keyword = tuKhoa.value.toLowerCase();
      if (loaiTimKiem.value === "maDonHang") {
        return String(d.maDonHang).toLowerCase().includes(keyword);
      }
      // Mặc định tìm theo tên khách hàng
      return d.tenNguoiNhan.toLowerCase().includes(keyword);
    })();

    const matchTrangThai = !trangThai.value || d.trangThai === trangThai.value;

    return matchNgay && matchTuKhoa && matchTrangThai;
  })
);

// --- Phân trang ---
const trangHienTai = ref(0);
const soLuongMoiTrang = 50;
const tongSoTrang = computed(() =>
  Math.ceil(donHangDaLoc.value.length / soLuongMoiTrang)
);

// --- Định dạng dữ liệu ---
const formatDateTime = (date: Date): string => {
  return date.toLocaleString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

const formatCurrency = (value: number): string => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(value);
};

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

// --- Reset về trang đầu nếu bộ lọc thay đổi ---
watch(donHangDaLoc, () => {
  trangHienTai.value = 0;
});
</script>
