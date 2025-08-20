<template>
  <BaseModal
    :visible="visible"
    :title="tieuDeModal"
    @close="dongModal"
    width-class="w-[550px] max-w-full"
  >
    <div v-if="!donHang" class="text-center p-8 text-gray-500">
      Đang tải chi tiết đơn hàng...
    </div>
    <div v-else class="space-y-4 text-sm">
      <!-- Danh sách sản phẩm -->
      <div class="overflow-y-auto mb-2 px-2 py-1 space-y-2 max-h-60">
        <CardSanPhamDonHang
          v-for="sp in donHang.items"
          :key="sp.maSanPham"
          :san-pham="sp"
        />
      </div>

      <!-- Tổng tiền -->
      <div class="text-right font-semibold text-base">
        Tổng tiền: {{ formatCurrency(donHang.tongTien) }}
      </div>

      <!-- Thông tin người nhận -->
      <div class="space-y-2 border-t pt-3">
        <div class="flex items-start gap-2">
          <i class="fi fi-rr-user mt-1"></i>
          <span
            ><strong>Tên người nhận:</strong> {{ donHang.tenNguoiNhan }}</span
          >
        </div>
        <div class="flex items-start gap-2">
          <i class="fi fi-rr-phone-call mt-1"></i>
          <span><strong>SĐT:</strong> {{ donHang.sdtNhanHang }}</span>
        </div>
        <div class="flex items-start gap-2">
          <i class="fi fi-rr-marker mt-1"></i>
          <span><strong>Địa chỉ:</strong> {{ donHang.diaChiNhanHang }}</span>
        </div>
        <div class="flex items-start gap-2">
          <i class="fi fi-rr-credit-card mt-1"></i>
          <span
            ><strong>Hình thức thanh toán:</strong>
            {{ donHang.hinhThucThanhToan?.label }}</span
          >
        </div>
        <div class="flex items-start gap-2">
          <i class="fi fi-rr-money-bill mt-1"></i>
          <span
            ><strong>Trạng thái thanh toán:</strong>
            {{ donHang.trangThaiThanhToan?.label }}</span
          >
        </div>
      </div>

      <!-- Trạng thái giao hàng -->
      <div class="border-t pt-3 space-y-2">
        <h3 class="font-semibold">Cập nhật trạng thái giao hàng</h3>
        <div class="flex gap-4">
          <label class="flex items-center gap-2">
            <input
              type="radio"
              :value="TrangThaiGiaoHangKey.DA_GIAO_THANH_CONG"
              v-model="trangThaiGiao"
            />
            Thành công
          </label>
          <label class="flex items-center gap-2">
            <input
              type="radio"
              :value="TrangThaiGiaoHangKey.GIAO_HANG_THAT_BAI"
              v-model="trangThaiGiao"
            />
            Thất bại
          </label>
        </div>

        <div v-if="trangThaiGiao === TrangThaiGiaoHangKey.GIAO_HANG_THAT_BAI">
          <label class="block text-gray-700 mb-1"
            >Ghi chú (Lý do thất bại)</label
          >
          <textarea
            v-model="ghiChu"
            rows="3"
            class="w-full p-2 border rounded resize-none"
            placeholder="Nhập lý do..."
          ></textarea>
        </div>
      </div>
    </div>
    <template #footer>
      <div class="flex justify-end gap-2">
        <button
          @click="dongModal"
          class="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300"
        >
          Đóng
        </button>
        <button
          @click="xacNhanGiaoHang"
          class="px-5 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition disabled:bg-gray-400"
          :disabled="!trangThaiGiao"
        >
          Xác nhận cập nhật
        </button>
      </div>
    </template>
  </BaseModal>
</template>

<script setup lang="ts">
import { computed, ref, watch } from "vue";
import BaseModal from "../base/modals/BaseModal.vue";
import CardSanPhamDonHang from "../base/card/CardSanPhamDonHang.vue";
import { useToast } from "@/composables/useToast";
import { formatCurrency } from "@/utils/formatters";
import {
  TrangThaiGiaoHangKey,
  type ChiTietDonHangGiaoHangResponse,
  type CapNhatGiaoHangRequest,
} from "@/types/giaohang.types";

interface Props {
  visible: boolean;
  donHang: ChiTietDonHangGiaoHangResponse | null;
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  donHang: null,
});

const emit = defineEmits<{
  (e: "close"): void;
  (e: "xacNhan", payload: CapNhatGiaoHangRequest): void;
}>();

const { showToast } = useToast();

const dongModal = () => emit("close");

// Trạng thái giao hàng
const trangThaiGiao = ref<TrangThaiGiaoHangKey | "">("");
const ghiChu = ref("");

// Xác nhận gửi đi
const xacNhanGiaoHang = () => {
  if (!trangThaiGiao.value) {
    showToast({
      loai: "loi",
      thongBao: "Vui lòng chọn trạng thái giao hàng.",
    });
    return;
  }
  if (
    trangThaiGiao.value === TrangThaiGiaoHangKey.GIAO_HANG_THAT_BAI &&
    !ghiChu.value.trim()
  ) {
    showToast({
      loai: "loi",
      thongBao: "Vui lòng nhập lý do giao hàng thất bại.",
    });
    return;
  }

  const payload: CapNhatGiaoHangRequest = {
    trangThai: trangThaiGiao.value,
  };

  if (trangThaiGiao.value === TrangThaiGiaoHangKey.GIAO_HANG_THAT_BAI) {
    payload.ghiChu = ghiChu.value;
  }

  emit("xacNhan", payload);

  // Reset + đóng modal
  dongModal();
};

const tieuDeModal = computed(() =>
  props.donHang
    ? `Chi tiết đơn hàng #${props.donHang.maDonHang}`
    : "Đang tải..."
);

// Reset state khi modal được mở với đơn hàng mới
watch(
  () => props.visible,
  (isVisible) => {
    if (isVisible) {
      trangThaiGiao.value = "";
      ghiChu.value = "";
    }
  }
);
</script>
