/**
 * Các key của nhãn sản phẩm, dùng để gửi lên API.
 * Giá trị của enum này phải TRÙNG KHỚP với tên của enum constant trong `NhanSanPham.java` ở backend.
 * Ví dụ: `MOI` trong TypeScript tương ứng với `NhanSanPham.MOI` trong Java.
 */
export enum NhanSanPhamKey {
  MOI = "MOI",
  // Thêm các key khác nếu có, ví dụ: BAN_CHAY = "BAN_CHAY"
}

/**
 * Ánh xạ từ key của nhãn sản phẩm sang tên hiển thị (label).
 * Hữu ích cho việc hiển thị trên UI mà không cần chờ dữ liệu từ API.
 */
export const NhanSanPhamLabel: Record<NhanSanPhamKey, string> = {
  [NhanSanPhamKey.MOI]: "Mới",
};

/**
 * Cấu trúc đối tượng nhãn sản phẩm nhận về từ API.
 * Backend serialize enum thành một object chứa key (value) và tên hiển thị (label).
 */
export interface NhanSanPhamObject {
  value: NhanSanPhamKey; // Key của enum, ví dụ: "MOI"
  label: string; // Nhãn hiển thị, ví dụ: "Mới"
}

/**
 * Các key của trạng thái tồn kho, dùng để nhận từ API.
 * Giá trị của enum này phải TRÙNG KHỚP với tên của enum constant trong `TrangThaiTonKho.java` ở backend.
 */
export enum TrangThaiTonKhoKey {
  CON_HANG = "CON_HANG",
  HET_HANG = "HET_HANG",
}

/**
 * Ánh xạ từ key của trạng thái tồn kho sang tên hiển thị (label).
 */
export const TrangThaiTonKhoLabel: Record<TrangThaiTonKhoKey, string> = {
  [TrangThaiTonKhoKey.CON_HANG]: "Còn hàng",
  [TrangThaiTonKhoKey.HET_HANG]: "Hết hàng",
};

/**
 * Cấu trúc đối tượng trạng thái tồn kho nhận về từ API.
 */
export interface TrangThaiTonKhoObject {
  value: TrangThaiTonKhoKey;
  label: string;
}

/**
 * @deprecated Alias for `TrangThaiTonKhoKey`. Provided for backward compatibility to fix import errors.
 * Please update imports from `TrangThaiTonKho` to `TrangThaiTonKhoKey`.
 * Also, note that product objects now use `TrangThaiTonKhoObject`, so you should check against `product.trangThaiTonKho.value`.
 */
export const TrangThaiTonKho = TrangThaiTonKhoKey;

/**
 * Các key của trạng thái kinh doanh sản phẩm (dành cho quản lý và kiểm tra logic).
 * Giá trị của enum này phải TRÙNG KHỚP với tên của enum constant trong `TrangThaiSanPham.java` ở backend.
 */
export enum TrangThaiSanPhamKey {
  DANG_BAN = "BAN", // Lưu ý: Backend đang dùng "BAN"
  NGUNG_BAN = "KHONG_BAN", // Lưu ý: Backend đang dùng "KHONG_BAN"
}

/**
 * Cấu trúc đối tượng trạng thái kinh doanh sản phẩm nhận về từ API.
 */
export interface TrangThaiSanPhamObject {
  value: TrangThaiSanPhamKey;
  label: string;
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
  trangThaiTonKho: TrangThaiTonKhoObject;
  nhan?: NhanSanPhamObject;
  banChay: boolean;
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
  giaToiThieu?: number;
  giaToiDa?: number;
  nhaSanXuat?: string;
  nhan?: NhanSanPhamKey;
  banChay?: boolean;
}

/**
 * Các tham số để lấy dữ liệu cho bộ lọc.
 * Tương ứng với các @RequestParam trong FilterController.java
 */
export interface FilterParams {
  danhMucSlug?: string;
  tuKhoa?: string;
  nhan?: NhanSanPhamKey;
  banChay?: boolean;
}
