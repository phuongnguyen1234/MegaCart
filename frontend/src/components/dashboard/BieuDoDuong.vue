<template>
  <Line :data="chartData" :options="chartOptions" />
</template>

<script setup lang="ts">
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  LineElement,
  PointElement,
  CategoryScale,
  LinearScale,
} from "chart.js";
import { Line } from "vue-chartjs";
import { computed } from "vue";
import colors from "tailwindcss/colors";

ChartJS.register(
  Title,
  Tooltip,
  Legend,
  LineElement,
  PointElement,
  CategoryScale,
  LinearScale
);

type Props = {
  labels: string[];
  data: number[];
};

const props = defineProps<Props>();

const chartData = computed(() => ({
  labels: props.labels,
  datasets: [
    {
      label: "Số lượng",
      // Đổi màu từ xanh lá sang xanh dương (sky) để đồng bộ
      borderColor: colors.sky[400],
      backgroundColor: colors.sky[100],
      data: props.data,
      tension: 0.4,
      fill: true,
      pointRadius: 4,
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
      display: false,
    },
  },
};
</script>
