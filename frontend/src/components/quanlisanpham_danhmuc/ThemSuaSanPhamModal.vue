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
          <!-- Đảm bảo kiểu value là số -->
          <select
            id="danhMucCha"
            v-model="selectedDanhMucCha"
            class="input w-full mt-1"
          >
            <option :value="null">-- Chọn danh mục cha --</option>
            <option
              v-for="dmc in danhMucChaOptions"
              :key="dmc.maDanhMuc"
              :value="dmc.maDanhMuc"
            >
              {{ dmc.tenDanhMuc || "Không rõ tên" }}
            </option>
          </select>
        </div>

        <!-- Danh mục con -->
        <div>
          <label for="danhMucCon">Danh mục con</label>
          <select
            id="danhMucCon"
            v-model="selectedDanhMucCon"
            class="input w-full mt-1"
            :disabled="!selectedDanhMucCha || danhMucConOptions.length === 0"
            required
          >
            <option :value="null">-- Chọn danh mục con --</option>
            <option
              v-for="dmc in danhMucConOptions"
              :key="dmc.maDanhMuc"
              :value="dmc.maDanhMuc"
            >
              {{ dmc.tenDanhMuc || "Không rõ tên" }}
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
            min="0"
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
            min="0"
            required
          />
        </div>

        <!-- Nhãn -->
        <div>
          <label for="nhan">Nhãn sản phẩm</label>
          <select id="nhan" v-model="formData.nhan" class="input w-full mt-1">
            <option :value="null">-- Không có nhãn --</option>
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
              class="relative group cursor-pointer rounded-lg"
              @click="setPrimaryImage('existing', image.duongDan)"
              :class="{
                'ring-2 ring-offset-2 ring-blue-500':
                  primaryImageIdentifier === image.duongDan,
              }"
            >
              <img
                :src="image.duongDan"
                alt="Ảnh sản phẩm"
                class="w-full aspect-square object-cover rounded-lg border"
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
              class="relative group cursor-pointer rounded-lg"
              @click="setPrimaryImage('new', index)"
              :class="{
                'ring-2 ring-offset-2 ring-blue-500':
                  primaryImageIdentifier === newImageFiles[index]?.name,
              }"
            >
              <img
                :src="preview"
                alt="Ảnh xem trước"
                class="w-full aspect-square object-cover rounded-lg border"
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
              class="flex flex-col justify-center items-center w-full aspect-square border-2 border-dashed border-gray-300 rounded-lg cursor-pointer hover:bg-gray-50"
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
          :disabled="isLoading || (isEditMode && !isFormDirty) || !canSubmit"
        >
          {{ isEditMode ? "Cập nhật sản phẩm" : "Thêm sản phẩm" }}
        </button>
      </div>
    </template>
  </BaseModal>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick } from "vue";
import BaseModal from "../base/modals/BaseModal.vue";
import { useDanhMucStore } from "@/store/danhmuc.store";
import { useToast } from "@/composables/useToast";
import { themSanPham, capNhatSanPham } from "@/service/sanpham.service";
import type {
  DanhMucOptionResponse,
  DanhMucConOption,
} from "@/types/danhmuc.types";
import type {
  ThemSanPhamRequest,
  CapNhatSanPhamRequest,
  ChiTietSanPhamQuanLyResponse,
  AnhMinhHoa,
} from "@/types/sanpham.types";
import {
  NhanSanPhamLabel,
  TrangThaiSanPhamKey,
  NhanSanPhamKey,
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
  (e: "success"): void;
}>();

// --- Store ---
const danhMucStore = useDanhMucStore();
const { showToast } = useToast();

// --- Refs ---
const isLoading = ref(false);
const fileInputRef = ref<HTMLInputElement | null>(null);

// Form data
const initialFormData: ThemSanPhamRequest = {
  tenSanPham: "",
  moTa: "",
  ghiChu: "",
  donGia: 0,
  donVi: "",
  maDanhMuc: 0, // 0 is used as the "not selected" value for validation
  nhaSanXuat: "",
  trangThai: TrangThaiSanPhamKey.BAN,
  nhan: NhanSanPhamKey.MOI,
  anhChinhIndex: 0,
};
const formData = ref<ThemSanPhamRequest>({ ...initialFormData });

// Category selection state
const selectedDanhMucCha = ref<number | null>(null);
const selectedDanhMucCon = ref<number | null>(null);

// Image state
const existingImages = ref<AnhMinhHoa[]>([]);
const newImageFiles = ref<File[]>([]);
const newImagePreviews = ref<string[]>([]);
const urlsAnhXoaToDelete = ref<string[]>([]);
const primaryImageIdentifier = ref<string | null>(null);

// Dirty checking state
const initialEditData = ref<string>("");
const initialPrimaryImageIdentifier = ref<string | null>(null);
const initialExistingImagesCount = ref(0);

// --- Computed Properties ---
const isDangBan = computed({
  get: () => formData.value.trangThai === TrangThaiSanPhamKey.BAN,
  set: (value) => {
    formData.value.trangThai = value
      ? TrangThaiSanPhamKey.BAN
      : TrangThaiSanPhamKey.KHONG_BAN;
  },
});

const allCategories = computed(() => danhMucStore.allCategoriesFlat);

const danhMucChaOptions = computed(() => {
  // Lọc ra các danh mục cha từ danh sách phẳng (những danh mục không có tenDanhMucCha)
  return allCategories.value.filter((dm) => !dm.tenDanhMucCha);
});

const danhMucConOptions = computed(() => {
  if (!selectedDanhMucCha.value) return [];
  // Tìm đối tượng danh mục cha đã chọn để lấy tên của nó
  const parent = danhMucChaOptions.value.find(
    (p) => p.maDanhMuc === selectedDanhMucCha.value
  );
  if (!parent) return [];
  // Lọc ra các danh mục con từ danh sách phẳng dựa vào tenDanhMucCha
  return allCategories.value.filter(
    (dm) => dm.tenDanhMucCha === parent.tenDanhMuc
  );
});

const isFormDirty = computed(() => {
  if (!props.isEditMode) {
    return true; // Always enable for "Add" mode
  }
  // In edit mode, check for changes
  const formDataChanged =
    JSON.stringify(formData.value) !== initialEditData.value;
  const newImagesAdded = newImageFiles.value.length > 0;
  const imagesRemoved =
    existingImages.value.length !== initialExistingImagesCount.value;
  const primaryImageChanged =
    primaryImageIdentifier.value !== initialPrimaryImageIdentifier.value;

  return (
    formDataChanged || newImagesAdded || imagesRemoved || primaryImageChanged
  );
});

const canSubmit = computed(() => {
  // Additional check to ensure a primary image is selected before allowing submission
  const hasImages =
    existingImages.value.length > 0 || newImageFiles.value.length > 0;
  const hasPrimaryImage = !!primaryImageIdentifier.value;
  return hasImages && hasPrimaryImage;
});

// --- Watchers ---
watch(selectedDanhMucCha, (maCha) => {
  // When parent category changes, reset the child category selection
  selectedDanhMucCon.value = null;
});

watch(selectedDanhMucCon, (maCon) => {
  // Use 0 as the "not selected" value to satisfy type constraints and validation
  formData.value.maDanhMuc = maCon ?? 0;
});

watch(
  () => props.visible,
  async (isVisible) => {
    if (isVisible) {
      resetFormState();

      // Lấy danh sách danh mục dạng phẳng, store sẽ cache lại.
      await danhMucStore.fetchAllCategoriesFlat();

      // If editing, populate the form after categories are loaded.
      if (props.isEditMode && props.sanPhamSua) {
        // Use nextTick to ensure dependent computed properties (like danhMucConOptions)
        // have a chance to update before we set selected values.
        await nextTick();
        populateFormForEdit(props.sanPhamSua);
      }
    }
  }
);

// --- Methods ---
const resetFormState = () => {
  formData.value = { ...initialFormData };
  selectedDanhMucCha.value = null;
  selectedDanhMucCon.value = null;

  // Revoke old blob URLs to prevent memory leaks
  newImagePreviews.value.forEach((url) => URL.revokeObjectURL(url));

  existingImages.value = [];
  newImageFiles.value = [];
  newImagePreviews.value = [];
  urlsAnhXoaToDelete.value = [];
  primaryImageIdentifier.value = null;

  // Reset dirty check state
  initialEditData.value = "";
  initialPrimaryImageIdentifier.value = null;
  initialExistingImagesCount.value = 0;

  // Reset the file input visually
  if (fileInputRef.value) {
    fileInputRef.value.value = "";
  }
};

const populateFormForEdit = (sanPham: ChiTietSanPhamQuanLyResponse) => {
  const populatedData: Omit<ThemSanPhamRequest, "anhChinhIndex"> = {
    tenSanPham: sanPham.tenSanPham,
    moTa: sanPham.moTa,
    ghiChu: sanPham.ghiChu,
    donGia: sanPham.donGia,
    donVi: sanPham.donVi,
    maDanhMuc: sanPham.maDanhMuc,
    nhaSanXuat: sanPham.nhaSanXuat,
    trangThai: sanPham.trangThai.value,
    nhan: sanPham.nhan?.value || null,
  };
  formData.value = { ...populatedData, anhChinhIndex: 0 };

  // Store initial state for dirty checking (deep copy)
  initialEditData.value = JSON.stringify(formData.value);
  existingImages.value = [...sanPham.anhMinhHoas];
  initialExistingImagesCount.value = sanPham.anhMinhHoas.length;

  const anhChinh = sanPham.anhMinhHoas.find((a) => a.laAnhChinh);
  const initialPrimary = anhChinh
    ? anhChinh.duongDan
    : sanPham.anhMinhHoas[0]?.duongDan || null;
  primaryImageIdentifier.value = initialPrimary;
  initialPrimaryImageIdentifier.value = initialPrimary;

  // Tìm và đặt danh mục cha và con từ danh sách phẳng
  const productCategory = allCategories.value.find(
    (c) => c.maDanhMuc === sanPham.maDanhMuc
  );

  if (productCategory?.tenDanhMucCha) {
    // Đây là danh mục con, tìm cha của nó
    const parentCategory = allCategories.value.find(
      (p) => p.tenDanhMuc === productCategory.tenDanhMucCha
    );
    if (parentCategory) {
      selectedDanhMucCha.value = parentCategory.maDanhMuc;
      // Đợi DOM cập nhật danh sách con rồi mới chọn
      nextTick(() => {
        selectedDanhMucCon.value = sanPham.maDanhMuc;
      });
    }
  }
};

const triggerFileInput = () => {
  fileInputRef.value?.click();
};

const handleFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files) {
    const files = Array.from(target.files);
    // Kiểm tra xem đây có phải là lần tải ảnh đầu tiên không (khi chưa có ảnh nào)
    const isFirstUpload =
      existingImages.value.length === 0 && newImageFiles.value.length === 0;

    newImageFiles.value.push(...files);
    files.forEach((file) => {
      const previewUrl = URL.createObjectURL(file);
      newImagePreviews.value.push(previewUrl);
    });

    // Nếu đây là lần tải ảnh đầu tiên, tự động đặt ảnh mới đầu tiên làm ảnh chính
    if (isFirstUpload && files.length > 0) {
      // Sử dụng tên file làm định danh, giống như logic trong setPrimaryImage
      primaryImageIdentifier.value = files[0].name;
    }
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
  if (primaryImageIdentifier.value === fileToRemove.name) {
    primaryImageIdentifier.value = null;
  }
  URL.revokeObjectURL(urlToRemove);
  newImageFiles.value.splice(index, 1);
  newImagePreviews.value.splice(index, 1);
};

const removeExistingImage = (image: AnhMinhHoa) => {
  if (!image.duongDan) return;
  if (primaryImageIdentifier.value === image.duongDan) {
    primaryImageIdentifier.value = null;
  }
  urlsAnhXoaToDelete.value.push(image.duongDan);
  existingImages.value = existingImages.value.filter(
    (img) => img.duongDan !== image.duongDan
  );
};

const handleSubmit = async () => {
  if (!formData.value.maDanhMuc) {
    showToast({ loai: "loi", thongBao: "Vui lòng chọn danh mục sản phẩm." });
    return;
  }
  const totalImages = existingImages.value.length + newImageFiles.value.length;
  if (totalImages === 0) {
    showToast({ loai: "loi", thongBao: "Vui lòng tải lên ít nhất một ảnh." });
    return;
  }
  if (!primaryImageIdentifier.value) {
    showToast({
      loai: "loi",
      thongBao: "Vui lòng chọn một ảnh làm ảnh chính.",
    });
    return;
  }

  isLoading.value = true;
  try {
    if (props.isEditMode && props.sanPhamSua) {
      // Xây dựng payload một cách cẩn thận để khớp với CapNhatSanPhamRequest
      // và tránh gửi các trường không cần thiết như 'anhChinhIndex'.
      const updateData: CapNhatSanPhamRequest = {
        tenSanPham: formData.value.tenSanPham,
        maDanhMuc: formData.value.maDanhMuc,
        moTa: formData.value.moTa,
        nhaSanXuat: formData.value.nhaSanXuat,
        donGia: formData.value.donGia,
        donVi: formData.value.donVi,
        nhan: formData.value.nhan,
        ghiChu: formData.value.ghiChu,
        trangThai: formData.value.trangThai,
        urlsAnhXoa:
          urlsAnhXoaToDelete.value.length > 0
            ? urlsAnhXoaToDelete.value
            : undefined,
        // Validation ở trên đảm bảo primaryImageIdentifier là một string ở đây.
        anhChinhIdentifier: primaryImageIdentifier.value as string,
      };

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
      let anhChinhIndex = 0;
      // For new products, the primary image must be one of the new files
      const foundIndex = newImageFiles.value.findIndex(
        (f) => f.name === primaryImageIdentifier.value
      );
      if (foundIndex !== -1) {
        anhChinhIndex = foundIndex;
      }
      const createData: ThemSanPhamRequest = {
        ...formData.value,
        anhChinhIndex,
      };
      await themSanPham(createData, newImageFiles.value);
      showToast({
        loai: "thanhCong",
        thongBao: "Thêm sản phẩm mới thành công!",
      });
    }
    emit("success");
    emit("close");
  } catch (error) {
    const err = error as any;
    const errorMessage =
      err.response?.data?.message || "Đã có lỗi xảy ra. Vui lòng thử lại.";

    showToast({
      loai: "loi",
      thongBao: errorMessage,
    });
    console.error("ERROR - handleSubmit:", error);
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
  @apply border border-gray-300 rounded px-3 py-2 bg-white;
}
.input:focus {
  @apply outline-none ring-2 ring-blue-500;
}
.input:disabled {
  @apply bg-gray-100 cursor-not-allowed;
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
