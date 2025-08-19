import apiClient from "./apiClient";
import type { PagedResponse } from "@/types/api.types";
import type {
  GetKhachHangParams,
  HienThiDanhSachKhachHangResponse,
  CapNhatTrangThaiTaiKhoanRequest,
} from "@/types/khachhang.types";

/**
 * Lấy danh sách khách hàng đã được phân trang và lọc.
 * Tương ứng với endpoint `GET /api/admin/khach-hang`.
 * @param params - Các tham số lọc, tìm kiếm và phân trang.
 * @returns Một Promise chứa danh sách khách hàng đã được phân trang.
 */
export const getDanhSachKhachHang = (
  params: GetKhachHangParams
): Promise<PagedResponse<HienThiDanhSachKhachHangResponse>> => {
  return apiClient.get("/admin/khach-hang", { params });
};

/**
 * Lấy thông tin chi tiết của một khách hàng.
 * Tương ứng với endpoint `GET /api/admin/khach-hang/{maKhachHang}`.
 * @param maKhachHang - Mã của khách hàng cần xem.
 * @returns Một Promise chứa thông tin chi tiết của khách hàng.
 */
export const getChiTietKhachHang = (
  maKhachHang: number
): Promise<HienThiDanhSachKhachHangResponse> => {
  return apiClient.get(`/admin/khach-hang/${maKhachHang}`);
};

/**
 * Cập nhật trạng thái tài khoản của một khách hàng (ví dụ: khóa hoặc mở khóa).
 * Tương ứng với endpoint `PATCH /api/admin/khach-hang/{maKhachHang}/cap-nhat-trang-thai`.
 * @param maKhachHang - Mã của khách hàng cần cập nhật.
 * @param data - Dữ liệu chứa trạng thái mới.
 * @returns Một Promise chứa thông tin khách hàng sau khi đã được cập nhật.
 */
export const capNhatTrangThaiKhachHang = (
  maKhachHang: number,
  data: CapNhatTrangThaiTaiKhoanRequest
): Promise<HienThiDanhSachKhachHangResponse> => {
  return apiClient.patch(
    `/admin/khach-hang/${maKhachHang}/cap-nhat-trang-thai`,
    data
  );
};
