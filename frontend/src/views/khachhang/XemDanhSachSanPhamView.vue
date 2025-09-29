<template>
  <CustomerWithNav>
    <ThemVaoGioHangModal
      :visible="isAddToCartModalVisible"
      :sanPham="selectedProduct"
      @dong="closeAddToCartModal"
      @them="handleAddToCart"
    />

    <div class="max-w-7xl mx-auto px-4 py-6">
      <!-- Breadcrumbs -->
      <Breadcrumbs :items="breadcrumbItems" class="mb-4" />

      <!-- Mobile Filter Toggle -->
      <div class="lg:hidden mb-4">
        <button
          @click="toggleMobileFilter"
          class="w-full px-4 py-2 bg-white border rounded-md flex justify-between items-center"
        >
          <span class="font-semibold">Bộ lọc</span>
          <i
            :class="
              isFilterVisibleOnMobile
                ? 'fi-rr-angle-small-up'
                : 'fi-rr-angle-small-down'
            "
            class="text-xl flex items-center"
          ></i>
        </button>
      </div>

      <div class="flex flex-col lg:flex-row gap-6">
        <!-- Filters -->
        <aside
          class="w-full lg:w-64 shrink-0"
          :class="isFilterVisibleOnMobile ? 'block' : 'hidden lg:block'"
        >
          <AccordionSanPham
            :danhMucOption="filterData.danhMucs"
            :nhaSanXuatOption="filterData.nhaSanXuats"
            :khoangGia="filterData.khoangGia"
            v-model:danhMuc="selectedCategory"
            v-model:nhaSanXuat="selectedManufacturer"
            v-model:donGia="selectedPrice"
            v-model:sapXep="selectedSort"
          />
        </aside>

        <!-- Product Grid -->
        <main class="flex-1 min-h-[500px]">
          <!-- Loading State (Skeleton) -->
          <div
            v-if="isLoading"
            class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4"
          >
            <CardSanPhamSkeleton v-for="n in pageSize" :key="n" />
          </div>

          <!-- Error State -->
          <div v-else-if="apiError" class="text-center py-20">
            <p class="text-xl text-red-500 font-semibold">{{ apiError }}</p>
          </div>

          <!-- Content -->
          <div v-else>
            <header class="mb-4">
              <h1 class="text-xl font-bold">{{ pageTitle }}</h1>
              <p class="text-sm text-gray-600">
                Tìm thấy {{ totalElements }} sản phẩm.
              </p>
            </header>

            <GridSanPham
              v-if="dsSanPham.length > 0"
              :dsSanPham="dsSanPham"
              :trangHienTai="currentPage"
              :tongSoTrang="totalPages"
              @them-vao-gio-hang="showAddToCartModal"
              @chuyen-trang="(trang: number) => (currentPage = trang)"
            />
            <p v-else class="text-center py-10 text-gray-500">
              Không có sản phẩm nào trong danh mục này.
            </p>
          </div>
        </main>
      </div>
    </div>
  </CustomerWithNav>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import CustomerWithNav from "@/components/layouts/CustomerWithNav.vue";
import AccordionSanPham from "@/components/base/AccordionSanPham.vue";
import GridSanPham from "@/components/base/GridSanPham.vue";
import Breadcrumbs from "@/components/base/Breadcrumbs.vue";
import CardSanPhamSkeleton from "@/components/base/card/CardSanPhamSkeleton.vue";
import ThemVaoGioHangModal from "@/components/xemsanpham/ThemVaoGioHangModal.vue";
import { useToast } from "@/composables/useToast";
import { useCartStore } from "@/store/giohang.store";
import { useDanhMucStore } from "@/store/danhmuc.store";
import { useAuthStore } from "@/store/auth.store";
import {
  timKiemVaLocSanPham,
  getSanPhamTheoNhan,
  getSanPhamBanChay,
  getFilterOptions,
  NhanSanPhamKey,
  createSortString,
} from "@/service/sanpham.service";
import { themVaoGioHang } from "@/service/giohang.service";
import type {
  SanPhamResponse,
  BreadcrumbItem,
  FilterParams,
  TimKiemFilterParams,
} from "@/types/sanpham.types";
import type {
  DanhMucMenuItem,
  DanhMucConMenuItem,
} from "@/types/danhmuc.types";
import type { FilterDataResponse } from "@/types/filter.types";
import type { PageableParams } from "@/types/api.types";
import { AxiosError } from "axios";
import type { FilterOption } from "@/types/filter.types";

const route = useRoute();
const router = useRouter();
const { showToast } = useToast();
const cartStore = useCartStore();
const danhMucStore = useDanhMucStore();
const authStore = useAuthStore();

// --- State for UI ---
const isFilterVisibleOnMobile = ref(false);
const isAddToCartModalVisible = ref(false);
const pageTitle = ref("");
const breadcrumbItems = ref<BreadcrumbItem[]>([]);

// --- State for Filters ---
const selectedCategory = ref<number | null>(null);
const selectedManufacturer = ref<string | null>(null);
const selectedPrice = ref(0);
const selectedSort = ref<"asc" | "desc">("asc");

// --- State for Data ---
const dsSanPham = ref<SanPhamResponse[]>([]);
const isLoading = ref(true);
const apiError = ref<string | null>(null);
const selectedProduct = ref<SanPhamResponse | null>(null);
const filterData = ref<FilterDataResponse>({
  danhMucs: [],
  nhaSanXuats: [],
  khoangGia: { min: 0, max: 0 },
});

// --- State for Pagination ---
const currentPage = ref(0); // API is 0-indexed
const totalPages = ref(0);
const totalElements = ref(0);
const pageSize = 20; // Số sản phẩm mỗi trang

// --- Methods ---

/**
 * Finds category details (ID, breadcrumbs) by searching the category tree from the store.
 * @param targetSlug The slug of the category to find.
 * @param parentSlug The slug of the parent category, if it's a child category route.
 * @param categories The category tree from the Pinia store.
 * @returns An object containing the found category and its breadcrumbs.
 */
function findCategoryInfoBySlug(
  targetSlug: string,
  parentSlug: string | undefined,
  categories: DanhMucMenuItem[]
): {
  category: DanhMucMenuItem | DanhMucConMenuItem | null;
  breadcrumbs: BreadcrumbItem[];
} {
  for (const parent of categories) {
    // Case 1: It's a parent category
    if (!parentSlug && parent.slug === targetSlug) {
      return {
        category: parent,
        breadcrumbs: [
          { text: "Trang chủ", to: "/trang-chu" },
          { text: parent.tenDanhMuc },
        ],
      };
    }

    // Case 2: It's a child category
    if (parentSlug && parent.slug === parentSlug) {
      const child = parent.danhMucCons?.find((c) => c.slug === targetSlug);
      if (child) {
        return {
          category: child,
          breadcrumbs: [
            { text: "Trang chủ", to: "/trang-chu" },
            { text: parent.tenDanhMuc, to: `/danh-muc/${parent.slug}` },
            { text: child.tenDanhMuc },
          ],
        };
      }
    }
  }
  return { category: null, breadcrumbs: [] };
}

/**
 * Lấy cấu hình cho route hiện tại, bao gồm tiêu đề, breadcrumbs, và promise để lấy dữ liệu.
 * Điều này giúp tách biệt logic cho từng loại trang (danh mục, sản phẩm mới, v.v.)
 * ra khỏi hàm fetchProducts chính.
 */
function getRouteConfig() {
  const path = route.path;
  const tuKhoa = (route.query.q as string) || undefined;

  const pageable: PageableParams = {
    page: currentPage.value,
    size: pageSize,
    sort: selectedSort.value
      ? createSortString("donGia", selectedSort.value)
      : undefined,
  };

  const accordionFilters: TimKiemFilterParams = {
    maDanhMuc: selectedCategory.value ?? undefined,
    nhaSanXuat: selectedManufacturer.value ?? undefined,
    giaToiDa: selectedPrice.value > 0 ? selectedPrice.value : undefined,
  };

  let productApiPromise;
  const filterApiParams: FilterParams = { tuKhoa };
  let currentCategory: DanhMucMenuItem | DanhMucConMenuItem | null = null;

  if (path.startsWith("/danh-muc")) {
    const parentSlug = route.params.danhMucCon
      ? (route.params.danhMucCha as string)
      : undefined;
    const targetSlug = (route.params.danhMucCon ||
      route.params.danhMucCha) as string;

    const { category, breadcrumbs } = findCategoryInfoBySlug(
      targetSlug,
      parentSlug,
      danhMucStore.menuItems
    );

    if (!category) throw new Error("Không tìm thấy danh mục phù hợp.");

    currentCategory = category;
    pageTitle.value = category.tenDanhMuc;
    breadcrumbItems.value = breadcrumbs;
    filterApiParams.danhMucSlug = category.slug;

    const productApiFilters: TimKiemFilterParams = {
      ...accordionFilters,
      maDanhMuc: selectedCategory.value ?? category.maDanhMuc,
    };
    productApiPromise = timKiemVaLocSanPham(productApiFilters, pageable);
  } else if (path.startsWith("/san-pham-moi")) {
    pageTitle.value = "Sản phẩm mới";
    breadcrumbItems.value = [
      { text: "Trang chủ", to: "/trang-chu" },
      { text: "Sản phẩm mới" },
    ];
    filterApiParams.nhan = NhanSanPhamKey.MOI;
    productApiPromise = getSanPhamTheoNhan(
      NhanSanPhamKey.MOI,
      accordionFilters,
      pageable
    );
  } else if (path.startsWith("/ban-chay")) {
    pageTitle.value = "Sản phẩm bán chạy";
    breadcrumbItems.value = [
      { text: "Trang chủ", to: "/trang-chu" },
      { text: "Sản phẩm bán chạy" },
    ];
    filterApiParams.banChay = true;
    productApiPromise = getSanPhamBanChay(accordionFilters, pageable);
  } else if (path.startsWith("/tim-kiem")) {
    pageTitle.value = `Kết quả cho "${tuKhoa}"`;
    breadcrumbItems.value = [
      { text: "Trang chủ", to: "/trang-chu" },
      { text: `Tìm kiếm: ${tuKhoa}` },
    ];
    productApiPromise = timKiemVaLocSanPham(
      { ...accordionFilters, tuKhoa },
      pageable
    );
  } else {
    throw new Error(`Đường dẫn không được hỗ trợ: ${path}`);
  }

  return { productApiPromise, filterApiParams, currentCategory };
}

const toggleMobileFilter = () => {
  isFilterVisibleOnMobile.value = !isFilterVisibleOnMobile.value;
};

function showAddToCartModal(sanPham: SanPhamResponse) {
  selectedProduct.value = sanPham;
  isAddToCartModalVisible.value = true;
}

function closeAddToCartModal() {
  isAddToCartModalVisible.value = false;
  selectedProduct.value = null;
}

async function handleAddToCart(payload: {
  sanPham: SanPhamResponse;
  soLuong: number;
}) {
  // Kiểm tra nếu người dùng chưa đăng nhập
  if (!authStore.isLoggedIn) {
    closeAddToCartModal(); // Đóng modal trước khi chuyển hướng
    showToast({
      thongBao: "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng.",
      loai: "loi",
    });
    // Chuyển hướng đến trang đăng nhập và lưu lại trang hiện tại để quay về
    router.push({
      name: "DangNhap",
      query: { redirect: router.currentRoute.value.fullPath },
    });
    return; // Dừng hàm tại đây
  }

  closeAddToCartModal();
  try {
    const response = await themVaoGioHang({
      maSanPham: payload.sanPham.maSanPham,
      soLuong: payload.soLuong,
    });
    cartStore.setCartCount(response.tongSoLuongSanPham);
    showToast({ thongBao: response.message, loai: "thanhCong" });
  } catch (error: any) {
    const message =
      error.response?.data?.message || "Thêm vào giỏ hàng thất bại!";
    showToast({ thongBao: message, loai: "loi" });
  }
}

const fetchProducts = async () => {
  isLoading.value = true;
  apiError.value = null;
  try {
    await danhMucStore.fetchMenuDanhMuc();
    if (danhMucStore.error) throw new Error(danhMucStore.error);

    // --- Xác định danh mục cha/con theo route ---
    let danhMucCons: FilterOption[] = [];
    if (route.path.startsWith("/danh-muc")) {
      const parentSlug = route.params.danhMucCha as string;
      const parent = danhMucStore.menuItems.find((p) => p.slug === parentSlug);
      if (parent) {
        danhMucCons =
          parent.danhMucCons?.map((c) => ({
            maDanhMuc: c.maDanhMuc,
            tenDanhMuc: c.tenDanhMuc,
            slug: c.slug,
          })) ?? [];
        // Nếu có danhMucCon thì chọn nó, nếu không thì null
        if (route.params.danhMucCon) {
          // ...existing code...
          watch(selectedCategory, (newVal, oldVal) => {
            // Nếu đang ở trang danh mục cha, chuyển sang route danh mục con khi chọn
            if (
              route.path.startsWith("/danh-muc") &&
              newVal !== null &&
              route.params.danhMucCha
            ) {
              const parentSlug = route.params.danhMucCha as string;
              const parent = danhMucStore.menuItems.find(
                (p) => p.slug === parentSlug
              );
              const child = parent?.danhMucCons?.find(
                (c) => c.maDanhMuc === newVal
              );
              if (child) {
                router.push(`/danh-muc/${parentSlug}/${child.slug}`);
                return;
              }
            }
            // Nếu chọn "Tất cả", về route cha
            if (
              route.path.startsWith("/danh-muc") &&
              newVal === null &&
              route.params.danhMucCha
            ) {
              router.push(`/danh-muc/${route.params.danhMucCha}`);
              return;
            }
            // Khi user chọn lại filter, refetch như cũ
            currentPage.value = 0;
            fetchProducts();
          });
          // ...existing code...          import type { FilterOption } from "@/types/filter.types";
          // ...existing code...
          const child = parent.danhMucCons?.find(
            (c) => c.slug === route.params.danhMucCon
          );
          selectedCategory.value = child?.maDanhMuc ?? null;
        } else {
          selectedCategory.value = null;
        }
      }
    }

    // ...lấy filter options từ API như cũ...
    const { productApiPromise, filterApiParams, currentCategory } =
      getRouteConfig();
    const filterOptionsPromise = getFilterOptions(filterApiParams);
    const [productResponse, filterResponse] = await Promise.all([
      productApiPromise,
      filterOptionsPromise,
    ]);

    // --- Truyền đúng danh mục con vào Accordion ---
    filterData.value = {
      ...filterResponse,
      danhMucs: danhMucCons.length > 0 ? danhMucCons : filterResponse.danhMucs,
    };

    dsSanPham.value = productResponse.content;
    totalPages.value = productResponse.totalPages;
    totalElements.value = productResponse.totalElements;
  } catch (err) {
    console.error("Lỗi khi tải danh sách sản phẩm:", err);
    if (err instanceof AxiosError && err.response?.data?.message) {
      apiError.value = err.response.data.message;
    } else if (err instanceof Error) {
      apiError.value = err.message;
    } else {
      apiError.value = "Không thể tải dữ liệu sản phẩm. Vui lòng thử lại.";
    }
    dsSanPham.value = [];
  } finally {
    isLoading.value = false;
  }
};

// --- Lifecycle & Watchers ---
onMounted(fetchProducts);

// Watch route thay đổi để đồng bộ filter & breadcrumbs
watch(
  () => route.fullPath,
  async () => {
    // Reset bộ lọc khi chuyển route
    selectedManufacturer.value = null;
    selectedPrice.value = 0;
    selectedSort.value = "asc";

    // Đồng bộ selectedCategory với route danh mục
    if (route.path.startsWith("/danh-muc")) {
      await danhMucStore.fetchMenuDanhMuc();
      const parentSlug = route.params.danhMucCon
        ? (route.params.danhMucCha as string)
        : undefined;
      const targetSlug = (route.params.danhMucCon ||
        route.params.danhMucCha) as string;
      const { category, breadcrumbs } = findCategoryInfoBySlug(
        targetSlug,
        parentSlug,
        danhMucStore.menuItems
      );
      if (category) {
        selectedCategory.value = category.maDanhMuc;
        breadcrumbItems.value = breadcrumbs;
        pageTitle.value = category.tenDanhMuc;
      }
    } else {
      selectedCategory.value = null;
    }

    // Reset về trang đầu tiên. Watcher của currentPage sẽ xử lý việc gọi API.
    // Nếu đã ở trang 0, gọi API thủ công.
    if (currentPage.value !== 0) {
      currentPage.value = 0;
    } else {
      fetchProducts();
    }
  }
);

// Watch selectedCategory để refetch khi user chọn lại filter
watch(selectedCategory, (newVal, oldVal) => {
  // Nếu đang ở trang danh mục cha, chuyển sang route danh mục con khi chọn
  if (
    route.path.startsWith("/danh-muc") &&
    newVal !== null &&
    route.params.danhMucCha
  ) {
    const parentSlug = route.params.danhMucCha as string;
    const parent = danhMucStore.menuItems.find((p) => p.slug === parentSlug);
    const child = parent?.danhMucCons?.find((c) => c.maDanhMuc === newVal);
    if (child) {
      router.push(`/danh-muc/${parentSlug}/${child.slug}`);
      return;
    }
  }
  // Nếu chọn "Tất cả", về route cha
  if (
    route.path.startsWith("/danh-muc") &&
    newVal === null &&
    route.params.danhMucCha
  ) {
    router.push(`/danh-muc/${route.params.danhMucCha}`);
    return;
  }
  // Khi user chọn lại filter, refetch như cũ
  // Reset về trang đầu tiên. Watcher của currentPage sẽ xử lý việc gọi API.
  if (currentPage.value !== 0) {
    currentPage.value = 0;
  } else {
    // Nếu đã ở trang 0, watcher sẽ không chạy, cần gọi thủ công.
    fetchProducts();
  }
});

// Watch các bộ lọc khác (nhà sản xuất, giá, sắp xếp) để tải lại dữ liệu
watch([selectedManufacturer, selectedPrice, selectedSort], () => {
  // Reset về trang đầu tiên mỗi khi bộ lọc thay đổi
  if (currentPage.value !== 0) {
    currentPage.value = 0;
  } else {
    fetchProducts();
  }
});

// Watch trang hiện tại để tải lại dữ liệu khi chuyển trang
watch(currentPage, fetchProducts);
</script>
