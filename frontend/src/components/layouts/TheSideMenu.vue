<template>
  <aside
    class="w-[250px] h-screen bg-gray-800 text-white flex flex-col fixed top-0 left-0"
  >
    <nav class="flex-grow overflow-y-auto">
      <ul class="space-y-1 p-2">
        <!-- Dashboard -->
        <li>
          <router-link
            to="/admin/dashboard"
            class="flex items-center gap-3 px-4 py-2.5 rounded-md hover:bg-gray-700 transition-colors duration-300"
            active-class="bg-sky-600 font-bold text-white"
            exact
          >
            <Icon icon="lucide:layout-dashboard" class="w-5 h-5" />
            <span>Dashboard</span>
          </router-link>
        </li>

        <!-- Đơn hàng -->
        <li>
          <router-link
            to="/admin/don-hang"
            class="flex items-center gap-3 px-4 py-2.5 rounded-md hover:bg-gray-700 transition-colors duration-300"
            active-class="bg-sky-600 font-bold text-white"
            exact
          >
            <Icon icon="lucide:shopping-cart" class="w-5 h-5" />
            <span>Đơn hàng</span>
          </router-link>
        </li>

        <!-- Giao hàng -->
        <li>
          <router-link
            to="/admin/giao-hang"
            class="flex items-center gap-3 px-4 py-2.5 rounded-md hover:bg-gray-700 transition-colors duration-300"
            active-class="bg-sky-600 font-bold text-white"
            exact
          >
            <Icon icon="lucide:truck" class="w-5 h-5" />
            <span>Giao hàng</span>
          </router-link>
        </li>

        <!-- Sản phẩm - Danh mục (Accordion) -->
        <li>
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
                  to="/admin/san-pham"
                  class="block px-4 py-2 rounded-md hover:bg-gray-600 transition-colors duration-300"
                  active-class="text-sky-400 font-bold"
                  exact
                >
                  Sản phẩm
                </router-link>
              </li>
              <li>
                <router-link
                  to="/admin/danh-muc"
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
        <li>
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
                  to="/admin/khach-hang"
                  class="block px-4 py-2 rounded-md hover:bg-gray-600 transition-colors duration-300"
                  active-class="text-sky-400 font-bold"
                  exact
                >
                  Khách hàng
                </router-link>
              </li>
              <li>
                <router-link
                  to="/admin/nhan-vien"
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
        class="w-full flex items-center justify-center gap-2 py-2 bg-red-600 hover:bg-red-700 text-white rounded text-center transition-colors duration-300"
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
      @xacNhan="handleConfirmLogout"
      @huy="isLogoutModalVisible = false"
    />
  </aside>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { Icon } from "@iconify/vue";
import { useRouter } from "vue-router";
import ConfirmModal from "@/components/base/modals/ConfirmModal.vue";

const router = useRouter();

const subMenu = reactive({
  products: false,
  accounts: false,
});

const isLogoutModalVisible = ref(false);

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
const handleConfirmLogout = () => {
  localStorage.removeItem("user-token");
  router.push({ name: "DangNhap" });
  isLogoutModalVisible.value = false;
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
