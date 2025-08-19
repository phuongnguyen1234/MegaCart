<template>
  <BaseModal
    :visible="visible"
    :title="modalTitle"
    width-class="w-[600px]"
    @close="$emit('close')"
  >
    <div v-if="donHang" class="flex flex-col gap-6">
      <!-- Danh sách sản phẩm -->
      <div
        class="max-h-[30vh] overflow-y-auto space-y-2 pr-2 -mr-2 custom-scrollbar"
      >
        <CardSanPhamDonHang
          v-for="sp in danhSachSanPham"
          :key="sp.maSanPham"
          :sanPham="sp"
          :is-management-mode="true"
        />
      </div>

      <!-- Tổng tiền -->
      <div class="text-right font-semibold border-t pt-3">
        Tổng tiền: {{ formatCurrency(tongTien) }}
      </div>

      <!-- Thông tin cố định -->
      <div class="text-sm space-y-3 border-t pt-4">
        <div class="flex items-start gap-2">
          <i class="fi fi-rr-user mt-1 text-gray-600"></i>
          <span><strong>Người nhận:</strong> {{ donHang.tenNguoiNhan }}</span>
        </div>
        <div class="flex items-start gap-2">
          <i class="fi fi-rr-phone-call mt-1 text-gray-600"></i>
          <span><strong>SĐT:</strong> {{ donHang.sdtNhanHang }}</span>
        </div>
        <div class="flex items-start gap-2">
          <i class="fi fi-rr-marker mt-1 text-gray-600"></i>
          <span><strong>Địa chỉ:</strong> {{ donHang.diaChiNhanHang }}</span>
        </div>
        <div class="flex items-start gap-2">
          <i class="fi fi-rr-calendar-clock mt-1 text-gray-600"></i>
          <span
            ><strong>Thời gian đặt:</strong>
            {{ formatDateTime(donHang.thoiGianDatHang) }}</span
          >
        </div>
        <div class="flex items-start gap-2">
          <i class="fi fi-rr-truck mt-1 text-gray-600"></i>
          <span
            ><strong>Hình thức giao hàng:</strong>
            {{ donHang.hinhThucGiaoHang?.label }}</span
          >
        </div>
        <div class="flex items-start gap-2">
          <i class="fi fi-rr-money-bill mt-1 text-gray-600"></i>
          <span
            ><strong>Hình thức thanh toán:</strong>
            {{ donHang.hinhThucThanhToan?.label }}</span
          >
        </div>
      </div>

      <!-- Form chỉnh sửa (hiển thị có điều kiện) -->
      <form v-if="laCheDoChinhSua" class="text-sm space-y-4 border-t pt-4">
        <!-- Trạng thái đơn hàng -->
        <div>
          <label for="trang-thai-don-hang" class="font-semibold"
            >Trạng thái đơn hàng</label
          >
          <select
            id="trang-thai-don-hang"
            v-model="formData.trangThai"
            class="input w-full mt-1"
          >
            <option
              v-for="(label, key) in TrangThaiDonHangLabel"
              :key="key"
              :value="key"
            >
              {{ label }}
            </option>
          </select>
        </div>

        <!-- Trạng thái thanh toán -->
        <div>
          <label for="trang-thai-thanh-toan" class="font-semibold"
            >Trạng thái thanh toán</label
          >
          <select
            id="trang-thai-thanh-toan"
            v-model="formData.trangThaiThanhToan"
            class="input w-full mt-1"
          >
            <option
              v-for="(label, key) in TrangThaiThanhToanLabel"
              :key="key"
              :value="key"
            >
              {{ label }}
            </option>
          </select>
        </div>

        <!-- Ngày dự kiến giao -->
        <div>
          <label for="du-kien-giao" class="font-semibold"
            >Ngày dự kiến giao</label
          >
          <input
            type="datetime-local"
            id="du-kien-giao"
            v-model="formData.duKienGiaoHang"
            class="input w-full mt-1"
          />
        </div>

        <!-- Ghi chú -->
        <div>
          <label for="ghi-chu" class="font-semibold">Ghi chú</label>
          <textarea
            id="ghi-chu"
            v-model="formData.ghiChu"
            rows="2"
            class="input w-full mt-1 resize-none"
          ></textarea>
        </div>
      </form>

      <!-- Thông tin chỉ xem (khi không ở chế độ sửa) -->
      <div v-else class="text-sm space-y-3 border-t pt-4">
        <div class="flex items-start gap-2">
          <i class="fi fi-rr-badge-check mt-1 text-gray-600"></i>
          <span
            ><strong>Trạng thái:</strong> {{ donHang.trangThai.label }}</span
          >
        </div>
        <div class="flex items-start gap-2">
          <i class="fi fi-rr-credit-card mt-1 text-gray-600"></i>
          <span
            ><strong>Thanh toán:</strong>
            {{ donHang.trangThaiThanhToan.label }}</span
          >
        </div>
        <div class="flex items-start gap-2">
          <i class="fi fi-rr-shipping-fast mt-1 text-gray-600"></i>
          <span
            ><strong>Dự kiến giao:</strong>
            {{
              donHang.duKienGiaoHang
                ? formatDateTime(donHang.duKienGiaoHang)
                : "Chưa có"
            }}
          </span>
        </div>
        <div class="flex items-start gap-2" v-if="donHang.ghiChu">
          <i class="fi fi-rr-comment mt-1 text-gray-600"></i>
          <span><strong>Ghi chú:</strong> {{ donHang.ghiChu }}</span>
        </div>
      </div>
    </div>
    <div v-else class="text-center py-8 text-gray-500">
      Đang tải thông tin đơn hàng...
    </div>

    <template #footer>
      <div class="flex justify-end gap-4">
        <button class="btn" @click="$emit('close')">
          {{ laCheDoChinhSua ? "Hủy" : "Đóng" }}
        </button>
        <button
          v-if="laCheDoChinhSua"
          class="btn btn-primary"
          @click="handleSave"
        >
          Lưu thay đổi
        </button>
      </div>
    </template>
  </BaseModal>
</template>

<script setup lang="ts">
import BaseModal from "../base/modals/BaseModal.vue";
import CardSanPhamDonHang from "@/components/base/card/CardSanPhamDonHang.vue";
import { ref, computed, watch } from "vue";
import type {
  ChiTietDonHangQuanLyResponse,
  CapNhatDonHangRequest,
  ChiTietDonHangItem,
} from "@/types/donhang.types";
import {
  TrangThaiDonHangLabel,
  TrangThaiThanhToanLabel,
} from "@/types/donhang.types";

const props = defineProps<{
  visible: boolean;
  laCheDoChinhSua: boolean;
  danhSachSanPham: ChiTietDonHangItem[];
  tongTien: number;
  donHang: ChiTietDonHangQuanLyResponse | null;
}>();

const emit = defineEmits<{
  (e: "close"): void;
  (e: "luu", data: CapNhatDonHangRequest): void;
}>();

// --- Form State for Editing ---
const formData = ref<CapNhatDonHangRequest>({});

const modalTitle = computed(() => {
  if (!props.donHang) return "";
  const prefix = props.laCheDoChinhSua ? "Cập nhật" : "Chi tiết";
  return `${prefix} đơn hàng #${props.donHang.maDonHang}`;
});

// --- Watchers ---
watch(
  () => props.donHang,
  (newDonHang) => {
    if (newDonHang && props.laCheDoChinhSua) {
      // Populate form data when the order details are available
      formData.value = {
        trangThai: newDonHang.trangThai.value,
        trangThaiThanhToan: newDonHang.trangThaiThanhToan.value,
        duKienGiaoHang: newDonHang.duKienGiaoHang
          ? newDonHang.duKienGiaoHang.slice(0, 16)
          : undefined, // Format for datetime-local
        ghiChu: newDonHang.ghiChu,
      };
    } else {
      // Reset form data if not in edit mode or no order
      formData.value = {};
    }
  },
  { immediate: true }
);

// --- Methods ---
const handleSave = () => {
  // Convert datetime-local string back to ISO string if it exists
  const dataToEmit = { ...formData.value };
  if (dataToEmit.duKienGiaoHang) {
    dataToEmit.duKienGiaoHang = new Date(
      dataToEmit.duKienGiaoHang
    ).toISOString();
  }
  emit("luu", dataToEmit);
};

const formatCurrency = (value: number): string => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(value);
};

const formatDateTime = (iso: string): string => {
  if (!iso) return "";
  const d = new Date(iso);
  return d.toLocaleString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};
</script>

<style scoped>
.custom-scrollbar::-webkit-scrollbar {
  width: 6px;
}
.custom-scrollbar::-webkit-scrollbar-thumb {
  background-color: #d1d5db; /* gray-400 */
  border-radius: 10px;
}
</style>
