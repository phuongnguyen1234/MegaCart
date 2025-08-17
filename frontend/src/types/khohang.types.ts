export interface SanPhamTonKho {
  maSanPham: string;
  tenSanPham: string;
  idDanhMucCha: number;
  danhMucCha: string;
  idDanhMucCon: number;
  danhMucCon: string;
  soLuong: number;
  thoiGianCapNhat: Date;
  noiDungCapNhat: string;
}

/**
 * Represents the type of inventory update operation.
 * 'them': Adds to the existing quantity.
 * 'ghide': Overwrites the existing quantity with the new value.
 */
export enum HinhThucCapNhatKho {
  THEM = "them",
  GHI_DE = "ghide",
}

export interface DuLieuCapNhat {
  maSanPham: string;
  hinhThuc: HinhThucCapNhatKho;
  soLuong: number;
  noiDung: string;
}
