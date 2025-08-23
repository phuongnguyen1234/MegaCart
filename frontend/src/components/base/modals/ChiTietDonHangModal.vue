<template>
  <BaseModal
    :visible="visible"
    :title="modalTitle"
    width-class="w-[500px]"
    @close="$emit('close')"
  >
    <!-- Loading State -->
    <div v-if="isLoading" class="text-center py-10">
      <div
        class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"
      ></div>
      <p class="mt-3 text-gray-600">Đang tải chi tiết đơn hàng...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="apiError" class="text-center py-10">
      <p class="text-red-500">{{ apiError }}</p>
    </div>

    <!-- Main Content -->
    <div v-else-if="donHang">
      <!-- Chế độ xem chi tiết -->
      <div v-if="!dangHuy" class="space-y-4">
        <!-- Danh sách sản phẩm -->
        <div class="max-h-[250px] overflow-y-auto pr-1 space-y-2">
          <CardSanPhamDonHang
            v-for="item in donHang.items"
            :key="item.maSanPham"
            :san-pham="item"
          />
        </div>

        <!-- Tổng tiền -->
        <div class="text-right font-semibold text-lg border-t pt-3">
          Tổng tiền:
          <span class="text-red-600">{{
            donHang.tongTien.toLocaleString()
          }}</span>
          VND
        </div>

        <!-- Thông tin đơn hàng -->
        <div class="text-sm space-y-2">
          <div class="flex items-start gap-2">
            <i class="fi fi-rr-user mt-1 w-4 text-center"></i>
            <span><strong>Người nhận:</strong> {{ donHang.tenNguoiNhan }}</span>
          </div>
          <div class="flex items-start gap-2">
            <i class="fi fi-rr-phone-call mt-1 w-4 text-center"></i>
            <span><strong>SĐT: </strong> {{ donHang.sdtNhanHang }}</span>
          </div>
          <div class="flex items-start gap-2">
            <i class="fi fi-rr-marker mt-1 w-4 text-center"></i>
            <span><strong>Địa chỉ: </strong> {{ donHang.diaChiNhanHang }}</span>
          </div>
          <div class="flex items-start gap-2">
            <i class="fi fi-rr-info mt-1 w-4 text-center"></i>
            <span
              ><strong>Trạng thái: </strong>
              <span class="font-semibold" :class="trangThaiColor">{{
                donHang.trangThai.label
              }}</span></span
            >
          </div>
          <div class="flex items-start gap-2">
            <i class="fi fi-rr-calendar-clock mt-1 w-4 text-center"></i>
            <span
              ><strong>Thời gian đặt: </strong>
              {{ new Date(donHang.thoiGianDatHang).toLocaleString() }}</span
            >
          </div>
          <div class="flex items-start gap-2">
            <i class="fi fi-rr-box mt-1 w-4 text-center"></i>
            <span
              ><strong>Hình thức giao hàng:</strong>
              {{ donHang.hinhThucGiaoHang.label }}</span
            >
          </div>
          <div class="flex items-start gap-2">
            <i class="fi fi-rr-wallet mt-1 w-4 text-center"></i>
            <span
              ><strong>Hình thức thanh toán:</strong>
              {{ donHang.hinhThucThanhToan.label }}</span
            >
          </div>
          <div class="flex items-start gap-2">
            <i class="fi fi-rr-credit-card mt-1 w-4 text-center"></i>
            <span
              ><strong>Trạng thái thanh toán:</strong>
              {{ donHang.trangThaiThanhToan.label }}</span
            >
          </div>
          <div v-if="donHang.duKienGiaoHang" class="flex items-start gap-2">
            <i class="fi fi-rr-clock mt-1 w-4 text-center"></i>
            <span
              ><strong>Dự kiến giao:</strong>
              {{ new Date(donHang.duKienGiaoHang).toLocaleString() }}</span
            >
          </div>
          <div v-if="donHang.ghiChu" class="flex items-start gap-2">
            <i class="fi fi-rr-comment mt-1 w-4 text-center"></i>
            <span><strong>Ghi chú:</strong> {{ donHang.ghiChu }}</span>
          </div>
          <div v-if="donHang.thoiGianThanhToan" class="flex items-start gap-2">
            <i class="fi fi-rr-calendar-check mt-1 w-4 text-center"></i>
            <span
              ><strong>Đã thanh toán:</strong>
              {{ new Date(donHang.thoiGianThanhToan).toLocaleString() }}</span
            >
          </div>
        </div>
      </div>

      <!-- Chế độ hủy đơn -->
      <div v-if="dangHuy" class="text-sm">
        <button
          @click="dangHuy = false"
          class="text-sm mb-2 text-blue-600 flex items-center gap-1"
        >
          <i class="fi fi-rr-arrow-left text-sm"></i> Quay về
        </button>

        <div class="space-y-2 mb-4">
          <label class="flex items-center gap-2 cursor-pointer">
            <input
              type="radio"
              value="Tôi không muốn mua nữa"
              v-model="lyDoHuy"
            />
            Tôi không muốn mua nữa
          </label>
          <label class="flex items-center gap-2 cursor-pointer">
            <input
              type="radio"
              value="Tôi ghi nhầm thông tin giao hàng"
              v-model="lyDoHuy"
            />
            Tôi ghi nhầm thông tin giao hàng
          </label>
          <label class="flex items-center gap-2 cursor-pointer">
            <input
              type="radio"
              value="Tôi chọn nhầm sản phẩm/số lượng"
              v-model="lyDoHuy"
            />
            Tôi chọn nhầm sản phẩm/số lượng
          </label>
          <label class="flex items-start gap-2 cursor-pointer">
            <input
              type="radio"
              value="ly-do-khac"
              v-model="lyDoHuy"
              class="mt-1"
            />
            <div class="flex-1">
              Lý do khác (Ghi rõ):
              <textarea
                rows="3"
                class="w-full mt-1 border rounded px-2 py-1 text-sm resize-none disabled:bg-gray-100 disabled:cursor-not-allowed"
                placeholder="Nhập lý do cụ thể..."
                v-model="ghiChuKhac"
                :disabled="lyDoHuy !== 'ly-do-khac'"
                @focus="lyDoHuy = 'ly-do-khac'"
              ></textarea>
            </div>
          </label>
        </div>
      </div>
    </div>

    <!-- Footer: Các nút hành động -->
    <template #footer>
      <div v-if="donHang && !isLoading" class="flex gap-2">
        <!-- Nút cho chế độ hủy đơn -->
        <template v-if="dangHuy">
          <button
            class="w-full bg-red-600 hover:bg-red-700 text-white py-2 rounded"
            @click="handleHuyDon"
          >
            Xác nhận hủy
          </button>
        </template>

        <!-- Nút cho chế độ xem chi tiết -->
        <template v-else>
          <template v-if="donHang.trangThai.value === 'CHO_XAC_NHAN'">
            <button
              class="flex-1 bg-gray-700 hover:bg-gray-800 text-white py-2 rounded"
              @click="handleGiaoPhanConLai"
            >
              Giao phần còn lại
            </button>
            <button
              class="flex-1 bg-red-600 hover:bg-red-700 text-white py-2 rounded"
              @click="handleHuyDonHetHang"
            >
              Hủy đơn (do hết hàng)
            </button>
          </template>
          <template
            v-else-if="
              ['CHO_XU_LI', 'DANG_GIAO'].includes(donHang.trangThai.value)
            "
          >
            <button
              class="w-full bg-red-600 hover:bg-red-700 text-white py-2 rounded"
              @click="dangHuy = true"
            >
              Hủy đơn
            </button>
          </template>
        </template>
      </div>
    </template>
  </BaseModal>

  <!-- Confirmation Modal -->
  <ConfirmModal
    :hien-thi="confirmState.visible"
    :tieu-de="confirmState.title"
    :noi-dung="confirmState.content"
    @xac-nhan="confirmState.onConfirm"
    @huy="hideConfirm"
  />
</template>

<script setup lang="ts">
import { reactive, ref, watch, computed } from "vue";
import BaseModal from "./BaseModal.vue";
import ConfirmModal from "./ConfirmModal.vue";
import CardSanPhamDonHang from "@/components/base/card/CardSanPhamDonHang.vue";
import {
  getChiTietDonHang,
  huyDonHang,
  giaoPhanConLai,
} from "@/service/donhang.service";
import type { ChiTietDonHang } from "@/types/donhang.types";
import { TrangThaiDonHangKey } from "@/types/donhang.types";
import { useToast } from "@/composables/useToast";
import { AxiosError } from "axios";

const { showToast } = useToast();

const props = defineProps<{
  visible: boolean;
  maDonHang: number;
}>();

const emit = defineEmits<{
  (e: "close"): void;
  (e: "update"): void; // Gửi tín hiệu để component cha tải lại danh sách
}>();

// --- State ---
const isLoading = ref(true);
const donHang = ref<ChiTietDonHang | null>(null);
const apiError = ref<string | null>(null);
const dangHuy = ref(false);
const lyDoHuy = ref("Tôi không muốn mua nữa");
const ghiChuKhac = ref("");

const confirmState = reactive({
  visible: false,
  title: "",
  content: "",
  onConfirm: () => {},
});

// --- Computed ---
const modalTitle = computed(() => {
  if (dangHuy.value) return "Tại sao bạn muốn hủy đơn?";
  if (donHang.value) return `Chi tiết đơn hàng #${donHang.value.maDonHang}`;
  return "Chi tiết đơn hàng";
});

const trangThaiColor = computed(() => {
  if (!donHang.value) return "text-gray-600";
  switch (donHang.value.trangThai.value) {
    case TrangThaiDonHangKey.DA_GIAO:
      return "text-green-600";
    case TrangThaiDonHangKey.DA_HUY:
      return "text-red-600";
    case TrangThaiDonHangKey.DANG_GIAO:
      return "text-blue-600";
    default:
      return "text-yellow-600";
  }
});

// --- Methods ---
const fetchData = async () => {
  if (!props.maDonHang) return;
  isLoading.value = true;
  apiError.value = null;
  donHang.value = null;
  dangHuy.value = false;

  try {
    donHang.value = await getChiTietDonHang(props.maDonHang);
  } catch (error) {
    if (error instanceof AxiosError && error.response?.data?.message) {
      apiError.value = error.response.data.message;
    } else {
      apiError.value = "Không thể tải chi tiết đơn hàng. Vui lòng thử lại.";
    }
  } finally {
    isLoading.value = false;
  }
};

const hideConfirm = () => {
  confirmState.visible = false;
};

const handleHuyDon = async () => {
  if (!donHang.value) return;

  let lyDoFinal = lyDoHuy.value;
  if (lyDoHuy.value === "ly-do-khac") {
    if (!ghiChuKhac.value.trim()) {
      showToast({ thongBao: "Vui lòng nhập lý do cụ thể.", loai: "loi" });
      return;
    }
    lyDoFinal = ghiChuKhac.value.trim();
  }

  try {
    await huyDonHang(donHang.value.maDonHang, { lyDo: lyDoFinal });
    showToast({ thongBao: "Hủy đơn hàng thành công.", loai: "thanhCong" });
    emit("update");
  } catch (error) {
    showToast({ thongBao: "Hủy đơn hàng thất bại.", loai: "loi" });
  }
};

const handleHuyDonHetHang = () => {
  confirmState.title = "Xác nhận hủy đơn";
  confirmState.content =
    "Bạn có chắc chắn muốn hủy đơn hàng này vì lý do hết hàng không?";
  confirmState.onConfirm = async () => {
    if (!donHang.value) return;
    try {
      await huyDonHang(donHang.value.maDonHang, { lyDo: "Hủy do hết hàng" });
      showToast({ thongBao: "Hủy đơn hàng thành công.", loai: "thanhCong" });
      emit("update");
    } catch (error) {
      showToast({ thongBao: "Hủy đơn hàng thất bại.", loai: "loi" });
    }
    hideConfirm();
  };
  confirmState.visible = true;
};

const handleGiaoPhanConLai = async () => {
  if (!donHang.value) return;
  confirmState.title = "Xác nhận giao hàng";
  confirmState.content =
    "Xác nhận giao các sản phẩm còn lại? Các sản phẩm hết hàng sẽ bị hủy.";
  confirmState.onConfirm = async () => {
    if (!donHang.value) return;
    try {
      await giaoPhanConLai(donHang.value.maDonHang);
      showToast({
        thongBao: "Yêu cầu giao phần còn lại thành công.",
        loai: "thanhCong",
      });
      emit("update");
    } catch (error) {
      showToast({ thongBao: "Yêu cầu thất bại.", loai: "loi" });
    }
    hideConfirm();
  };
  confirmState.visible = true;
};

// --- Watchers ---
watch(
  () => props.visible,
  (isVisible) => {
    if (isVisible) {
      fetchData();
    }
  },
  { immediate: true } // Tải dữ liệu ngay khi component được tạo nếu visible=true
);
</script>
