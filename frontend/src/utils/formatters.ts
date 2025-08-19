/**
 * Định dạng một số thành chuỗi tiền tệ VND.
 * @param value - Số cần định dạng.
 * @returns Chuỗi đã định dạng, ví dụ: "100.000 ₫".
 */
export const formatCurrency = (value: number): string => {
  if (typeof value !== "number") return "";
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(value);
};

/**
 * Định dạng một chuỗi ISO date thành chuỗi ngày giờ dễ đọc theo chuẩn Việt Nam.
 * @param iso - Chuỗi ISO date, ví dụ: "2023-10-27T10:00:00Z".
 * @returns Chuỗi đã định dạng, ví dụ: "27/10/2023, 17:00".
 */
export const formatDateTime = (iso?: string): string => {
  if (!iso) return "";
  const d = new Date(iso);
  if (isNaN(d.getTime())) return "";
  return d.toLocaleString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

/**
 * Chuyển đổi một chuỗi ISO date thành định dạng mà input[type=datetime-local] yêu cầu.
 * @param date - Chuỗi ISO date hoặc đối tượng Date.
 * @returns Chuỗi có định dạng "YYYY-MM-DDTHH:mm".
 */
export const formatDateForInput = (date?: Date | string): string => {
  if (!date) return "";
  const d = new Date(date);
  if (isNaN(d.getTime())) return "";

  // Lấy thông tin ngày tháng năm giờ phút theo múi giờ địa phương
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  const hours = String(d.getHours()).padStart(2, "0");
  const minutes = String(d.getMinutes()).padStart(2, "0");

  return `${year}-${month}-${day}T${hours}:${minutes}`;
};
