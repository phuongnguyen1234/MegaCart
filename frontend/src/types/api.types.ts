/**
 * Dữ liệu phân trang gửi lên API.
 */
export interface PageableParams {
  page?: number;
  size?: number;
  sort?: string; // Ví dụ: "tenSanPham,asc" hoặc "donGia,desc"
}

/**
 * Hướng sắp xếp.
 */
export type SortDirection = "asc" | "desc";

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
