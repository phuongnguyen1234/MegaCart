<template>
  <BaseModal
    :visible="visible"
    title="Xác nhận đặt hàng"
    width-class="w-[400px]"
    @close="$emit('close')"
  >
    <!-- Slot mặc định cho nội dung chính -->
    <!-- Danh sách sản phẩm -->
    <div class="max-h-[30vh] overflow-y-auto mb-4 space-y-2">
      <CardSanPhamDonHang
        v-for="sp in sanPhamForCard"
        :key="sp.maSanPham"
        :sanPham="sp"
      />
    </div>

    <!-- Tổng tiền -->
    <div class="text-right font-semibold mb-4">
      Tổng tiền: {{ tongTien.toLocaleString() }} VND
    </div>

    <!-- Thông tin người nhận -->
    <div class="text-sm space-y-2">
      <div class="flex items-start gap-2">
        <i class="fi fi-rr-user mt-1"></i>
        <span
          ><strong>Tên người nhận:</strong> {{ thongTin.tenNguoiNhan }}</span
        >
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
        <span><strong>Địa chỉ nhận hàng:</strong> {{ thongTin.diaChi }}</span>
      </div>
      <div class="flex items-start gap-2">
        <i class="fi fi-rr-truck-side mt-1"></i>
        <span
          ><strong>Hình thức giao hàng:</strong> {{ thongTin.giaoHang }}</span
        >
      </div>
      <div class="flex items-start gap-2">
        <i class="fi fi-rr-wallet mt-1"></i>
        <span
          ><strong>Hình thức thanh toán:</strong> {{ thongTin.thanhToan }}</span
        >
      </div>
    </div>

    <!-- Slot footer cho các nút hành động -->
    <template #footer>
      <div class="flex justify-end">
        <button
          class="cursor-pointer bg-blue-600 hover:bg-blue-700 text-white py-2 rounded w-full"
          @click="$emit('xacNhan')"
        >
          Xác nhận
        </button>
      </div>
    </template>
  </BaseModal>
</template>
<script setup lang="ts">
import { computed } from "vue";
import BaseModal from "@/components/base/modals/BaseModal.vue";
import CardSanPhamDonHang from "@/components/base/card/CardSanPhamDonHang.vue";
import type { GioHangItem } from "@/types/giohang.types";
import type { ChiTietDonHangItem } from "@/types/donhang.types";

const props = defineProps<{
  visible: boolean;
  danhSachSanPham: GioHangItem[];
  thongTin: {
    tenNguoiNhan: string;
    soDienThoai: string;
    diaChi: string;
    giaoHang: string;
    thanhToan: string;
  };
  tongTien: number;
}>();

defineEmits<{ (e: "close"): void; (e: "xacNhan"): void }>();

/**
 * Chuyển đổi dữ liệu từ `GioHangItem` sang một dạng tương thích với `ChiTietDonHangItem`
 * để component `CardSanPhamDonHang` có thể sử dụng.
 * Sự khác biệt chính là tên thuộc tính ảnh (`anhMinhHoa` vs `anhMinhHoaChinh`)
 * và tên thuộc tính trạng thái (`trangThai` vs `trangThaiSanPham`).
 */
const sanPhamForCard = computed(() => {
  return props.danhSachSanPham.map(
    (item) =>
      ({
        ...item,
        anhMinhHoaChinh: item.anhMinhHoa,
        trangThaiSanPham: item.trangThai,
      } as unknown as ChiTietDonHangItem) // Cast để TypeScript hài lòng
  );
});
</script>
