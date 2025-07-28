<template>
  <CustomerWithNav>
    <ThemVaoGioHang
      :visible="isThemVaoGioHangModalVisible"
      :sanPham="sanPhamDuocChon"
      @close="dongThemVaoGioHangModal"
      @add="handleThemVaoGioHang"
    />
    <BannerSlider />

    <ListSanPham
      tieuDe="Sản phẩm mới"
      :dsSanPham="dsSanPhamMoi"
      linkXemThem="/san-pham-moi"
      @themVaoGioHang="hienThiThemVaoGioHangModal"
    />

    <ListSanPham
      tieuDe="Bán chạy nhất"
      :dsSanPham="dsBanChay"
      linkXemThem="/ban-chay-nhat"
      @themVaoGioHang="hienThiThemVaoGioHangModal"
    />
  </CustomerWithNav>
</template>

<script setup lang="ts">
import { ref } from "vue";
import CustomerWithNav from "@/components/layouts/CustomerWithNav.vue";
import BannerSlider from "@/components/trangchu/BannerSlider.vue";
import { useToast } from "@/composables/useToast";
import type { SanPham } from "@/types/SanPham.ts";
import ThemVaoGioHang from "@/components/base/ThemVaoGioHang.vue";
import ListSanPham from "@/components/base/ListSanPham.vue";

const { showToast } = useToast();

// Mock data sản phẩm mới
const dsSanPhamMoi: SanPham[] = [
  {
    maSanPham: 1,
    tenSanPham: "Giày cao gót",
    nhan: "Mới",
    donGia: 200000,
    anhMinhHoa: ["https://picsum.photos/300?random=1"],
    donVi: "Đôi",
    nhaSanXuat: "Township",
    danhMucCha: "Quần áo",
    danhMucCon: "Giày dép",
  },
  {
    maSanPham: 2,
    tenSanPham: "Áo sơ mi nam",
    nhan: "Mới",
    donGia: 350000,
    anhMinhHoa: ["https://picsum.photos/300?random=2"],
    donVi: "Cái",
    nhaSanXuat: "Urban Outfit",
    danhMucCha: "Quần áo",
    danhMucCon: "Áo sơ mi",
  },
  {
    maSanPham: 5,
    tenSanPham: "Áo sơ mi nam",
    nhan: "Mới",
    donGia: 350000,
    anhMinhHoa: ["https://picsum.photos/300?random=2"],
    donVi: "Cái",
    nhaSanXuat: "Urban Outfit",
    danhMucCha: "Quần áo",
    danhMucCon: "Áo sơ mi",
  },
  {
    maSanPham: 6,
    tenSanPham: "Áo sơ mi nam",
    nhan: "Mới",
    donGia: 350000,
    anhMinhHoa: ["https://picsum.photos/300?random=2"],
    donVi: "Cái",
    nhaSanXuat: "Urban Outfit",
    danhMucCha: "Quần áo",
    danhMucCon: "Áo sơ mi",
  },
  {
    maSanPham: 7,
    tenSanPham: "Áo sơ mi nam",
    nhan: "Mới",
    donGia: 350000,
    anhMinhHoa: ["https://picsum.photos/300?random=2"],
    donVi: "Cái",
    nhaSanXuat: "Urban Outfit",
    danhMucCha: "Quần áo",
    danhMucCon: "Áo sơ mi",
  },
];

// Mock data bán chạy
const dsBanChay = [
  {
    maSanPham: 3,
    tenSanPham: "Balo thời trang",
    nhan: "Bán chạy",
    donGia: 400000,
    anhMinhHoa: ["https://picsum.photos/300?random=3"],
    donVi: "Cái",
    nhaSanXuat: "GearBag",
    danhMucCha: "Khác",
    danhMucCon: "Balo và túi xách",
  },
  {
    maSanPham: 4,
    tenSanPham: "Quần jeans nữ",
    donGia: 290000,
    anhMinhHoa: ["https://picsum.photos/300?random=4"],
    donVi: "Cái",
    nhaSanXuat: "DenimX",
    danhMucCha: "Quần áo",
    danhMucCon: "Quần jeans",
  },
];

const isThemVaoGioHangModalVisible = ref(false);
const sanPhamDuocChon = ref<SanPham | null>(null);

const hienThiThemVaoGioHangModal = (product: SanPham) => {
  sanPhamDuocChon.value = product;
  isThemVaoGioHangModalVisible.value = true;
};

const dongThemVaoGioHangModal = () => {
  isThemVaoGioHangModalVisible.value = false;
  sanPhamDuocChon.value = null;
};

const handleThemVaoGioHang = (payload: {
  sanPham: SanPham;
  soLuong: number;
}) => {
  console.log(
    "🛒 Thêm sản phẩm vào giỏ:",
    payload.sanPham,
    "Số lượng:",
    payload.soLuong
  );
  dongThemVaoGioHangModal();
  showToast({
    thongBao: `Đã thêm "${payload.soLuong} ${payload.sanPham.tenSanPham}" vào giỏ hàng!`,
    loai: "thanhCong",
  });
};
</script>
