<template>
  <div
    class="w-full border border-blue-500 rounded-lg text-sm overflow-hidden bg-white shadow-sm"
  >
    <!-- Danh mục -->
    <div class="border-b border-blue-500">
      <button
        class="cursor-pointer w-full text-left px-4 py-3 bg-blue-500 hover:bg-blue-600 font-semibold text-white flex justify-between items-center transition-colors"
        @click="toggle('danhMuc')"
      >
        Danh mục
        <i
          :class="
            isOpen.danhMuc ? 'fi-rr-angle-small-up' : 'fi-rr-angle-small-down'
          "
          class="text-xl flex items-center"
        ></i>
      </button>
      <transition name="slide-fade">
        <div v-if="isOpen.danhMuc" class="px-4 py-3 space-y-2 bg-white">
          <label class="flex items-center gap-2 cursor-pointer">
            <input
              type="radio"
              name="category"
              :value="null"
              v-model="modelDanhMuc"
              class="text-blue-600 focus:ring-blue-500"
            />
            Tất cả
          </label>
          <label
            v-for="danhMuc in danhMucOption"
            :key="danhMuc.maDanhMuc"
            class="flex items-center gap-2 cursor-pointer"
          >
            <input
              type="radio"
              name="category"
              :value="danhMuc.maDanhMuc"
              v-model="modelDanhMuc"
              class="text-blue-600 focus:ring-blue-500"
            />
            {{ danhMuc.tenDanhMuc }}
          </label>
        </div>
      </transition>
    </div>

    <!-- Đơn giá -->
    <div class="border-b border-blue-500">
      <button
        class="cursor-pointer w-full text-left px-4 py-3 bg-blue-500 hover:bg-blue-600 font-semibold text-white flex justify-between items-center transition-colors"
        @click="toggle('donGia')"
      >
        Đơn giá
        <i
          :class="
            isOpen.donGia ? 'fi-rr-angle-small-up' : 'fi-rr-angle-small-down'
          "
          class="text-xl flex items-center"
        ></i>
      </button>
      <transition name="slide-fade">
        <div v-if="isOpen.donGia" class="px-4 py-3 space-y-4 bg-white">
          <!-- Khoảng giá -->
          <div v-if="khoangGia && khoangGia.max > 0">
            <label class="block font-medium text-gray-800">Khoảng giá</label>
            <p class="text-gray-700 mt-1">
              Từ
              <strong class="font-semibold text-blue-600"
                >{{ khoangGia.min.toLocaleString() }} VND</strong
              >
              đến
              <strong class="font-semibold text-blue-600"
                >{{ localDonGia.toLocaleString() }} VND</strong
              >
            </p>
            <input
              type="range"
              v-model="localDonGia"
              :min="khoangGia.min"
              :max="khoangGia.max"
              :step="donGiaStep"
              @change="updateDonGiaModel"
              class="w-full h-2 bg-gray-200 rounded-lg appearance-none cursor-pointer accent-blue-600 mt-2"
            />
          </div>

          <!-- Sắp xếp -->
          <div>
            <label class="block font-medium text-gray-800"
              >Sắp xếp đơn giá</label
            >
            <div class="flex items-center gap-4 pt-2">
              <label class="flex items-center gap-1.5 cursor-pointer">
                <input
                  type="radio"
                  value="asc"
                  v-model="modelSapXep"
                  class="text-blue-600 focus:ring-blue-500"
                />
                Tăng dần
              </label>
              <label class="flex items-center gap-1.5 cursor-pointer">
                <input
                  type="radio"
                  value="desc"
                  v-model="modelSapXep"
                  class="text-blue-600 focus:ring-blue-500"
                />
                Giảm dần
              </label>
            </div>
          </div>
        </div>
      </transition>
    </div>

    <!-- Nhà sản xuất -->
    <div>
      <button
        class="cursor-pointer w-full text-left px-4 py-3 bg-blue-500 hover:bg-blue-600 font-semibold text-white flex justify-between items-center transition-colors"
        @click="toggle('nhaSanXuat')"
      >
        Nhà sản xuất
        <i
          :class="
            isOpen.nhaSanXuat
              ? 'fi-rr-angle-small-up'
              : 'fi-rr-angle-small-down'
          "
          class="text-xl flex items-center"
        ></i>
      </button>
      <transition name="slide-fade">
        <div v-if="isOpen.nhaSanXuat" class="px-4 py-3 space-y-2 bg-white">
          <label class="flex items-center gap-2 cursor-pointer">
            <input
              type="radio"
              name="manufacturer"
              :value="null"
              v-model="modelNhaSanXuat"
              class="text-blue-600 focus:ring-blue-500"
            />
            Tất cả
          </label>
          <label
            v-for="tenNhaSanXuat in nhaSanXuatOption"
            :key="tenNhaSanXuat"
            class="flex items-center gap-2 cursor-pointer"
          >
            <input
              type="radio"
              name="manufacturer"
              :value="tenNhaSanXuat"
              v-model="modelNhaSanXuat"
              class="text-blue-600 focus:ring-blue-500"
            />
            {{ tenNhaSanXuat }}
          </label>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import type { FilterOption, KhoangGia } from "@/types/filter.types";

const props = defineProps<{
  danhMucOption: FilterOption[];
  nhaSanXuatOption: string[];
  khoangGia?: KhoangGia;
}>();

// Sử dụng defineModel để tạo v-model cho component
const modelDanhMuc = defineModel<number | null>("danhMuc"); // number cho ID, null cho "Tất cả"
const modelNhaSanXuat = defineModel<string | null>("nhaSanXuat");
const modelDonGia = defineModel<number>("donGia", { default: 0 });
const modelSapXep = defineModel<"asc" | "desc">("sapXep");

// State nội bộ cho thanh trượt để cập nhật UI ngay lập tức
const localDonGia = ref(modelDonGia.value);

// Cập nhật model của component cha chỉ khi người dùng thả chuột (kết thúc kéo).
// Sự kiện @change trên input[type=range] sẽ gọi hàm này.
function updateDonGiaModel() {
  modelDonGia.value = localDonGia.value;
}

// Đồng bộ state nội bộ nếu model từ cha thay đổi (ví dụ: nút reset filter)
watch(modelDonGia, (newModelValue) => {
  if (newModelValue !== localDonGia.value) {
    localDonGia.value = newModelValue;
  }
});

const isOpen = ref({
  danhMuc: true,
  donGia: true,
  nhaSanXuat: true,
});

// Khi khoangGia được truyền vào, đặt giá trị mặc định cho thanh trượt là giá trị max
// để tránh hiển thị "đến 0" khi mới tải.
watch(
  () => props.khoangGia,
  (newKhoangGia) => {
    if (newKhoangGia && newKhoangGia.max > 0 && localDonGia.value === 0) {
      // Cập nhật cả model cha và state nội bộ để đồng bộ khi khởi tạo
      localDonGia.value = newKhoangGia.max;
      modelDonGia.value = newKhoangGia.max;
    }
  },
  { immediate: true }
);

const donGiaStep = computed(() => {
  if (!props.khoangGia || props.khoangGia.max <= 0) {
    return 1; // Giá trị mặc định nếu không có khoảng giá
  }
  if (props.khoangGia.max <= 1000000) {
    return 10000;
  }
  return 100000;
});

function toggle(section: keyof typeof isOpen.value) {
  isOpen.value[section] = !isOpen.value[section];
}
</script>

<style scoped>
.slide-fade-enter-active {
  transition: all 0.2s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.2s cubic-bezier(1, 0.5, 0.8, 1);
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateY(-10px);
  opacity: 0;
}
</style>
