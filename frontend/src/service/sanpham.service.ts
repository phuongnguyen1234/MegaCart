import apiClient from "./apiClient";
import type {
  PageableParams,
  PagedResponse,
  SortDirection,
  MessageResponse,
} from "@/types/api.types";
import { NhanSanPhamKey } from "@/types/sanpham.types";
import type { FilterDataResponse } from "@/types/filter.types";
import type {
  SanPhamResponse,
  ChiTietSanPhamResponse,
  TimKiemFilterParams,
  FilterParams,
  GetSanPhamQuanLyParams,
  SanPhamQuanLyResponse,
  ChiTietSanPhamQuanLyResponse,
  ThemSanPhamRequest,
  ThemSanPhamAsyncResponse,
  CapNhatSanPhamRequest,
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

/**
 * =================================================================
 * FOR ADMIN PANEL (QUAN LY SAN PHAM)
 * =================================================================
 */

/**
 * Lấy danh sách sản phẩm cho trang quản lý, có phân trang và lọc.
 * Tương ứng với `GET /api/admin/san-pham`.
 * @param params - Các tham số lọc, tìm kiếm và phân trang.
 * @returns Promise chứa danh sách sản phẩm đã phân trang.
 */
export const getDanhSachSanPhamQuanLy = (
  params: GetSanPhamQuanLyParams
): Promise<PagedResponse<SanPhamQuanLyResponse>> => {
  return apiClient.get("/admin/san-pham", { params });
};

/**
 * Lấy thông tin chi tiết của một sản phẩm cho trang quản lý.
 * Tương ứng với `GET /api/admin/san-pham/{maSanPham}`.
 * @param maSanPham - Mã của sản phẩm cần xem.
 * @returns Promise chứa thông tin chi tiết sản phẩm.
 */
export const getChiTietSanPhamQuanLy = (
  maSanPham: number
): Promise<ChiTietSanPhamQuanLyResponse> => {
  return apiClient.get(`/admin/san-pham/${maSanPham}`);
};

/**
 * Helper function to create FormData for product creation/update.
 * This encapsulates the logic for combining JSON data and files into a single payload.
 * @private
 * @param data - The product data object (ThemSanPhamRequest or CapNhatSanPhamRequest).
 * @param files - An optional array of files to upload.
 * @returns A FormData object ready to be sent.
 */
function createSanPhamFormData(data: object, files?: File[]): FormData {
  const formData = new FormData();
  // Append the JSON part as a Blob to ensure correct Content-Type
  formData.append(
    "sanPham",
    new Blob([JSON.stringify(data)], { type: "application/json" })
  );
  if (files) {
    files.forEach((file) => formData.append("files", file));
  }
  return formData;
}
/**
 * Tạo một sản phẩm mới.
 * Gửi request multipart/form-data.
 * Tương ứng với `POST /api/admin/san-pham`.
 * @param data - Dữ liệu JSON của sản phẩm.
 * @param files - Danh sách các file ảnh.
 * @returns Promise chứa thông tin sản phẩm vừa tạo.
 */
export const themSanPham = (
  data: ThemSanPhamRequest,
  files: File[]
): Promise<ThemSanPhamAsyncResponse> => {
  const formData = createSanPhamFormData(data, files);
  return apiClient.post("/admin/san-pham", formData); // Axios handles multipart header automatically
};

/**
 * Cập nhật một sản phẩm đã có.
 * Gửi request multipart/form-data.
 * Tương ứng với `PATCH /api/admin/san-pham/{maSanPham}`.
 * @param maSanPham - Mã sản phẩm cần cập nhật.
 * @param data - Dữ liệu JSON của sản phẩm.
 * @param files - Danh sách các file ảnh mới (tùy chọn).
 * @returns Promise chứa thông tin chi tiết sản phẩm sau khi cập nhật.
 */
export const capNhatSanPham = (
  maSanPham: number,
  data: CapNhatSanPhamRequest,
  files?: File[]
): Promise<MessageResponse> => {
  const formData = createSanPhamFormData(data, files);
  return apiClient.patch(`/admin/san-pham/${maSanPham}`, formData); // Axios handles multipart header automatically
};
