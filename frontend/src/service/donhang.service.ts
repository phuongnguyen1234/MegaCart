import apiClient from "./apiClient";
import type { PageableParams, PagedResponse } from "@/types/api.types";
import type {
  ChiTietDonHang,
  HuyDonHangRequest,
  LichSuDonHang,
  TrangThaiDonHangFilter,
} from "@/types/donhang.types";

/**
 * Defines the parameters for fetching the order history.
 * Corresponds to the @RequestParams in `DonHangController.getLichSuMuaHang`.
 */
export interface GetLichSuDonHangParams extends PageableParams {
  trangThai: TrangThaiDonHangFilter;
  tuKhoa?: string;
  tuNgay?: string; // ISO Date string, e.g., "YYYY-MM-DD"
  denNgay?: string; // ISO Date string, e.g., "YYYY-MM-DD"
}

/**
 * Fetches the order history for the current user, with filtering and pagination.
 * @param params - The filtering and pagination parameters.
 * @returns A paged response of order summaries.
 */
export const getLichSuMuaHang = (
  params: GetLichSuDonHangParams
): Promise<PagedResponse<LichSuDonHang>> => {
  return apiClient.get("/don-hang/lich-su", { params });
};

/**
 * Fetches the detailed information of a specific order.
 * @param maDonHang - The ID of the order.
 * @returns The detailed order information.
 */
export const getChiTietDonHang = (
  maDonHang: number
): Promise<ChiTietDonHang> => {
  return apiClient.get(`/don-hang/${maDonHang}`);
};

/**
 * Cancels an order.
 * @param maDonHang - The ID of the order to cancel.
 * @param data - The reason for cancellation.
 * @returns The updated detailed order information.
 */
export const huyDonHang = (
  maDonHang: number,
  data: HuyDonHangRequest
): Promise<ChiTietDonHang> => {
  return apiClient.post(`/don-hang/${maDonHang}/huy`, data);
};

/**
 * Requests to ship the remaining items of a partially out-of-stock order.
 * @param maDonHang - The ID of the order.
 * @returns The updated detailed order information.
 */
export const giaoPhanConLai = (maDonHang: number): Promise<ChiTietDonHang> => {
  return apiClient.post(`/don-hang/${maDonHang}/giao-phan-con-lai`);
};
