/**
 * Định dạng một số thành chuỗi tiền tệ Việt Nam (VND).
 * @param value - Số cần định dạng.
 * @returns Chuỗi đã định dạng, ví dụ: "123.456 ₫".
 */
export const formatCurrency = (value: number): string => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(value);
};
