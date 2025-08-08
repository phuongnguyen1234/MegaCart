<template>
  <Overlay :visible="visible" z-index="z-[1002]" @close="$emit('close')">
    <div
      class="fixed inset-0 bg-black/40 z-50 flex items-center justify-center"
    >
      <div
        class="bg-white w-[400px] max-h-[90vh] rounded-lg shadow relative p-4 flex flex-col"
      >
        <!-- Nút đóng -->
        <button
          class="cursor-pointer absolute top-2 right-2 text-2xl"
          @click="$emit('close')"
        >
          ×
        </button>

        <h2 class="text-center text-lg font-semibold mb-2">
          Xác nhận đặt hàng
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

        <!-- Thông tin người nhận -->
        <div class="text-sm space-y-2 mb-4">
          <div class="flex items-start gap-2">
            <i class="fi fi-rr-user mt-1"></i>
            <span><strong>Tên người nhận:</strong> {{ thongTin.ten }}</span>
          </div>
          <div class="flex items-start gap-2">
            <i class="fi fi-rr-phone-call mt-1"></i>
            <span
              ><strong>Số điện thoại nhận hàng:</strong>
              {{ thongTin.soDienThoai }}</span
            >
          </div>
          <div class="flex items-start gap-2">
            <i class="fi fi-rr-marker mt-1"></i>
            <span><strong>Địa chỉ:</strong> {{ thongTin.diaChi }}</span>
          </div>
          <div class="flex items-start gap-2">
            <i class="fi fi-rr-truck mt-1"></i>
            <span
              ><strong>Hình thức giao hàng:</strong>
              {{ thongTin.giaoHang }}</span
            >
          </div>
          <div class="flex items-start gap-2">
            <i class="fi fi-rr-money-bill mt-1"></i>
            <span
              ><strong>Hình thức thanh toán:</strong>
              {{ thongTin.thanhToan }}</span
            >
          </div>
        </div>

        <!-- Nút xác nhận -->
        <button
          class="bg-gray-700 hover:bg-gray-800 text-white py-2 rounded"
          @click="$emit('xacNhan')"
        >
          Xác nhận
        </button>
      </div>
    </div>
  </Overlay>
</template>
<script setup lang="ts">
import Overlay from "../base/Overlay.vue";
import CardSanPhamDonHang from "@/components/base/card/CardSanPhamDonHang.vue";

defineProps<{
  visible: boolean;
  danhSachSanPham: {
    id: number;
    ten: string;
    donGia: number;
    soLuong: number;
    hinhAnh: string;
  }[];
  thongTin: {
    ten: string;
    soDienThoai: string;
    diaChi: string;
    giaoHang: string;
    thanhToan: string;
  };
  tongTien: number;
}>();

defineEmits<{ (e: "close"): void; (e: "xacNhan"): void }>();
</script>
