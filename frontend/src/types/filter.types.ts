/**
 * Dữ liệu cho một tùy chọn trong bộ lọc (ví dụ: một danh mục).
 */
export interface FilterOption {
  maDanhMuc: number;
  tenDanhMuc: string;
  slug: string;
}

export interface KhoangGia {
  min: number;
  max: number;
}

export interface FilterDataResponse {
  danhMucs: FilterOption[];
  nhaSanXuats: string[];
  khoangGia: KhoangGia;
}
