/**
 * Represents the status of an employee account.
 */
export enum TrangThaiTaiKhoan {
  HOAT_DONG = "Hoạt động",
  KHOA = "Khóa",
}

export interface TaiKhoan {
  maTaiKhoan: string;
  email: string;
  soDienThoai: string;
  trangThaiTaiKhoan: TrangThaiTaiKhoan;
  vaiTro: string;
}
