<template>
  <CustomerNoNav>
    <!-- Modal xác nhận -->
    <XacNhanDonHangModal
      v-if="isConfirmModalVisible"
      :visible="isConfirmModalVisible"
      :danhSachSanPham="sanPhamDaChon"
      :thongTin="thongTinXacNhan"
      :tongTien="tongTien"
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
                class="cursor-pointer"
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
              :disabled="sp.trangThai.value !== TrangThaiSanPhamKey.DANG_BAN"
              @chon="(isChecked:boolean) => toggleChon(sp.maSanPham, isChecked)"
              @thay-doi-so-luong="
                (soLuong:number) => handleCapNhatSoLuong(sp.maSanPham, soLuong)
              "
              @xoa="() => handleXoaSanPham(sp.maSanPham)"
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
            <label class="block font-medium mb-1">Tên người nhận</label>
            <input
              type="text"
              v-model="thongTinGiaoHangForm.tenNguoiNhan"
              class="w-full border rounded px-3 py-2 disabled:bg-gray-100 disabled:opacity-70 disabled:cursor-not-allowed"
              :disabled="suDungThongTinMacDinh"
            />
          </div>

          <div class="mb-3">
            <label class="block font-medium mb-1">Địa chỉ nhận hàng</label>
            <input
              type="text"
              v-model="thongTinGiaoHangForm.diaChi"
              class="w-full border rounded px-3 py-2 disabled:bg-gray-100 disabled:opacity-70 disabled:cursor-not-allowed"
              :disabled="suDungThongTinMacDinh"
            />
          </div>

          <div class="mb-3">
            <label class="block font-medium mb-1"
              >Số điện thoại nhận hàng</label
            >
            <input
              type="text"
              v-model="thongTinGiaoHangForm.soDienThoai"
              class="w-full border rounded px-3 py-2 disabled:bg-gray-100 disabled:opacity-70 disabled:cursor-not-allowed"
              :disabled="suDungThongTinMacDinh"
            />
          </div>

          <div class="mb-3 flex items-center">
            <input
              type="checkbox"
              id="sudungtk"
              v-model="suDungThongTinMacDinh"
              class="mr-2 cursor-pointer"
              @change="toggleSuDungThongTinMacDinh"
            />
            <label for="sudungtk" class="cursor-pointer"
              >Sử dụng thông tin tài khoản</label
            >
          </div>

          <div class="mb-3">
            <label class="block font-medium mb-1">Hình thức nhận hàng</label>
            <select
              v-model="hinhThucNhanHang"
              class="w-full border rounded px-3 py-2 cursor-pointer bg-white"
            >
              <option value="giao-tan-nha">Giao hàng tận nhà</option>
            </select>
          </div>

          <div class="mb-3">
            <label class="block font-medium mb-1">Hình thức thanh toán</label>
            <select
              v-model="hinhThucThanhToan"
              class="w-full border rounded px-3 py-2 cursor-pointer bg-white"
            >
              <option value="cod">Thanh toán khi nhận hàng</option>
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
              class="bg-gray-800 text-white px-6 py-2 rounded hover:bg-gray-700 cursor-pointer disabled:bg-gray-400 disabled:cursor-not-allowed"
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
        <h3 class="text-xl font-semibold text-gray-700 mb-4">
          Giỏ hàng hiện đang trống
        </h3>
        <button
          @click="goToHome"
          class="bg-gray-800 text-white px-6 py-2 rounded hover:bg-gray-700"
        >
          Quay về trang chủ
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
import type { DatHangRequest } from "@/types/dathang.types";
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
const selectedItems = ref(new Set<number>());

const thongTinGiaoHangForm = ref({
  tenNguoiNhan: "",
  diaChi: "",
  soDienThoai: "",
});

const suDungThongTinMacDinh = ref(true);
const hinhThucNhanHang = ref("giao-tan-nha");
const hinhThucThanhToan = ref("cod");

// --- Computed Properties ---

const availableItems = computed(() =>
  cartItems.value.filter(
    (item) => item.trangThai.value === TrangThaiSanPhamKey.DANG_BAN
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
      hinhThucNhanHang.value === "GIAO_HANG_TAN_NHA" ? "Giao hàng tận nhà" : "",
    thanhToan:
      hinhThucThanhToan.value === "THANH_TOAN_KHI_NHAN_HANG"
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
      if (item.trangThai.value === TrangThaiSanPhamKey.DANG_BAN)
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

const handleXoaSanPham = async (maSanPham: number) => {
  try {
    const response = await xoaKhoiGioHang(maSanPham);
    // Cập nhật lại state từ frontend để có trải nghiệm mượt hơn
    cartItems.value = cartItems.value.filter(
      (item) => item.maSanPham !== maSanPham
    );
    selectedItems.value.delete(maSanPham);
    cartStore.setCartCount(response.tongSoLuongSanPham);
    showToast({ thongBao: response.message, loai: "thanhCong" });
  } catch (error) {
    showToast({ thongBao: "Xóa sản phẩm thất bại.", loai: "loi" });
  }
};

const datLaiGioHang = async () => {
  try {
    await xoaToanBoGioHang();
    cartItems.value = [];
    selectedItems.value.clear();
    cartStore.clearCartCount();
    showToast({ thongBao: "Đã xóa toàn bộ giỏ hàng.", loai: "thanhCong" });
  } catch (error) {
    showToast({ thongBao: "Xóa giỏ hàng thất bại.", loai: "loi" });
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
  try {
    const payload: DatHangRequest = {
      items: sanPhamDaChon.value.map((item) => ({
        maSanPham: item.maSanPham,
        soLuong: item.soLuong,
      })),
      tenNguoiNhan: thongTinGiaoHangForm.value.tenNguoiNhan,
      diaChiNhanHang: thongTinGiaoHangForm.value.diaChi,
      sdtNhanHang: thongTinGiaoHangForm.value.soDienThoai,

      hinhThucThanhToan: "THANH_TOAN_KHI_NHAN_HANG", // TODO: Cần làm động nếu có nhiều lựa chọn
      hinhThucNhanHang: "GIAO_HANG_TAN_NHA", // TODO: Cần làm động
    };

    console.log(payload);

    const response = await taoDonHang(payload);
    showToast({ thongBao: response.message, loai: "thanhCong" });

    // Cập nhật lại số lượng giỏ hàng trên header
    await cartStore.fetchCartCount();

    // Đóng modal và chuyển hướng
    isConfirmModalVisible.value = false;
    router.push({ name: "LichSuMuaHang" }); // Giả sử bạn có route tên là 'LichSuMuaHang'
  } catch (error: any) {
    const message =
      error.response?.data?.message || "Đặt hàng thất bại. Vui lòng thử lại.";
    showToast({ thongBao: message, loai: "loi" });
    isConfirmModalVisible.value = false;
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
