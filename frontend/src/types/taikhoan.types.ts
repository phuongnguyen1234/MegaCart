/**
 * Represents the status of a customer account.
 * The values are the display labels used in the UI and expected by the API.
 */
export enum TrangThaiKhachHang {
  HOAT_DONG = "Hoạt động",
  NGUNG_HOAT_DONG = "Ngừng hoạt động",
}

/**
 * Represents the role/position of an employee.
 */
export enum ViTriNhanVien {
  QUAN_LI_DON = "Quản lí đơn",
  GIAO_HANG = "Giao hàng",
  QUAN_LI_KHO = "Quản lí kho",
}

/**
 * Represents the status of an employee account.
 */
export enum TrangThaiNhanVien {
  HOAT_DONG = "Hoạt động",
  KHOA = "Khóa",
}

export interface KhachHang {
  maKhachHang: string;
  tenKhachHang: string;
  email: string;
  diaChi: string;
  soDienThoai: string;
  trangThai: TrangThaiKhachHang;
}

export interface DuLieuCapNhatKhachHang {
  maKhachHang: string;
  trangThai: TrangThaiKhachHang;
}

export interface NhanVien {
  maNhanVien: string;
  hoTen: string;
  email: string;
  soDienThoai: string;
  viTri: ViTriNhanVien;
  trangThai: TrangThaiNhanVien;
}
