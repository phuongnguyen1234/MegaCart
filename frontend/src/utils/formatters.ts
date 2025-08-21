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

/**
 * Chuyển đổi chuỗi ISO 8601 (thường là UTC) thành chuỗi ngày giờ theo múi giờ địa phương của trình duyệt.
 * Trình duyệt sẽ tự động xử lý việc chuyển đổi từ UTC sang múi giờ của người dùng.
 * @param isoString - Chuỗi ngày giờ theo định dạng ISO 8601 (ví dụ: "2023-10-27T10:00:00Z").
 * @returns Chuỗi đã định dạng theo locale 'vi-VN' (ví dụ: "17:00, 27/10/2023") hoặc một chuỗi rỗng nếu đầu vào không hợp lệ.
 */
export const formatDateTimeLocal = (isoString?: string | null): string => {
  if (!isoString) {
    return "";
  }
  try {
    const date = new Date(isoString);

    // Kiểm tra xem đối tượng Date có hợp lệ không
    if (isNaN(date.getTime())) {
      console.warn(`Chuỗi ngày giờ không hợp lệ được cung cấp: ${isoString}`);
      return ""; // Trả về rỗng nếu chuỗi không thể parse
    }

    // Định dạng theo locale 'vi-VN' sẽ cho ra kết quả như "17:00, 27/10/2023"
    return new Intl.DateTimeFormat("vi-VN", {
      hour: "2-digit",
      minute: "2-digit",
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour12: false,
    }).format(date);
  } catch (error) {
    console.error(`Lỗi khi định dạng chuỗi ngày giờ: ${isoString}`, error);
    return ""; // Trả về rỗng để tránh làm hỏng giao diện
  }
};
