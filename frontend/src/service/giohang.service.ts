import apiClient from "./apiClient";
import {
  ThemVaoGioHangRequest,
  ThemVaoGioHangResponse,
  ThongTinThanhToanResponse,
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
 * Lấy thông tin giỏ hàng của người dùng hiện tại.
 */
export const getThongTinGioHang = (): Promise<ThongTinThanhToanResponse> => {
  return apiClient.get("/gio-hang");
};
