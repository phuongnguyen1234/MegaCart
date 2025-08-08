export interface ChiTietDonHang {
  maSanPham: string;
  tenSanPham: string;
  soLuong: number;
  gia: number;
  hinhAnh: string;
}

export interface DonHang {
  maDonHang: string;
  nguoiGiao: string;
  tenNguoiNhan: string;
  diaChiNhanHang: string;
  sdtNhanHang: string;
  tongTien: number;
  hinhThucThanhToan: "Thanh toán khi nhận hàng" | "Thanh toán online";
  hinhThucNhanHang: "Giao hàng tiêu chuẩn" | "Giao hàng nhanh";
  trangThaiDonHang: "Chờ xác nhận" | "Đang giao" | "Đã giao" | "Hủy";
  trangThaiThanhToan: "Chưa thanh toán" | "Đã thanh toán";
  thoiGianDatHang: string;
  thoiGianThanhToan: string;
  chiTietDonHang: ChiTietDonHang[];
}
