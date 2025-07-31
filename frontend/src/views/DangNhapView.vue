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

        <div class="mb-[15px] flex flex-col">
          <input
            type="email"
            placeholder="Email"
            class="h-[42px] px-[10px] border-0 border-b-2 border-[#0D47A1] bg-transparent text-[16px] focus:outline-none"
          />
        </div>
        <div class="mb-[15px] flex flex-col">
          <input
            type="password"
            placeholder="Mật khẩu"
            class="h-[42px] px-[10px] border-0 border-b-2 border-[#0D47A1] bg-transparent text-[16px] focus:outline-none"
          />
        </div>
        <div class="mb-[15px] flex flex-col">
          <input
            type="password"
            placeholder="Xác nhận mật khẩu"
            class="h-[42px] px-[10px] border-0 border-b-2 border-[#0D47A1] bg-transparent text-[16px] focus:outline-none"
          />
        </div>

        <button
          class="select-none w-full h-[52px] border-0 rounded-[8px] text-[25px] text-white cursor-pointer bg-gradient-to-br from-[#2196F3] to-[#1565C0] hover:bg-gradient-to-br hover:from-[#1976D2] hover:to-[#0D47A1]"
        >
          Đăng kí
        </button>

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

        <div class="mb-[15px] flex flex-col">
          <input
            v-model="loginEmail"
            type="email"
            placeholder="Email"
            class="h-[42px] px-[10px] border-0 border-b-2 border-[#0D47A1] bg-transparent text-[16px] focus:outline-none"
          />
        </div>
        <div class="mb-[15px] flex flex-col">
          <input
            v-model="loginPassword"
            type="password"
            placeholder="Mật khẩu"
            class="h-[42px] px-[10px] border-0 border-b-2 border-[#0D47A1] bg-transparent text-[16px] focus:outline-none"
          />
        </div>

        <button
          @click="handleLogin"
          class="select-none w-full h-[52px] border-0 rounded-[8px] text-[25px] text-[white] cursor-pointer bg-gradient-to-br from-[#2196F3] to-[#1565C0] hover:bg-gradient-to-br hover:from-[#1976D2] hover:to-[#0D47A1]"
        >
          Đăng nhập
        </button>

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

const isRegisterPanelActive = ref(false); // false = login visible, true = register visible
const router = useRouter();
const { showToast } = useToast();

const loginEmail = ref("");
const loginPassword = ref("");

const moveToSignup = () => {
  isRegisterPanelActive.value = true;
};

const moveToLogin = () => {
  isRegisterPanelActive.value = false;
};

const isLoading = ref(false);

const handleLogin = () => {
  if (loginEmail.value.trim() !== "" && loginPassword.value.trim() !== "") {
    isLoading.value = true;

    setTimeout(() => {
      isLoading.value = false;
      router.push({ name: "TrangChu" });
    }, 3000);
  } else {
    showToast({
      thongBao: "Vui lòng nhập đầy đủ email và mật khẩu.",
      loai: "loi",
    });
  }
};
</script>
