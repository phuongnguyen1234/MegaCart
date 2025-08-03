<template>
  <div class="overflow-x-auto">
    <table class="min-w-full divide-y divide-gray-200">
      <thead class="bg-gray-50">
        <tr>
          <th
            v-for="(header, index) in headers"
            :key="index"
            class="px-4 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
          >
            {{ header }}
          </th>
        </tr>
      </thead>
      <tbody class="bg-white divide-y divide-gray-200">
        <tr v-for="(row, rowIndex) in rows" :key="rowIndex">
          <td
            v-for="(cell, cellIndex) in row"
            :key="cellIndex"
            class="px-4 py-2 text-sm text-gray-700 whitespace-nowrap"
          >
            <a
              v-if="isLink(cell)"
              href="#"
              class="text-blue-600 hover:underline"
              >{{ cell }}</a
            >
            <span v-else>{{ cell }}</span>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  headers: string[];
  rows: (string | number)[][];
}>();

const isLink = (text: string | number): boolean => {
  return typeof text === "string" && text.toLowerCase() === "xem";
};
</script>
