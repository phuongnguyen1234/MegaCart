export interface SanPham {
  maSanPham: number | string;
  tenSanPham: string;
  donGia: number;
  anhMinhHoa: string[]; // Mảng các URL hình ảnh, ảnh đầu tiên là ảnh chính
  nhan?: string;
  donVi: string;
  nhaSanXuat: string;
  danhMucCha: string; // Danh mục cha, ví dụ: "Thực phẩm"
  danhMucCon: string; // Danh mục con, ví dụ: "Bơ sữa"
}
