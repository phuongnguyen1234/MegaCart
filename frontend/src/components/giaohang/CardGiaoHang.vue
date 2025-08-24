<template>
  <div class="rounded-lg p-4 shadow-sm bg-white">
    <!-- Dòng tiêu đề -->
    <div class="flex justify-between items-center mb-2 text-sm">
      <div>
        Đơn hàng <span class="font-bold">#{{ donHang.maDonHang }}</span>
      </div>
    </div>

    <!-- Thông tin người nhận -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
      <!-- Bên trái -->
      <div class="space-y-2">
        <div class="flex items-start">
          <i class="fi fi-rr-user mt-1 mr-2 text-gray-600"></i>
          <div>
            <span class="font-semibold">Tên người nhận:</span>
            <span class="ml-1">{{ donHang.tenNguoiNhan }}</span>
          </div>
        </div>

        <div class="flex items-start">
          <i class="fi fi-rr-marker mt-1 mr-2 text-gray-600"></i>
          <div>
            <span class="font-semibold">Địa chỉ nhận hàng:</span>
            <span class="ml-1">{{ donHang.diaChiNhanHang }}</span>
          </div>
        </div>

        <div class="flex items-start">
          <i class="fi fi-rr-money-bill-wave mt-1 mr-2 text-gray-600"></i>
          <div>
            <span class="font-semibold">Tổng tiền:</span>
            <span class="ml-1">{{ formatCurrency(donHang.tongTien) }}</span>
          </div>
        </div>

        <div class="flex items-start">
          <i class="fi fi-rr-credit-card mt-1 mr-2 text-gray-600"></i>
          <div>
            <span class="font-semibold">Trạng thái thanh toán:</span>
            <span class="ml-1">{{ donHang.trangThaiThanhToan?.label }}</span>
          </div>
        </div>
      </div>

      <!-- Bên phải -->
      <div class="flex flex-col justify-between items-start md:items-end">
        <div class="flex items-start">
          <i class="fi fi-rr-phone-call mt-1 mr-2 text-gray-600"></i>
          <div>
            <span class="font-semibold">SĐT nhận hàng:</span>
            <span class="ml-1">{{ donHang.sdtNhanHang }}</span>
          </div>
        </div>

        <button
          @click="$emit('xemChiTiet', donHang)"
          class="text-blue-600 text-sm hover:underline mt-2 md:mt-0"
        >
          Xem chi tiết
        </button>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import type { DonHangGiaoHangResponse } from "@/types/giaohang.types";

defineProps<{
  donHang: DonHangGiaoHangResponse;
}>();
defineEmits<{
  (e: "xemChiTiet", donHang: DonHangGiaoHangResponse): void;
}>();
const formatCurrency = (value: number): string => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(value);
};
</script>
