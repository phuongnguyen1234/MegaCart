import type { VaiTroKey, ViTriNhanVienKey } from "./taikhoan.types";

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
 * A generic response from the API that only contains a message.
 * Useful for operations that don't need to return data, like delete or some updates.
 */
export interface MessageResponse {
  message: string;
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

// --- JWT Payload Types ---

/**
 * Base structure for all JWT payloads in the system.
 */
interface BaseJwtPayload {
  /** Subject (usually the user's email) */
  sub: string;
  /** User's unique identifier */
  userId: number;
  /** User's full name */
  fullName: string;
  /** Issued at (Unix timestamp) */
  iat: number;
  /** Expiration time (Unix timestamp) */
  exp: number;
}

/**
 * JWT payload for a customer.
 */
interface KhachHangJwtPayload extends BaseJwtPayload {
  role: VaiTroKey.KHACH_HANG;
}

/**
 * JWT payload for an employee.
 */
export interface NhanVienJwtPayload extends BaseJwtPayload {
  role: VaiTroKey.NHAN_VIEN;
  /** The specific position/role of the employee */
  viTri: ViTriNhanVienKey;
}

/**
 * JWT payload for an admin.
 * Admins have a distinct role and do not have a 'viTri' (position).
 */
interface AdminJwtPayload extends BaseJwtPayload {
  role: VaiTroKey.ADMIN;
}

/**
 * Represents the decoded JWT payload, which can vary based on the user's role.
 * Using a discriminated union on the `role` property allows for type-safe
 * access to role-specific fields like `viTri` for employees.
 */
export type JwtPayload =
  | KhachHangJwtPayload
  | NhanVienJwtPayload
  | AdminJwtPayload;
