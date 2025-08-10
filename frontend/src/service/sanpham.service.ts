import apiClient from "./apiClient";

// --- Enums and Interfaces ---

/**
 * Các nhãn sản phẩm, tương ứng với NhanSanPham.java trên backend.
 */
export enum NhanSanPham {
  MOI = "MOI",
  BAN_CHAY = "BAN_CHAY",
  KHUYEN_MAI = "KHUYEN_MAI",
}

/**
 * Dữ liệu phân trang gửi lên API.
 */
export interface PageableParams {
  page?: number;
  size?: number;
  sort?: string; // Ví dụ: "tenSanPham,asc" hoặc "donGia,desc"
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

/**
 * Cấu trúc phản hồi chung cho các API trả về danh sách có phân trang.
 */
export interface PagedResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number; // Trang hiện tại (bắt đầu từ 0)
  last: boolean;
  first: boolean;
}

/**
 * Dữ liệu tóm tắt của một sản phẩm, dùng trong danh sách.
 */
export interface SanPhamResponse {
  maSanPham: number;
  tenSanPham: string;
  hinhAnh: string;
  donGia: number;
  giaKhuyenMai?: number;
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

// --- Helper Functions (Optional but Recommended) ---

/**
 * Hướng sắp xếp.
 */
export type SortDirection = "asc" | "desc";

/**
 * Tạo chuỗi sắp xếp một cách an toàn để tránh lỗi chính tả.
 * @param property - Tên thuộc tính cần sắp xếp (phải là một key của SanPhamResponse).
 * @param direction - Hướng sắp xếp ('asc' hoặc 'desc').
 * @returns Chuỗi sắp xếp hợp lệ, ví dụ: "donGia,desc".
 */
export const createSortString = (
  property: keyof SanPhamResponse,
  direction: SortDirection
): string => {
  return `${property},${direction}`;
};

// --- Service Functions ---

/**
 * Lấy gợi ý tìm kiếm dựa trên từ khóa.
 * @param tuKhoa - Từ khóa người dùng nhập.
 * @returns Promise chứa danh sách các chuỗi gợi ý.
 */
export const goiYTimKiem = (tuKhoa: string): Promise<string[]> => {
  return apiClient.get("/san-pham/goi-y", { params: { tuKhoa } });
};

/**
 * Tìm kiếm và lọc sản phẩm với nhiều tiêu chí và phân trang.
 * @param filters - Các tiêu chí lọc.
 * @param pageable - Thông tin phân trang.
 * @returns Promise chứa danh sách sản phẩm đã phân trang.
 */
export const timKiemVaLocSanPham = (
  filters: TimKiemFilterParams,
  pageable: PageableParams
): Promise<PagedResponse<SanPhamResponse>> => {
  const params = { ...filters, ...pageable };
  return apiClient.get("/san-pham/tim-kiem", { params });
};

/**
 * Lấy danh sách sản phẩm theo danh mục.
 */
export const getSanPhamTheoDanhMuc = (
  maDanhMuc: number,
  pageable: PageableParams
): Promise<PagedResponse<SanPhamResponse>> => {
  return apiClient.get(`/san-pham/theo-danh-muc/${maDanhMuc}`, {
    params: pageable,
  });
};

/**
 * Lấy danh sách sản phẩm theo nhãn (Mới, Bán chạy,...).
 */
export const getSanPhamTheoNhan = (
  nhan: NhanSanPham,
  pageable: PageableParams
): Promise<PagedResponse<SanPhamResponse>> => {
  return apiClient.get("/san-pham/theo-nhan", {
    params: { nhan, ...pageable },
  });
};

/**
 * Lấy danh sách sản phẩm bán chạy nhất.
 */
export const getSanPhamBanChay = (
  pageable: PageableParams
): Promise<PagedResponse<SanPhamResponse>> => {
  return apiClient.get("/san-pham/ban-chay", { params: pageable });
};

/**
 * Lấy thông tin chi tiết của một sản phẩm.
 * @param maSanPham - Mã của sản phẩm cần xem.
 * @returns Promise chứa thông tin chi tiết sản phẩm.
 */
export const getChiTietSanPham = (
  maSanPham: number
): Promise<ChiTietSanPhamResponse> => {
  return apiClient.get(`/san-pham/${maSanPham}`);
};
