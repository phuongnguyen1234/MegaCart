<template>
  <aside
    class="w-[250px] h-screen bg-gray-800 text-white flex flex-col fixed top-0 left-0 z-40"
  >
    <!-- Logo hoặc Tiêu đề -->
    <div class="p-4 border-b border-gray-700 text-center">
      <h1 class="text-xl font-bold text-white">MegaCart</h1>
    </div>

    <!-- Các liên kết điều hướng (hiển thị có điều kiện) -->
    <nav v-if="variant === 'full'" class="flex-grow overflow-y-auto">
      <ul class="space-y-1 p-2">
        <!-- Dashboard -->
        <li v-if="permissions.canViewDashboard">
          <router-link
            to="/quan-ly/dashboard"
            class="flex items-center gap-3 px-4 py-2.5 rounded-md hover:bg-gray-700 transition-colors duration-300"
            active-class="bg-sky-600 font-bold text-white"
            exact
          >
            <Icon icon="lucide:layout-dashboard" class="w-5 h-5" />
            <span>Dashboard</span>
          </router-link>
        </li>

        <!-- Đơn hàng -->
        <li v-if="permissions.canManageOrders">
          <router-link
            to="/quan-ly/don-hang"
            class="flex items-center gap-3 px-4 py-2.5 rounded-md hover:bg-gray-700 transition-colors duration-300"
            active-class="bg-sky-600 font-bold text-white"
            exact
          >
            <Icon icon="lucide:shopping-cart" class="w-5 h-5" />
            <span>Đơn hàng</span>
          </router-link>
        </li>

        <!-- Giao hàng -->
        <li v-if="permissions.canManageShipping">
          <router-link
            to="/quan-ly/giao-hang"
            class="flex items-center gap-3 px-4 py-2.5 rounded-md hover:bg-gray-700 transition-colors duration-300"
            active-class="bg-sky-600 font-bold text-white"
            exact
          >
            <Icon icon="lucide:truck" class="w-5 h-5" />
            <span>Giao hàng</span>
          </router-link>
        </li>

        <!-- Kho hàng -->
        <li v-if="permissions.canManageWarehouse">
          <router-link
            to="/quan-ly/kho-hang"
            class="flex items-center gap-3 px-4 py-2.5 rounded-md hover:bg-gray-700 transition-colors duration-300"
            active-class="bg-sky-600 font-bold text-white"
            exact
          >
            <Icon icon="lucide:warehouse" class="w-5 h-5" />
            <span>Kho hàng</span>
          </router-link>
        </li>

        <!-- Sản phẩm - Danh mục (Accordion) -->
        <li v-if="permissions.canManageProducts">
          <div
            @click="toggleSubMenu('products')"
            class="flex justify-between items-center px-4 py-2.5 rounded-md cursor-pointer hover:bg-gray-700 transition-colors duration-300"
          >
            <div class="flex items-center gap-3">
              <Icon icon="lucide:package" class="w-5 h-5" />
              <span>Sản phẩm - Danh mục</span>
            </div>
            <svg
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 20 20"
              fill="currentColor"
              class="w-5 h-5 transform transition-transform duration-300"
              :class="{ 'rotate-90': subMenu.products }"
            >
              <path
                fill-rule="evenodd"
                d="M7.21 14.77a.75.75 0 01.02-1.06L11.168 10 7.23 6.29a.75.75 0 111.04-1.08l4.5 4.25a.75.75 0 010 1.08l-4.5 4.25a.75.75 0 01-1.06-.02z"
                clip-rule="evenodd"
              />
            </svg>
          </div>
          <transition name="slide-fade">
            <ul v-if="subMenu.products" class="pl-4 mt-1 space-y-1">
              <li>
                <router-link
                  to="/quan-ly/san-pham"
                  class="block px-4 py-2 rounded-md hover:bg-gray-600 transition-colors duration-300"
                  active-class="text-sky-400 font-bold"
                  exact
                >
                  Sản phẩm
                </router-link>
              </li>
              <li>
                <router-link
                  to="/quan-ly/danh-muc"
                  class="block px-4 py-2 rounded-md hover:bg-gray-600 transition-colors duration-300"
                  active-class="text-sky-400 font-bold"
                  exact
                >
                  Danh mục
                </router-link>
              </li>
            </ul>
          </transition>
        </li>

        <!-- Tài khoản (Accordion) -->
        <li v-if="permissions.canManageAccounts">
          <div
            @click="toggleSubMenu('accounts')"
            class="flex justify-between items-center px-4 py-2.5 rounded-md cursor-pointer hover:bg-gray-700 transition-colors duration-300"
          >
            <div class="flex items-center gap-3">
              <Icon icon="lucide:users" class="w-5 h-5" />
              <span>Tài khoản</span>
            </div>
            <svg
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 20 20"
              fill="currentColor"
              class="w-5 h-5 transform transition-transform duration-300"
              :class="{ 'rotate-90': subMenu.accounts }"
            >
              <path
                fill-rule="evenodd"
                d="M7.21 14.77a.75.75 0 01.02-1.06L11.168 10 7.23 6.29a.75.75 0 111.04-1.08l4.5 4.25a.75.75 0 010 1.08l-4.5 4.25a.75.75 0 01-1.06-.02z"
                clip-rule="evenodd"
              />
            </svg>
          </div>
          <transition name="slide-fade">
            <ul v-if="subMenu.accounts" class="pl-4 mt-1 space-y-1">
              <li>
                <router-link
                  to="/quan-ly/khach-hang"
                  class="block px-4 py-2 rounded-md hover:bg-gray-600 transition-colors duration-300"
                  active-class="text-sky-400 font-bold"
                  exact
                >
                  Khách hàng
                </router-link>
              </li>
              <li>
                <router-link
                  to="/quan-ly/nhan-vien"
                  class="block px-4 py-2 rounded-md hover:bg-gray-600 transition-colors duration-300"
                  active-class="text-sky-400 font-bold"
                  exact
                >
                  Nhân viên
                </router-link>
              </li>
            </ul>
          </transition>
        </li>
      </ul>
    </nav>

    <!-- Nút Đăng xuất -->
    <div class="p-5 border-t border-gray-700">
      <button
        @click="logout"
        class="cursor-pointer w-full flex items-center justify-center gap-2 py-2 bg-red-600 hover:bg-red-700 text-white rounded text-center transition-colors duration-300"
      >
        <Icon icon="lucide:log-out" class="w-5 h-5" />
        Đăng xuất
      </button>
    </div>

    <!-- Modal xác nhận đăng xuất -->
    <ConfirmModal
      :hienThi="isLogoutModalVisible"
      tieuDe="Xác nhận đăng xuất"
      noiDung="Bạn có chắc chắn muốn đăng xuất không?"
      @xac-nhan="handleConfirmLogout"
      @huy="isLogoutModalVisible = false"
    />
    <!-- Loading overlay -->
    <Loading :visible="isLoading" />
  </aside>
</template>

<script setup lang="ts">
import { reactive, ref, computed, withDefaults } from "vue";
import { Icon } from "@iconify/vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "@/store/auth.store";
import ConfirmModal from "@/components/base/modals/ConfirmModal.vue";
import Loading from "@/components/base/Loading.vue";
import { VaiTroKey, ViTriNhanVienKey } from "@/types/taikhoan.types";
import type { JwtPayload, NhanVienJwtPayload } from "@/types/api.types";

interface Props {
  variant?: "full" | "minimal";
}

const { variant } = withDefaults(defineProps<Props>(), {
  variant: "full",
});

const router = useRouter();

const subMenu = reactive({
  products: false,
  accounts: false,
});

const authStore = useAuthStore();
const isLogoutModalVisible = ref(false);
const isLoading = ref(false);

/**
 * Lấy payload của nhân viên từ auth store.
 * Giả định rằng authStore có một state/getter là `payload` chứa thông tin giải mã từ JWT.
 */
const userPayload = computed(() => authStore.payload as JwtPayload | null);

/**
 * Tạo một đối tượng quyền dựa trên vị trí (`viTri`) của nhân viên.
 * Cách tiếp cận này giúp template gọn gàng và logic tập trung ở một nơi.
 */
const permissions = computed(() => {
  const payload = userPayload.value;

  if (!payload) {
    // Nếu không có payload, ẩn tất cả các mục menu để đảm bảo an toàn.
    return { canViewDashboard: false };
  }

  // Admin có vai trò riêng và có thể thấy tất cả mọi thứ.
  const isAdmin = payload.role === VaiTroKey.ADMIN;

  // Nếu không phải admin, kiểm tra xem có phải nhân viên không và lấy vị trí.
  const viTri =
    payload.role === VaiTroKey.NHAN_VIEN
      ? (payload as NhanVienJwtPayload).viTri
      : null;

  return {
    canViewDashboard: isAdmin, // Chỉ admin mới có thể xem dashboard.
    canManageOrders: isAdmin || viTri === ViTriNhanVienKey.NHAN_VIEN_BAN_HANG,
    canManageShipping: isAdmin, // Chỉ admin mới có thể quản lý giao hàng từ đây
    canManageWarehouse:
      isAdmin || viTri === ViTriNhanVienKey.NHAN_VIEN_QUAN_LI_KHO,
    canManageProducts: isAdmin, // Chỉ admin mới được quản lý sản phẩm/danh mục.
    canManageAccounts: isAdmin, // Chỉ admin mới được quản lý tài khoản.
  };
});

const toggleSubMenu = (menu: keyof typeof subMenu) => {
  subMenu[menu] = !subMenu[menu];
};

/**
 * Mở modal xác nhận khi người dùng click nút "Đăng xuất".
 */
const logout = () => {
  isLogoutModalVisible.value = true;
};

/**
 * Xử lý logic đăng xuất sau khi người dùng xác nhận.
 */
const handleConfirmLogout = async () => {
  isLogoutModalVisible.value = false;
  isLoading.value = true;
  try {
    // Hành động logout trong store sẽ gọi API, xóa token và chuyển hướng
    await authStore.logout();
  } catch (error) {
    console.error("Đăng xuất thất bại:", error);
    // Tùy chọn: hiển thị thông báo lỗi cho người dùng ở đây
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
/* Transition cho accordion */
.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.3s ease-out;
}
.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateY(-10px);
  opacity: 0;
}

/* Transition cho menu chính (ẩn/hiện từ AdminLayout) */
.slide-menu-enter-active,
.slide-menu-leave-active {
  transition: transform 0.3s ease-in-out;
}
.slide-menu-enter-from,
.slide-menu-leave-to {
  transform: translateX(-100%);
}
</style>
