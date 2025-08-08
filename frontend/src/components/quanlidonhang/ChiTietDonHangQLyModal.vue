<template>
  <Overlay :visible="visible" z-index="z-[1002]" @close="$emit('close')">
    <div
      v-if="donHang"
      class="bg-white w-[450px] max-h-[90vh] rounded-lg shadow-xl relative p-4 flex flex-col"
    >
      <!-- Nút đóng -->
      <button
        class="cursor-pointer absolute top-2 right-2 text-2xl"
        @click="$emit('close')"
      >
        ×
      </button>

      <!-- Tiêu đề -->
      <h2 class="text-center text-lg font-semibold mb-2">
        {{ modalTitle }}
      </h2>

      <!-- Danh sách sản phẩm -->
      <div class="flex-1 overflow-y-auto mb-4 px-2 py-1 space-y-2">
        <CardSanPhamDonHang
          v-for="sp in danhSachSanPham"
          :key="sp.id"
          :sanPham="sp"
        />
      </div>

      <!-- Tổng tiền -->
      <div class="text-right font-semibold mb-4">
        Tổng tiền: {{ tongTien.toLocaleString() }} VND
      </div>

      <!-- THÔNG TIN CỐ ĐỊNH (luôn hiển thị) -->
      <div class="text-sm space-y-2 mb-4 border-b pb-4">
        <div
          v-for="item in thongTinCoDinh"
          :key="item.key"
          class="flex items-start gap-2"
        >
          <i :class="`fi ${item.icon} mt-1`"></i>
          <span
            ><strong>{{ item.label }}:</strong> {{ item.value }}</span
          >
        </div>
      </div>

      <!-- THÔNG TIN CÓ THỂ SỬA -->
      <!-- Chế độ xem -->
      <div v-if="!laCheDoChinhSua" class="text-sm space-y-2 mb-4">
        <div
          v-for="item in thongTinXem"
          :key="item.key"
          class="flex items-start gap-2"
        >
          <i :class="`fi ${item.icon} mt-1`"></i>
          <span
            ><strong>{{ item.label }}:</strong> {{ item.value }}</span
          >
        </div>
      </div>

      <!-- FORM CHỈNH SỬA -->
      <div v-if="laCheDoChinhSua" class="text-sm space-y-3">
        <div>
          <label class="block text-sm font-medium">Trạng thái:</label>
          <select
            v-model="donHangForm.trangThai"
            class="w-full border rounded px-2 py-1"
          >
            <option
              v-for="tt in MANG_TRANG_THAI_DON_HANG"
              :key="tt"
              :value="tt"
            >
              {{ tt }}
            </option>
          </select>
        </div>
        <div>
          <label class="block font-medium">Trạng thái thanh toán:</label>
          <select
            v-model="donHangForm.trangThaiThanhToan"
            class="w-full border border-gray-300 rounded px-2 py-1 mt-1"
          >
            <option
              v-for="tt in MANG_TRANG_THAI_THANH_TOAN"
              :key="tt"
              :value="tt"
            >
              {{ tt }}
            </option>
          </select>
        </div>
        <div>
          <label class="block font-medium">Dự kiến giao hàng:</label>
          <input
            type="datetime-local"
            v-model="donHangForm.duKienGiaoHang"
            class="w-full border border-gray-300 rounded px-2 py-1 mt-1"
          />
        </div>
        <div>
          <label class="block font-medium">Ghi chú:</label>
          <textarea
            v-model="donHangForm.ghiChu"
            class="w-full border border-gray-300 rounded px-2 py-1 mt-1 resize-none"
            rows="3"
          ></textarea>
        </div>

        <!-- Nút lưu -->
        <button
          class="w-full bg-blue-600 hover:bg-blue-700 text-white py-2 rounded"
          @click="luuThayDoi"
        >
          Lưu thay đổi
        </button>
      </div>
    </div>
  </Overlay>
</template>

<script setup lang="ts">
import Overlay from "../base/Overlay.vue";
import CardSanPhamDonHang from "@/components/base/card/CardSanPhamDonHang.vue";
import { ref, watch, computed } from "vue";
import type { DonHang } from "@/types/DonHang";

const props = defineProps<{
  visible: boolean;
  danhSachSanPham: {
    id: number;
    ten: string;
    donGia: number;
    soLuong: number;
    hinhAnh: string;
  }[];
  tongTien: number;
  laCheDoChinhSua: boolean;
  donHang: DonHang | null;
}>();

const emit = defineEmits<{
  (e: "close"): void;
  (e: "luu", form: Partial<DonHang>): void;
}>();

interface DonHangForm {
  trangThai?: string;
  trangThaiThanhToan?: string;
  duKienGiaoHang?: string; // Dùng string cho input type="datetime-local"
  ghiChu?: string;
}

const donHangForm = ref<DonHangForm>({});

const MANG_TRANG_THAI_DON_HANG = [
  "Chờ xác nhận",
  "Chờ xử lí",
  "Đang giao",
  "Đã giao",
  "Đã hủy",
];

const MANG_TRANG_THAI_THANH_TOAN = ["Chưa thanh toán", "Đã thanh toán"];

const modalTitle = computed(() => {
  if (!props.donHang) return "";
  return props.laCheDoChinhSua
    ? `Chỉnh sửa đơn hàng #${props.donHang.maDonHang}`
    : `Chi tiết đơn hàng #${props.donHang.maDonHang}`;
});

const thongTinCoDinh = computed(() => {
  if (!props.donHang) return [];
  const details = [
    {
      key: "tenNguoiNhan",
      label: "Tên người nhận",
      value: props.donHang.tenNguoiNhan,
      icon: "fi-rr-user",
    },
    {
      key: "soDienThoai",
      label: "Số điện thoại",
      value: props.donHang.sdtNhanHang,
      icon: "fi-rr-phone-call",
    },
    {
      key: "diaChi",
      label: "Địa chỉ",
      value: props.donHang.diaChiNhanHang,
      icon: "fi-rr-marker",
    },
    {
      key: "thoiGianDat",
      label: "Thời gian đặt",
      value: new Date(props.donHang.thoiGianDatHang).toLocaleString("vi-VN"),
      icon: "fi-rr-calendar-clock",
    },
    {
      key: "hinhThucGiaoHang",
      label: "Hình thức giao hàng",
      value: props.donHang.hinhThucNhanHang,
      icon: "fi-rr-truck",
    },
    {
      key: "hinhThucThanhToan",
      label: "Hình thức thanh toán",
      value: props.donHang.hinhThucThanhToan,
      icon: "fi-rr-money-bill",
    },
  ];
  return details.filter((item) => item.value);
});

const thongTinXem = computed(() => {
  if (!props.donHang) return [];
  const details = [
    {
      key: "trangThai",
      label: "Trạng thái",
      value: props.donHang.trangThai,
      icon: "fi-rr-badge-check",
    },
    {
      key: "trangThaiThanhToan",
      label: "Trạng thái thanh toán",
      value: props.donHang.trangThaiThanhToan,
      icon: "fi-rr-credit-card",
    },
    {
      key: "duKienGiaoHang",
      label: "Dự kiến giao",
      value: props.donHang.duKienGiaoHang
        ? new Date(props.donHang.duKienGiaoHang).toLocaleString("vi-VN")
        : "Chưa có",
      icon: "fi-rr-shipping-fast",
    },
    {
      key: "ghiChu",
      label: "Ghi chú",
      value: props.donHang.ghiChu,
      icon: "fi-rr-comment",
    },
  ];

  // Lọc ra những mục có giá trị (đặc biệt hữu ích cho các trường tùy chọn như ghi chú)
  return details.filter((item) => item.value);
});

const formatDateForInput = (date: Date | string | undefined | null): string => {
  if (!date) return "";
  const d = new Date(date);
  if (isNaN(d.getTime())) return "";

  // Lấy thông tin ngày tháng năm giờ phút theo múi giờ địa phương
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  const hours = String(d.getHours()).padStart(2, "0");
  const minutes = String(d.getMinutes()).padStart(2, "0");

  return `${year}-${month}-${day}T${hours}:${minutes}`;
};

watch(
  () => props.donHang,
  (newDonHang) => {
    if (newDonHang) {
      donHangForm.value = {
        trangThai: newDonHang.trangThai,
        trangThaiThanhToan: newDonHang.trangThaiThanhToan,
        duKienGiaoHang: formatDateForInput(newDonHang.duKienGiaoHang),
        ghiChu: newDonHang.ghiChu || "",
      };
    }
  },
  { immediate: true }
);

const luuThayDoi = () => {
  // Chuyển đổi lại duKienGiaoHang từ string sang Date trước khi emit
  const payload: Partial<DonHang> = {
    ...donHangForm.value,
    duKienGiaoHang: donHangForm.value.duKienGiaoHang
      ? new Date(donHangForm.value.duKienGiaoHang)
      : undefined,
  };
  emit("luu", payload);
};
</script>
