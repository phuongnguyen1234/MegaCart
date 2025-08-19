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

/**
 * Cấu trúc chung cho các đối tượng giống enum được trả về từ backend.
 * Chứa tên hằng số enum (value) và tên hiển thị (label).
 * @template T - Kiểu string literal cho các key của enum.
 */
export interface EnumObject<T extends string> {
  value: T;
  label: string;
}

// --- Types ---
// Định nghĩa cấu trúc của JWT payload để đảm bảo an toàn kiểu dữ liệu.
export interface JwtPayload {
  sub: string; // User's email
  name: string; // User's full name
  role: string; // User's single role
  iat: number;
  exp: number;
}
