<template>
  <div
    class="border rounded p-4 flex flex-col md:flex-row justify-between gap-4"
  >
    <!-- Trái -->
    <div class="flex items-start gap-4">
      <div class="w-16 h-16 bg-gray-200 rounded"></div>
      <div>
        <p class="font-bold text-sm">#{{ donHang.ma }}</p>
        <p class="text-blue-600 font-medium">
          {{ donHang.sanPhamTieuBieu.ten }}
        </p>

        <p class="text-sm flex items-center gap-1">
          <span class="text-xs">🛍️</span> Số lượng:
          {{ donHang.sanPhamTieuBieu.soLuong }}
        </p>
        <p class="text-sm">+{{ donHang.soSanPhamConLai }} sản phẩm khác</p>
      </div>
    </div>

    <!-- Phải -->
    <div class="text-sm text-right">
      <p><strong>Tổng tiền:</strong> {{ formatPrice(donHang.tongTien) }} VND</p>
      <p><strong>Thời gian đặt:</strong> {{ donHang.thoiGianDat }}</p>
      <p>
        <a href="#" class="text-blue-600 hover:underline">Xem chi tiết</a>
      </p>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  donHang: Object,
});

const formatPrice = (vnd) => vnd.toLocaleString();
</script>
