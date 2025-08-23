<template>
  <Pie :data="chartData" :options="chartOptions" />
</template>

<script setup lang="ts">
import colors from "tailwindcss/colors";
import { Chart as ChartJS, Title, Tooltip, Legend, ArcElement } from "chart.js";
import { Pie } from "vue-chartjs";
import { computed } from "vue";

ChartJS.register(Title, Tooltip, Legend, ArcElement);

type Props = {
  labels: string[];
  data: number[];
};

const props = defineProps<Props>();

const chartData = computed(() => ({
  labels: props.labels,
  datasets: [
    {
      label: "Tỉ lệ",
      // Cập nhật bảng màu để phù hợp với 5 trạng thái đơn hàng
      backgroundColor: [
        colors.orange[400], // Chờ xác nhận
        colors.yellow[400], // Chờ xử lí
        colors.sky[400], // Đang giao
        colors.green[500], // Đã giao
        colors.red[500], // Đã hủy
      ],
      data: props.data,
    },
  ],
}));

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
};
</script>
