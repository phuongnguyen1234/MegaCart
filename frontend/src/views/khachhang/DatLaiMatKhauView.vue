<template>
  <div
    class="max-w-lg mx-auto mt-10 p-6 bg-white shadow-lg rounded-lg space-y-6"
  >
    <!-- Stepper -->
    <div class="flex justify-between items-center mb-6">
      <template v-for="(step, index) in steps" :key="index">
        <div class="flex items-center">
          <div
            class="w-10 h-10 flex items-center justify-center rounded-full text-white font-bold"
            :class="{
              'bg-blue-600': currentStep === index,
              'bg-gray-400': currentStep !== index && currentStep < index,
              'bg-green-500': currentStep > index,
            }"
          >
            {{ index + 1 }}
          </div>
          <span
            class="ml-2 text-sm font-medium"
            :class="{ 'text-blue-700': currentStep === index }"
          >
            {{ step }}
          </span>
        </div>
        <div
          v-if="index < steps.length - 1"
          class="flex-1 h-0.5 bg-gray-300 mx-2"
        ></div>
      </template>
    </div>

    <!-- Step 1 -->
    <div v-if="currentStep === 0" class="space-y-4">
      <h2 class="text-center text-xl font-semibold">ĐẶT LẠI MẬT KHẨU</h2>
      <p class="text-sm text-gray-600 text-center">
        Vui lòng nhập email liên kết với tài khoản của bạn. Chúng tôi sẽ gửi cho
        bạn mã OTP để xác nhận.
      </p>
      <input
        v-model="email"
        type="email"
        placeholder="Nhập email..."
        class="w-full border border-gray-300 p-3 rounded"
        @keyup.enter="sendOTP"
      />
      <div
        v-if="apiError"
        class="text-red-500 text-sm text-center p-2 bg-red-50 rounded"
      >
        {{ apiError }}
      </div>
      <button
        @click="sendOTP"
        :disabled="isLoading"
        class="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 disabled:bg-blue-400 disabled:cursor-not-allowed flex items-center justify-center"
      >
        <span
          v-if="isLoading"
          class="animate-spin rounded-full h-5 w-5 border-b-2 border-white"
        ></span>
        <span v-else>Gửi mã OTP</span>
      </button>
      <div class="text-center">
        <router-link
          :to="{ name: 'DangNhap' }"
          class="text-blue-600 text-sm hover:underline"
        >
          Quay về Đăng nhập
        </router-link>
      </div>
    </div>

    <!-- Step 2: Xác nhận OTP -->
    <div v-else-if="currentStep === 1" class="space-y-4">
      <h2 class="text-center text-xl font-semibold">XÁC NHẬN MÃ OTP</h2>

      <!-- Nếu OTP còn hiệu lực -->
      <div
        v-if="otpTimer > 0"
        class="bg-blue-100 border border-blue-300 text-blue-800 rounded-lg p-4 text-center"
      >
        <p class="text-base">
          Mã OTP đã được gửi đến
          <span class="font-semibold underline">{{ email }}</span>
        </p>
        <p class="mt-2 text-sm">
          Mã sẽ hết hạn sau:
          <span class="font-bold text-red-600 text-lg">{{
            otpTimerDisplay
          }}</span>
        </p>
      </div>

      <!-- Nếu OTP đã hết hạn -->
      <div
        v-else
        class="bg-red-100 border border-red-300 text-red-800 rounded-lg p-4 text-center"
      >
        <p class="text-base font-semibold">
          Mã OTP đã hết hạn. Vui lòng
          <a @click="resendOTP" class="text-blue-600 underline cursor-pointer">
            gửi lại mã</a
          >.
        </p>
      </div>

      <!-- Chỉ hiển thị input và button nếu OTP còn hiệu lực -->
      <div v-if="otpTimer > 0" class="space-y-4">
        <input
          v-model="otp"
          type="text"
          placeholder="Nhập mã OTP..."
          class="w-full border border-gray-300 p-3 rounded"
          @keyup.enter="verifyOTP"
        />
        <div
          v-if="apiError"
          class="text-red-500 text-sm text-center p-2 bg-red-50 rounded"
        >
          {{ apiError }}
        </div>
        <button
          @click="verifyOTP"
          :disabled="isLoading"
          class="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 disabled:bg-blue-400 disabled:cursor-not-allowed flex items-center justify-center"
        >
          <span
            v-if="isLoading"
            class="animate-spin rounded-full h-5 w-5 border-b-2 border-white"
          ></span>
          <span v-else>Xác nhận</span>
        </button>
      </div>

      <!-- Gửi lại mã khi OTP còn hiệu lực -->
      <p v-if="otpTimer > 0" class="text-sm text-center text-gray-600">
        Không nhận được mã OTP?
        <span v-if="resendTimer > 0">
          <span class="text-gray-500">
            Gửi lại mã (<strong>{{ resendTimer }}</strong
            >s)
          </span>
        </span>
        <a
          v-else
          @click="resendOTP"
          class="text-blue-600 hover:underline cursor-pointer"
        >
          Gửi lại mã
        </a>
      </p>

      <div class="text-center">
        <router-link
          :to="{ name: 'DangNhap' }"
          class="text-blue-600 text-sm hover:underline"
        >
          Quay về Đăng nhập
        </router-link>
      </div>
    </div>

    <!-- Step 3: Mật khẩu mới -->
    <div v-else-if="currentStep === 2" class="space-y-4">
      <h2 class="text-center text-xl font-semibold">ĐẶT LẠI MẬT KHẨU</h2>
      <input
        v-model="newPassword"
        type="password"
        placeholder="Mật khẩu mới"
        class="w-full border border-gray-300 p-3 rounded"
      />
      <input
        v-model="confirmPassword"
        type="password"
        placeholder="Xác nhận mật khẩu mới"
        class="w-full border border-gray-300 p-3 rounded"
        @keyup.enter="resetPassword"
      />
      <div
        v-if="apiError"
        class="text-red-500 text-sm text-center p-2 bg-red-50 rounded"
      >
        {{ apiError }}
      </div>
      <button
        @click="resetPassword"
        :disabled="isLoading"
        class="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700 disabled:bg-blue-400 disabled:cursor-not-allowed flex items-center justify-center"
      >
        <span
          v-if="isLoading"
          class="animate-spin rounded-full h-5 w-5 border-b-2 border-white"
        ></span>
        <span v-else>Đặt lại mật khẩu</span>
      </button>
      <div class="text-center">
        <router-link
          :to="{ name: 'DangNhap' }"
          class="text-blue-600 text-sm hover:underline"
        >
          Quay về Đăng nhập
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onBeforeUnmount } from "vue";
import { useRouter } from "vue-router";
import { useToast } from "@/composables/useToast";
import {
  guiEmailQuenMatKhau,
  xacThucOtp,
  datLaiMatKhau,
} from "@/service/taikhoan.service";

const steps = ["Nhập Email", "Xác nhận OTP", "Mật khẩu mới"];
const currentStep = ref(0);

const email = ref("");
const otp = ref("");
const newPassword = ref("");
const confirmPassword = ref("");

const isLoading = ref(false);
const apiError = ref("");

const router = useRouter();
const { showToast } = useToast();

const OTP_EXPIRE_TIME = 300; // 5 minutes
const RESEND_WAIT_TIME = 60; // 1 minute

const otpTimer = ref(0);
const resendTimer = ref(0);

let otpInterval: ReturnType<typeof setInterval> | null = null;
let resendInterval: ReturnType<typeof setInterval> | null = null;

const otpTimerDisplay = computed(() => {
  const minutes = Math.floor(otpTimer.value / 60);
  const seconds = otpTimer.value % 60;
  return `${minutes}:${seconds.toString().padStart(2, "0")}`;
});

const startTimers = () => {
  stopTimers(); // Clear any existing timers first

  otpTimer.value = OTP_EXPIRE_TIME;
  resendTimer.value = RESEND_WAIT_TIME;

  otpInterval = setInterval(() => {
    if (otpTimer.value > 0) {
      otpTimer.value--;
    } else {
      stopTimers();
    }
  }, 1000);

  resendInterval = setInterval(() => {
    if (resendTimer.value > 0) {
      resendTimer.value--;
    } else {
      if (resendInterval) clearInterval(resendInterval);
    }
  }, 1000);
};

const stopTimers = () => {
  if (otpInterval) clearInterval(otpInterval);
  if (resendInterval) clearInterval(resendInterval);
  otpInterval = null;
  resendInterval = null;
};

const handleApiCall = async (
  apiFunction: () => Promise<any>,
  onSuccess: (response?: any) => void
) => {
  isLoading.value = true;
  apiError.value = "";
  try {
    const response = await apiFunction();
    onSuccess(response);
  } catch (error: any) {
    const errorMessage =
      error.response?.data?.message ||
      "Đã có lỗi không mong muốn xảy ra. Vui lòng thử lại.";
    apiError.value = errorMessage;
    showToast({
      thongBao: errorMessage,
      loai: "loi",
    });
  } finally {
    isLoading.value = false;
  }
};

const sendOTP = async () => {
  if (!email.value || !/^\S+@\S+\.\S+$/.test(email.value)) {
    apiError.value = "Vui lòng nhập một địa chỉ email hợp lệ.";
    return;
  }

  await handleApiCall(
    () => guiEmailQuenMatKhau(email.value),
    (response) => {
      showToast({
        thongBao: response.message || "Mã OTP đã được gửi.",
        loai: "thanhCong",
      });
      currentStep.value = 1;
      startTimers();
    }
  );
};

const resendOTP = async () => {
  if (resendTimer.value > 0) return;

  await handleApiCall(
    () => guiEmailQuenMatKhau(email.value),
    (response) => {
      showToast({
        thongBao: response.message || "Mã OTP đã được gửi lại.",
        loai: "thanhCong",
      });
      startTimers(); // Restart timers
    }
  );
};

const verifyOTP = async () => {
  if (!otp.value) {
    apiError.value = "Vui lòng nhập mã OTP.";
    return;
  }

  await handleApiCall(
    () => xacThucOtp(email.value, otp.value),
    (response) => {
      showToast({
        thongBao: response.message || "Xác thực OTP thành công.",
        loai: "thanhCong",
      });
      currentStep.value = 2;
      stopTimers();
    }
  );
};

const resetPassword = async () => {
  if (!newPassword.value || !confirmPassword.value) {
    apiError.value = "Vui lòng nhập đầy đủ mật khẩu mới và xác nhận mật khẩu.";
    return;
  }
  if (newPassword.value.length < 6) {
    apiError.value = "Mật khẩu mới phải có ít nhất 6 ký tự.";
    return;
  }
  if (newPassword.value !== confirmPassword.value) {
    apiError.value = "Mật khẩu xác nhận không khớp.";
    return;
  }

  await handleApiCall(
    () => datLaiMatKhau(email.value, otp.value, newPassword.value),
    (response) => {
      showToast({
        thongBao: response.message || "Đặt lại mật khẩu thành công!",
        loai: "thanhCong",
      });
      router.push({ name: "DangNhap" });
    }
  );
};

onBeforeUnmount(() => {
  stopTimers();
});
</script>
