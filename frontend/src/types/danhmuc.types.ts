/**
 * =================================================================
 * FOR CUSTOMER-FACING UI (e.g., Navigation Menu)
 * =================================================================
 */

/**
 * Represents a child category item within a navigation menu dropdown (sub-category).
 * This is part of the `DanhMucMenuItem` structure.
 * It's derived from the backend's `DanhMucMenuItemResponse`.
 */
export interface DanhMucConMenuItem {
  maDanhMuc: number;
  tenDanhMuc: string;
  slug: string;
}

/**
 * Represents a top-level category item for the main navigation bar.
 * Corresponds to `DanhMucMenuItemResponse` from the backend, which is returned
 * by the `/api/danh-muc/menu` endpoint from `DanhMucController`.
 */
export interface DanhMucMenuItem {
  maDanhMuc: number;
  tenDanhMuc: string;
  slug: string;
  danhMucCons: DanhMucConMenuItem[];
}

/**
 * =================================================================
 * FOR ADMIN PANEL
 * =================================================================
 */

/**
 * Represents the status of a category, matching the backend enum `TrangThaiDanhMuc`.
 */
export enum TrangThaiDanhMuc {
  DANG_HOAT_DONG = "DANG_HOAT_DONG",
  NGUNG_HOAT_DONG = "NGUNG_HOAT_DONG",
}

/**
 * Represents a category in the admin list view.
 * Corresponds to `DanhMucQuanLyResponse` in the backend.
 */
export interface DanhMucQuanLyResponse {
  maDanhMuc: number;
  tenDanhMuc: string;
  tenDanhMucCha?: string; // Optional because a parent category won't have one
  trangThai: string; // e.g., "Đang hoạt động"
}

/**
 * Represents the detailed data of a category for editing.
 * Corresponds to `ChiTietDanhMucQuanLyResponse` in the backend.
 */
export interface ChiTietDanhMucQuanLyResponse {
  maDanhMuc: number;
  tenDanhMuc: string;
  maDanhMucCha?: number;
  trangThai: TrangThaiDanhMuc;
}

/**
 * Represents the payload for creating or updating a category.
 * Corresponds to `ThemDanhMucRequest` and `CapNhatDanhMucRequest` in the backend.
 */
export interface LuuDanhMucRequest {
  tenDanhMuc: string;
  maDanhMucCha?: number;
  trangThai?: TrangThaiDanhMuc;
}

/**
 * Represents a category option for select/dropdown inputs.
 * Corresponds to `DanhMucOptionResponse` in the backend.
 */
export interface DanhMucOptionResponse {
  id: number;
  ten: string;
}
