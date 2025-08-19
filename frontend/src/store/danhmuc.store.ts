import { defineStore } from "pinia";
import { ref } from "vue";
import { getMenuDanhMuc } from "@/service/danhmuc.service";
import type { DanhMucMenuItem } from "@/types/danhmuc.types";
import { AxiosError } from "axios";

export const useDanhMucStore = defineStore("danhMuc", () => {
  // --- State ---
  const menuItems = ref<DanhMucMenuItem[]>([]);
  const isLoading = ref(false);
  const error = ref<string | null>(null);

  // --- Actions ---
  async function fetchMenuDanhMuc(force: boolean = false) {
    // Không tải lại nếu dữ liệu đã có sẵn và không bị ép buộc
    if (menuItems.value.length > 0 && !force) {
      return;
    }

    isLoading.value = true;
    error.value = null;
    try {
      menuItems.value = await getMenuDanhMuc();
    } catch (err) {
      console.error("Lỗi khi tải menu danh mục:", err);
      if (err instanceof AxiosError) {
        error.value = "Không thể kết nối đến máy chủ để tải danh mục.";
      } else {
        error.value = "Không thể tải danh mục sản phẩm.";
      }
    } finally {
      isLoading.value = false;
    }
  }

  return { menuItems, isLoading, error, fetchMenuDanhMuc };
});
