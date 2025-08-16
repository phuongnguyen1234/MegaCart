import apiClient from "./apiClient";
import type {
  PageableParams,
  PagedResponse,
  SortDirection,
} from "@/types/api.types";
import { NhanSanPhamKey } from "@/types/sanpham.types";
import type { FilterDataResponse } from "@/types/filter.types";
import type {
  SanPhamResponse,
  ChiTietSanPhamResponse,
  TimKiemFilterParams,
  FilterParams,
} from "@/types/sanpham.types";

export { NhanSanPhamKey };

// --- Helper Functions (Optional but Recommended) ---

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
  return `${String(property)},${direction}`;
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
 * Lấy các tùy chọn bộ lọc (nhà sản xuất, khoảng giá) dựa trên các tiêu chí hiện tại.
 * @param filters - Các tiêu chí lọc như slug danh mục, từ khóa, v.v.
 * @returns Promise chứa dữ liệu cho bộ lọc.
 */
export const getFilterOptions = (
  filters: FilterParams
): Promise<FilterDataResponse> => {
  // Endpoint này được định nghĩa trong FilterController
  return apiClient.get("/filter-options", { params: filters });
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
 * @deprecated Endpoint /api/san-pham/theo-danh-muc/{maDanhMuc} đã bị vô hiệu hóa ở backend.
 */
// export const getSanPhamTheoDanhMuc = (
//   maDanhMuc: number,
//   pageable: PageableParams
// ): Promise<PagedResponse<SanPhamResponse>> => {
//   return apiClient.get(`/san-pham/theo-danh-muc/${maDanhMuc}`, {
//     params: pageable,
//   });
// };

/**
 * Lấy danh sách sản phẩm theo nhãn (Mới, Bán chạy,...).
 */
export const getSanPhamTheoNhan = (
  nhan: NhanSanPhamKey,
  filters: TimKiemFilterParams,
  pageable: PageableParams
): Promise<PagedResponse<SanPhamResponse>> => {
  // Sử dụng endpoint tìm kiếm chung với bộ lọc `nhan`
  const allFilters: TimKiemFilterParams = { ...filters, nhan };
  return timKiemVaLocSanPham(allFilters, pageable);
};

/**
 * Lấy danh sách sản phẩm bán chạy nhất.
 */
export const getSanPhamBanChay = (
  filters: TimKiemFilterParams,
  pageable: PageableParams
): Promise<PagedResponse<SanPhamResponse>> => {
  // Sử dụng endpoint tìm kiếm chung với bộ lọc `banChay`
  const allFilters: TimKiemFilterParams = { ...filters, banChay: true };
  return timKiemVaLocSanPham(allFilters, pageable);
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
