import apiClient from "./apiClient";
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
  DonHangGanDayResponse,
} from "../types/thongke.types";
import { MessageResponse, PagedResponse } from "../types/api.types";
import { ChiTietDonHangQuanLyResponse } from "@/types/donhang.types";

export const thongKeService = {
  getThongKeTongQuan(): Promise<ThongKeTongQuanResponse> {
    return apiClient.get(`/admin/thong-ke/tong-quan`);
  },

  getDoanhThuTheoNgay(period = 7): Promise<BieuDoDuongResponse> {
    return apiClient.get(`/admin/thong-ke/doanh-thu-theo-ngay`, {
      params: { period },
    });
  },

  getDonHangTheoThang(): Promise<BieuDoDuongResponse> {
    return apiClient.get(`/admin/thong-ke/don-hang-theo-thang`);
  },

  getMucTieuDoanhThu(): Promise<MucTieuDoanhThuResponse> {
    return apiClient.get(`/admin/thong-ke/muc-tieu-doanh-thu`);
  },

  capNhatMucTieuDoanhThu(
    request: CapNhatMucTieuDoanhThuRequest
  ): Promise<MessageResponse> {
    return apiClient.put(`/admin/thong-ke/muc-tieu-doanh-thu`, request);
  },

  getTiLeDonHang(): Promise<BieuDoTronResponse[]> {
    return apiClient.get(`/admin/thong-ke/ti-le-don-hang`);
  },

  getSanPhamBanChay(limit = 10): Promise<SanPhamBanChayResponse[]> {
    return apiClient.get(`/admin/thong-ke/san-pham-ban-chay`, {
      params: { limit },
    });
  },

  getSanPhamTonKhoCao(limit = 10): Promise<SanPhamTonKhoResponse[]> {
    return apiClient.get(`/admin/thong-ke/san-pham-ton-kho-cao`, {
      params: { limit },
    });
  },

  getChiTietDoanhThuThang(): Promise<ChiTietDoanhThuThangResponse[]> {
    return apiClient.get(`/admin/thong-ke/chi-tiet-doanh-thu-thang`);
  },

  getDoanhThuTheoThang(): Promise<BieuDoDuongResponse> {
    return apiClient.get(`/admin/thong-ke/doanh-thu-theo-thang`);
  },

  getDonHangTheoNgay(period = 7): Promise<BieuDoDuongResponse> {
    return apiClient.get(`/admin/thong-ke/don-hang-theo-ngay`, {
      params: { period },
    });
  },

  getChiTietDonHangThang(): Promise<ChiTietDonHangThangResponse[]> {
    return apiClient.get(`/admin/thong-ke/chi-tiet-don-hang-thang`);
  },

  getChiTietSanPhamBanChay(
    page = 0,
    size = 10
  ): Promise<PagedResponse<ChiTietSanPhamBanChayResponse>> {
    return apiClient.get(`/admin/thong-ke/chi-tiet-san-pham-ban-chay`, {
      params: { page, size },
    });
  },

  getDonHangGanDay(limit = 5): Promise<DonHangGanDayResponse[]> {
    return apiClient.get(`/admin/thong-ke/don-hang-gan-day`, {
      params: { limit },
    });
  },
};

export const getChiTietDonHang = (
  maDonHang: number
): Promise<ChiTietDonHangQuanLyResponse> => {
  return apiClient.get(`/admin/thong-ke/don-hang/${maDonHang}`);
};
