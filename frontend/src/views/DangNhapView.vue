<template>
  <div
    class="min-h-screen flex items-center justify-center bg-white font-[Open_Sans,sans-serif]"
  >
    <div
      class="relative w-full max-w-5xl h-[650px] flex overflow-hidden rounded-xl shadow-lg"
    >
      <!-- Đăng ký -->
      <div
        class="relative w-1/2 p-[50px] box-border flex flex-col justify-center z-[1] bg-[#E3F2FD]"
      >
        <h1 class="text-center mb-5 text-[#0D47A1] text-3xl font-bold">
          Đăng kí
        </h1>
        <form @submit.prevent="handleRegister">
          <div class="mb-[15px] flex flex-col">
            <input
              v-model="registerEmail"
              type="email"
              placeholder="Email"
              required
              class="h-[42px] px-[10px] border-0 border-b-2 border-[#0D47A1] bg-transparent text-[16px] focus:outline-none"
            />
          </div>
          <div class="mb-[15px] flex flex-col">
            <input
              v-model="registerPassword"
              type="password"
              placeholder="Mật khẩu"
              required
              class="h-[42px] px-[10px] border-0 border-b-2 border-[#0D47A1] bg-transparent text-[16px] focus:outline-none"
            />
          </div>
          <div class="mb-[15px] flex flex-col">
            <input
              v-model="registerConfirmPassword"
              type="password"
              placeholder="Xác nhận mật khẩu"
              required
              class="h-[42px] px-[10px] border-0 border-b-2 border-[#0D47A1] bg-transparent text-[16px] focus:outline-none"
            />
          </div>

          <button
            type="submit"
            :disabled="isLoading"
            class="select-none w-full h-[52px] border-0 rounded-[8px] text-[25px] text-white cursor-pointer bg-gradient-to-br from-[#2196F3] to-[#1565C0] hover:bg-gradient-to-br hover:from-[#1976D2] hover:to-[#0D47A1] disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Đăng kí
          </button>
        </form>

        <div
          class="absolute bottom-[40px] left-[100px] right-[100px] flex items-center justify-center"
        >
          <span class="mr-[5px]">Đã có tài khoản?</span>
          <a
            href="#"
            @click.prevent="moveToLogin"
            class="text-[#0D47A1] text-[18px] underline"
            >Đăng nhập tại đây!</a
          >
        </div>
      </div>

      <!-- Logo Panel -->
      <div
        :class="[
          'absolute top-0 left-0 w-1/2 h-full z-[5] flex items-center justify-center transition-transform duration-500 ease-in-out',
          isRegisterPanelActive ? 'translate-x-full' : '',
        ]"
        style="background: linear-gradient(to bottom, #bbdefb, #90caf9)"
      >
        <img
          src="@/assets/images/megacart-logo.png"
          alt="Logo"
          class="max-w-[80%] max-h-[80%]"
        />
      </div>

      <!-- Đăng nhập -->
      <div
        class="relative w-1/2 p-[50px] box-border flex flex-col justify-center z-[1] bg-[#E3F2FD]"
      >
        <h1 class="text-center mb-5 text-[#0D47A1] text-3xl font-bold">
          Đăng nhập
        </h1>

        <form @submit.prevent="handleLogin">
          <div class="mb-[15px] flex flex-col">
            <input
              v-model="loginEmail"
              type="email"
              placeholder="Email"
              required
              class="h-[42px] px-[10px] border-0 border-b-2 border-[#0D47A1] bg-transparent text-[16px] focus:outline-none"
            />
          </div>
          <div class="mb-[15px] flex flex-col">
            <input
              v-model="loginPassword"
              type="password"
              placeholder="Mật khẩu"
              required
              class="h-[42px] px-[10px] border-0 border-b-2 border-[#0D47A1] bg-transparent text-[16px] focus:outline-none"
            />
          </div>

          <div class="text-right mb-4">
            <router-link
              :to="{ name: 'DatLaiMatKhau' }"
              class="text-sm text-[#0D47A1] hover:underline"
            >
              Bạn quên mật khẩu?
            </router-link>
          </div>

          <button
            type="submit"
            :disabled="isLoading"
            class="select-none w-full h-[52px] border-0 rounded-[8px] text-[25px] text-[white] cursor-pointer bg-gradient-to-br from-[#2196F3] to-[#1565C0] hover:bg-gradient-to-br hover:from-[#1976D2] hover:to-[#0D47A1] disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Đăng nhập
          </button>
        </form>

        <div
          class="absolute bottom-[40px] left-[100px] right-[100px] flex items-center justify-center"
        >
          <span class="mr-[5px]">Chưa có tài khoản?</span>
          <a
            href="#"
            @click.prevent="moveToSignup"
            class="text-[#0D47A1] text-[18px] underline"
            >Đăng kí tại đây!</a
          >
        </div>
      </div>
    </div>
    <!-- Overlay + Loading Spinner -->
    <Loading :visible="isLoading" />
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { useToast } from "@/composables/useToast";
import Loading from "@/components/base/Loading.vue";
import { useAuthStore } from "@/store/auth.store";
import { decodeJwtPayload } from "@/utils/jwt";

const isRegisterPanelActive = ref(false); // false = login visible, true = register visible
const router = useRouter();
const { showToast } = useToast();
const authStore = useAuthStore();

const loginEmail = ref("");
const loginPassword = ref("");

const registerEmail = ref("");
const registerPassword = ref("");
const registerConfirmPassword = ref("");

const moveToSignup = () => {
  isRegisterPanelActive.value = true;
};

const moveToLogin = () => {
  isRegisterPanelActive.value = false;
};

const isLoading = ref(false);

const handleLogin = async () => {
  if (!loginEmail.value || !loginPassword.value) {
    showToast({
      thongBao: "Vui lòng nhập đầy đủ email và mật khẩu.",
      loai: "loi",
    });
    return;
  }

  isLoading.value = true;
  try {
    await authStore.login({
      email: loginEmail.value,
      matKhau: loginPassword.value,
    });

    // Đăng nhập thành công, token đã được lưu trong localStorage bởi authStore
    const token = localStorage.getItem("access_token");
    if (!token) {
      // Trường hợp hiếm gặp, nhưng nên xử lý
      throw new Error("Không tìm thấy token sau khi đăng nhập.");
    }

    const payload = decodeJwtPayload(token);
    // Ghi log payload để debug ngay sau khi đăng nhập
    console.log("Decoded JWT Payload on Login:", payload);
    // SỬA LỖI: Dùng 'vaiTro' để nhất quán với router guard
    const userRole = payload?.role;

    showToast({ thongBao: "Đăng nhập thành công!", loai: "thanhCong" });

    // Chuyển hướng dựa trên vai trò
    if (userRole === "ADMIN" || userRole === "NHAN_VIEN") {
      router.push({ path: "/admin/dashboard" });
    } else {
      router.push({ name: "TrangChu" });
    }
  } catch (error: any) {
    // Hiển thị lỗi từ API (nếu có) hoặc lỗi chung
    const errorMessage =
      error.response?.data?.message || "Đăng nhập thất bại. Vui lòng thử lại.";
    showToast({
      thongBao: errorMessage,
      loai: "loi",
    });
  } finally {
    isLoading.value = false;
  }
};

const handleRegister = async () => {
  if (registerPassword.value !== registerConfirmPassword.value) {
    showToast({
      thongBao: "Mật khẩu xác nhận không khớp.",
      loai: "loi",
    });
    return;
  }

  isLoading.value = true;
  try {
    // TODO: Gọi API đăng ký ở đây
    console.log("Đăng ký với:", registerEmail.value, registerPassword.value);
    showToast({
      thongBao: "Đăng ký thành công! Vui lòng đăng nhập.",
      loai: "thanhCong",
    });
    moveToLogin(); // Chuyển sang form đăng nhập sau khi đăng ký thành công
  } catch (error: any) {
    const errorMessage =
      error.response?.data?.message || "Đăng ký thất bại. Vui lòng thử lại.";
    showToast({ thongBao: errorMessage, loai: "loi" });
  } finally {
    isLoading.value = false;
  }
};
</script>
