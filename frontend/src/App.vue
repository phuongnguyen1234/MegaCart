<template>
  <div>
    <router-view />
    <TheToaster />
  </div>
</template>

<script setup lang="ts">
import TheToaster from "@/components/base/TheToaster.vue";
import { useAuthStore } from "@/store/auth.store";
import { useCartStore } from "@/store/giohang.store";
import { onMounted } from "vue";

const authStore = useAuthStore();
const cartStore = useCartStore();

// Khi ứng dụng được khởi tạo
onMounted(() => {
  // Nếu người dùng đã đăng nhập (có token), hãy lấy thông tin mới nhất của họ
  if (authStore.isLoggedIn) {
    // Lấy thông tin người dùng và giỏ hàng song song
    Promise.all([authStore.fetchUser(), cartStore.fetchCartCount()]);
  }
});
</script>
