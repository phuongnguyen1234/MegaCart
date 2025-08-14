<template>
  <CustomerWithNav>
    <BaseModal
      :visible="isConfirmEmailModalVisible"
      title="Xác nhận thay đổi Email"
      width-class="w-[500px]"
      @close="closeConfirmModal"
    >
      <p class="text-sm text-gray-600 mb-4">
        Chúng tôi đã gửi một mã OTP đến email mới của bạn
        <strong class="font-semibold">{{ email }}</strong
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
            :disabled="isLoading"
            class="px-4 py-2 rounded bg-gray-800 text-white hover:bg-gray-700 disabled:bg-gray-400 disabled:cursor-not-allowed flex items-center justify-center min-w-[100px]"
          >
            <span
              v-if="isLoading"
              class="animate-spin rounded-full h-5 w-5 border-b-2 border-white"
            ></span>
            Xác nhận
          </button>
        </div>
      </template>
    </BaseModal>

    <div class="max-w-2xl mx-auto mt-10 p-6 bg-white rounded shadow">
      <h2 class="text-xl font-bold mb-4">Thông tin khách hàng</h2>

      <div class="mb-4">
        <label class="flex items-center font-medium mb-1" for="hoTen">
          <i class="fi fi-rr-user w-5 text-center mr-2 text-gray-500"></i>
          Họ tên:
        </label>
        <input
          id="hoTen"
          v-model="tenKhachHang"
          type="text"
          class="w-full border rounded px-3 py-2"
        />
      </div>

      <div class="mb-6">
        <label class="flex items-center font-medium mb-1" for="email">
          <i class="fi fi-rr-envelope w-5 text-center mr-2 text-gray-500"></i>
          Email:
        </label>
        <input
          id="email"
          v-model="email"
          type="email"
          class="w-full border rounded px-3 py-2"
        />
      </div>

      <h2 class="text-xl font-bold mb-4">Thông tin giao hàng</h2>

      <div class="mb-4">
        <label class="flex items-center font-medium mb-1" for="diaChi">
          <i
            class="fi fi-rs-house-chimney w-5 text-center mr-2 text-gray-500"
          ></i>
          Địa chỉ:
        </label>
        <textarea
          id="diaChi"
          v-model="diaChi"
          rows="3"
          class="w-full border rounded px-3 py-2"
        ></textarea>
      </div>

      <div class="mb-6">
        <label class="flex items-center font-medium mb-1" for="soDienThoai">
          <i class="fi fi-rr-phone-call w-5 text-center mr-2 text-gray-500"></i>
          Số điện thoại:
        </label>
        <input
          id="soDienThoai"
          v-model="soDienThoai"
          type="text"
          class="w-full border rounded px-3 py-2"
        />
      </div>

      <div class="flex justify-center">
        <button
          @click="luuThayDoi"
          :disabled="isLoading"
          class="cursor-pointer bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700 disabled:bg-blue-400 disabled:cursor-not-allowed flex items-center justify-center min-w-[150px]"
        >
          <span
            v-if="isLoading"
            class="animate-spin rounded-full h-5 w-5 border-b-2 border-white mr-2"
          ></span>
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
import { useAuthStore } from "@/store/auth.store";
import {
  layThongTinTaiKhoan,
  capNhatTaiKhoan,
  xacNhanDoiEmail,
} from "@/service/taikhoan.service";
import type {
  User,
  CapNhatHoSoResponse,
  AuthResponse,
} from "@/service/taikhoan.service";

const tenKhachHang = ref("");
const email = ref("");
const diaChi = ref("");
const soDienThoai = ref("");

const originalEmail = ref("");
const isConfirmEmailModalVisible = ref(false);
const otp = ref("");
const isLoading = ref(false);

const { showToast } = useToast();
const authStore = useAuthStore();

const fetchUserData = async () => {
  isLoading.value = true;
  try {
    const userData = await layThongTinTaiKhoan();
    tenKhachHang.value = userData.tenKhachHang;
    email.value = userData.email;
    originalEmail.value = userData.email; // Lưu lại email ban đầu
    diaChi.value = userData.diaChi || "";
    soDienThoai.value = userData.soDienThoai || "";
  } catch (error) {
    showToast({
      thongBao: "Không thể tải thông tin tài khoản. Vui lòng thử lại.",
      loai: "loi",
    });
  } finally {
    isLoading.value = false;
  }
};

onMounted(fetchUserData);

const luuThayDoi = async () => {
  isLoading.value = true;
  try {
    const payload: Partial<User> = {
      tenKhachHang: tenKhachHang.value,
      email: email.value,
      diaChi: diaChi.value,
      soDienThoai: soDienThoai.value,
    };

    const response: CapNhatHoSoResponse = await capNhatTaiKhoan(payload);

    showToast({
      thongBao: response.message,
      loai: "thanhCong",
    });

    if (response.emailChangeInitiated) {
      // Luồng thay đổi email đã được kích hoạt, backend đã gửi OTP.
      // Giờ chỉ cần hiển thị modal để người dùng nhập OTP.
      isConfirmEmailModalVisible.value = true;
    } else {
      // Cập nhật thông tin thành công, không có thay đổi email.
      // Cập nhật lại email gốc để cho các lần chỉnh sửa sau.
      originalEmail.value = email.value;
      // Cập nhật tên người dùng trong store để Header tự động thay đổi
      authStore.updateUserName(tenKhachHang.value);
    }
  } catch (error: any) {
    const errorMessage =
      error.response?.data?.message ||
      "Cập nhật thông tin thất bại. Vui lòng thử lại.";
    showToast({
      thongBao: errorMessage,
      loai: "loi",
    });
    // Hoàn tác lại email trên form nếu có lỗi xảy ra
    email.value = originalEmail.value;
  } finally {
    isLoading.value = false;
  }
};

const handleConfirmEmailChange = async () => {
  if (!otp.value) {
    showToast({ thongBao: "Vui lòng nhập mã OTP.", loai: "loi" });
    return;
  }

  isLoading.value = true;
  try {
    // Gửi OTP lên server để xác nhận
    const response: AuthResponse = await xacNhanDoiEmail(otp.value);

    // Lưu token mới vì thông tin định danh (email) đã thay đổi
    localStorage.setItem("access_token", response.token);

    showToast({
      thongBao: "Thay đổi email thành công!",
      loai: "thanhCong",
    });

    // Đóng modal và cập nhật lại trạng thái
    isConfirmEmailModalVisible.value = false;
    originalEmail.value = email.value; // Cập nhật email gốc thành email mới
    otp.value = "";

    // Tải lại trang để cập nhật toàn bộ trạng thái người dùng (ví dụ: ở header)
    window.location.reload();
  } catch (error: any) {
    const errorMessage =
      error.response?.data?.message ||
      "Mã OTP không hợp lệ hoặc đã hết hạn. Vui lòng thử lại.";
    showToast({
      thongBao: errorMessage,
      loai: "loi",
    });
  } finally {
    isLoading.value = false;
  }
};

const closeConfirmModal = () => {
  isConfirmEmailModalVisible.value = false;
  // Hoàn tác lại thay đổi email nếu người dùng hủy
  email.value = originalEmail.value;
  otp.value = "";
};
</script>
