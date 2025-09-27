<template>
  <CustomerNoNav>
    <!-- Modal xác nhận xóa sản phẩm -->
    <ConfirmModal
      :hien-thi="isDeleteItemConfirmVisible"
      tieu-de="Xác nhận xóa sản phẩm"
      noi-dung="Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng không?"
      :dang-tai="isDeletingItem"
      @xac-nhan="handleConfirmXoaSanPham"
      @huy="isDeleteItemConfirmVisible = false"
    />

    <!-- Modal xác nhận xóa giỏ hàng -->
    <ConfirmModal
      :hien-thi="isResetCartConfirmVisible"
      tieu-de="Xác nhận đặt lại giỏ hàng"
      noi-dung="Bạn có chắc chắn muốn xóa tất cả sản phẩm khỏi giỏ hàng không?
        Hành động này không thể hoàn tác."
      :dang-tai="isResettingCart"
      @xac-nhan="handleConfirmResetCart"
      @huy="isResetCartConfirmVisible = false"
    />

    <!-- Modal xác nhận -->
    <XacNhanDonHangModal
      v-if="isConfirmModalVisible"
      :visible="isConfirmModalVisible"
      :danhSachSanPham="sanPhamDaChon"
      :thongTin="thongTinXacNhan"
      :tongTien="tongTien"
      :dang-tai="isPlacingOrder"
      @close="isConfirmModalVisible = false"
      @xacNhan="handleXacNhanDatHang"
    />

    <div class="max-w-6xl mx-auto p-6 mt-6">
      <!-- Giao diện khi giỏ hàng có sản phẩm -->
      <div v-if="isLoading" class="text-center py-20">
        <div
          class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"
        ></div>
        <p class="mt-4 text-gray-600">Đang tải giỏ hàng...</p>
      </div>

      <div
        v-else-if="!isLoading && cartItems.length > 0"
        class="grid grid-cols-1 md:grid-cols-2 gap-6"
      >
        <!-- Giỏ hàng -->
        <div>
          <h2 class="text-xl font-bold text-center mb-4">GIỎ HÀNG</h2>

          <div class="flex items-center justify-between mb-2 text-sm">
            <span
              >Đã chọn
              <strong
                >{{ selectedItems.size }}/{{ availableItems.length }}</strong
              >
              sản phẩm</span
            >
            <label class="flex items-center gap-1">
              <input
                type="checkbox"
                v-model="chonTatCa"
                class="cursor-pointer accent-blue-500"
              />
              Chọn tất cả
            </label>
          </div>

          <div class="space-y-4 max-h-[400px] overflow-y-auto pr-2">
            <CardSanPhamGioHang
              v-for="sp in cartItems"
              :key="sp.maSanPham"
              :sanPham="sp"
              :isChecked="selectedItems.has(sp.maSanPham)"
              :disabled="sp.trangThai.value !== TrangThaiSanPhamKey.BAN"
              @chon="(isChecked:boolean) => toggleChon(sp.maSanPham, isChecked)"
              @thay-doi-so-luong="
                (soLuong:number) => handleCapNhatSoLuong(sp.maSanPham, soLuong)
              "
              @xoa="handleXoaSanPham(sp.maSanPham)"
            />
          </div>

          <div class="text-right mt-2">
            <a
              href="#"
              @click.prevent="datLaiGioHang"
              class="text-red-600 text-sm hover:underline cursor-pointer"
              >Đặt lại giỏ hàng</a
            >
          </div>
        </div>

        <!-- Thông tin giao hàng -->
        <div>
          <h2 class="text-xl text-center font-bold mb-4">
            THÔNG TIN GIAO HÀNG
          </h2>

          <div class="mb-3">
            <label class="flex items-center font-medium mb-1">
              <i class="fi fi-rr-user w-5 mr-2"></i>
              <span>Tên người nhận</span>
            </label>
            <input
              type="text"
              v-model="thongTinGiaoHangForm.tenNguoiNhan"
              class="w-full border border-gray-300 rounded px-3 py-2 bg-white disabled:bg-gray-100 disabled:opacity-70 disabled:cursor-not-allowed focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
              :disabled="suDungThongTinMacDinh"
            />
          </div>

          <div class="mb-3">
            <label class="flex items-center font-medium mb-1">
              <i class="fi fi-rr-marker w-5 mr-2"></i>
              <span>Địa chỉ nhận hàng</span>
            </label>
            <textarea
              v-model="thongTinGiaoHangForm.diaChi"
              rows="3"
              class="w-full border border-gray-300 rounded px-3 py-2 bg-white disabled:bg-gray-100 disabled:opacity-70 disabled:cursor-not-allowed focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 resize-none"
              :disabled="suDungThongTinMacDinh"
            >
            </textarea>
          </div>

          <div class="mb-3">
            <label class="flex items-center font-medium mb-1">
              <i class="fi fi-rr-phone-call w-5 mr-2"></i>
              <span>Số điện thoại nhận hàng</span>
            </label>
            <input
              type="text"
              v-model="thongTinGiaoHangForm.soDienThoai"
              class="w-full border border-gray-300 rounded px-3 py-2 bg-white disabled:bg-gray-100 disabled:opacity-70 disabled:cursor-not-allowed focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
              :disabled="suDungThongTinMacDinh"
            />
          </div>

          <div class="mb-3 flex items-center">
            <input
              type="checkbox"
              id="sudungtk"
              v-model="suDungThongTinMacDinh"
              class="mr-2 cursor-pointer accent-blue-500"
              @change="toggleSuDungThongTinMacDinh"
            />
            <label for="sudungtk" class="cursor-pointer"
              >Sử dụng thông tin tài khoản</label
            >
          </div>

          <div class="mb-3">
            <label class="flex items-center font-medium mb-1">
              <i class="fi fi-rr-truck-side w-5 mr-2"></i>
              <span>Hình thức nhận hàng</span>
            </label>
            <select
              v-model="hinhThucNhanHang"
              class="w-full border border-gray-300 rounded px-3 py-2 cursor-pointer bg-white focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
            >
              <option :value="HinhThucNhanHangRequest.GIAO_HANG_TAN_NHA">
                Giao hàng tận nhà
              </option>
            </select>
          </div>

          <div class="mb-3">
            <label class="flex items-center font-medium mb-1">
              <i class="fi fi-rr-credit-card w-5 mr-2"></i>
              <span>Hình thức thanh toán</span>
            </label>
            <select
              v-model="hinhThucThanhToan"
              class="w-full border border-gray-300 rounded px-3 py-2 cursor-pointer bg-white focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500"
            >
              <option
                :value="HinhThucThanhToanRequest.THANH_TOAN_KHI_NHAN_HANG"
              >
                Thanh toán khi nhận hàng
              </option>
            </select>
          </div>

          <div
            class="mt-4 flex justify-between items-center font-semibold text-lg"
          >
            <span>Tổng tiền:</span>
            <span>{{ tongTien.toLocaleString("vi-VN") }} VND</span>
          </div>

          <div class="mt-6 text-center">
            <button
              class="bg-blue-500 text-white px-6 py-2 rounded hover:bg-blue-600 cursor-pointer disabled:bg-gray-400 disabled:cursor-not-allowed"
              :disabled="selectedItems.size === 0"
              @click="handleDatHang"
            >
              Đặt hàng
            </button>
          </div>
        </div>
      </div>

      <!-- Giao diện khi giỏ hàng trống -->
      <div
        v-else
        class="flex flex-col items-center justify-center text-center py-16"
      >
        <i class="fi fi-rr-shopping-cart text-8xl text-gray-300 mb-4"></i>
        <h3 class="text-xl font-semibold mb-4">Giỏ hàng hiện đang trống</h3>
        <button
          @click="goToHome"
          class="px-6 py-2 rounded text-white bg-blue-600 hover:bg-blue-700 cursor-pointer"
        >
          Quay về Trang chủ
        </button>
      </div>
    </div>
  </CustomerNoNav>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useToast } from "@/composables/useToast";
import CustomerNoNav from "@/components/layouts/CustomerNoNav.vue";
import CardSanPhamGioHang from "@/components/dathang/CardSanPhamGioHang.vue";
import XacNhanDonHangModal from "@/components/dathang/XacNhanDonHangModal.vue";
import ConfirmModal from "@/components/base/modals/ConfirmModal.vue";
import {
  getThongTinThanhToan,
  capNhatSoLuong,
  xoaKhoiGioHang,
  xoaToanBoGioHang,
} from "@/service/giohang.service";
import { taoDonHang } from "@/service/dathang.service";
import type {
  GioHangItem,
  ThongTinGiaoHangMacDinh,
} from "@/types/giohang.types";
import {
  HinhThucNhanHangRequest,
  HinhThucThanhToanRequest,
  type DatHangRequest,
} from "@/types/dathang.types";
import { TrangThaiSanPhamKey } from "@/types/sanpham.types";
import { useCartStore } from "@/store/giohang.store";

const router = useRouter();
const { showToast } = useToast();
const cartStore = useCartStore();

// --- State ---
const isLoading = ref(true);
const cartItems = ref<GioHangItem[]>([]);
const thongTinGiaoHangMacDinh = ref<ThongTinGiaoHangMacDinh | null>(null);
const isConfirmModalVisible = ref(false);
const isResetCartConfirmVisible = ref(false);
const isResettingCart = ref(false); // Trạng thái loading cho việc xóa giỏ hàng
const isDeleteItemConfirmVisible = ref(false);
const isDeletingItem = ref(false);
const itemToDeleteId = ref<number | null>(null);
const isPlacingOrder = ref(false); // Trạng thái loading cho việc đặt hàng
const selectedItems = ref(new Set<number>());

const thongTinGiaoHangForm = ref({
  tenNguoiNhan: "",
  diaChi: "",
  soDienThoai: "",
});

const suDungThongTinMacDinh = ref(true);
const hinhThucNhanHang = ref(HinhThucNhanHangRequest.GIAO_HANG_TAN_NHA);
const hinhThucThanhToan = ref(
  HinhThucThanhToanRequest.THANH_TOAN_KHI_NHAN_HANG
);

// --- Computed Properties ---

const availableItems = computed(() =>
  cartItems.value.filter(
    (item) => item.trangThai.value === TrangThaiSanPhamKey.BAN
  )
);

const chonTatCa = computed({
  get: () => {
    // Chỉ có thể "chọn tất cả" nếu có sản phẩm và tất cả sản phẩm có thể mua đã được chọn
    return (
      availableItems.value.length > 0 &&
      selectedItems.value.size === availableItems.value.length
    );
  },
  set: (value) => {
    if (value) {
      // Chỉ chọn những sản phẩm đang được bán
      availableItems.value.forEach((item) =>
        selectedItems.value.add(item.maSanPham)
      );
    } else {
      selectedItems.value.clear();
    }
  },
});

const sanPhamDaChon = computed(() =>
  cartItems.value.filter((item) => selectedItems.value.has(item.maSanPham))
);

const tongTien = computed(() =>
  sanPhamDaChon.value.reduce((total, item) => {
    return total + item.thanhTien;
  }, 0)
);

const thongTinXacNhan = computed(() => {
  // Định dạng lại dữ liệu để phù hợp với prop `thongTin` của modal
  // Dựa trên cấu trúc dữ liệu ở các modal khác (ví dụ: ChiTietDonHangModal)
  return {
    tenNguoiNhan: thongTinGiaoHangForm.value.tenNguoiNhan,
    soDienThoai: thongTinGiaoHangForm.value.soDienThoai,
    diaChi: thongTinGiaoHangForm.value.diaChi,
    giaoHang:
      hinhThucNhanHang.value === HinhThucNhanHangRequest.GIAO_HANG_TAN_NHA
        ? "Giao hàng tận nhà"
        : "",
    thanhToan:
      hinhThucThanhToan.value ===
      HinhThucThanhToanRequest.THANH_TOAN_KHI_NHAN_HANG
        ? "Thanh toán khi nhận hàng"
        : "",
  };
});

// --- Methods ---

const fetchCartData = async () => {
  try {
    isLoading.value = true;
    const response = await getThongTinThanhToan();
    cartItems.value = response.items;
    thongTinGiaoHangMacDinh.value = response.thongTinGiaoHangMacDinh;

    // Tự động chọn tất cả sản phẩm CÓ THỂ MUA khi tải trang
    response.items.forEach((item) => {
      if (item.trangThai.value === TrangThaiSanPhamKey.BAN)
        selectedItems.value.add(item.maSanPham);
    });

    // Điền thông tin giao hàng mặc định vào form
    if (response.thongTinGiaoHangMacDinh) {
      thongTinGiaoHangForm.value.tenNguoiNhan =
        response.thongTinGiaoHangMacDinh.tenKhachHang;
      thongTinGiaoHangForm.value.diaChi =
        response.thongTinGiaoHangMacDinh.diaChi;
      thongTinGiaoHangForm.value.soDienThoai =
        response.thongTinGiaoHangMacDinh.soDienThoai;
    }
  } catch (error) {
    console.error("Lỗi khi tải giỏ hàng:", error);
    showToast({ thongBao: "Không thể tải giỏ hàng.", loai: "loi" });
  } finally {
    isLoading.value = false;
  }
};

const toggleChon = (maSanPham: number, isChecked: boolean) => {
  if (isChecked) {
    selectedItems.value.add(maSanPham);
  } else {
    selectedItems.value.delete(maSanPham);
  }
};

const handleCapNhatSoLuong = async (maSanPham: number, soLuong: number) => {
  try {
    const response = await capNhatSoLuong(maSanPham, { soLuong });
    cartItems.value = response.items;
    cartStore.setCartCount(response.tongSoLuongSanPham);
    showToast({ thongBao: "Cập nhật số lượng thành công.", loai: "thanhCong" });
  } catch (error) {
    showToast({ thongBao: "Cập nhật số lượng thất bại.", loai: "loi" });
    // Tải lại dữ liệu để đảm bảo đồng bộ
    await fetchCartData();
  }
};

const handleXoaSanPham = (maSanPham: number) => {
  itemToDeleteId.value = maSanPham;
  isDeleteItemConfirmVisible.value = true;
};

const handleConfirmXoaSanPham = async () => {
  if (!itemToDeleteId.value) return;

  isDeletingItem.value = true;
  try {
    const response = await xoaKhoiGioHang(itemToDeleteId.value);
    // Cập nhật lại state từ frontend để có trải nghiệm mượt hơn
    cartItems.value = cartItems.value.filter(
      (item) => item.maSanPham !== itemToDeleteId.value
    );
    selectedItems.value.delete(itemToDeleteId.value);
    cartStore.setCartCount(response.tongSoLuongSanPham);
    showToast({ thongBao: response.message, loai: "thanhCong" });
    isDeleteItemConfirmVisible.value = false;
  } catch (error) {
    showToast({ thongBao: "Xóa sản phẩm thất bại.", loai: "loi" });
  } finally {
    isDeletingItem.value = false;
    itemToDeleteId.value = null;
  }
};

const datLaiGioHang = () => {
  // Chỉ hiển thị modal xác nhận
  isResetCartConfirmVisible.value = true;
};

const handleConfirmResetCart = async () => {
  isResettingCart.value = true;
  try {
    await xoaToanBoGioHang();
    cartItems.value = [];
    selectedItems.value.clear();
    cartStore.clearCartCount();
    showToast({ thongBao: "Đã xóa toàn bộ giỏ hàng.", loai: "thanhCong" });
    isResetCartConfirmVisible.value = false;
  } catch (error) {
    showToast({ thongBao: "Xóa giỏ hàng thất bại.", loai: "loi" });
  } finally {
    isResettingCart.value = false;
  }
};

const toggleSuDungThongTinMacDinh = () => {
  // Nếu người dùng chọn sử dụng thông tin mặc định, hãy điền lại thông tin đó vào form.
  if (suDungThongTinMacDinh.value && thongTinGiaoHangMacDinh.value) {
    thongTinGiaoHangForm.value.tenNguoiNhan =
      thongTinGiaoHangMacDinh.value.tenKhachHang;
    thongTinGiaoHangForm.value.diaChi = thongTinGiaoHangMacDinh.value.diaChi;
    thongTinGiaoHangForm.value.soDienThoai =
      thongTinGiaoHangMacDinh.value.soDienThoai;
  }
  // Khi bỏ chọn, không làm gì cả để giữ lại dữ liệu cho người dùng chỉnh sửa.
};

const handleDatHang = () => {
  if (selectedItems.value.size === 0) {
    showToast({
      thongBao: "Vui lòng chọn ít nhất một sản phẩm để đặt hàng.",
      loai: "loi",
    });
    return;
  }

  if (
    !thongTinGiaoHangForm.value.tenNguoiNhan ||
    !thongTinGiaoHangForm.value.diaChi ||
    !thongTinGiaoHangForm.value.soDienThoai
  ) {
    showToast({
      thongBao: "Vui lòng điền đầy đủ thông tin giao hàng.",
      loai: "loi",
    });
    return;
  }

  isConfirmModalVisible.value = true;
};

const handleXacNhanDatHang = async () => {
  isPlacingOrder.value = true;
  try {
    const payload: DatHangRequest = {
      items: sanPhamDaChon.value.map((item) => ({
        maSanPham: item.maSanPham,
        soLuong: item.soLuong,
      })),
      tenNguoiNhan: thongTinGiaoHangForm.value.tenNguoiNhan,
      diaChiNhanHang: thongTinGiaoHangForm.value.diaChi,
      sdtNhanHang: thongTinGiaoHangForm.value.soDienThoai,

      hinhThucThanhToan: hinhThucThanhToan.value,
      hinhThucNhanHang: hinhThucNhanHang.value,
    };

    const response = await taoDonHang(payload);
    showToast({ thongBao: response.thongBao, loai: "thanhCong" });

    // Cập nhật lại số lượng giỏ hàng trên header
    await cartStore.fetchCartCount();

    // Đóng modal và chuyển hướng
    isConfirmModalVisible.value = false;
    router.push({ name: "LichSuMuaHang" }); // Giả sử bạn có route tên là 'LichSuMuaHang'
  } catch (error: any) {
    const message =
      error.response?.data?.message || "Đặt hàng thất bại. Vui lòng thử lại.";
    showToast({ thongBao: message, loai: "loi" });
  } finally {
    isPlacingOrder.value = false;
  }
};

const goToHome = () => {
  router.push({ name: "TrangChu" });
};

// --- Lifecycle Hooks ---
onMounted(() => {
  fetchCartData();
});
</script>
