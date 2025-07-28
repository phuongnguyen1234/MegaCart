<template>
  <nav aria-label="breadcrumb">
    <ol class="flex items-center space-x-2 text-sm font-medium">
      <li
        v-for="(item, index) in items"
        :key="item.text"
        class="flex items-center"
      >
        <!-- Render a router-link if 'to' is provided -->
        <router-link
          v-if="item.to"
          :to="item.to"
          class="text-gray-500 transition-colors hover:text-blue-600"
        >
          {{ item.text }}
        </router-link>
        <!-- Render a span for the current page (without a link) -->
        <span v-else class="text-gray-800">
          {{ item.text }}
        </span>

        <!-- Separator for all but the last item -->
        <span v-if="index < items.length - 1" class="mx-2 text-gray-400">
          /
        </span>
      </li>
    </ol>
  </nav>
</template>

<script setup lang="ts">
interface BreadcrumbItem {
  text: string;
  to?: string; // The 'to' for router-link. The last item should not have it.
}

defineProps<{
  items: BreadcrumbItem[];
}>();
</script>
