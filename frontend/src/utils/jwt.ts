/**
 * Giải mã payload của một JWT.
 * Lưu ý: Hàm này không xác thực chữ ký của token. Nó chỉ dùng để đọc
 * dữ liệu an toàn (như vai trò, id người dùng) ở phía client.
 * @param token - Chuỗi JWT.
 * @returns Payload của token dưới dạng object, hoặc null nếu token không hợp lệ.
 */
export function decodeJwtPayload(token: string): { [key: string]: any } | null {
  try {
    // Tách phần payload (phần thứ 2) của token
    const base64Url = token.split(".")[1];
    if (!base64Url) return null;
    // Thay thế các ký tự không hợp lệ cho base64 decoding
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    // Giải mã và chuyển thành chuỗi JSON
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join("")
    );
    return JSON.parse(jsonPayload);
  } catch (e) {
    console.error("Lỗi giải mã token:", e);
    return null;
  }
}
