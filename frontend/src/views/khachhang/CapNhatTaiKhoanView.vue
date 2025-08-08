<template>
  <CustomerWithNav>
    <BaseModal
      :visible="isConfirmEmailModalVisible"
      title="Xác nhận thay đổi Email"
      width-class="w-[500px]"
      @close="closeConfirmModal"
    >
      <p class="text-sm text-gray-600 mb-4">
        Vì bạn đổi email, nên chúng tôi đã gửi một mã OTP đến
        <strong class="font-semibold">{{ originalEmail }}</strong
        >. Vui lòng nhập mã OTP để xác nhận thay đổi.
      </p>
      <div>
        <label for="otp" class="block font-medium mb-1">Mã OTP:</label>
        <input
          id="otp"
          v-model="otp"
          type="text"
          class="w-full border rounded px-3 py-2"
          placeholder="Nhập mã OTP"
        />
      </div>
      <template #footer>
        <div class="flex justify-end space-x-2">
          <button
            @click="closeConfirmModal"
            class="px-4 py-2 rounded bg-gray-200 hover:bg-gray-300"
          >
            Hủy
          </button>
          <button
            @click="handleConfirmEmailChange"
            class="px-4 py-2 rounded bg-gray-800 text-white hover:bg-gray-700"
          >
            Xác nhận
          </button>
        </div>
      </template>
    </BaseModal>

    <div class="max-w-2xl mx-auto mt-10 p-6 bg-white rounded shadow">
      <h2 class="text-xl font-bold mb-4">Thông tin khách hàng</h2>

      <div class="mb-4">
        <label class="block font-medium mb-1" for="hoTen">Họ tên:</label>
        <input
          id="hoTen"
          v-model="hoTen"
          type="text"
          class="w-full border rounded px-3 py-2"
        />
      </div>

      <div class="mb-6">
        <label class="block font-medium mb-1" for="email">Email:</label>
        <input
          id="email"
          v-model="email"
          type="email"
          class="w-full border rounded px-3 py-2"
        />
      </div>

      <h2 class="text-xl font-bold mb-4">Thông tin giao hàng</h2>

      <div class="mb-4">
        <label class="block font-medium mb-1" for="diaChi">Địa chỉ:</label>
        <textarea
          id="diaChi"
          v-model="diaChi"
          rows="3"
          class="w-full border rounded px-3 py-2"
        ></textarea>
      </div>

      <div class="mb-6">
        <label class="block font-medium mb-1" for="soDienThoai"
          >Số điện thoại:</label
        >
        <input
          id="soDienThoai"
          v-model="soDienThoai"
          type="text"
          class="w-full border rounded px-3 py-2"
        />
      </div>

      <div class="text-center">
        <button
          @click="luuThayDoi"
          class="bg-gray-800 text-white px-6 py-2 rounded hover:bg-gray-700"
        >
          Lưu thay đổi
        </button>
      </div>
    </div>
  </CustomerWithNav>
</template>

<script setup lang="ts">
import CustomerWithNav from "@/components/layouts/CustomerWithNav.vue";
import BaseModal from "@/components/base/modals/BaseModal.vue";
import { ref, onMounted } from "vue";
import { useToast } from "@/composables/useToast";

const hoTen = ref("Nguyễn Văn A");
const email = ref("nguyenVanA@gmail.com");
const diaChi = ref("12 phố A, phường B, quận C, thành phố D");
const soDienThoai = ref("0987654321");

const originalEmail = ref("");
const isConfirmEmailModalVisible = ref(false);
const otp = ref("");

const { showToast } = useToast();

// Lưu lại email ban đầu khi component được tải
onMounted(() => {
  originalEmail.value = email.value;
});

const luuThayDoi = () => {
  if (email.value !== originalEmail.value) {
    // Nếu email thay đổi, hiển thị modal xác nhận
    isConfirmEmailModalVisible.value = true;
    // TODO: Gửi request tới backend để yêu cầu gửi mã OTP đến email cũ
    console.log(`Yêu cầu gửi OTP đến email cũ: ${originalEmail.value}`);
  } else {
    // Nếu không có thay đổi email, lưu thông tin trực tiếp
    updateProfile();
  }
};

const updateProfile = (otpCode?: string) => {
  console.log("Lưu thông tin:", {
    hoTen: hoTen.value,
    email: email.value,
    diaChi: diaChi.value,
    soDienThoai: soDienThoai.value,
    ...(otpCode && { otp: otpCode }), // Gửi kèm OTP nếu có
  });
  // TODO: Gửi request cập nhật lên backend
  // Sau khi backend xác nhận thành công:
  originalEmail.value = email.value; // Cập nhật lại email gốc
  showToast({
    thongBao: "Cập nhật thông tin tài khoản thành công!",
    loai: "thanhCong",
  });
};

const handleConfirmEmailChange = () => {
  // Giả sử OTP hợp lệ, tiến hành cập nhật
  updateProfile(otp.value);
  isConfirmEmailModalVisible.value = false;
  otp.value = "";
};

const closeConfirmModal = () => {
  isConfirmEmailModalVisible.value = false;
  // Hoàn tác lại thay đổi email nếu người dùng hủy
  email.value = originalEmail.value;
  otp.value = "";
};
</script>
