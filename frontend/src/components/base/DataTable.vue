<template>
  <div class="align-middle inline-block min-w-full">
    <div class="shadow overflow-hidden border-b border-gray-200 sm:rounded-lg">
      <table class="min-w-full divide-y divide-gray-200">
        <thead class="bg-gray-50">
          <tr>
            <th
              v-for="header in headers"
              :key="header"
              scope="col"
              class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
            >
              {{ header }}
            </th>
          </tr>
        </thead>
        <tbody class="bg-white divide-y divide-gray-200">
          <!-- Hiển thị thông báo khi không có dữ liệu -->
          <tr v-if="rows.length === 0">
            <td
              :colspan="headers.length"
              class="px-6 py-4 whitespace-nowrap text-sm text-gray-500 text-center"
            >
              Không có dữ liệu để hiển thị
            </td>
          </tr>
          <!-- Lặp qua các dòng dữ liệu -->
          <tr
            v-else
            v-for="(row, rowIndex) in rows"
            :key="rowIndex"
            class="hover:bg-gray-50"
          >
            <td
              v-for="(cell, cellIndex) in row"
              :key="cellIndex"
              class="px-6 py-4 whitespace-nowrap text-sm text-gray-500 align-top"
            >
              <!-- 
                Đây là phần quan trọng để tùy chỉnh ô:
                - Chúng ta sử dụng <slot> để cho phép component cha (QuanLiSanPhamView) chèn nội dung tùy chỉnh.
                - :name="`cell-${cellIndex}`" tạo ra một tên slot động, ví dụ: "cell-0", "cell-1",...
                - :value="cell" truyền dữ liệu của ô hiện tại vào slot.
                - Nếu không có slot nào được cung cấp từ cha, nội dung bên trong <slot> (chính là giá trị của ô) sẽ được hiển thị.
              -->
              <slot :name="`cell-${cellIndex}`" :value="cell" :row="row">
                {{ cell }}
              </slot>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  headers: string[];
  // Cho phép `rows` chứa bất kỳ kiểu dữ liệu nào để linh hoạt hơn khi dùng slot
  rows: any[][];
}>();
</script>
