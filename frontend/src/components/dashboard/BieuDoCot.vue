<template>
  <Bar :data="chartData" :options="chartOptions" />
</template>

<script setup lang="ts">
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale,
} from "chart.js";
import { Bar } from "vue-chartjs";
import { computed } from "vue";
import colors from "tailwindcss/colors";

ChartJS.register(
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale
);

type Props = {
  labels: string[];
  data: number[];
};

const props = defineProps<Props>();

const chartData = computed(() => ({
  // Sửa lỗi: Thuộc tính đúng phải là 'labels', không phải 'nhan'.
  labels: props.labels,
  datasets: [
    {
      label: "Số lượng",
      // Cải thiện: Sử dụng màu từ Tailwind để nhất quán.
      backgroundColor: colors.sky[400],
      data: props.data,
      borderRadius: 6,
    },
  ],
}));

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  scales: {
    y: {
      beginAtZero: true,
    },
  },
  plugins: {
    legend: {
      // Cải thiện: Ẩn chú thích để giao diện gọn gàng hơn.
      display: false,
    },
  },
};
</script>
