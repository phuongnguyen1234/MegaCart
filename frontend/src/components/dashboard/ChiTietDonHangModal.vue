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
    <div v-else-if="donHang" class="space-y-4">
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
          <i class="fi fi-rr-truck mt-1 w-4 text-center"></i>
          <span
            ><strong>Giao hàng:</strong>
            {{ donHang.hinhThucGiaoHang.label }}</span
          >
        </div>
        <div class="flex items-start gap-2">
          <i class="fi fi-rr-money-bill mt-1 w-4 text-center"></i>
          <span
            ><strong>Thanh toán:</strong>
            {{ donHang.hinhThucThanhToan.label }}</span
          >
        </div>
        <div class="flex items-start gap-2">
          <i class="fi fi-rr-credit-card mt-1 w-4 text-center"></i>
          <span
            ><strong>Tình trạng TT:</strong>
            {{ donHang.trangThaiThanhToan.label }}</span
          >
        </div>
        <div v-if="donHang.duKienGiaoHang" class="flex items-start gap-2">
          <i class="fi fi-rr-shipping-fast mt-1 w-4 text-center"></i>
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
  </BaseModal>
</template>

<script setup lang="ts">
import { ref, watch, computed } from "vue";
import BaseModal from "../base/modals/BaseModal.vue";
import CardSanPhamDonHang from "@/components/base/card/CardSanPhamDonHang.vue";
import { getChiTietDonHang } from "@/service/thongke.service";
import type { ChiTietDonHang } from "@/types/donhang.types";
import { TrangThaiDonHangKey } from "@/types/donhang.types";
import { AxiosError } from "axios";

const props = defineProps<{
  visible: boolean;
  maDonHang: number;
}>();

defineEmits<{
  (e: "close"): void;
}>();

const isLoading = ref(true);
const donHang = ref<ChiTietDonHang | null>(null);
const apiError = ref<string | null>(null);

const modalTitle = computed(() => {
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

const fetchData = async () => {
  if (!props.maDonHang) return;
  isLoading.value = true;
  apiError.value = null;
  donHang.value = null;

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

watch(
  () => props.visible,
  (isVisible) => {
    if (isVisible) {
      fetchData();
    }
  },
  { immediate: true }
);
</script>
