import { computed, type Ref, type ComputedRef } from "vue";

interface BreadcrumbItem {
  text: string;
  to?: string;
}

/**
 * Tự động thêm "Trang chủ" vào đầu danh sách breadcrumbs.
 * @param tailItems - Một Ref chứa mảng các mục breadcrumb (không bao gồm Trang chủ).
 * @returns Một ComputedRef chứa mảng breadcrumbs hoàn chỉnh.
 */
export function useBreadcrumbs(
  tailItems: Ref<BreadcrumbItem[]>
): ComputedRef<BreadcrumbItem[]> {
  const homeBreadcrumb: BreadcrumbItem = {
    text: "Trang chủ",
    to: "/trang-chu",
  };
  return computed(() => [homeBreadcrumb, ...tailItems.value]);
}
