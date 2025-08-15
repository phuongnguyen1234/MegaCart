<template>
  <header
    class="top-0 left-0 right-0 z-[1000] flex items-center justify-between px-[10px] pr-[30px] h-[80px] bg-[linear-gradient(135deg,_#BBDEFB,_#1976D2)] relative"
  >
    <!-- Logo -->
    <router-link to="/trang-chu" class="shrink-0">
      <img
        src="@/assets/images/megacart-logo.png"
        alt="MegaCart Logo"
        class="h-[66px]"
      />
    </router-link>

    <!-- Search -->
    <div
      ref="searchContainer"
      class="absolute left-1/2 -translate-x-1/2 flex items-center w-1/2 max-w-[700px]"
    >
      <input
        v-model="searchQuery"
        @input="fetchSuggestions"
        @keydown.enter="performSearch"
        type="text"
        placeholder="Tìm kiếm sản phẩm..."
        class="bg-white w-full h-[50px] pl-[20px] pr-[40px] py-[5px] text-[16px] rounded-[26px] border border-[#ccc] focus:outline-none"
      />
      <button
        @click="performSearch"
        class="absolute right-[5px] w-[40px] h-[40px] rounded-full cursor-pointer flex items-center justify-center border-none bg-[linear-gradient(135deg,_#1976D2,_#0D47A1)] hover:bg-[linear-gradient(135deg,_#2196F3,_#1565C0)]"
      >
        <i class="fi fi-rr-search text-white text-[20px]"></i>
      </button>

      <!-- Danh sách gợi ý -->
      <ul
        v-if="suggestions.length > 0"
        class="absolute top-full mt-1 w-full max-h-[250px] overflow-y-auto bg-white border border-gray-300 rounded-[10px] shadow z-[1001]"
      >
        <li
          v-for="(item, index) in suggestions"
          :key="index"
          class="px-4 py-2 hover:bg-blue-100 cursor-pointer"
          @click="selectSuggestion(item)"
        >
          {{ item }}
        </li>
      </ul>
    </div>

    <!-- User & Cart -->
    <div class="flex items-center gap-[20px] relative">
      <!-- Hiển thị khi người dùng đã đăng nhập -->
      <div
        ref="profileMenuContainer"
        v-if="authStore.isLoggedIn"
        @click="toggleProfileMenu"
        class="relative flex items-center text-white no-underline cursor-pointer"
      >
        <i class="fi fi-rr-user text-[24px]"></i>
        <span class="inline-block truncate max-w-[200px] select-none pl-[10px]"
          >Xin chào, {{ authStore.userName }}!</span
        >
        <i
          :class="
            isProfileMenuOpen
              ? 'fi-rr-angle-small-up'
              : 'fi-rr-angle-small-down'
          "
          class="ml-1 text-[24px] flex items-center"
        ></i>

        <!-- Dropdown menu tài khoản -->
        <div
          v-if="isProfileMenuOpen"
          class="absolute top-full right-0 mt-2 w-[220px] bg-white shadow-lg rounded-md overflow-hidden z-[49]"
        >
          <ul class="flex flex-col">
            <li
              class="flex items-center gap-2 px-4 py-3 hover:bg-blue-100 cursor-pointer"
              @click.stop="goToEditAccount"
            >
              <i class="fi fi-rr-edit text-blue-600 text-[18px]"></i>
              <span class="text-blue-600 font-semibold select-none"
                >Chỉnh sửa tài khoản
              </span>
            </li>
            <li
              class="flex items-center gap-2 px-4 py-3 hover:bg-blue-100 cursor-pointer"
              @click.stop="goToOrderHistory"
            >
              <i class="fi fi-rr-time-past text-blue-600 text-[18px]"></i>
              <span class="text-blue-600 font-semibold select-none"
                >Lịch sử mua hàng
              </span>
            </li>
            <li
              class="flex items-center gap-2 px-4 py-3 hover:bg-red-100 cursor-pointer"
              @click.stop="showLogoutConfirm"
            >
              <i class="fi fi-rr-exit text-red-600 text-[18px]"></i>
              <span class="text-red-600 font-semibold select-none"
                >Đăng xuất
              </span>
            </li>
          </ul>
        </div>
      </div>

      <!-- Hiển thị khi người dùng chưa đăng nhập -->
      <router-link
        v-else
        :to="{ name: 'DangNhap' }"
        class="flex items-center text-white no-underline cursor-pointer"
      >
        <i class="fi fi-rr-user text-[24px]"></i>
        <span class="select-none pl-[10px]">Đăng nhập</span>
      </router-link>

      <router-link
        to="/gio-hang"
        class="relative flex items-center no-underline text-white"
      >
        <i class="fi fi-rr-shopping-cart text-[24px]"></i>
        <div
          v-if="cartStore.soLuongSanPham > 0"
          class="absolute -top-[5px] -right-[8px] bg-yellow-300 rounded-full px-[6px] text-[14px] text-black select-none"
        >
          {{ cartStore.soLuongSanPham }}
        </div>
      </router-link>
    </div>

    <!-- Modal xác nhận đăng xuất -->
    <ConfirmModal
      :hienThi="isLogoutModalVisible"
      tieuDe="Xác nhận Đăng xuất"
      noiDung="Bạn có chắc chắn muốn đăng xuất không?"
      @xac-nhan="handleLogout"
      @huy="hideLogoutConfirm"
    />

    <!-- Loading overlay -->
    <Loading :visible="isLoading" />
  </header>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from "vue";
import { useRouter } from "vue-router";
import Loading from "@/components/base/Loading.vue";
import ConfirmModal from "@/components/base/modals/ConfirmModal.vue";
import { useAuthStore } from "@/store/auth.store";
import { useCartStore } from "@/store/giohang.store";
import { goiYTimKiem } from "@/service/sanpham.service";

const isProfileMenuOpen = ref(false);
const searchContainer = ref<HTMLElement | null>(null);
const profileMenuContainer = ref<HTMLElement | null>(null);
const toggleProfileMenu = () => {
  isProfileMenuOpen.value = !isProfileMenuOpen.value;
};

const goToEditAccount = () => {
  console.log("Đi đến chỉnh sửa tài khoản");
  router.push({ name: "TaiKhoan" });
  isProfileMenuOpen.value = false;
};

const goToOrderHistory = () => {
  console.log("Đi đến lịch sử mua hàng");
  router.push({ name: "LichSuMuaHang" });
  isProfileMenuOpen.value = false;
};

const router = useRouter();
const authStore = useAuthStore();
const cartStore = useCartStore();
const isLogoutModalVisible = ref(false);
const isLoading = ref(false);

const showLogoutConfirm = () => {
  isProfileMenuOpen.value = false;
  isLogoutModalVisible.value = true;
};

const hideLogoutConfirm = () => {
  isLogoutModalVisible.value = false;
};

const handleLogout = async () => {
  hideLogoutConfirm();
  isLoading.value = true;
  try {
    // Hành động logout trong store đã xử lý việc xóa dữ liệu và chuyển hướng.
    await authStore.logout();
  } catch (error) {
    console.error("Đăng xuất thất bại:", error);
  } finally {
    isLoading.value = false;
  }
};

const searchQuery = ref("");
const suggestions = ref<string[]>([]);
let debounceTimer: number | undefined;

const fetchSuggestions = () => {
  clearTimeout(debounceTimer);

  const query = searchQuery.value.trim();
  if (!query) {
    suggestions.value = [];
    return;
  }

  // Chỉ gọi API sau khi người dùng ngừng gõ 300ms
  debounceTimer = window.setTimeout(async () => {
    try {
      suggestions.value = await goiYTimKiem(query);
    } catch (error) {
      console.error("Lỗi khi lấy gợi ý tìm kiếm:", error);
      suggestions.value = []; // Xóa gợi ý nếu có lỗi
    }
  }, 300);
};

const performSearch = () => {
  const query = searchQuery.value.trim();
  if (query) {
    router.push({ name: "KetQuaTimKiem", query: { q: query } });
  }
  // Xóa input và ẩn gợi ý sau khi tìm kiếm
  searchQuery.value = "";
  suggestions.value = [];
};

const selectSuggestion = (item: string) => {
  searchQuery.value = item; // Cập nhật ô tìm kiếm với gợi ý đã chọn
  performSearch(); // Thực hiện tìm kiếm
};

const handleClickOutside = (event: MouseEvent) => {
  const target = event.target as Node;

  // Ẩn danh sách gợi ý tìm kiếm nếu click ra ngoài
  if (searchContainer.value && !searchContainer.value.contains(target)) {
    suggestions.value = [];
  }

  // Đóng menu người dùng nếu click ra ngoài
  if (
    profileMenuContainer.value &&
    !profileMenuContainer.value.contains(target)
  ) {
    isProfileMenuOpen.value = false;
  }
};

onMounted(() => {
  document.addEventListener("mousedown", handleClickOutside);
});

onBeforeUnmount(() => {
  document.removeEventListener("mousedown", handleClickOutside);
});
</script>
