import type {
  TrangThaiSanPhamObject,
  TrangThaiTonKhoObject,
} from "./sanpham.types";
/**
 * A generic structure for enum-like objects returned from the backend.
 * Contains the enum constant name (value) and its display name (label).
 * @template T - The string literal type for the enum keys.
 */
export interface EnumObject<T extends string> {
  value: T;
  label: string;
}

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
  anhMinhHoa: string;
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
 * These kebab-case values are what `LichSuMuaHangView.vue` uses and what the backend
 * `DonHangController` expects for the `@RequestParam("trangThai")`.
 * Spring Boot automatically maps "dang-giao" to the `TrangThaiDonHang.DANG_GIAO` enum.
 */
export enum TrangThaiDonHangFilter {
  CHO_XAC_NHAN = "CHO_XAC_NHAN",
  CHO_XU_LY = "CHO_XU_LY",
  DANG_GIAO = "DANG_GIAO",
  DA_GIAO = "DA_GIAO",
  DA_HUY = "DA_HUY",
}
