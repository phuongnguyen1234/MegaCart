import { ref, readonly } from "vue";

// Định nghĩa kiểu dữ liệu cho một toast
interface Toast {
  id: number;
  thongBao: string;
  loai: "thanhCong" | "loi";
  thoiLuong?: number;
}

// Trạng thái global cho các toasts
const toasts = ref<Toast[]>([]);
let toastId = 0;
const MAX_TOASTS = 5; // Giới hạn số lượng toast hiển thị cùng lúc
const DEFAULT_DURATION = 5000; // 5 giây

// Composable function
export function useToast() {
  /**
   * Xóa một toast bằng id của nó.
   */
  const dongToast = (id: number) => {
    const index = toasts.value.findIndex((toast) => toast.id === id);
    if (index > -1) {
      toasts.value.splice(index, 1);
    }
  };

  /**
   * Hiển thị một toast mới.
   * Nếu đã đạt đến giới hạn, toast cũ nhất sẽ bị xóa.
   */
  const hienThiToast = (options: Omit<Toast, "id">) => {
    // Nếu số lượng toast vượt quá giới hạn, xóa toast cũ nhất (ở đầu mảng)
    if (toasts.value.length >= MAX_TOASTS) {
      toasts.value.shift();
    }

    const id = toastId++;
    const newToast: Toast = {
      id,
      ...options,
    };
    toasts.value.push(newToast); // Thêm toast mới vào cuối mảng

    // Tự động xóa toast sau một khoảng thời gian
    const duration = options.thoiLuong ?? DEFAULT_DURATION;
    setTimeout(() => {
      dongToast(id);
    }, duration);
  };

  // `readonly` để ngăn chặn việc sửa đổi trực tiếp mảng toasts từ bên ngoài
  return {
    mangToast: readonly(toasts),
    showToast: hienThiToast,
    removeToast: dongToast,
  };
}
