import { defineStore } from "pinia";
import { ref } from "vue";
import {
  getMenuDanhMuc,
  getDanhMucOptionsForFilter,
  getDanhSachDanhMuc,
} from "@/service/danhmuc.service";
import type {
  DanhMucMenuItem,
  DanhMucQuanLyResponse,
  DanhMucOptionResponse,
} from "@/types/danhmuc.types";

export const useDanhMucStore = defineStore("danhMuc", () => {
  // --- State ---

  // Dữ liệu cho menu điều hướng của khách hàng
  const menuItems = ref<DanhMucMenuItem[]>([]);
  // Dữ liệu cho các ô chọn và bộ lọc trong trang quản trị (dạng cây cha-con)
  const adminCategoryOptions = ref<DanhMucOptionResponse[]>([]);
  // Dữ liệu cho các ô chọn danh mục (dạng phẳng, tất cả danh mục)
  const allCategoriesFlat = ref<DanhMucQuanLyResponse[]>([]);

  const isLoading = ref(false);
  const error = ref<string | null>(null);

  // --- Actions ---

  /**
   * Tải dữ liệu danh mục cho menu của khách hàng.
   * Dữ liệu sẽ được cache lại để tránh gọi API không cần thiết.
   * @param force - Nếu true, sẽ tải lại dữ liệu ngay cả khi đã có.
   */
  const fetchMenuDanhMuc = async (force = false) => {
    if (menuItems.value.length > 0 && !force) return;

    isLoading.value = true;
    error.value = null;
    try {
      menuItems.value = await getMenuDanhMuc();
    } catch (e) {
      error.value = "Không thể tải danh mục cho menu.";
      console.error(e);
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Tải dữ liệu danh mục cho các ô chọn/bộ lọc của trang quản trị.
   * @param force - Nếu true, sẽ tải lại dữ liệu ngay cả khi đã có.
   */
  const fetchAdminCategoryOptions = async (force = false) => {
    if (adminCategoryOptions.value.length > 0 && !force) return;

    isLoading.value = true;
    error.value = null;
    try {
      adminCategoryOptions.value = await getDanhMucOptionsForFilter();
    } catch (e) {
      error.value = "Không thể tải danh mục cho bộ lọc.";
      console.error(e);
    } finally {
      isLoading.value = false;
    }
  };

  /**
   * Tải danh sách phẳng của TẤT CẢ các danh mục để dùng trong dropdown.
   * Sử dụng một mẹo nhỏ là gọi API danh sách với size lớn để lấy hết.
   * @param force - Nếu true, sẽ tải lại dữ liệu ngay cả khi đã có.
   */
  const fetchAllCategoriesFlat = async (force = false) => {
    if (allCategoriesFlat.value.length > 0 && !force) return;

    isLoading.value = true;
    error.value = null;
    try {
      // Lấy tối đa 1000 danh mục, một con số đủ lớn cho hầu hết các trường hợp
      const response = await getDanhSachDanhMuc({ page: 0, size: 1000 });
      allCategoriesFlat.value = response.content;
    } catch (e) {
      error.value = "Không thể tải danh sách phẳng các danh mục.";
      console.error(e);
    } finally {
      isLoading.value = false;
    }
  };

  return {
    menuItems,
    adminCategoryOptions,
    allCategoriesFlat,
    isLoading,
    error,
    fetchMenuDanhMuc,
    fetchAdminCategoryOptions,
    fetchAllCategoriesFlat,
  };
});
