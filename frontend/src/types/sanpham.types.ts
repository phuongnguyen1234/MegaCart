/**
 * Các nhãn sản phẩm, tương ứng với NhanSanPham.java trên backend.
 */
export enum NhanSanPham {
  MOI = "MOI",
  BAN_CHAY = "BAN_CHAY",
  KHUYEN_MAI = "KHUYEN_MAI",
}

/**
 * Dữ liệu tóm tắt của một sản phẩm, dùng trong danh sách.
 */
export interface SanPhamResponse {
  maSanPham: number;
  tenSanPham: string;
  anhMinhHoaChinh: string;
  donGia: number;
  giaKhuyenMai?: number;
  nhan?: NhanSanPham;
}

/**
 * Dữ liệu chi tiết của một sản phẩm.
 */
export interface ChiTietSanPhamResponse extends SanPhamResponse {
  moTa: string;
  nhaSanXuat: string;
  soLuongTon: number;
  danhGiaTrungBinh: number;
  soLuongDaBan: number;
  tenDanhMuc: string;
}

/**
 * Các tham số để lọc và tìm kiếm sản phẩm.
 */
export interface TimKiemFilterParams {
  tuKhoa?: string;
  maDanhMuc?: number;
  giaToiDa?: number;
  nhaSanXuat?: string;
}
