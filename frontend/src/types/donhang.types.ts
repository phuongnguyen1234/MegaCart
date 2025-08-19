import type { EnumObject, PageableParams } from "./api.types";
import type {
  TrangThaiSanPhamObject,
  TrangThaiTonKhoObject,
} from "./sanpham.types";

/**
 * Represents the canonical keys for order statuses, matching the backend's enum constant names.
 * Used in the `TrangThaiDonHangObject` received from the API.
 * Example: `TrangThaiDonHang.DANG_GIAO` in Java becomes "DANG_GIAO" here.
 */
export enum TrangThaiDonHangKey {
  CHO_XAC_NHAN = "CHO_XAC_NHAN",
  CHO_XU_LY = "CHO_XU_LY",
  DANG_GIAO = "DANG_GIAO",
  DA_GIAO = "DA_GIAO",
  DA_HUY = "DA_HUY",
}

/**
 * Represents the display labels for order statuses.
 */
export const TrangThaiDonHangLabel: Record<TrangThaiDonHangKey, string> = {
  [TrangThaiDonHangKey.CHO_XAC_NHAN]: "Chờ xác nhận",
  [TrangThaiDonHangKey.CHO_XU_LY]: "Chờ xử lý",
  [TrangThaiDonHangKey.DANG_GIAO]: "Đang giao",
  [TrangThaiDonHangKey.DA_GIAO]: "Đã giao",
  [TrangThaiDonHangKey.DA_HUY]: "Đã hủy",
};

/**
 * =================================================================
 * FOR ADMIN PANEL (QUAN LY DON HANG)
 * =================================================================
 */

/**
 * Defines the parameters for fetching the admin order list.
 * Corresponds to the @RequestParam in `QuanLyDonHangController.getDSDonHang`.
 */
export interface GetDonHangQuanLyParams extends PageableParams {
  searchField?: "maDonHang" | "tenKhachHang";
  searchValue?: string;
  trangThai?: TrangThaiDonHangKey;
  ngayDat?: string; // ISO Date string, e.g., "YYYY-MM-DD"
}

/**
 * Represents an order summary in the admin list view.
 * Corresponds to `DonHangQuanLyResponse` in the backend.
 */
export interface DonHangQuanLyResponse {
  maDonHang: number;
  tenKhachHang: string;
  thoiGianDatHang: string; // ISO DateTime string
  trangThai: TrangThaiDonHangObject;
  tongTien: number;
}

/**
 * Represents the detailed information of a single order for the admin panel.
 * Corresponds to `ChiTietDonHangQuanLyResponse` in the backend.
 */
export interface ChiTietDonHangQuanLyResponse {
  maDonHang: number;
  tenNguoiNhan: string;
  sdtNhanHang: string;
  diaChiNhanHang: string;
  thoiGianDatHang: string; // ISO DateTime string
  trangThai: TrangThaiDonHangObject;
  hinhThucGiaoHang: HinhThucGiaoHangObject;
  hinhThucThanhToan: HinhThucThanhToanObject;
  trangThaiThanhToan: TrangThaiThanhToanObject;
  tongTien: number;
  duKienGiaoHang?: string; // ISO DateTime string, optional
  ghiChu?: string; // Optional
  items: ChiTietDonHangItem[];
}

/**
 * Represents the request payload for updating an order from the admin panel.
 * Corresponds to `CapNhatDonHangRequest` in the backend.
 */
export type CapNhatDonHangRequest = Partial<
  Pick<ChiTietDonHangQuanLyResponse, "ghiChu" | "duKienGiaoHang"> & {
    trangThai: TrangThaiDonHangKey;
    trangThaiThanhToan: TrangThaiThanhToanKey;
  }
>;

/**
 * Represents the structure of an order status object returned from the API.
 * The backend serializes the `TrangThaiDonHang` enum into this object.
 */
export type TrangThaiDonHangObject = EnumObject<TrangThaiDonHangKey>;

/**
 * Represents the canonical keys for delivery methods.
 */
export enum HinhThucGiaoHangKey {
  GIAO_HANG_TAN_NHA = "GIAO_HANG_TAN_NHA",
}

/**
 * Represents a delivery method object from the API.
 */
export type HinhThucGiaoHangObject = EnumObject<HinhThucGiaoHangKey>;

/**
 * Represents the canonical keys for payment methods.
 */
export enum HinhThucThanhToanKey {
  THANH_TOAN_KHI_NHAN_HANG = "THANH_TOAN_KHI_NHAN_HANG", // COD
}

/**
 * Represents a payment method object from the API.
 */
export type HinhThucThanhToanObject = EnumObject<HinhThucThanhToanKey>;

/**
 * Represents the canonical keys for payment statuses.
 */
export enum TrangThaiThanhToanKey {
  CHUA_THANH_TOAN = "CHUA_THANH_TOAN",
  DA_THANH_TOAN = "DA_THANH_TOAN",
}

/**
 * Represents the display labels for payment statuses.
 */
export const TrangThaiThanhToanLabel: Record<TrangThaiThanhToanKey, string> = {
  [TrangThaiThanhToanKey.CHUA_THANH_TOAN]: "Chưa thanh toán",
  [TrangThaiThanhToanKey.DA_THANH_TOAN]: "Đã thanh toán",
};

/**
 * Represents a payment status object from the API.
 */
export type TrangThaiThanhToanObject = EnumObject<TrangThaiThanhToanKey>;

/**
 * Represents an item within a detailed order view.
 * Corresponds to `ChiTietDonHangResponse.ItemResponse` in the backend.
 */
export interface ChiTietDonHangItem {
  maSanPham: number;
  tenSanPham: string;
  anhMinhHoaChinh: string;
  donGia: number;
  soLuong: number;
  /**
   * The business status of the product at the time of viewing the order (e.g., Discontinued).
   * Corresponds to the `TrangThaiSanPham` enum from the backend.
   */
  trangThaiSanPham?: TrangThaiSanPhamObject;
  /**
   * The stock status of the product at the time of viewing the order (e.g., Out of Stock).
   * Corresponds to the `TrangThaiTonKho` enum from the backend.
   */
  trangThaiTonKho?: TrangThaiTonKhoObject;
}

/**
 * Represents the detailed information of a single order.
 * Corresponds to `ChiTietDonHangResponse` in the backend.
 */
export interface ChiTietDonHang {
  maDonHang: number;
  tenNguoiNhan: string;
  sdtNhanHang: string;
  diaChiNhanHang: string;
  thoiGianDatHang: string; // ISO DateTime string
  trangThai: TrangThaiDonHangObject;
  hinhThucGiaoHang: HinhThucGiaoHangObject;
  hinhThucThanhToan: HinhThucThanhToanObject;
  trangThaiThanhToan: TrangThaiThanhToanObject;
  tongTien: number;
  duKienGiaoHang?: string; // ISO DateTime string, optional
  ghiChu?: string; // Optional
  thoiGianThanhToan?: string; // ISO DateTime string, optional
  items: ChiTietDonHangItem[];
}

/**
 * Represents a summary of an order for the order history list.
 * Corresponds to `LichSuDonHangResponse` in the backend.
 */
export interface LichSuDonHang {
  maDonHang: number;
  trangThai: TrangThaiDonHangObject;
  thoiGianDatHang: string; // ISO DateTime string
  tongTien: number;
  tenSanPhamDauTien: string;
  anhMinhHoaDauTien: string;
  soLuongDauTien: number;
  banChayDauTien: boolean;
  soLuongLoaiSanPhamKhac?: number; // Optional
}

/**
 * Represents the request payload for cancelling an order.
 * Corresponds to `HuyDonHangRequest` in the backend.
 */
export interface HuyDonHangRequest {
  lyDo: string; // The reason for cancellation as a plain string.
}

/**
 * Represents the string values used for filtering order history via API request parameters.
 * These `UPPER_SNAKE_CASE` values are what `LichSuMuaHangView.vue` uses and what the backend
 * `DonHangController` expects for the `@RequestParam("trangThai")`.
 * Spring Boot automatically maps the string "DANG_GIAO" to the `TrangThaiDonHang.DANG_GIAO` enum constant.
 */
export enum TrangThaiDonHangFilter {
  CHO_XAC_NHAN = "CHO_XAC_NHAN",
  CHO_XU_LY = "CHO_XU_LY",
  DANG_GIAO = "DANG_GIAO",
  DA_GIAO = "DA_GIAO",
  DA_HUY = "DA_HUY",
}
