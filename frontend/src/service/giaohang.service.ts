import apiClient from "./apiClient";
import type { MessageResponse, PagedResponse } from "@/types/api.types";
import type {
  GetDonHangGiaoHangParams,
  DonHangGiaoHangResponse,
  ChiTietDonHangGiaoHangResponse,
  CapNhatGiaoHangRequest,
} from "@/types/giaohang.types";

/**
 * Lấy danh sách các đơn hàng được gán cho nhân viên giao hàng hiện tại.
 * Tương ứng với `GET /api/giao-hang/don-hang`.
 * @param params - Các tham số lọc, tìm kiếm và phân trang.
 * @returns Promise chứa danh sách đơn hàng cần giao đã phân trang.
 */
export const getDSDonHangCanGiao = (
  params: GetDonHangGiaoHangParams
): Promise<PagedResponse<DonHangGiaoHangResponse>> => {
  return apiClient.get("/giao-hang/don-hang", { params });
};

/**
 * Lấy thông tin chi tiết của một đơn hàng để nhân viên giao hàng xem.
 * Tương ứng với `GET /api/giao-hang/don-hang/{maDonHang}`.
 * @param maDonHang - Mã của đơn hàng cần xem.
 * @returns Promise chứa thông tin chi tiết của đơn hàng.
 */
export const getChiTietDonHangGiaoHang = (
  maDonHang: number
): Promise<ChiTietDonHangGiaoHangResponse> => {
  return apiClient.get(`/giao-hang/don-hang/${maDonHang}`);
};

/**
 * Cập nhật trạng thái giao hàng (thành công/thất bại) cho một đơn hàng.
 * Tương ứng với `PATCH /api/giao-hang/don-hang/{maDonHang}`.
 * @param maDonHang - Mã đơn hàng cần cập nhật.
 * @param data - Dữ liệu chứa trạng thái mới và ghi chú.
 * @returns Promise chứa thông báo thành công.
 */
export const capNhatTrangThaiGiaoHang = (
  maDonHang: number,
  data: CapNhatGiaoHangRequest
): Promise<MessageResponse> => {
  return apiClient.patch(`/giao-hang/don-hang/${maDonHang}`, data);
};
