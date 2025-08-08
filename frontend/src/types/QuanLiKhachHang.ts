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
