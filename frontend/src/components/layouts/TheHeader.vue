<template>
  <header
    class="fixed top-0 left-0 right-0 z-[1000] flex items-center justify-between px-[10px] pr-[30px] h-[70px] bg-[linear-gradient(135deg,_#BBDEFB,_#1976D2)]"
  >
    <!-- Logo -->
    <a href="/trang-chu" class="shrink-0">
      <img src="@/assets/images/megacart-logo.png" alt="MegaCart Logo" class="h-[66px]" />
    </a>

    <!-- Search -->
    <div class="flex items-center relative w-1/2 flex-1 justify-center max-w-[700px]">
      <input
        v-model="searchQuery"
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
        v-if="filteredSuggestions.length > 0"
        class="absolute top-full mt-1 w-full max-h-[250px] overflow-y-auto bg-white border border-gray-300 rounded-[10px] shadow z-[1001]"
      >
        <li
          v-for="(item, index) in filteredSuggestions"
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
      <div
        @click="toggleProfileMenu"
        class="relative flex items-center text-white no-underline cursor-pointer"
      >
        <i class="fi fi-rr-user text-[24px]"></i>
        <span class="select-none pl-[10px]">Đăng nhập</span>
        <i
          :class="isProfileMenuOpen ? 'fi-rr-angle-small-up' : 'fi-rr-angle-small-down'"
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
              @click="goToEditAccount"
            >
              <i class="fi fi-rr-edit text-blue-600 text-[18px]"></i>
              <span class="text-blue-600 font-semibold select-none"
              >Chỉnh sửa tài khoản
            </span>
            </li>
            <li
              class="flex items-center gap-2 px-4 py-3 hover:bg-blue-100 cursor-pointer"
              @click="goToOrderHistory"
            >
              <i class="fi fi-rr-time-past text-blue-600 text-[18px]"></i>
              <span class="text-blue-600 font-semibold select-none"
              >Lịch sử mua hàng
            </span>
            </li>
            <li
              class="flex items-center gap-2 px-4 py-3 hover:bg-red-100 cursor-pointer"
              @click="showLogoutConfirm"
            >
              <i class="fi fi-rr-exit text-red-600 text-[18px]"></i>
              <span class="text-red-600 font-semibold select-none"
              >Đăng xuất
            </span>
            </li>
          </ul>
        </div>
      </div>

      <a href="/cart" class="relative flex items-center no-underline text-white">
        <i class="fi fi-rr-shopping-cart text-[24px]"></i>
        <div
          class="absolute -top-[5px] -right-[8px] bg-yellow-400 rounded-full px-[6px] text-[14px] text-black select-none"
        >
          0
        </div>
      </a>
    </div>

    <!-- Modal xác nhận đăng xuất -->
    <ConfirmModal
      :visible="isLogoutModalVisible"
      title="Xác nhận Đăng xuất"
      message="Bạn có chắc chắn muốn đăng xuất không?"
      @confirm="handleLogout"
      @cancel="hideLogoutConfirm"
    />

    <!-- Loading overlay -->
    <Loading :visible="isLoading" />
  </header>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import Loading from '@/components/base/Loading.vue'
import ConfirmModal from '@/components/base/ConfirmModal.vue'

const isProfileMenuOpen = ref(false)
const toggleProfileMenu = () => {
  isProfileMenuOpen.value = !isProfileMenuOpen.value
}

const goToEditAccount = () => {
  console.log('Đi đến chỉnh sửa tài khoản')
  // router.push({ name: 'ChinhSuaTaiKhoan' })
}

const goToOrderHistory = () => {
  console.log('Đi đến lịch sử mua hàng')
  // router.push({ name: 'LichSuMuaHang' })
}

const router = useRouter()
const isLogoutModalVisible = ref(false)
const isLoading = ref(false)

const showLogoutConfirm = () => {
  isProfileMenuOpen.value = false // Đóng menu dropdown trước
  isLogoutModalVisible.value = true
}

const hideLogoutConfirm = () => {
  isLogoutModalVisible.value = false
}

const handleLogout = () => {
  hideLogoutConfirm()
  isLoading.value = true

  setTimeout(() => {
    isLoading.value = false
    console.log('Đăng xuất thành công!')
    router.push({ name: 'DangNhap' })
  }, 3000)
}

const searchQuery = ref('')
const suggestions = ref([
  'Laptop ASUS ROG',
  'iPhone 15 Pro Max',
  'Samsung Galaxy S24',
  'Chuột không dây Logitech',
  'Bàn phím cơ Keychron',
  'Tai nghe Bluetooth Sony',
  'Màn hình LG 4K',
  'MacBook Air M2',
  'iPad Pro 2024',
  'Ổ cứng SSD Samsung',
  'iPad Pro 2024',
])

const filteredSuggestions = computed(() => {
  const rawQuery = searchQuery.value.trim().toLowerCase()
  if (!rawQuery) return []

  return suggestions.value.filter(product =>
    product.toLowerCase().startsWith(rawQuery)
  )
})

const performSearch = () => {
  const query = searchQuery.value.trim()
  if (query) {
    router.push({ name: 'KetQuaTimKiem', query: { q: query } })
    searchQuery.value = '' // Xóa input và ẩn gợi ý sau khi tìm kiếm
  }
}

const selectSuggestion = (item: string) => {
  searchQuery.value = item // Cập nhật ô tìm kiếm với gợi ý đã chọn
  performSearch() // Thực hiện tìm kiếm
}
</script>
