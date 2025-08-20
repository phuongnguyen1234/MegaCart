import type { PageableParams } from "./api.types";
import type {
  TrangThaiThanhToanObject,
  HinhThucThanhToanObject,
  ChiTietDonHangItem,
  TrangThaiThanhToanKey,
} from "./donhang.types";

/**
 * =================================================================
 * FOR QUAN LY GIAO HANG (Admin Panel)
 * Các type này tương ứng với các API trong QuanLyDonHangController
 * dành cho việc quản lý và gán đơn vị vận chuyển.
 * =================================================================
 */

/**
 * Các tham số để lọc và tìm kiếm các đơn hàng đang được giao.
 * Tương ứng với các @RequestParam trong `QuanLyDonHangController.getDSDonHangDangGiao`.
 */
export interface GetDonHangDangGiaoParams extends PageableParams {
  searchField?:
    | "maDonHang"
    | "tenNguoiNhan"
    | "tenNhanVienGiaoHang"
    | "sdtNhanHang"
    | "diaChiNhanHang";
  searchValue?: string;
}

/**
 * Dữ liệu tóm tắt của một đơn hàng đang giao, hiển thị trong danh sách quản lý giao hàng.
 * Tương ứng với `DonHangDangGiaoQuanLyResponse.java` ở backend.
 */
export interface DonHangDangGiaoQuanLyResponse {
  maDonHang: number;
  tenNguoiNhan: string;
  diaChiNhanHang: string;
  sdtNhanHang: string;
  tenNhanVienGiaoHang: string;
  tongTien: number;
  trangThaiThanhToan?: TrangThaiThanhToanObject;
}

/**
 * Dữ liệu chi tiết về việc giao hàng của một đơn, dùng cho modal "Gán nhân viên giao hàng".
 * Tương ứng với `ChiTietGiaoHangQuanLyResponse.java` ở backend.
 */
export interface ChiTietGiaoHangQuanLyResponse {
  maDonHang: number;
  tenNguoiNhan: string;
  diaChiNhanHang: string;
  maNhanVienGiaoHang?: number; // Có thể null nếu chưa được gán
  tenNhanVienGiaoHang?: string; // Có thể null nếu chưa được gán
}

/**
 * Dữ liệu gửi lên khi gán hoặc thay đổi nhân viên giao hàng cho một đơn.
 * Tương ứng với `GanGiaoHangRequest.java` ở backend.
 */
export interface GanGiaoHangRequest {
  maNhanVienGiaoHang: number;
}

/**
 * =================================================================
 * FOR GIAO HANG (Delivery Staff Panel)
 * Các type này tương ứng với các API trong GiaoHangController
 * dành cho nhân viên giao hàng thực hiện nghiệp vụ.
 * =================================================================
 */

/**
 * Các tham số để lọc và tìm kiếm các đơn hàng được gán cho nhân viên giao hàng.
 * Tương ứng với các @RequestParam trong `GiaoHangController.getDSDonHangDangGiao`.
 */
export interface GetDonHangGiaoHangParams extends PageableParams {
  searchField?: "maDonHang" | "tenKhachHang";
  searchValue?: string;
}

/**
 * Dữ liệu tóm tắt của một đơn hàng, hiển thị trong danh sách "Cần giao" của nhân viên.
 * Tương ứng với `DonHangGiaoHangResponse.java` ở backend.
 */
export interface DonHangGiaoHangResponse {
  maDonHang: number;
  tenNguoiNhan: string;
  diaChiNhanHang: string;
  sdtNhanHang: string;
  tongTien: number;
  trangThaiThanhToan?: TrangThaiThanhToanObject;
}

/**
 * Dữ liệu chi tiết của một đơn hàng, hiển thị trong modal "Chi tiết giao hàng" của nhân viên.
 * Tương ứng với `ChiTietDonHangGiaoHangResponse.java` ở backend.
 */
export interface ChiTietDonHangGiaoHangResponse {
  maDonHang: number;
  tenNguoiNhan: string;
  sdtNhanHang: string;
  diaChiNhanHang: string;
  tongTien: number;
  hinhThucThanhToan: HinhThucThanhToanObject;
  trangThaiThanhToan: TrangThaiThanhToanObject;
  ghiChu?: string;
  items: ChiTietDonHangItem[];
}

/**
 * Các key của kết quả giao hàng, dùng để gửi lên API.
 * Giá trị của enum này phải TRÙNG KHỚP với tên của enum constant trong `KetQuaGiaoHang.java` ở backend.
 */
export enum KetQuaGiaoHangKey {
  THANH_CONG = "THANH_CONG",
  THAT_BAI = "THAT_BAI",
}

/**
 * Dữ liệu gửi lên khi nhân viên giao hàng cập nhật trạng thái của một đơn hàng.
 * Tương ứng với `CapNhatGiaoHangRequest.java` ở backend.
 */
export interface CapNhatGiaoHangRequest {
  ketQua: KetQuaGiaoHangKey;
  lyDoThatBai?: string;
  trangThaiThanhToan?: TrangThaiThanhToanKey;
}
