<template>
  <Overlay :visible="visible" z-index="z-[1002]" @close="$emit('close')">
    <!-- Main Modal Content -->
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

        <!-- Tiêu đề -->
        <h2 class="text-center text-lg font-semibold mb-2">
          {{
            dangHuy
              ? "Tại sao bạn muốn hủy đơn?"
              : `Chi tiết đơn hàng #${maDonHang}`
          }}
        </h2>

        <!-- Danh sách sản phẩm -->
        <div
          v-if="!dangHuy"
          class="flex-1 overflow-y-auto mb-4 px-2 py-1 space-y-2"
        >
          <CardSanPhamDonHang
            v-for="sp in danhSachSanPham"
            :key="sp.id"
            :sanPham="sp"
          />
        </div>

        <!-- Tổng tiền -->
        <div v-if="!dangHuy" class="text-right font-semibold mb-4">
          Tổng tiền: {{ tongTien.toLocaleString() }} VND
        </div>

        <!-- THÔNG TIN ĐƠN HÀNG -->
        <div v-if="!dangHuy" class="text-sm space-y-2 mb-4">
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
            <i class="fi fi-rr-info mt-1"></i>
            <span><strong>Trạng thái:</strong> {{ thongTin.trangThai }}</span>
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
          <div class="flex items-start gap-2">
            <i class="fi fi-rr-calendar-clock mt-1"></i>
            <span
              ><strong>Thời gian đặt:</strong> {{ thongTin.thoiGianDat }}</span
            >
          </div>
          <div class="flex items-start gap-2">
            <i class="fi fi-rr-shipping-fast mt-1"></i>
            <span
              ><strong>Dự kiến giao:</strong> {{ thongTin.duKienGiao }}</span
            >
          </div>
          <div v-if="thongTin.ghiChu" class="flex items-start gap-2">
            <i class="fi fi-rr-comment mt-1"></i>
            <span><strong>Ghi chú:</strong> {{ thongTin.ghiChu }}</span>
          </div>
        </div>

        <!-- LÝ DO HỦY ĐƠN -->
        <div v-if="dangHuy" class="text-sm">
          <button
            @click="quayVe"
            class="text-sm mb-2 text-blue-600 flex items-center gap-1"
          >
            <i class="fi fi-rr-arrow-left text-sm"></i> Quay về
          </button>

          <div class="space-y-2 mb-4">
            <label class="flex items-center gap-2">
              <input type="radio" value="khong-muon-mua" v-model="lyDoHuy" />
              Tôi không muốn mua nữa
            </label>
            <label class="flex items-center gap-2">
              <input
                type="radio"
                value="ghi-nham-thong-tin"
                v-model="lyDoHuy"
              />
              Tôi ghi nhầm thông tin giao hàng
            </label>
            <label class="flex items-center gap-2">
              <input type="radio" value="chon-nham" v-model="lyDoHuy" />
              Tôi chọn nhầm sản phẩm/số lượng
            </label>
            <label class="flex items-start gap-2">
              <input type="radio" value="ly-do-khac" v-model="lyDoHuy" />
              <div class="flex-1">
                Lý do khác (Ghi rõ):
                <textarea
                  rows="3"
                  class="w-full mt-1 border rounded px-2 py-1 text-sm resize-none"
                  placeholder="Nhập lý do cụ thể..."
                  v-model="ghiChuKhac"
                  :disabled="lyDoHuy !== 'ly-do-khac'"
                ></textarea>
              </div>
            </label>
          </div>

          <button
            class="w-full bg-red-600 hover:bg-red-700 text-white py-2 rounded"
            @click="askToConfirmHuyDon"
          >
            Xác nhận hủy
          </button>
        </div>

        <!-- NÚT HÀNH ĐỘNG -->
        <div v-if="!dangHuy" class="flex gap-2">
          <template v-if="thongTin.trangThai === 'Chờ xác nhận'">
            <button
              class="flex-1 bg-gray-700 hover:bg-gray-800 text-white py-2 rounded"
              @click="$emit('giaoPhanConLai')"
            >
              Giao phần còn lại
            </button>
            <button
              class="flex-1 bg-red-600 hover:bg-red-700 text-white py-2 rounded"
              @click="askToConfirmHuyDonHetHang"
            >
              Hủy đơn
            </button>
          </template>
          <template
            v-else-if="['Chờ xử lí', 'Đang giao'].includes(thongTin.trangThai)"
          >
            <button
              class="w-full bg-red-600 hover:bg-red-700 text-white py-2 rounded"
              @click="dangHuy = true"
            >
              Hủy đơn
            </button>
          </template>
        </div>
      </div>
    </div>
  </Overlay>

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
import { reactive, ref } from "vue";
import Overlay from "../Overlay.vue";
import ConfirmModal from "./ConfirmModal.vue";
import CardSanPhamDonHang from "@/components/base/card/CardSanPhamDonHang.vue";

const props = defineProps<{
  visible: boolean;
  danhSachSanPham: {
    id: number;
    ten: string;
    donGia: number;
    soLuong: number;
    hinhAnh: string;
  }[];
  maDonHang: string;
  thongTin: {
    ten: string;
    soDienThoai: string;
    diaChi: string;
    trangThai: string;
    giaoHang: string;
    thanhToan: string;
    thoiGianDat: string;
    duKienGiao: string;
    ghiChu?: string;
  };
  tongTien: number;
}>();

const emit = defineEmits<{
  (e: "close"): void;
  (e: "giaoPhanConLai"): void;
  (e: "huyDon", payload: { lyDo: string; ghiChu?: string }): void;
}>();

const dangHuy = ref(false);
const lyDoHuy = ref("khong-muon-mua");
const ghiChuKhac = ref("");

const quayVe = () => {
  dangHuy.value = false;
};

const confirmState = reactive({
  visible: false,
  title: "",
  content: "",
  onConfirm: () => {},
});

const hideConfirm = () => {
  confirmState.visible = false;
};

const askToConfirmHuyDon = () => {
  confirmState.title = "Xác nhận hủy đơn";
  confirmState.content = "Bạn có chắc chắn muốn hủy đơn hàng này không?";
  confirmState.onConfirm = () => {
    const payload = {
      lyDo: lyDoHuy.value,
      ghiChu: lyDoHuy.value === "ly-do-khac" ? ghiChuKhac.value : "",
    };
    emit("huyDon", payload);
    hideConfirm();
  };
  confirmState.visible = true;
};

const askToConfirmHuyDonHetHang = () => {
  confirmState.title = "Xác nhận hủy đơn";
  confirmState.content =
    "Bạn có chắc chắn muốn hủy đơn hàng này vì lý do hết hàng không?";
  confirmState.onConfirm = () => {
    emit("huyDon", { lyDo: "Hết hàng" });
    hideConfirm();
  };
  confirmState.visible = true;
};
</script>
