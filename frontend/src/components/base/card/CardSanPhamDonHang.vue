<template>
  <div class="flex items-start gap-4 rounded-lg bg-white p-3 shadow">
    <!-- Hình ảnh -->
    <component
      :is="isManagementMode ? 'div' : 'router-link'"
      :to="isManagementMode ? undefined : `/san-pham/${sanPhamData.maSanPham}`"
      class="shrink-0"
    >
      <img
        :src="displayImage"
        alt="Ảnh sản phẩm"
        class="h-20 w-20 rounded-md object-cover"
      />
    </component>

    <!-- Thông tin sản phẩm -->
    <div class="flex-grow text-sm">
      <component
        :is="isManagementMode ? 'span' : 'router-link'"
        :to="
          isManagementMode ? undefined : `/san-pham/${sanPhamData.maSanPham}`
        "
        class="font-semibold text-gray-800"
        :class="{ 'hover:text-blue-600 hover:underline': !isManagementMode }"
      >
        {{ sanPhamData.tenSanPham }}
      </component>
      <div class="text-gray-600">{{ formatPrice(sanPhamData.donGia) }}</div>
      <div class="text-gray-500">Số lượng: {{ sanPhamData.soLuong }}</div>

      <!-- Trạng thái: Ngừng kinh doanh (ưu tiên cao nhất) -->
      <div
        v-if="
          sanPhamData.trangThaiSanPham?.value === TrangThaiSanPhamKey.KHONG_BAN
        "
        class="mt-1 text-xs font-semibold text-red-600"
      >
        <i class="fi fi-rr-ban mr-1"></i
        >{{ sanPhamData.trangThaiSanPham.label }}
      </div>
      <!-- Trạng thái: Hết hàng -->
      <div
        v-else-if="
          sanPhamData.trangThaiTonKho?.value === TrangThaiTonKhoKey.HET_HANG
        "
        class="mt-1 text-xs font-semibold text-orange-500"
      >
        <i class="fi fi-rr-box-open mr-1"></i
        >{{ sanPhamData.trangThaiTonKho.label }}
      </div>
    </div>

    <!-- Thành tiền -->
    <div class="shrink-0 text-right font-bold text-red-600">
      {{ formatPrice(sanPhamData.donGia * sanPhamData.soLuong) }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import type { ChiTietDonHangItem } from "@/types/donhang.types";
import { TrangThaiSanPhamKey, TrangThaiTonKhoKey } from "@/types/sanpham.types";

// Định nghĩa một kiểu dữ liệu mở rộng để TypeScript hiểu được thuộc tính mới
interface TransformedChiTietDonHangItem extends ChiTietDonHangItem {
  anhMinhHoaChinh?: string;
}

const props = withDefaults(
  defineProps<{
    sanPham: ChiTietDonHangItem;
    isManagementMode?: boolean;
    isDeliveryStatus?: boolean;
  }>(),
  {
    isManagementMode: false,
    isDeliveryStatus: false,
  }
);

// Tạo một computed property để xử lý việc "map" dữ liệu.
// Việc này giúp giữ cho logic được tập trung và template được sạch sẽ.
const sanPhamData = computed((): TransformedChiTietDonHangItem => {
  // Nếu ở trạng thái giao hàng, tạo một object mới
  // và map giá trị từ 'anhMinhHoa' sang key mới là 'anhMinhHoaChinh'.
  if (props.isDeliveryStatus) {
    return {
      ...props.sanPham,
      anhMinhHoaChinh: props.sanPham.anhMinhHoa,
    };
  }
  // Nếu không, trả về object sản phẩm gốc.
  return props.sanPham;
});

const displayImage = computed(() => {
  // Giờ đây, logic chọn ảnh có thể dựa vào 'sanPhamData' đã được xử lý.
  // Nếu 'anhMinhHoaChinh' tồn tại trên object đã map, sử dụng nó.
  // Nếu không, quay về 'anhMinhHoa' mặc định.
  return sanPhamData.value.anhMinhHoaChinh || sanPhamData.value.anhMinhHoa;
});

const formatPrice = (price: number) => {
  return price.toLocaleString("vi-VN") + " VND";
};
</script>
