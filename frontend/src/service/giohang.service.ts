import apiClient from "./apiClient";
import {
  CapNhatSoLuongRequest,
  GioHangResponse,
  ThemVaoGioHangRequest,
  ThemVaoGioHangResponse,
  ThongTinThanhToanResponse,
  XoaKhoiGioHangResponse,
} from "@/types/giohang.types";

// --- Service Functions ---

/**
 * Thêm một sản phẩm vào giỏ hàng của người dùng hiện tại.
 */
export const themVaoGioHang = (
  data: ThemVaoGioHangRequest
): Promise<ThemVaoGioHangResponse> => {
  return apiClient.post("/gio-hang/them", data);
};

/**
 * Lấy thông tin giỏ hàng và thanh toán của người dùng hiện tại.
 * Bao gồm danh sách sản phẩm và thông tin giao hàng mặc định.
 */
export const getThongTinThanhToan = (): Promise<ThongTinThanhToanResponse> => {
  return apiClient.get("/gio-hang");
};

/**
 * Cập nhật số lượng của một sản phẩm trong giỏ hàng.
 * @param maSanPham - Mã sản phẩm cần cập nhật.
 * @param data - Dữ liệu chứa số lượng mới.
 * @returns Trạng thái mới của giỏ hàng.
 */
export const capNhatSoLuong = (
  maSanPham: number,
  data: CapNhatSoLuongRequest
): Promise<GioHangResponse> => {
  return apiClient.patch(`/gio-hang/cap-nhat/${maSanPham}`, data);
};

/**
 * Xóa một sản phẩm khỏi giỏ hàng.
 * @param maSanPham - Mã sản phẩm cần xóa.
 * @returns Thông báo và thông tin tóm tắt giỏ hàng sau khi xóa.
 */
export const xoaKhoiGioHang = (
  maSanPham: number
): Promise<XoaKhoiGioHangResponse> => {
  return apiClient.delete(`/gio-hang/xoa/${maSanPham}`);
};

/**
 * Xóa toàn bộ sản phẩm khỏi giỏ hàng của người dùng.
 */
export const xoaToanBoGioHang = (): Promise<{ message: string }> => {
  return apiClient.delete("/gio-hang/xoa-toan-bo");
};
