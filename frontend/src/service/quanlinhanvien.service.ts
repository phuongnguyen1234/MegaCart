import apiClient from "./apiClient";
import type { PagedResponse } from "@/types/api.types";
import type {
  GetNhanVienParams,
  HienThiDanhSachNhanVienResponse,
  ThemNhanVienRequest,
  CapNhatNhanVienRequest,
  NhanVienOptionResponse,
} from "@/types/nhanvien.types";

/**
 * Lấy danh sách nhân viên đã được phân trang và lọc.
 * Tương ứng với endpoint `GET /api/admin/nhan-vien`.
 * @param params - Các tham số lọc, tìm kiếm và phân trang.
 * @returns Một Promise chứa danh sách nhân viên đã được phân trang.
 */
export const getDanhSachNhanVien = (
  params: GetNhanVienParams
): Promise<PagedResponse<HienThiDanhSachNhanVienResponse>> => {
  return apiClient.get("/admin/nhan-vien", { params });
};

/**
 * Lấy thông tin chi tiết của một nhân viên.
 * Tương ứng với endpoint `GET /api/admin/nhan-vien/{maNhanVien}`.
 * @param maNhanVien - Mã của nhân viên cần xem.
 * @returns Một Promise chứa thông tin chi tiết của nhân viên.
 */
export const getChiTietNhanVien = (
  maNhanVien: number
): Promise<HienThiDanhSachNhanVienResponse> => {
  return apiClient.get(`/admin/nhan-vien/${maNhanVien}`);
};

/**
 * Tạo một tài khoản nhân viên mới.
 * Tương ứng với endpoint `POST /api/admin/nhan-vien`.
 * @param data - Dữ liệu chứa thông tin nhân viên mới.
 * @returns Một Promise chứa thông tin nhân viên sau khi đã được tạo.
 */
export const themNhanVien = (
  data: ThemNhanVienRequest
): Promise<HienThiDanhSachNhanVienResponse> => {
  return apiClient.post("/admin/nhan-vien", data);
};

/**
 * Cập nhật thông tin của một nhân viên.
 * Tương ứng với endpoint `PATCH /api/admin/nhan-vien/{maNhanVien}`.
 * @param maNhanVien - Mã của nhân viên cần cập nhật.
 * @param data - Dữ liệu chứa các thông tin cần thay đổi.
 * @returns Một Promise chứa thông tin nhân viên sau khi đã được cập nhật.
 */
export const capNhatNhanVien = (
  maNhanVien: number,
  data: CapNhatNhanVienRequest
): Promise<HienThiDanhSachNhanVienResponse> => {
  return apiClient.patch(`/admin/nhan-vien/${maNhanVien}`, data);
};

/**
 * Lấy danh sách các nhân viên có vị trí "Giao hàng" để gán cho đơn hàng.
 * Tương ứng với endpoint `GET /api/admin/nhan-vien/giao-hang`.
 * @returns Một Promise chứa danh sách nhân viên giao hàng.
 */
export const getDSNhanVienGiaoHang = (): Promise<NhanVienOptionResponse[]> => {
  return apiClient.get("/admin/nhan-vien/giao-hang");
};
