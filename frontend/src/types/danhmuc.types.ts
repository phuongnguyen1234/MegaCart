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

import type { EnumObject, PageableParams } from "./api.types";

/**
 * Represents the status of a category, matching the backend enum `TrangThaiDanhMuc`.
 * The keys must match the enum constant names in `TrangThaiDanhMuc.java`.
 */
export enum TrangThaiDanhMucKey {
  HOAT_DONG = "HOAT_DONG",
  KHONG_HOAT_DONG = "KHONG_HOAT_DONG",
}

/**
 * Represents the display labels for category statuses.
 */
export const TrangThaiDanhMucLabel: Record<TrangThaiDanhMucKey, string> = {
  [TrangThaiDanhMucKey.HOAT_DONG]: "Hoạt động",
  [TrangThaiDanhMucKey.KHONG_HOAT_DONG]: "Không hoạt động",
};

/**
 * Represents the structure of a category status object returned from the API.
 */
export type TrangThaiDanhMucObject = EnumObject<TrangThaiDanhMucKey>;

/**
 * Represents a category in the admin list view.
 * Corresponds to `DanhMucQuanLyResponse` in the backend.
 */
export interface DanhMucQuanLyResponse {
  maDanhMuc: number;
  tenDanhMuc: string;
  tenDanhMucCha?: string; // Optional because a parent category won't have one
  trangThai: TrangThaiDanhMucObject;
}

/**
 * Represents the detailed data of a category for editing.
 * Corresponds to `ChiTietDanhMucQuanLyResponse` in the backend.
 */
export interface ChiTietDanhMucQuanLyResponse {
  maDanhMuc: number;
  tenDanhMuc: string;
  maDanhMucCha?: number;
  trangThai: TrangThaiDanhMucObject;
}

/**
 * Represents the payload for creating or updating a category.
 * Corresponds to `ThemDanhMucRequest` and `CapNhatDanhMucRequest` in the backend.
 */
export interface ThemDanhMucRequest {
  tenDanhMuc: string;
  maDanhMucCha?: number;
  trangThai: TrangThaiDanhMucKey;
}

/**
 * Represents the payload for updating a category.
 * Corresponds to `CapNhatDanhMucRequest` in the backend.
 * Fields are optional as it's typically used for PATCH requests.
 */
export interface CapNhatDanhMucRequest {
  tenDanhMuc?: string;
  maDanhMucCha?: number;
  trangThai?: TrangThaiDanhMucKey;
  isDanhMucChaUpdated?: boolean; // Corresponds to @JsonProperty("isDanhMucChaUpdated")
}

/**
 * Represents a child category option for select/dropdown inputs.
 * This is part of the `DanhMucOptionResponse` structure.
 */
export interface DanhMucConOption {
  maDanhMuc: number;
  tenDanhMucHienThi: string;
}

/**
 * Represents a category option for select/dropdown inputs, especially for parent categories
 * that contain child categories. This is used for populating category filters and
 * selection in admin forms (e.g., in `ThemSuaSanPhamModal` or `BoLocDanhMuc`).
 * Corresponds to `DanhMucOptionResponse` in the backend.
 * This is expected to be a nested structure (parent with children).
 */
export interface DanhMucOptionResponse {
  maDanhMuc: number;
  tenDanhMucHienThi: string;
  danhMucCons: DanhMucConOption[];
}

/**
 * Defines the parameters for fetching the admin category list.
 * Corresponds to the @RequestParam in `QuanLyDanhMucController.getDSDanhMuc`.
 */
export interface GetDanhMucParams extends PageableParams {
  tuKhoa?: string;
  trangThai?: TrangThaiDanhMucKey;
}
