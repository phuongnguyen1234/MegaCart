<template>
  <BaseModal
    :visible="visible"
    :title="tieuDeModal"
    @close="dongModal"
    width-class="w-[550px] max-w-full"
  >
    <div v-if="donHang" class="space-y-4 text-sm">
      <!-- Danh sách sản phẩm -->
      <div class="overflow-y-auto mb-2 px-2 py-1 space-y-2 max-h-60">
        <CardSanPhamDonHang
          v-for="sp in danhSachSanPham"
          :key="sp.maSanPham"
          :sanPham="sp"
        />
      </div>

      <!-- Tổng tiền -->
      <div class="text-right font-semibold text-base">
        Tổng tiền: {{ tongTien.toLocaleString() }} VND
      </div>

      <!-- Thông tin người nhận -->
      <div class="space-y-2 border-t pt-3">
        <div class="flex items-start gap-2">
          <i class="fi fi-rr-user mt-1"></i>
          <span><strong>Tên người nhận:</strong> {{ thongTin.ten }}</span>
        </div>
        <div class="flex items-start gap-2">
          <i class="fi fi-rr-phone-call mt-1"></i>
          <span><strong>SĐT:</strong> {{ thongTin.soDienThoai }}</span>
        </div>
        <div class="flex items-start gap-2">
          <i class="fi fi-rr-marker mt-1"></i>
          <span><strong>Địa chỉ:</strong> {{ thongTin.diaChi }}</span>
        </div>
        <div class="flex items-start gap-2">
          <i class="fi fi-rr-truck mt-1"></i>
          <span><strong>Giao hàng:</strong> {{ thongTin.giaoHang }}</span>
        </div>
        <div class="flex items-start gap-2">
          <i class="fi fi-rr-money-bill mt-1"></i>
          <span><strong>Thanh toán:</strong> {{ thongTin.thanhToan }}</span>
        </div>
      </div>

      <!-- Trạng thái giao hàng -->
      <div class="border-t pt-3 space-y-2">
        <h3 class="font-semibold">Trạng thái giao hàng</h3>
        <div class="flex gap-4">
          <label class="flex items-center gap-2">
            <input type="radio" value="thanhCong" v-model="trangThaiGiao" />
            Thành công
          </label>
          <label class="flex items-center gap-2">
            <input type="radio" value="thatBai" v-model="trangThaiGiao" />
            Thất bại
          </label>
        </div>

        <div v-if="trangThaiGiao === 'thatBai'">
          <label class="block text-gray-700 mb-1">Lý do thất bại</label>
          <textarea
            v-model="lyDoThatBai"
            rows="3"
            class="w-full p-2 border rounded resize-none"
            placeholder="Nhập lý do..."
          ></textarea>
        </div>
      </div>

      <!-- Trạng thái thanh toán -->
      <div class="border-t pt-3 space-y-2">
        <h3 class="font-semibold">Trạng thái thanh toán</h3>
        <div class="flex gap-4">
          <label class="flex items-center gap-2">
            <input type="radio" value="chua" v-model="trangThaiThanhToan" />
            Chưa thanh toán
          </label>
          <label class="flex items-center gap-2">
            <input type="radio" value="da" v-model="trangThaiThanhToan" />
            Đã thanh toán
          </label>
        </div>
      </div>

      <!-- Footer -->
      <div class="pt-4 border-t flex justify-end">
        <button
          @click="xacNhanGiaoHang"
          class="px-5 py-2 bg-green-600 text-white rounded hover:bg-green-700 transition"
        >
          Xác nhận
        </button>
      </div>
    </div>
  </BaseModal>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import BaseModal from "../base/modals/BaseModal.vue";
import CardSanPhamDonHang from "../base/card/CardSanPhamDonHang.vue";
import type { DonHang } from "@/types/DonHang";

interface Props {
  visible: boolean;
  donHang: DonHang | null;
}

const props = withDefaults(defineProps<Props>(), {
  visible: false,
  donHang: null,
});

const emit = defineEmits(["close", "xacNhan"]);

const dongModal = () => emit("close");

// Trạng thái giao hàng
const trangThaiGiao = ref<"thanhCong" | "thatBai" | "">("");
const lyDoThatBai = ref("");

// Trạng thái thanh toán
const trangThaiThanhToan = ref<"chua" | "da">("chua");

// Xác nhận gửi đi
const xacNhanGiaoHang = () => {
  if (!trangThaiGiao.value) {
    alert("Vui lòng chọn trạng thái giao hàng.");
    return;
  }
  if (trangThaiGiao.value === "thatBai" && !lyDoThatBai.value.trim()) {
    alert("Vui lòng nhập lý do giao hàng thất bại.");
    return;
  }

  emit("xacNhan", {
    maDonHang: props.donHang?.maDonHang,
    giaoHang: trangThaiGiao.value,
    lyDo: lyDoThatBai.value,
    thanhToan: trangThaiThanhToan.value,
  });

  // Reset + đóng modal
  trangThaiGiao.value = "";
  lyDoThatBai.value = "";
  trangThaiThanhToan.value = "chua";
  dongModal();
};

const tieuDeModal = computed(() =>
  props.donHang
    ? `Chi tiết đơn hàng #${props.donHang.maDonHang}`
    : "Chi tiết đơn hàng"
);

const danhSachSanPham = computed(
  () =>
    props.donHang?.chiTietDonHang?.map((sp) => ({
      maSanPham: sp.maSanPham,
      ten: sp.tenSanPham,
      donGia: sp.gia,
      soLuong: sp.soLuong,
      hinhAnh: sp.hinhAnh,
    })) || []
);

const tongTien = computed(() => props.donHang?.tongTien || 0);

const thongTin = computed(() => {
  const d = props.donHang;
  return {
    ten: d?.tenNguoiNhan || "",
    soDienThoai: d?.sdtNhanHang || "",
    diaChi: d?.diaChiNhanHang || "",
    giaoHang: d?.hinhThucNhanHang || "",
    thanhToan: d?.hinhThucThanhToan || "",
  };
});
</script>
