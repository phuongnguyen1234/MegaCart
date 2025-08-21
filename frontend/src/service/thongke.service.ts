import axios from "axios";
import {
  ThongKeTongQuanResponse,
  BieuDoDuongResponse,
  BieuDoTronResponse,
  MucTieuDoanhThuResponse,
  CapNhatMucTieuDoanhThuRequest,
  SanPhamBanChayResponse,
  SanPhamTonKhoResponse,
  ChiTietDoanhThuThangResponse,
  ChiTietDonHangThangResponse,
  ChiTietSanPhamBanChayResponse,
} from "../types/thongke.types";
import { MessageResponse, PagedResponse } from "../types/api.types";

const BASE_URL = "/api/admin/thong-ke";

export const thongKeService = {
  getThongKeTongQuan(): Promise<ThongKeTongQuanResponse> {
    return axios.get(`${BASE_URL}/tong-quan`).then((res) => res.data);
  },

  getDoanhThuTheoNgay(period = 7): Promise<BieuDoDuongResponse> {
    return axios
      .get(`${BASE_URL}/doanh-thu-theo-ngay`, { params: { period } })
      .then((res) => res.data);
  },

  getDonHangTheoThang(): Promise<BieuDoDuongResponse> {
    return axios.get(`${BASE_URL}/don-hang-theo-thang`).then((res) => res.data);
  },

  getMucTieuDoanhThu(): Promise<MucTieuDoanhThuResponse> {
    return axios.get(`${BASE_URL}/muc-tieu-doanh-thu`).then((res) => res.data);
  },

  capNhatMucTieuDoanhThu(
    request: CapNhatMucTieuDoanhThuRequest
  ): Promise<MessageResponse> {
    return axios
      .put(`${BASE_URL}/muc-tieu-doanh-thu`, request)
      .then((res) => res.data);
  },

  getTiLeDonHang(): Promise<BieuDoTronResponse[]> {
    return axios.get(`${BASE_URL}/ti-le-don-hang`).then((res) => res.data);
  },

  getSanPhamBanChay(limit = 10): Promise<SanPhamBanChayResponse[]> {
    return axios
      .get(`${BASE_URL}/san-pham-ban-chay`, { params: { limit } })
      .then((res) => res.data);
  },

  getSanPhamTonKhoCao(limit = 5): Promise<SanPhamTonKhoResponse[]> {
    return axios
      .get(`${BASE_URL}/san-pham-ton-kho-cao`, { params: { limit } })
      .then((res) => res.data);
  },

  getChiTietDoanhThuThang(): Promise<ChiTietDoanhThuThangResponse[]> {
    return axios
      .get(`${BASE_URL}/chi-tiet-doanh-thu-thang`)
      .then((res) => res.data);
  },

  getDoanhThuTheoThang(): Promise<BieuDoDuongResponse> {
    return axios
      .get(`${BASE_URL}/doanh-thu-theo-thang`)
      .then((res) => res.data);
  },

  getDonHangTheoNgay(period = 7): Promise<BieuDoDuongResponse> {
    return axios
      .get(`${BASE_URL}/don-hang-theo-ngay`, { params: { period } })
      .then((res) => res.data);
  },

  getChiTietDonHangThang(): Promise<ChiTietDonHangThangResponse[]> {
    return axios
      .get(`${BASE_URL}/chi-tiet-don-hang-thang`)
      .then((res) => res.data);
  },

  getChiTietSanPhamBanChay(
    page = 0,
    size = 10
  ): Promise<PagedResponse<ChiTietSanPhamBanChayResponse>> {
    return axios
      .get(`${BASE_URL}/chi-tiet-san-pham-ban-chay`, { params: { page, size } })
      .then((res) => res.data);
  },
};
