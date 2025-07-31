<template>
  <CustomerWithNav>
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
import { ref } from "vue";
import { useToast } from "@/composables/useToast";

const hoTen = ref("Nguyễn Văn A");
const email = ref("nguyenVanA@gmail.com");
const diaChi = ref("12 phố A, phường B, quận C, thành phố D");
const soDienThoai = ref("0987654321");

const { showToast } = useToast();

const luuThayDoi = () => {
  console.log("Lưu thông tin:", {
    hoTen: hoTen.value,
    email: email.value,
    diaChi: diaChi.value,
    soDienThoai: soDienThoai.value,
  });
  // TODO: Gửi request cập nhật lên backend`
  showToast({
    thongBao: "Cập nhật thông tin tài khoản thành công!",
    loai: "thanhCong",
  });
};
</script>
