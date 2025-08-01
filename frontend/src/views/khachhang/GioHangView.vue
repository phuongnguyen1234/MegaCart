<template>
  <CustomerNoNav>
    <!-- Modal xác nhận -->
    <XacNhanDonHangModal
      v-if="isConfirmModalVisible"
      :visible="isConfirmModalVisible"
      :danhSachSanPham="sanPhamDaChon"
      :thongTin="thongTinGiaoHang"
      :tongTien="tongTien"
      @close="isConfirmModalVisible = false"
      @xacNhan="handleXacNhanDatHang"
    />

    <div class="max-w-6xl mx-auto p-6 mt-6">
      <!-- Giao diện khi giỏ hàng có sản phẩm -->
      <div
        v-if="sanPhamTrongGio.length > 0"
        class="grid grid-cols-1 md:grid-cols-2 gap-6"
      >
        <!-- Giỏ hàng -->
        <div>
          <h2 class="text-xl font-bold text-center mb-4">GIỎ HÀNG</h2>

          <div class="flex items-center justify-between mb-2 text-sm">
            <span
              >Đã chọn
              <strong>{{ selectedCount }}/{{ sanPhamTrongGio.length }}</strong>
              sản phẩm</span
            >
            <label class="flex items-center gap-1">
              <input type="checkbox" v-model="chonTatCa" />
              Chọn tất cả
            </label>
          </div>

          <div class="space-y-4 max-h-[400px] overflow-y-auto pr-2">
            <CardSanPhamGioHang
              v-for="(sp, index) in sanPhamTrongGio"
              :key="index"
              :sanPham="sp"
              @chon="capNhatChon(index, $event)"
              @thayDoiSoLuong="capNhatSoLuong(index, $event)"
              @xoa="xoaSanPham(index)"
            />
          </div>

          <div class="text-right mt-2">
            <a
              href="#"
              @click.prevent="datLaiGioHang"
              class="text-blue-600 text-sm hover:underline cursor-pointer"
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
              v-model="nguoiNhan"
              class="w-full border rounded px-3 py-2"
              :disabled="suDungThongTinTaiKhoan"
            />
          </div>

          <div class="mb-3">
            <label class="block font-medium mb-1">Địa chỉ nhận hàng</label>
            <input
              type="text"
              v-model="diaChiNhan"
              class="w-full border rounded px-3 py-2"
              :disabled="suDungThongTinTaiKhoan"
            />
          </div>

          <div class="mb-3">
            <label class="block font-medium mb-1"
              >Số điện thoại nhận hàng</label
            >
            <input
              type="text"
              v-model="soDienThoai"
              class="w-full border rounded px-3 py-2"
              :disabled="suDungThongTinTaiKhoan"
            />
          </div>

          <div class="mb-3 flex items-center">
            <input
              type="checkbox"
              id="sudungtk"
              v-model="suDungThongTinTaiKhoan"
              class="mr-2 cursor-pointer"
              @change="layThongTinTaiKhoan"
            />
            <label for="sudungtk" class="cursor-pointer"
              >Sử dụng thông tin tài khoản</label
            >
          </div>

          <div class="mb-3">
            <label class="block font-medium mb-1">Hình thức nhận hàng</label>
            <select
              v-model="hinhThucNhanHang"
              class="w-full border rounded px-3 py-2 cursor-pointer"
            >
              <option value="giao-tan-nha">Giao hàng tận nhà</option>
            </select>
          </div>

          <div class="mb-3">
            <label class="block font-medium mb-1">Hình thức thanh toán</label>
            <select
              v-model="hinhThucThanhToan"
              class="w-full border rounded px-3 py-2 cursor-pointer"
            >
              <option value="cod">Thanh toán khi nhận hàng</option>
            </select>
          </div>

          <div
            class="mt-4 flex justify-between items-center font-semibold text-lg"
          >
            <span>Tổng tiền:</span>
            <span>{{ tongTien.toLocaleString() }} VND</span>
          </div>

          <div class="mt-6 text-center">
            <button
              class="bg-gray-800 text-white px-6 py-2 rounded hover:bg-gray-700 cursor-pointer"
              @click="datHang"
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

<script setup>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { useToast } from "@/composables/useToast";
import CustomerNoNav from "@/components/layouts/CustomerNoNav.vue";
import CardSanPhamGioHang from "@/components/base/card/CardSanPhamGioHang.vue";
import XacNhanDonHangModal from "@/components/base/modals/XacNhanDonHangModal.vue";

const router = useRouter();
const { showToast } = useToast();

const isConfirmModalVisible = ref(false);

const sanPhamTrongGio = ref([
  {
    id: 1,
    ten: "Bánh mì",
    donGia: 7000,
    donVi: "Cái",
    soLuong: 2,
    daChon: true,
    hinhAnh: "https://picsum.photos/100?random=1",
    danhMucCha: "thuc-pham",
    danhMucCon: "banh-mi",
  },
  {
    id: 2,
    ten: "Đường trắng",
    donGia: 23000,
    donVi: "Túi 1kg",
    soLuong: 1,
    daChon: false,
    hinhAnh: "https://picsum.photos/100?random=2",
    danhMucCha: "thuc-pham",
    danhMucCon: "gia-vi",
  },
  {
    id: 3,
    ten: "Trứng gà",
    donGia: 25000,
    donVi: "Hộp 12 quả",
    soLuong: 1,
    daChon: true,
    hinhAnh: "https://picsum.photos/100?random=3",
    danhMucCha: "thuc-pham",
    danhMucCon: "trung",
  },
]);

const chonTatCa = computed({
  // Getter: Trả về true nếu tất cả sản phẩm được chọn và giỏ hàng không rỗng
  get: () =>
    sanPhamTrongGio.value.length > 0 &&
    sanPhamTrongGio.value.every((sp) => sp.daChon),

  // Setter: Cập nhật trạng thái 'daChon' cho tất cả sản phẩm
  set: (value) => {
    sanPhamTrongGio.value.forEach((sp) => {
      sp.daChon = value;
    });
  },
});

const capNhatChon = (index, value) => {
  sanPhamTrongGio.value[index].daChon = value;
};

const capNhatSoLuong = (index, newSL) => {
  sanPhamTrongGio.value[index].soLuong = newSL;
};

const xoaSanPham = (index) => {
  sanPhamTrongGio.value.splice(index, 1);
};

const datLaiGioHang = () => {
  sanPhamTrongGio.value = [];
  chonTatCa.value = false;
};

const selectedCount = computed(
  () => sanPhamTrongGio.value.filter((sp) => sp.daChon).length
);

const sanPhamDaChon = computed(() =>
  sanPhamTrongGio.value.filter((sp) => sp.daChon)
);

const tongTien = computed(() =>
  sanPhamTrongGio.value.reduce((total, sp) => {
    if (sp.daChon) total += sp.donGia * sp.soLuong;
    return total;
  }, 0)
);

const nguoiNhan = ref("Nguyễn Văn A");
const diaChiNhan = ref("12 phố A, phường B, quận C, thành phố D");
const soDienThoai = ref("09876543210");
const suDungThongTinTaiKhoan = ref(true);
const hinhThucNhanHang = ref("giao-tan-nha");
const hinhThucThanhToan = ref("cod");

const layThongTinTaiKhoan = () => {
  if (suDungThongTinTaiKhoan.value) {
    // Placeholder: Gọi API lấy thông tin tài khoản
    console.log("Đang lấy thông tin tài khoản từ backend...");
  }
};

const thongTinGiaoHang = computed(() => ({
  ten: nguoiNhan.value,
  soDienThoai: soDienThoai.value,
  diaChi: diaChiNhan.value,
  giaoHang:
    hinhThucNhanHang.value === "giao-tan-nha" ? "Giao hàng tận nhà" : "",
  thanhToan:
    hinhThucThanhToan.value === "cod" ? "Thanh toán khi nhận hàng" : "",
}));

const datHang = () => {
  // 1. Kiểm tra xem có sản phẩm nào được chọn không
  if (sanPhamDaChon.value.length === 0) {
    showToast({
      thongBao: "Vui lòng chọn ít nhất một sản phẩm để đặt hàng.",
      loai: "canhBao",
    });
    return;
  }

  // 2. Kiểm tra thông tin giao hàng đã được điền đầy đủ chưa
  if (!nguoiNhan.value || !diaChiNhan.value || !soDienThoai.value) {
    showToast({
      thongBao: "Vui lòng điền đầy đủ thông tin giao hàng.",
      loai: "canhBao",
    });
    return;
  }

  // 3. Nếu mọi thứ hợp lệ, hiển thị modal xác nhận
  isConfirmModalVisible.value = true;
};

const handleXacNhanDatHang = () => {
  isConfirmModalVisible.value = false;
  // Tại đây, bạn có thể gọi API để gửi đơn hàng lên server
  console.log("Đơn hàng đã được xác nhận và gửi đi!", {
    sanPham: sanPhamDaChon.value,
    thongTin: thongTinGiaoHang.value,
  });
  showToast({ thongBao: "Đặt hàng thành công!", loai: "thanhCong" });
  datLaiGioHang(); // Xóa giỏ hàng sau khi đặt thành công
  router.push("/lich-su-mua-hang"); // Chuyển về lịch sử mua hàng
};

const goToHome = () => {
  router.push("/trang-chu");
};
</script>
