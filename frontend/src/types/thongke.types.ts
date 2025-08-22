import { TrangThaiDonHangObject } from "./donhang.types";

/**
 * Dữ liệu thống kê tổng quan cho trang dashboard.
 * Tương ứng với `ThongKeTongQuanResponse` từ API.
 */
export interface ThongKeTongQuanResponse {
  doanhThu: {
    homNay: number;
    thangNay: number;
    tangTruongSoVoiThangTruoc?: number;
  };
  donHang: {
    homNay: number;
    thangNay: number;
    tangTruongSoVoiThangTruoc?: number;
    soDonDangGiao: number;
  };
}

export interface BieuDoDuongResponse {
  labels: string[];
  data: number[];
}

/**
 * Dữ liệu cho một lát cắt trong biểu đồ tròn (pie/doughnut chart).
 * Tương ứng với `BieuDoTronResponse` từ API.
 */
export interface BieuDoTronResponse {
  label: string;
  value: number;
}

/**
 * Dữ liệu về mục tiêu doanh thu của tháng.
 * Tương ứng với `MucTieuDoanhThuResponse` từ API.
 */
export interface MucTieuDoanhThuResponse {
  mucTieu: number;
  thucTe: number;
}

/**
 * Dữ liệu gửi đi khi cập nhật mục tiêu doanh thu.
 * Tương ứng với `CapNhatMucTieuDoanhThuRequest` từ API.
 */
export interface CapNhatMucTieuDoanhThuRequest {
  mucTieuMoi: number;
}

/**
 * Dữ liệu tóm tắt của một sản phẩm bán chạy.
 * Tương ứng với `SanPhamBanChayResponse` từ API.
 */
export interface SanPhamBanChayResponse {
  tenSanPham: string;
  soLuongDaBan: number;
}

/**
 * Dữ liệu tóm tắt của một sản phẩm có lượng tồn kho cao.
 * Tương ứng với `SanPhamTonKhoResponse` từ API.
 */
export interface SanPhamTonKhoResponse {
  tenSanPham: string;
  soLuongTon: number;
  soLuongDaBan: number;
}

/**
 * Dữ liệu chi tiết doanh thu theo từng ngày trong tháng.
 * Tương ứng với `ChiTietDoanhThuThangResponse` từ API.
 */
export interface ChiTietDoanhThuThangResponse {
  thang: string;
  mucTieu?: number;
  doanhThu?: number;
  tiLeDatMucTieu?: number;
  tangTruong?: number;
  trungBinhMoiDon?: number;
}

/**
 * Dữ liệu chi tiết đơn hàng theo từng ngày trong tháng.
 * Tương ứng với `ChiTietDonHangThangResponse` từ API.
 */
export interface ChiTietDonHangThangResponse {
  thang: string;
  soDonHang: number;
  tangTruong: number;
  trungBinhMoiDon: number;
  donGiaoThanhCong: ThongKeTiLe;
  donBiHuy: ThongKeTiLe;
}

type ThongKeTiLe = {
  soLuong: number;
  phanTram: number;
};

/**
 * Dữ liệu chi tiết của một sản phẩm bán chạy (dùng trong bảng chi tiết).
 * Tương ứng với `ChiTietSanPhamBanChayResponse` từ API.
 */
export interface ChiTietSanPhamBanChayResponse {
  maSanPham: number;
  tenSanPham: string;
  soLuongBanRa: number;
  soLuongTrungBinhMoiDon: number;
  soDonDat: number;
}

export interface DonHangGanDayResponse {
  maDonHang: number;
  tenKhachHang: string;
  thoiGianDatHang: string;
  trangThai: TrangThaiDonHangObject;
  tongTien: number;
}
