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
      />
      <button
        @click="sendOTP"
        class="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700"
      >
        Gửi mã OTP
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
        <p class="text-xl font-semibold">
          Mã OTP đã được gửi đến <span class="underline">{{ email }}</span>
        </p>
        <p class="mt-2 text-lg">
          Mã sẽ hết hạn sau:
          <span class="font-bold text-red-600 text-2xl">{{
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
        />
        <button
          @click="verifyOTP"
          class="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700"
        >
          Xác nhận
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
      />
      <button
        @click="resetPassword"
        class="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700"
      >
        Đặt lại mật khẩu
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

<script setup>
import { ref, computed, onBeforeUnmount } from "vue";
import { useRouter } from "vue-router";

const steps = ["Nhập Email", "Xác nhận OTP", "Mật khẩu mới"];
const currentStep = ref(0);

const email = ref("");
const otp = ref("");
const newPassword = ref("");
const confirmPassword = ref("");

const router = useRouter();

const OTP_EXPIRE_TIME = 300; // thời gian hết hạn OTP (giây)
const RESEND_WAIT_TIME = 60; // thời gian chờ gửi lại (giây)

const otpTimer = ref(OTP_EXPIRE_TIME);
const resendTimer = ref(RESEND_WAIT_TIME);

const otpTimerDisplay = computed(() => {
  const minutes = Math.floor(otpTimer.value / 60);
  const seconds = otpTimer.value % 60;
  return `${minutes}:${seconds.toString().padStart(2, "0")}`;
});

let otpInterval = null;
let resendInterval = null;

function startTimers() {
  clearInterval(otpInterval);
  clearInterval(resendInterval);

  otpInterval = setInterval(() => {
    if (otpTimer.value > 0) otpTimer.value--;
    else {
      clearInterval(otpInterval);
      resendTimer.value = 0;
    }
  }, 1000);

  resendInterval = setInterval(() => {
    if (resendTimer.value > 0) resendTimer.value--;
    else clearInterval(resendInterval);
  }, 1000);
}

function sendOTP() {
  if (!email.value) return alert("Vui lòng nhập email");
  currentStep.value = 1;
  otpTimer.value = OTP_EXPIRE_TIME;
  resendTimer.value = RESEND_WAIT_TIME;
  startTimers();
  // TODO: Gọi API gửi OTP
}

function resendOTP() {
  if (resendTimer.value > 0) return;
  otpTimer.value = OTP_EXPIRE_TIME;
  resendTimer.value = RESEND_WAIT_TIME;
  startTimers();
  // TODO: Gọi API gửi lại OTP
}

function verifyOTP() {
  if (!otp.value) return alert("Vui lòng nhập mã OTP");
  currentStep.value = 2;
  clearInterval(otpInterval);
  clearInterval(resendInterval);
  // TODO: Xác thực mã OTP
}

function resetPassword() {
  if (!newPassword.value || !confirmPassword.value)
    return alert("Vui lòng nhập đầy đủ mật khẩu");
  if (newPassword.value !== confirmPassword.value)
    return alert("Mật khẩu không khớp");
  // TODO: Gọi API đặt lại mật khẩu
  alert("Đặt lại mật khẩu thành công!");
  router.push({ name: "DangNhap" });
}

onBeforeUnmount(() => {
  clearInterval(otpInterval);
  clearInterval(resendInterval);
});
</script>
