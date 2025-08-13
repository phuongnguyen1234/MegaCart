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

//ko nho la cho view/component nao :(
/*export interface ChiTietDonHang {
  maChiTietDonHang: number;
  maDonHang: number;
  tenSanPham: string;
  anhMinhHoa: string;
  nhaSanXuat: string;
  donGia: number;
  donVi: string;
  soLuong: number;
} */
