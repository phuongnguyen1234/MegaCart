/**
 * Các nhãn sản phẩm, tương ứng với NhanSanPham.java trên backend.
 */
export enum NhanSanPham {
  MOI = "MOI",
  BAN_CHAY = "BAN_CHAY",
  KHUYEN_MAI = "KHUYEN_MAI",
}

export enum TrangThaiTonKho {
  CON_HANG = "CON_HANG",
  HET_HANG = "HET_HANG",
}

export interface AnhMinhHoa {
  duongDan: string;
  laAnhChinh: boolean;
}

/**
 * Dữ liệu cho breadcrumbs, tương ứng với BreadcrumbItem.java
 */
export interface BreadcrumbItem {
  text: string;
  to?: string;
}

/**
 * Dữ liệu cơ bản của một sản phẩm, chứa các trường chung.
 */
interface SanPhamBase {
  maSanPham: number;
  tenSanPham: string;
  donGia: number;
  donVi: string;
  nhaSanXuat: string;
  trangThaiTonKho: TrangThaiTonKho;
  nhan?: NhanSanPham;
}

/**
 * Dữ liệu tóm tắt của một sản phẩm, dùng trong danh sách.
 * Tương ứng với `SanPhamResponse.java` trên backend.
 */
export interface SanPhamResponse extends SanPhamBase {
  anhMinhHoaChinh: string;
}

/**
 * Dữ liệu chi tiết của một sản phẩm.
 * Tương ứng với `ChiTietSanPhamResponse.java` trên backend.
 */
export interface ChiTietSanPhamResponse extends SanPhamBase {
  moTa: string;
  ghiChu: string;
  anhMinhHoas: AnhMinhHoa[];
  breadcrumbs: BreadcrumbItem[];
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
