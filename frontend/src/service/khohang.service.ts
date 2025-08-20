import apiClient from "./apiClient";
import type { PagedResponse } from "@/types/api.types";
import type {
  GetKhoParams,
  KhoResponse,
  ChiTietKhoResponse,
  CapNhatKhoRequest,
} from "@/types/khohang.types";

/**
 * Lấy danh sách sản phẩm trong kho, có phân trang và lọc.
 * Tương ứng với `GET /api/quan-ly/kho`.
 * @param params - Các tham số lọc, tìm kiếm và phân trang.
 * @returns Promise chứa danh sách sản phẩm trong kho đã phân trang.
 */
export const getDanhSachKho = (
  params: GetKhoParams
): Promise<PagedResponse<KhoResponse>> => {
  return apiClient.get("/quan-ly/kho", { params });
};

/**
 * Lấy thông tin chi tiết tồn kho của một sản phẩm.
 * Tương ứng với `GET /api/quan-ly/kho/{maSanPham}`.
 * @param maSanPham - Mã của sản phẩm cần xem.
 * @returns Promise chứa thông tin chi tiết tồn kho.
 */
export const getChiTietKho = (
  maSanPham: number
): Promise<ChiTietKhoResponse> => {
  return apiClient.get(`/quan-ly/kho/${maSanPham}`);
};

/**
 * Cập nhật số lượng tồn kho cho một sản phẩm.
 * Tương ứng với `PATCH /api/quan-ly/kho/{maSanPham}`.
 * @param maSanPham - Mã sản phẩm cần cập nhật.
 * @param data - Dữ liệu cập nhật (hình thức, số lượng, nội dung).
 * @returns Promise chứa thông tin tồn kho của sản phẩm sau khi cập nhật.
 */
export const capNhatKho = (
  maSanPham: number,
  data: CapNhatKhoRequest
): Promise<KhoResponse> => {
  return apiClient.patch(`/quan-ly/kho/${maSanPham}`, data);
};
