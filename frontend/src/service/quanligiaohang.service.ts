import apiClient from "./apiClient";
import type { MessageResponse, PagedResponse } from "@/types/api.types";
import type {
  GetDonHangDangGiaoParams,
  DonHangDangGiaoQuanLyResponse,
  GanGiaoHangRequest,
} from "@/types/giaohang.types";
import type { ChiTietGiaoHangQuanLyResponse } from "@/types/giaohang.types";

/**
 * Lấy danh sách các đơn hàng đang ở trạng thái "Đang giao" cho trang quản lý.
 * Tương ứng với `GET /api/admin/don-hang/dang-giao`.
 * @param params - Các tham số lọc, tìm kiếm và phân trang.
 * @returns Promise chứa danh sách đơn hàng đang giao đã phân trang.
 */
export const getDSDonHangDangGiao = (
  params: GetDonHangDangGiaoParams
): Promise<PagedResponse<DonHangDangGiaoQuanLyResponse>> => {
  return apiClient.get("/admin/don-hang/dang-giao", { params });
};

/**
 * Lấy thông tin chi tiết về việc giao hàng của một đơn.
 * Tương ứng với `GET /api/admin/don-hang/{maDonHang}/chi-tiet-giao-hang`.
 * @param maDonHang - Mã của đơn hàng cần xem.
 * @returns Promise chứa thông tin chi tiết về việc gán người giao hàng.
 */
export const getChiTietGiaoHangQuanLy = (
  maDonHang: number
): Promise<ChiTietGiaoHangQuanLyResponse> => {
  return apiClient.get(`/admin/don-hang/${maDonHang}/chi-tiet-giao-hang`);
};

/**
 * Gán hoặc thay đổi nhân viên giao hàng cho một đơn hàng.
 * Tương ứng với `PATCH /api/admin/don-hang/{maDonHang}/gan-giao-hang`.
 * @param maDonHang - Mã đơn hàng cần cập nhật.
 * @param data - Dữ liệu chứa mã nhân viên giao hàng mới.
 * @returns Promise chứa thông báo thành công.
 */
export const ganNhanVienGiaoHang = (
  maDonHang: number,
  data: GanGiaoHangRequest
): Promise<MessageResponse> => {
  return apiClient.patch(`/admin/don-hang/${maDonHang}/gan-giao-hang`, data);
};

//them API lấy danh sách nhân viên giao hàng
