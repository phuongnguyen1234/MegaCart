<template>
  <div
    class="border rounded-lg p-4 flex flex-col sm:flex-row justify-between gap-4 bg-white shadow-sm hover:shadow-md transition-shadow"
  >
    <!-- Left Section: Product Info -->
    <div class="flex items-start gap-4 flex-1">
      <img
        :src="donHang.anhMinhHoaDauTien"
        alt="Ảnh sản phẩm"
        class="w-20 h-20 object-cover rounded-md bg-gray-100"
      />
      <div class="flex-1">
        <div class="flex justify-between items-start">
          <p class="font-bold text-sm text-gray-800">
            Đơn hàng #{{ donHang.maDonHang }}
          </p>
          <span
            class="text-xs font-semibold px-2 py-1 rounded-full whitespace-nowrap"
            :class="trangThaiColor"
          >
            {{ donHang.trangThai?.label || "Đang xử lý" }}
          </span>
        </div>
        <p class="text-blue-600 font-medium mt-1">
          {{ donHang.tenSanPhamDauTien }}
        </p>
        <p class="text-sm text-gray-500">
          Số lượng: {{ donHang.soLuongDauTien }}
        </p>
        <p
          v-if="
            donHang.soLuongLoaiSanPhamKhac && donHang.soLuongLoaiSanPhamKhac > 0
          "
          class="text-sm text-gray-500"
        >
          +{{ donHang.soLuongLoaiSanPhamKhac }} sản phẩm khác
        </p>
      </div>
    </div>

    <!-- Right Section: Order Summary -->
    <div
      class="text-sm text-left sm:text-right flex flex-col justify-between items-start sm:items-end pt-2 sm:pt-0 border-t sm:border-t-0 sm:border-l sm:pl-4"
    >
      <div class="mb-2">
        <span class="text-gray-600">Tổng tiền:</span>
        <p class="font-bold text-red-600 text-base">
          {{ formatPrice(donHang.tongTien) }} VND
        </p>
      </div>
      <div>
        <div class="text-gray-500 text-xs">
          {{ formattedDate }}
        </div>
        <button
          class="text-blue-600 hover:underline font-semibold mt-2 text-sm"
          @click.stop="$emit('xemChiTiet')"
        >
          Xem chi tiết
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import type { LichSuDonHang } from "@/types/donhang.types";
import { TrangThaiDonHangKey } from "@/types/donhang.types";

defineEmits(["xemChiTiet"]);

const props = defineProps<{
  donHang: LichSuDonHang;
}>();

const formatPrice = (vnd: number) => vnd.toLocaleString("vi-VN");

const formattedDate = computed(() => {
  return new Date(props.donHang.thoiGianDatHang).toLocaleString("vi-VN");
});

const trangThaiColor = computed(() => {
  // Thêm kiểm tra để tránh lỗi khi dữ liệu chưa sẵn sàng hoặc không hợp lệ
  if (!props.donHang?.trangThai?.value) {
    return "text-gray-600 bg-gray-100";
  }
  switch (props.donHang.trangThai.value) {
    case TrangThaiDonHangKey.DA_GIAO:
      return "text-green-600 bg-green-100";
    case TrangThaiDonHangKey.DA_HUY:
      return "text-red-600 bg-red-100";
    case TrangThaiDonHangKey.DANG_GIAO:
      return "text-blue-600 bg-blue-100";
    default:
      return "text-yellow-600 bg-yellow-100";
  }
});
</script>
