import type { EnumObject, PageableParams } from "./api.types";
import type {
  TrangThaiTaiKhoanKey,
  TrangThaiTaiKhoanObject,
} from "./khachhang.types";

/**
 * Các key của vị trí nhân viên, dùng để giao tiếp với API.
 * Giá trị của enum này phải TRÙNG KHỚP với tên của enum constant trong `ViTri.java` ở backend.
 */
export enum ViTriKey {
  NHAN_VIEN_QUAN_LI_DON = "NHAN_VIEN_QUAN_LI_DON",
  NHAN_VIEN_GIAO_HANG = "NHAN_VIEN_GIAO_HANG",
  NHAN_VIEN_QUAN_LI_KHO = "NHAN_VIEN_QUAN_LI_KHO",
}

/**
 * Ánh xạ từ key của vị trí nhân viên sang tên hiển thị (label).
 */
export const ViTriLabel: Record<ViTriKey, string> = {
  [ViTriKey.NHAN_VIEN_QUAN_LI_DON]: "Quản lí đơn",
  [ViTriKey.NHAN_VIEN_GIAO_HANG]: "Giao hàng",
  [ViTriKey.NHAN_VIEN_QUAN_LI_KHO]: "Quản lí kho",
};

/**
 * Cấu trúc đối tượng vị trí nhân viên nhận về từ API.
 */
export type ViTriObject = EnumObject<ViTriKey>;

/**
 * Dữ liệu hiển thị cho một nhân viên trong danh sách quản lý.
 * Tương ứng với `HienThiDanhSachNhanVienResponse.java` trên backend.
 */
export interface HienThiDanhSachNhanVienResponse {
  maNhanVien: number;
  tenNhanVien: string;
  email: string;
  soDienThoai: string;
  viTri: ViTriObject;
  trangThaiTaiKhoan: TrangThaiTaiKhoanObject;
}

/**
 * Các tham số để lọc và tìm kiếm nhân viên trong trang quản lý.
 * Tương ứng với các @RequestParam trong `QuanLyNhanVienController.getDSNhanVien`.
 */
export interface GetNhanVienParams extends PageableParams {
  searchField?: "hoTen" | "email" | "soDienThoai";
  searchValue?: string;
  viTri?: ViTriKey;
  trangThai?: TrangThaiTaiKhoanKey;
}

/**
 * Dữ liệu gửi lên khi tạo một nhân viên mới.
 * Tương ứng với `ThemNhanVienRequest.java` trên backend.
 */
export interface ThemNhanVienRequest {
  hoTen: string;
  email: string;
  soDienThoai: string;
  matKhau: string;
  viTri: ViTriKey;
}

/**
 * Dữ liệu gửi lên khi cập nhật thông tin một nhân viên.
 * Tương ứng với `CapNhatNhanVienRequest.java` trên backend.
 * Tất cả các trường đều là tùy chọn.
 */
export type CapNhatNhanVienRequest = Partial<
  Pick<ThemNhanVienRequest, "hoTen" | "soDienThoai" | "viTri"> & {
    trangThai: TrangThaiTaiKhoanKey;
  }
>;
