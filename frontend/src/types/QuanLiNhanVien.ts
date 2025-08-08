export interface NhanVien {
  maNhanVien: string;
  hoTen: string;
  email: string;
  soDienThoai: string;
  viTri: "Quản lí đơn" | "Giao hàng" | "Quản lí kho";
  trangThai: "Hoạt động" | "Khóa";
}
