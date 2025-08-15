import apiClient from "./apiClient";
import type { DatHangRequest, DatHangResponse } from "@/types/dathang.types";

export const taoDonHang = (data: DatHangRequest): Promise<DatHangResponse> => {
  return apiClient.post("/dat-hang", data);
};
