export interface KhachHang {
  maKhachHang: string;
  tenKhachHang: string;
  email: string;
  diaChi: string;
  soDienThoai: string;
  trangThai: "Hoạt động" | "Ngừng hoạt động";
}

export interface DuLieuCapNhatKhachHang {
  maKhachHang: string;
  trangThai: "Hoạt động" | "Ngừng hoạt động";
}

export interface NhanVien {
  maNhanVien: string;
  hoTen: string;
  email: string;
  soDienThoai: string;
  viTri: "Quản lí đơn" | "Giao hàng" | "Quản lí kho";
  trangThai: "Hoạt động" | "Khóa";
}
