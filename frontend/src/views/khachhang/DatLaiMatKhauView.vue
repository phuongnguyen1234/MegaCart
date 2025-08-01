<template>
  <div class="max-w-md mx-auto mt-10 p-6 bg-white shadow rounded-md space-y-6">
    <!-- Stepper -->
    <div class="flex justify-between items-center mb-6">
      <div
        v-for="(step, index) in steps"
        :key="index"
        class="flex items-center space-x-2"
      >
        <div
          class="w-8 h-8 flex items-center justify-center rounded-full font-bold text-white"
          :class="{
            'bg-blue-600': currentStep === index,
            'bg-gray-400': currentStep !== index && currentStep < index,
            'bg-green-500': currentStep > index,
          }"
        >
          {{ index + 1 }}
        </div>
        <span
          class="text-sm"
          :class="{ 'font-semibold': currentStep === index }"
        >
          {{ step }}
        </span>
        <div
          v-if="index < steps.length - 1"
          class="w-5 h-0.5 bg-gray-300 mx-2"
        ></div>
      </div>
    </div>

    <!-- Step 1: Nhập email -->
    <div v-if="currentStep === 0" class="space-y-4">
      <h2 class="text-center text-lg font-semibold">ĐẶT LẠI MẬT KHẨU</h2>
      <p class="text-sm text-gray-600 text-center">
        Vui lòng nhập email liên kết với tài khoản của bạn. Chúng tôi sẽ gửi cho
        bạn mã OTP xác nhận thay đổi mật khẩu.
      </p>
      <input
        v-model="email"
        type="email"
        placeholder="Nhập email..."
        class="w-full border border-gray-300 p-2 rounded"
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
          >Quay về Đăng nhập</router-link
        >
      </div>
    </div>

    <!-- Step 2: Xác nhận OTP -->
    <div v-else-if="currentStep === 1" class="space-y-4">
      <h2 class="text-center text-lg font-semibold">ĐẶT LẠI MẬT KHẨU</h2>
      <p class="text-sm text-gray-600 text-center">
        Nhập mã OTP chúng tôi đã gửi vào email
        <span class="font-medium">{{ email }}</span> để xác nhận đặt lại mật
        khẩu.
      </p>
      <input
        v-model="otp"
        type="text"
        placeholder="Nhập mã OTP..."
        class="w-full border border-gray-300 p-2 rounded"
      />
      <button
        @click="verifyOTP"
        class="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700"
      >
        Xác nhận
      </button>
      <p class="text-sm text-gray-500 text-center">
        Không nhận được mã OTP? Vui lòng đợi 60 giây để gửi lại mã OTP
      </p>
      <div class="text-center">
        <router-link
          :to="{ name: 'DangNhap' }"
          class="text-blue-600 text-sm hover:underline"
          >Quay về Đăng nhập</router-link
        >
      </div>
    </div>

    <!-- Step 3: Nhập mật khẩu mới -->
    <div v-else-if="currentStep === 2" class="space-y-4">
      <h2 class="text-center text-lg font-semibold">ĐẶT LẠI MẬT KHẨU</h2>
      <input
        v-model="newPassword"
        type="password"
        placeholder="Mật khẩu mới"
        class="w-full border border-gray-300 p-2 rounded"
      />
      <input
        v-model="confirmPassword"
        type="password"
        placeholder="Xác nhận mật khẩu mới"
        class="w-full border border-gray-300 p-2 rounded"
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
          >Quay về Đăng nhập</router-link
        >
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";

const steps = ["Nhập Email", "Xác nhận OTP", "Mật khẩu mới"];
const currentStep = ref(0);

const email = ref("");
const otp = ref("");
const newPassword = ref("");
const confirmPassword = ref("");

const router = useRouter();

function sendOTP() {
  if (!email.value) return alert("Vui lòng nhập email");
  // TODO: Gọi API gửi OTP
  currentStep.value = 1;
}

function verifyOTP() {
  if (!otp.value) return alert("Vui lòng nhập mã OTP");
  // TODO: Gọi API xác nhận OTP
  currentStep.value = 2;
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
</script>
