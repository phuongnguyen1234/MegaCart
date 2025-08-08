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

export interface DuLieuCapNhat {
  maSanPham: string;
  hinhThuc: "them" | "ghide";
  soLuong: number;
  noiDung: string;
}
