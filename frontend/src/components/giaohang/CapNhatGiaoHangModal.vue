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
    <div v-else class="space-y-4 text-base">
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
          <span><strong>SĐT nhận hàng:</strong> {{ donHang.sdtNhanHang }}</span>
        </div>
        <div class="flex items-start gap-2">
          <i class="fi fi-rr-marker mt-1"></i>
          <span
            ><strong>Địa chỉ nhận hàng:</strong>
            {{ donHang.diaChiNhanHang }}</span
          >
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
              :value="KetQuaGiaoHangKey.THANH_CONG"
              v-model="ketQuaGiao"
            />
            Thành công
          </label>
          <label class="flex items-center gap-2">
            <input
              type="radio"
              :value="KetQuaGiaoHangKey.THAT_BAI"
              v-model="ketQuaGiao"
            />
            Thất bại
          </label>
        </div>

        <!-- Hiển thị khi là đơn COD -->
        <div v-if="isCODOrder" class="mt-2 space-y-2">
          <p class="font-medium text-gray-800">
            Cập nhật trạng thái thanh toán:
          </p>
          <div class="flex gap-4">
            <label class="flex items-center gap-2">
              <input
                type="radio"
                :value="TrangThaiThanhToanKey.DA_THANH_TOAN"
                v-model="trangThaiThanhToanCapNhat"
                class="h-4 w-4 border-gray-300 text-indigo-600 focus:ring-indigo-600 disabled:bg-gray-200"
                :disabled="isDeliveryFailed"
              />
              Đã thanh toán (Đã thu tiền COD)
            </label>
            <label
              class="flex items-center gap-2"
              :class="{ 'text-gray-500': isDeliveryFailed }"
            >
              <input
                type="radio"
                :value="TrangThaiThanhToanKey.CHUA_THANH_TOAN"
                v-model="trangThaiThanhToanCapNhat"
                class="h-4 w-4 border-gray-300 text-indigo-600 focus:ring-indigo-600 disabled:bg-gray-200"
                :disabled="isDeliveryFailed"
              />
              Chưa thanh toán
            </label>
          </div>
        </div>

        <!-- Hiển thị khi giao hàng thất bại -->
        <div v-if="isDeliveryFailed">
          <label class="block text-gray-700 mb-1"
            >Lý do thất bại (bắt buộc)</label
          >
          <textarea
            v-model="lyDoThatBai"
            rows="3"
            class="w-full p-2 border rounded resize-none focus:ring-indigo-500 focus:border-indigo-500"
            placeholder="Ví dụ: Khách không nghe máy, khách hẹn lại ngày giao,..."
          ></textarea>
        </div>
      </div>
    </div>
    <template #footer>
      <div class="flex justify-end gap-2">
        <button
          @click="dongModal"
          class="cursor-pointerpx-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300"
        >
          Đóng
        </button>
        <button
          @click="xacNhanGiaoHang"
          class="cursor-pointer px-5 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition disabled:bg-gray-400"
          :disabled="isConfirmButtonDisabled"
        >
          Xác nhận
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
  HinhThucThanhToanKey,
  TrangThaiThanhToanKey,
} from "@/types/donhang.types";
import {
  KetQuaGiaoHangKey,
  type ChiTietDonHangGiaoHangResponse,
  type CapNhatGiaoHangRequest,
} from "@/types/giaohang.types";

interface Props {
  visible?: boolean;
  donHang?: ChiTietDonHangGiaoHangResponse | null;
}

const { visible = false, donHang = null } = defineProps<Props>();

const emit = defineEmits<{
  (e: "close"): void;
  (e: "xacNhan", payload: CapNhatGiaoHangRequest): void;
}>();

const { showToast } = useToast();

const dongModal = () => emit("close");

// Trạng thái giao hàng
const ketQuaGiao = ref<KetQuaGiaoHangKey | "">("");
const lyDoThatBai = ref("");
const trangThaiThanhToanCapNhat = ref<TrangThaiThanhToanKey | "">("");

const isDeliveryFailed = computed(
  () => ketQuaGiao.value === KetQuaGiaoHangKey.THAT_BAI
);

const isConfirmButtonDisabled = computed(() => {
  // Case 1: Vô hiệu hóa khi giao thất bại nhưng chưa có lý do
  if (isDeliveryFailed.value) {
    return !lyDoThatBai.value.trim();
  }

  // Case 2: Vô hiệu hóa khi giao thành công đơn COD nhưng chưa chọn trạng thái thanh toán
  if (
    ketQuaGiao.value === KetQuaGiaoHangKey.THANH_CONG &&
    isCODOrder.value &&
    !trangThaiThanhToanCapNhat.value
  ) {
    return true;
  }

  // Bật trong các trường hợp còn lại
  return false;
});

const isCODOrder = computed(() => {
  if (!donHang) return false;
  return (
    donHang.hinhThucThanhToan?.value ===
    HinhThucThanhToanKey.THANH_TOAN_KHI_NHAN_HANG
  );
});

// Xác nhận gửi đi
const xacNhanGiaoHang = () => {
  if (!ketQuaGiao.value) {
    showToast({
      loai: "loi",
      thongBao: "Vui lòng chọn trạng thái giao hàng.",
    });
    return;
  }
  if (isDeliveryFailed.value && !lyDoThatBai.value.trim()) {
    showToast({
      loai: "loi",
      thongBao: "Vui lòng nhập lý do giao hàng thất bại.",
    });
    return;
  }

  // Bảo vệ bổ sung: Kiểm tra trạng thái thanh toán cho đơn COD thành công
  if (
    ketQuaGiao.value === KetQuaGiaoHangKey.THANH_CONG &&
    isCODOrder.value &&
    !trangThaiThanhToanCapNhat.value
  ) {
    showToast({
      loai: "loi",
      thongBao: "Vui lòng chọn trạng thái thanh toán cho đơn hàng COD.",
    });
    return;
  }

  const payload: CapNhatGiaoHangRequest = {
    ketQua: ketQuaGiao.value,
  };

  if (isDeliveryFailed.value) {
    payload.lyDoThatBai = lyDoThatBai.value.trim();
  }

  // Nếu là đơn COD, gửi trạng thái thanh toán đã chọn.
  if (isCODOrder.value && trangThaiThanhToanCapNhat.value) {
    payload.trangThaiThanhToan = trangThaiThanhToanCapNhat.value;
  }

  emit("xacNhan", payload);

  // Reset + đóng modal
  dongModal();
};

const tieuDeModal = computed(() =>
  donHang ? `Chi tiết đơn hàng #${donHang.maDonHang}` : "Đang tải..."
);

// Reset state khi modal được mở với đơn hàng mới
watch(
  () => visible,
  (isVisible) => {
    if (isVisible) {
      // Mặc định chọn "Thất bại" khi mở modal
      ketQuaGiao.value = KetQuaGiaoHangKey.THAT_BAI;
      lyDoThatBai.value = "";
      // Reset trạng thái thanh toán về giá trị mặc định của đơn hàng khi modal mở
      trangThaiThanhToanCapNhat.value =
        donHang?.trangThaiThanhToan?.value ?? "";
    }
  }
);

watch(
  () => donHang,
  (newDonHang) => {
    if (newDonHang) {
      // Đặt trạng thái thanh toán mặc định là trạng thái hiện tại của đơn hàng
      // Điều này đảm bảo radio button được chọn đúng khi modal mở
      // Sử dụng optional chaining để tránh lỗi nếu trangThaiThanhToan không tồn tại
      trangThaiThanhToanCapNhat.value =
        newDonHang.trangThaiThanhToan?.value ?? "";
    }
  }
);

watch(ketQuaGiao, (newKetQua) => {
  // Nếu giao hàng thất bại, tự động chọn "Chưa thanh toán".
  if (newKetQua === KetQuaGiaoHangKey.THAT_BAI) {
    trangThaiThanhToanCapNhat.value = TrangThaiThanhToanKey.CHUA_THANH_TOAN;
  }
  // Nếu là đơn COD và chuyển sang thành công, reset lựa chọn thanh toán
  // để buộc nhân viên phải chọn lại, tránh sai sót.
  else if (newKetQua === KetQuaGiaoHangKey.THANH_CONG && isCODOrder.value) {
    trangThaiThanhToanCapNhat.value = "";
  }
});
</script>
