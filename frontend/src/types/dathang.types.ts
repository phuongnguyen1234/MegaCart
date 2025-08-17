/**
 * Represents the payment method keys for an order creation request.
 * These values must match what the backend `DatHangRequest` DTO expects.
 */
export enum HinhThucThanhToanRequest {
  THANH_TOAN_KHI_NHAN_HANG = "THANH_TOAN_KHI_NHAN_HANG",
  // Add other payment methods like VNPAY if available
}

/**
 * Represents the delivery method keys for an order creation request.
 */
export enum HinhThucNhanHangRequest {
  GIAO_HANG_TAN_NHA = "GIAO_HANG_TAN_NHA",
}

export interface SanPhamDatHang {
  maSanPham: number;
  soLuong: number;
}

export interface DatHangRequest {
  items: SanPhamDatHang[];
  tenNguoiNhan: string;
  diaChiNhanHang: string;
  sdtNhanHang: string;
  hinhThucThanhToan: HinhThucThanhToanRequest;
  hinhThucNhanHang: HinhThucNhanHangRequest;
}

export interface DatHangResponse {
  maDonHang: string;
  thongBao: string;
  // Thêm các thuộc tính khác nếu API trả về
}
