import apiClient from "./apiClient";
import type { PageableParams, PagedResponse } from "@/types/api.types";
import type {
  DanhMucMenuItem,
  DanhMucQuanLyResponse,
  LuuDanhMucRequest,
  ChiTietDanhMucQuanLyResponse,
  TrangThaiDanhMuc,
  DanhMucOptionResponse,
} from "@/types/danhmuc.types";

// --- For Customer-facing UI ---

/**
 * Fetches the category structure for the main navigation menu.
 * Corresponds to `GET /api/danh-muc/menu`.
 * @returns A promise that resolves to an array of menu items.
 */
export const getMenuDanhMuc = (): Promise<DanhMucMenuItem[]> => {
  return apiClient.get("/danh-muc/menu");
};

// --- For Admin Panel ---

/**
 * Defines the parameters for fetching the admin category list.
 */
export interface GetDanhMucParams extends PageableParams {
  tuKhoa?: string;
  trangThai?: TrangThaiDanhMuc;
}

/**
 * Fetches a paginated list of categories for the admin panel.
 * Corresponds to `GET /api/admin/danh-muc`.
 * @param params - Filtering and pagination parameters.
 * @returns A paged response of admin category data.
 */
export const getDanhSachDanhMuc = (
  params: GetDanhMucParams
): Promise<PagedResponse<DanhMucQuanLyResponse>> => {
  return apiClient.get("/admin/danh-muc", { params });
};

/**
 * Fetches a list of categories to be used as options in a filter/select input.
 * Specifically for parent category selection.
 * Corresponds to `GET /api/admin/danh-muc/filter-options`.
 * @returns A promise that resolves to an array of category options.
 */
export const getDanhMucOptionsForFilter = (): Promise<DanhMucOptionResponse[]> => {
  return apiClient.get("/admin/danh-muc/filter-options");
};

/**
 * Creates a new category.
 * Corresponds to `POST /api/admin/danh-muc`.
 * @param data - The data for the new category.
 * @returns The created category data.
 */
export const themDanhMuc = (
  data: LuuDanhMucRequest
): Promise<DanhMucQuanLyResponse> => {
  return apiClient.post("/admin/danh-muc", data);
};

/**
 * Fetches the detailed information of a category for editing.
 * Corresponds to `GET /api/admin/danh-muc/{maDanhMuc}`.
 * @param maDanhMuc - The ID of the category.
 * @returns The detailed category data.
 */
export const getChiTietDanhMuc = (
  maDanhMuc: number
): Promise<ChiTietDanhMucQuanLyResponse> => {
  return apiClient.get(`/admin/danh-muc/${maDanhMuc}`);
};

/**
 * Updates an existing category.
 * Corresponds to `PATCH /api/admin/danh-muc/{maDanhMuc}`.
 * @param maDanhMuc - The ID of the category to update.
 * @param data - The updated category data.
 * @returns The updated category data.
 */
export const capNhatDanhMuc = (
  maDanhMuc: number,
  data: LuuDanhMucRequest
): Promise<DanhMucQuanLyResponse> => {
  return apiClient.patch(`/admin/danh-muc/${maDanhMuc}`, data);
};