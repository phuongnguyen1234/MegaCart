export interface SanPhamDatHang {
  maSanPham: number;
  soLuong: number;
}

export interface DatHangRequest {
  items: SanPhamDatHang[];
  tenNguoiNhan: string;
  diaChiNhanHang: string;
  sdtNhanHang: string;
  hinhThucThanhToan: string; // "COD"
  hinhThucNhanHang: string; // "GIAO_HANG_TAN_NHA"
}

export interface DatHangResponse {
  maDonHang: string;
  message: string;
  // Thêm các thuộc tính khác nếu API trả về
}
