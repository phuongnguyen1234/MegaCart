<template>
  <BaseModal
    :visible="visible"
    :title="title"
    :is-loading="isLoading"
    :width-class="widthClass"
    @close="$emit('close')"
  >
    <form @submit.prevent="handleSubmit">
      <div class="grid grid-cols-2 gap-x-6 gap-y-4">
        <!-- Tên sản phẩm chiếm 2 cột -->
        <div class="col-span-2">
          <label for="tenSanPham">Tên sản phẩm</label>
          <input
            id="tenSanPham"
            type="text"
            v-model="formData.tenSanPham"
            class="input w-full mt-1"
            required
          />
        </div>

        <!-- Danh mục -->
        <div>
          <label for="danhMucCha">Danh mục cha</label>
          <select
            id="danhMucCha"
            v-model="selectedDanhMucCha"
            class="input w-full mt-1"
          >
            <option value="">-- Chọn danh mục cha --</option>
            <option
              v-for="dmc in danhMucChaOptions"
              :key="dmc.maDanhMuc"
              :value="dmc.maDanhMuc"
            >
              {{ dmc.tenDanhMuc }}
            </option>
          </select>
        </div>

        <div>
          <label for="danhMucCon">Danh mục con</label>
          <select
            id="danhMucCon"
            v-model="formData.maDanhMuc"
            class="input w-full mt-1"
            :disabled="!selectedDanhMucCha || danhMucConOptions.length === 0"
          >
            <option value="">-- Chọn danh mục con --</option>
            <option
              v-for="dmc in danhMucConOptions"
              :key="dmc.maDanhMuc"
              :value="dmc.maDanhMuc"
            >
              {{ dmc.tenDanhMuc }}
            </option>
          </select>
        </div>

        <!-- Mô tả chiếm 2 cột -->
        <div class="col-span-2">
          <label for="moTa">Mô tả</label>
          <textarea
            id="moTa"
            rows="3"
            v-model="formData.moTa"
            class="input w-full resize-none mt-1"
          ></textarea>
        </div>

        <!-- Nhà sản xuất -->
        <div>
          <label for="nhaSanXuat">Nhà sản xuất</label>
          <input
            id="nhaSanXuat"
            type="text"
            v-model="formData.nhaSanXuat"
            class="input w-full mt-1"
            required
          />
        </div>

        <!-- Đơn giá và Đơn vị -->
        <div>
          <label for="donGia">Đơn giá (VND)</label>
          <input
            id="donGia"
            type="number"
            v-model="formData.donGia"
            class="input w-full mt-1"
            required
          />
        </div>
        <div>
          <label for="donVi">Đơn vị</label>
          <input
            id="donVi"
            type="text"
            v-model="formData.donVi"
            class="input w-full mt-1"
            required
          />
        </div>

        <!-- Nhãn -->
        <div>
          <label for="nhan">Nhãn sản phẩm</label>
          <select
            id="nhan"
            v-model="formData.nhan"
            class="input w-full mt-1"
            required
          >
            <option
              v-for="(label, key) in NhanSanPhamLabel"
              :key="key"
              :value="key"
            >
              {{ label }}
            </option>
          </select>
        </div>

        <!-- Ghi chú chiếm 2 cột -->
        <div class="col-span-2">
          <label for="ghiChu">Ghi chú (Tùy chọn)</label>
          <textarea
            id="ghiChu"
            rows="2"
            v-model="formData.ghiChu"
            class="input w-full resize-none mt-1"
          ></textarea>
        </div>

        <!-- Trạng thái -->
        <div>
          <label>Trạng thái</label>
          <div class="mt-2">
            <label
              for="trangThai"
              class="relative inline-flex items-center cursor-pointer"
            >
              <input
                type="checkbox"
                v-model="isDangBan"
                id="trangThai"
                class="sr-only peer"
              />
              <div
                class="w-11 h-6 bg-gray-200 rounded-full peer peer-focus:outline-none peer-focus:ring-2 peer-focus:ring-blue-500 peer-checked:after:translate-x-full rtl:peer-checked:after:-translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:start-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-green-500"
              ></div>
              <span class="ms-3 text-sm font-medium">{{
                isDangBan ? "Bán" : "Không bán"
              }}</span>
            </label>
          </div>
        </div>

        <!-- Ảnh minh họa chiếm 2 cột -->
        <div class="col-span-2">
          <label class="block text-sm font-medium text-gray-700"
            >Ảnh minh họa (chọn ít nhất 1, ảnh đầu tiên là ảnh chính)</label
          >
          <!-- Input ẩn để chọn file -->
          <input
            type="file"
            ref="fileInputRef"
            multiple
            accept="image/*"
            class="hidden"
            @change="handleFileChange"
          />
          <!-- Vùng hiển thị ảnh và nút tải lên -->
          <div class="mt-1 grid grid-cols-4 gap-4">
            <!-- Hiển thị ảnh đã có (chế độ sửa) -->
            <div
              v-for="image in existingImages"
              :key="image.maAnh"
              class="relative group cursor-pointer"
              @click="setPrimaryImage('existing', image.duongDan)"
            >
              <img
                :src="image.duongDan"
                alt="Ảnh sản phẩm"
                class="w-full h-24 object-cover rounded-lg border"
                :class="{
                  'ring-2 ring-offset-2 ring-blue-500':
                    primaryImageIdentifier === image.duongDan,
                }"
              />
              <button
                @click.stop="removeExistingImage(image)"
                class="absolute top-1 right-1 bg-black/50 text-white rounded-full w-5 h-5 flex items-center justify-center text-xs opacity-0 group-hover:opacity-100 transition-opacity"
              >
                ✕
              </button>
            </div>
            <!-- Hiển thị ảnh mới chọn -->
            <div
              v-for="(preview, index) in newImagePreviews"
              :key="index"
              class="relative group cursor-pointer"
              @click="setPrimaryImage('new', index)"
            >
              <img
                :src="preview"
                alt="Ảnh xem trước"
                class="w-full h-24 object-cover rounded-lg border"
                :class="{
                  'ring-2 ring-offset-2 ring-blue-500':
                    primaryImageIdentifier === newImageFiles[index]?.name,
                }"
              />
              <button
                @click.stop="removeNewImage(index)"
                class="absolute top-1 right-1 bg-black/50 text-white rounded-full w-5 h-5 flex items-center justify-center text-xs opacity-0 group-hover:opacity-100 transition-opacity"
              >
                ✕
              </button>
            </div>
            <!-- Nút tải ảnh -->
            <div
              @click="triggerFileInput"
              class="flex flex-col justify-center items-center w-full h-24 border-2 border-dashed border-gray-300 rounded-lg cursor-pointer hover:bg-gray-50"
            >
              <i class="fi fi-rr-images text-2xl text-gray-400"></i>
              <p class="text-xs text-gray-500 mt-1">Tải ảnh lên</p>
            </div>
          </div>
        </div>
      </div>
    </form>
    <template #footer>
      <div class="flex justify-end gap-4">
        <button class="btn" @click="$emit('close')">Hủy</button>
        <button
          type="submit"
          class="btn btn-primary"
          @click="handleSubmit"
          :disabled="isLoading || (isEditMode && !isFormDirty)"
        >
          {{ isEditMode ? "Cập nhật sản phẩm" : "Thêm sản phẩm" }}
        </button>
      </div>
    </template>
  </BaseModal>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick } from "vue";
import BaseModal from "@/components/base/modals/BaseModal.vue";
import { useDanhMucStore } from "@/store/danhmuc.store";
import { useToast } from "@/composables/useToast";
import { themSanPham, capNhatSanPham } from "@/service/sanpham.service";
import type {
  ThemSanPhamRequest,
  CapNhatSanPhamRequest,
  ChiTietSanPhamQuanLyResponse,
  AnhMinhHoa,
} from "@/types/sanpham.types";
import {
  TrangThaiSanPhamKey,
  NhanSanPhamKey,
  NhanSanPhamLabel,
} from "@/types/sanpham.types";

const props = defineProps<{
  visible: boolean;
  title?: string;
  widthClass?: string;
  isEditMode?: boolean;
  sanPhamSua?: ChiTietSanPhamQuanLyResponse | null;
}>();

const emit = defineEmits<{
  (e: "close"): void;
  (e: "success"): void; // Gửi sự kiện khi thêm/sửa thành công
}>();

// --- Store ---
const danhMucStore = useDanhMucStore();
const { showToast } = useToast();
const danhMucChaOptions = computed(() => danhMucStore.menuItems);

// --- Form State ---
const initialFormData: ThemSanPhamRequest = {
  tenSanPham: "",
  moTa: "",
  ghiChu: "",
  donGia: 0,
  donVi: "",
  maDanhMuc: 0,
  nhaSanXuat: "",
  trangThai: TrangThaiSanPhamKey.BAN,
  nhan: NhanSanPhamKey.MOI,
  anhChinhIndex: 0, // Mặc định ảnh đầu tiên là ảnh chính
};

const formData = ref<ThemSanPhamRequest>({ ...initialFormData });
const initialEditData = ref<ThemSanPhamRequest | null>(null); // Lưu trạng thái ban đầu để so sánh
const initialPrimaryImageIdentifier = ref<string | null>(null);
const selectedDanhMucCha = ref<number | "">("");
const isLoading = ref(false);

// --- Image State ---
const fileInputRef = ref<HTMLInputElement | null>(null);
const newImageFiles = ref<File[]>([]);
const newImagePreviews = ref<string[]>([]);
const existingImages = ref<AnhMinhHoa[]>([]);
const urlsAnhXoaToDelete = ref<string[]>([]);
const primaryImageIdentifier = ref<string | null>(null); // Lưu URL hoặc tên file của ảnh chính

// --- Computed Properties ---
const isFormDirty = computed(() => {
  // Chỉ áp dụng cho chế độ sửa
  if (!props.isEditMode || !initialEditData.value) {
    return false;
  }

  // 1. Kiểm tra có ảnh mới được thêm không
  if (newImageFiles.value.length > 0) {
    return true;
  }

  // 2. Kiểm tra có ảnh cũ bị xóa không
  if (urlsAnhXoaToDelete.value.length > 0) {
    return true;
  }

  // 3. Kiểm tra ảnh chính có thay đổi không
  if (primaryImageIdentifier.value !== initialPrimaryImageIdentifier.value) {
    return true;
  }

  // 4. So sánh dữ liệu form (dùng JSON.stringify để so sánh sâu)
  return (
    JSON.stringify(formData.value) !== JSON.stringify(initialEditData.value)
  );
});

const isDangBan = computed({
  get: () => formData.value.trangThai === TrangThaiSanPhamKey.BAN,
  set: (value) => {
    formData.value.trangThai = value
      ? TrangThaiSanPhamKey.BAN
      : TrangThaiSanPhamKey.KHONG_BAN;
  },
});

const danhMucConOptions = computed(() => {
  if (!selectedDanhMucCha.value) return [];
  const parent = danhMucChaOptions.value.find(
    (p) => p.maDanhMuc === selectedDanhMucCha.value
  );
  return parent ? parent.danhMucCons : [];
});

// --- Watchers ---
watch(selectedDanhMucCha, (newParentId) => {
  // Khi danh mục cha thay đổi, reset danh mục con
  // Việc chọn danh mục con sẽ cập nhật formData.maDanhMuc
  formData.value.maDanhMuc = 0;
});

// Watch khi modal được mở/đóng
watch(
  () => props.visible,
  (isVisible) => {
    if (isVisible) {
      resetFormState(); // Luôn reset khi mở
      if (props.isEditMode && props.sanPhamSua) {
        // Chế độ sửa: điền dữ liệu
        populateFormForEdit(props.sanPhamSua);
      }
    } else {
      // Dọn dẹp URL ảnh preview khi đóng modal để giải phóng bộ nhớ
      newImagePreviews.value.forEach((url) => URL.revokeObjectURL(url));
    }
  }
);

// --- Methods ---

const findParentCategoryByChildId = (childId: number) => {
  if (!childId) return undefined;
  return danhMucChaOptions.value.find((parent) =>
    parent.danhMucCons.some((child) => child.maDanhMuc === childId)
  );
};

const resetFormState = () => {
  formData.value = { ...initialFormData };
  initialEditData.value = null;
  selectedDanhMucCha.value = "";
  newImageFiles.value = [];
  newImagePreviews.value = [];
  existingImages.value = [];
  urlsAnhXoaToDelete.value = [];
  primaryImageIdentifier.value = null;
  initialPrimaryImageIdentifier.value = null;
};

const populateFormForEdit = async (sanPham: ChiTietSanPhamQuanLyResponse) => {
  const populatedData: ThemSanPhamRequest = {
    tenSanPham: sanPham.tenSanPham,
    moTa: sanPham.moTa,
    ghiChu: sanPham.ghiChu,
    donGia: sanPham.donGia,
    donVi: sanPham.donVi,
    maDanhMuc: sanPham.maDanhMuc,
    nhaSanXuat: sanPham.nhaSanXuat,
    trangThai: sanPham.trangThai.value, // Lấy key từ object
    nhan: sanPham.nhan.value, // Lấy key từ object
    anhChinhIndex: 0, // Placeholder for edit mode to satisfy the type
  };
  // Điền dữ liệu vào form và lưu lại trạng thái ban đầu để so sánh
  formData.value = { ...populatedData };
  initialEditData.value = { ...populatedData };
  existingImages.value = [...sanPham.anhMinhHoas];

  // Set ảnh chính ban đầu
  const anhChinh = sanPham.anhMinhHoas.find((a) => a.laAnhChinh);
  const initialPrimary = anhChinh
    ? anhChinh.duongDan
    : sanPham.anhMinhHoas[0]?.duongDan || null;
  primaryImageIdentifier.value = initialPrimary;
  initialPrimaryImageIdentifier.value = initialPrimary;

  // Tìm và set danh mục cha
  const parent = findParentCategoryByChildId(sanPham.maDanhMuc);
  if (parent) {
    selectedDanhMucCha.value = parent.maDanhMuc;
    // Đợi DOM cập nhật để combobox con có options
    await nextTick();
    formData.value.maDanhMuc = sanPham.maDanhMuc;
  }
};

const triggerFileInput = () => {
  fileInputRef.value?.click();
};

const handleFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files) {
    const files = Array.from(target.files);
    newImageFiles.value.push(...files);
    files.forEach((file) => {
      const previewUrl = URL.createObjectURL(file);
      newImagePreviews.value.push(previewUrl);
    });
  }
};

const setPrimaryImage = (
  type: "existing" | "new",
  identifier: string | number
) => {
  if (type === "existing") {
    primaryImageIdentifier.value = identifier as string;
  } else {
    const file = newImageFiles.value[identifier as number];
    if (file) {
      primaryImageIdentifier.value = file.name;
    }
  }
};

const removeNewImage = (index: number) => {
  const fileToRemove = newImageFiles.value[index];
  const urlToRemove = newImagePreviews.value[index];

  // Nếu ảnh bị xóa là ảnh chính, reset lại
  if (primaryImageIdentifier.value === fileToRemove.name) {
    primaryImageIdentifier.value = null;
  }

  URL.revokeObjectURL(urlToRemove); // Giải phóng bộ nhớ
  newImageFiles.value.splice(index, 1);
  newImagePreviews.value.splice(index, 1);
};

const removeExistingImage = (image: AnhMinhHoa) => {
  if (!image.duongDan) return;

  // Nếu ảnh bị xóa là ảnh chính, reset lại
  if (primaryImageIdentifier.value === image.duongDan) {
    primaryImageIdentifier.value = null;
  }

  // Thêm URL vào danh sách cần xóa
  urlsAnhXoaToDelete.value.push(image.duongDan);

  // Xóa khỏi danh sách hiển thị
  existingImages.value = existingImages.value.filter(
    (img) => img.duongDan !== image.duongDan
  );
};

const handleSubmit = async () => {
  // Validation
  if (!formData.value.maDanhMuc) {
    showToast({ loai: "loi", thongBao: "Vui lòng chọn danh mục sản phẩm." });
    return;
  }
  if (existingImages.value.length === 0 && newImageFiles.value.length === 0) {
    showToast({ loai: "loi", thongBao: "Vui lòng tải lên ít nhất một ảnh." });
    return;
  }

  isLoading.value = true;
  try {
    if (props.isEditMode && props.sanPhamSua) {
      // --- Logic Cập nhật ---
      if (!primaryImageIdentifier.value) {
        showToast({
          loai: "loi",
          thongBao: "Vui lòng chọn một ảnh làm ảnh chính.",
        });
        isLoading.value = false;
        return;
      }

      const updateData: CapNhatSanPhamRequest = {
        ...formData.value,
        urlsAnhXoa:
          urlsAnhXoaToDelete.value.length > 0
            ? urlsAnhXoaToDelete.value
            : undefined,
        anhChinhIdentifier: primaryImageIdentifier.value,
      };

      // Xóa các trường không cần thiết cho payload cập nhật
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      delete (updateData as any).anhChinhIndex;

      await capNhatSanPham(
        props.sanPhamSua.maSanPham,
        updateData,
        newImageFiles.value
      );
      showToast({
        loai: "thanhCong",
        thongBao: "Cập nhật sản phẩm thành công!",
      });
    } else {
      // --- Logic Thêm mới ---
      // Xác định index của ảnh chính
      let anhChinhIndex = 0; // Mặc định là ảnh đầu tiên
      if (primaryImageIdentifier.value) {
        const foundIndex = newImageFiles.value.findIndex(
          (f) => f.name === primaryImageIdentifier.value
        );
        if (foundIndex !== -1) {
          anhChinhIndex = foundIndex;
        }
      }

      const payload: ThemSanPhamRequest = {
        ...formData.value,
        anhChinhIndex: anhChinhIndex,
      };

      await themSanPham(payload, newImageFiles.value);
      showToast({
        loai: "thanhCong",
        thongBao: "Thêm sản phẩm mới thành công!",
      });
    }
    emit("success");
    emit("close");
  } catch (error) {
    console.error("Lỗi khi lưu sản phẩm:", error);
    showToast({ loai: "loi", thongBao: "Đã có lỗi xảy ra." });
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
/* 
  Thêm @reference để cung cấp ngữ cảnh của Tailwind cho khối style này.
  Đường dẫn cần trỏ đến file CSS chính của bạn.
*/
@reference "../../assets/styles/main.css";

.input {
  @apply border border-gray-300 rounded px-3 py-2;
}
.input:focus {
  @apply outline-none ring-2 ring-blue-500;
}

.btn {
  @apply inline-flex justify-center items-center py-2 px-4 border shadow-sm text-sm font-medium rounded-md focus:outline-none focus:ring-2 focus:ring-offset-2 transition-colors;
  /* Kiểu mặc định cho nút "Hủy" */
  @apply border-gray-300 bg-white text-gray-700 hover:bg-gray-50 focus:ring-gray-400;
}

.btn-primary {
  /* Ghi đè kiểu cho nút chính */
  @apply border-transparent bg-blue-600 text-white hover:bg-blue-700 focus:ring-blue-500;
}
</style>
