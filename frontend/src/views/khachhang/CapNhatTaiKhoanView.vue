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
            :disabled="isConfirmingOtp"
            class="px-4 py-2 rounded bg-gray-800 text-white hover:bg-gray-700 disabled:bg-gray-400 disabled:cursor-not-allowed flex items-center justify-center min-w-[100px]"
          >
            <span
              v-if="isConfirmingOtp"
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
          :disabled="isSavingProfile"
          class="cursor-pointer bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-700 disabled:bg-blue-400 disabled:cursor-not-allowed flex items-center justify-center min-w-[150px]"
        >
          <span
            v-if="isSavingProfile"
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
  // Types are now in a dedicated file
  CapNhatHoSoRequest,
  CapNhatHoSoResponse,
  AuthResponse,
  XacNhanDoiEmailRequest,
} from "@/types/taikhoan.types";

const tenKhachHang = ref("");
const email = ref("");
const diaChi = ref("");
const soDienThoai = ref("");

const originalUserData = ref({
  tenKhachHang: "",
  email: "",
  diaChi: "",
  soDienThoai: "",
});
const isConfirmEmailModalVisible = ref(false);
const otp = ref("");
const isSavingProfile = ref(false); // Trạng thái loading cho việc lưu hồ sơ
const isConfirmingOtp = ref(false); // Trạng thái loading riêng cho việc xác nhận OTP

const { showToast } = useToast();
const authStore = useAuthStore();

const fetchUserData = async () => {
  isSavingProfile.value = true;
  try {
    const userData = await layThongTinTaiKhoan();
    tenKhachHang.value = userData.tenKhachHang;
    email.value = userData.email;
    diaChi.value = userData.diaChi || "";
    soDienThoai.value = userData.soDienThoai || "";

    // Lưu lại toàn bộ dữ liệu ban đầu để có thể hoàn tác
    originalUserData.value = {
      tenKhachHang: userData.tenKhachHang,
      email: userData.email,
      diaChi: userData.diaChi || "",
      soDienThoai: userData.soDienThoai || "",
    };
  } catch (error) {
    showToast({
      thongBao: "Không thể tải thông tin tài khoản. Vui lòng thử lại.",
      loai: "loi",
    });
  } finally {
    isSavingProfile.value = false;
  }
};

onMounted(fetchUserData);

const luuThayDoi = () => {
  isSavingProfile.value = true;
  const emailDaThayDoi = email.value !== originalUserData.value.email;

  const payload: CapNhatHoSoRequest = {
    tenKhachHang: tenKhachHang.value,
    emailMoi: email.value,
    diaChi: diaChi.value,
    soDienThoai: soDienThoai.value,
  };

  if (emailDaThayDoi) {
    // UI Lạc quan: Mở modal ngay lập tức, không chờ API.
    isConfirmEmailModalVisible.value = true;

    // Gửi yêu cầu API trong nền
    capNhatTaiKhoan(payload)
      .then((response) => {
        // API thành công, backend đã gửi OTP.
        // Hiển thị thông báo và giữ modal mở để người dùng nhập OTP.
        showToast({
          thongBao: response.message,
          loai: "thanhCong",
        });
        // Đã gửi API thành công, kết thúc loading cho form chính.
        isSavingProfile.value = false;
      })
      .catch((error: any) => {
        // API thất bại (ví dụ: email đã tồn tại).
        // Đóng modal, hiển thị lỗi và hoàn tác các thay đổi.
        const errorMessage =
          error.response?.data?.message || "Yêu cầu thay đổi email thất bại.";
        showToast({
          thongBao: errorMessage,
          loai: "loi",
        });
        closeConfirmModal(); // Hàm này sẽ hoàn tác form và reset các trạng thái loading.
      });
  } else {
    // Luồng cập nhật thông thường (không đổi email)
    capNhatTaiKhoan(payload)
      .then((response) => {
        showToast({
          thongBao: response.message,
          loai: "thanhCong",
        });
        // Cập nhật UI với dữ liệu mới từ backend
        const { thongTinCapNhat } = response;
        tenKhachHang.value = thongTinCapNhat.tenKhachHang;
        email.value = thongTinCapNhat.email;
        diaChi.value = thongTinCapNhat.diaChi || "";
        soDienThoai.value = thongTinCapNhat.soDienThoai || "";
        originalUserData.value = { ...thongTinCapNhat };
        authStore.updateUserName(thongTinCapNhat.tenKhachHang);
      })
      .catch((error: any) => {
        const errorMessage =
          error.response?.data?.message ||
          "Cập nhật thông tin thất bại. Vui lòng thử lại.";
        showToast({
          thongBao: errorMessage,
          loai: "loi",
        });
        // Hoàn tác lại các thay đổi trên form nếu có lỗi từ API
        tenKhachHang.value = originalUserData.value.tenKhachHang;
        email.value = originalUserData.value.email;
        diaChi.value = originalUserData.value.diaChi;
        soDienThoai.value = originalUserData.value.soDienThoai;
      })
      .finally(() => {
        isSavingProfile.value = false;
      });
  }
};

const handleConfirmEmailChange = async () => {
  if (!otp.value) {
    showToast({ thongBao: "Vui lòng nhập mã OTP.", loai: "loi" });
    return;
  }

  isConfirmingOtp.value = true;
  try {
    // Gửi OTP lên server để xác nhận
    const payload: XacNhanDoiEmailRequest = { otp: otp.value };
    const response: AuthResponse = await xacNhanDoiEmail(payload);

    // Lưu token mới vì thông tin định danh (email) đã thay đổi
    localStorage.setItem("access_token", response.token);

    showToast({
      thongBao: "Thay đổi email thành công!",
      loai: "thanhCong",
    });

    // Đóng modal và cập nhật lại trạng thái
    isConfirmEmailModalVisible.value = false;
    // Không cần cập nhật state ở đây vì trang sẽ được tải lại
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
    isConfirmingOtp.value = false;
  }
};

const closeConfirmModal = () => {
  isConfirmEmailModalVisible.value = false;
  // Hoàn tác lại tất cả các thay đổi trên form nếu người dùng hủy
  tenKhachHang.value = originalUserData.value.tenKhachHang;
  email.value = originalUserData.value.email;
  diaChi.value = originalUserData.value.diaChi;
  soDienThoai.value = originalUserData.value.soDienThoai;
  otp.value = "";
  isSavingProfile.value = false;
  isConfirmingOtp.value = false;
};
</script>
