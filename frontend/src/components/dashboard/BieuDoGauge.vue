<template>
  <div class="relative flex flex-col items-center w-full h-full">
    <div class="relative w-full h-[160px]">
      <Doughnut :data="chartData" :options="chartOptions" />
      <div
        class="absolute inset-0 flex items-end justify-center pb-4 pointer-events-none"
      >
        <div class="text-xl font-semibold text-gray-800">
          {{ formattedTienDo }}%
        </div>
      </div>
    </div>

    <div class="grid grid-cols-2 gap-4 mt-4 w-full">
      <div class="p-2 bg-gray-50 rounded-xl text-center">
        <span class="text-xs text-gray-500 block">Mục tiêu</span>
        <span class="text-sm font-semibold">{{ format(mucTieu) }}</span>
      </div>
      <div class="p-2 bg-gray-50 rounded-xl text-center">
        <span class="text-xs text-gray-500 block">Doanh thu</span>
        <span class="text-sm font-semibold">{{ format(tienDoHienTai) }}</span>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed } from "vue";
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from "chart.js";
import { Doughnut } from "vue-chartjs";

ChartJS.register(ArcElement, Tooltip, Legend);

const props = defineProps<{
  tienDo: number;
  mucTieu: number;
  tienDoHienTai: number;
}>();

const formattedTienDo = computed(() => props.tienDo.toFixed(2));

/**
 * Trả về mã màu RGB nội suy từ đỏ -> vàng -> xanh
 * Nếu phần trăm < 50: nội suy đỏ -> vàng
 * Nếu phần trăm >= 50: nội suy vàng -> xanh
 */
function getInterpolatedColor(percent: number): string {
  const r = percent < 50 ? 255 : Math.round(255 - (percent - 50) * 5.1);
  const g = percent < 50 ? Math.round(percent * 5.1) : 255;
  const b = 0;
  return `rgb(${r},${g},${b})`;
}

const chartData = computed(() => {
  const isExceeded = props.tienDo >= 100;
  const percentToDraw = isExceeded ? 100 : props.tienDo;

  const mainColor = isExceeded
    ? "rgb(22, 163, 74)" // xanh đậm (green-600)
    : getInterpolatedColor(props.tienDo);

  return {
    datasets: [
      {
        data: [percentToDraw, 100 - percentToDraw],
        backgroundColor: [mainColor, "#E5E7EB"], // nền xám nhạt
        borderWidth: 0,
      },
    ],
  };
});

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  rotation: -90,
  circumference: 180,
  cutout: "75%",
  plugins: {
    legend: {
      display: false,
    },
    tooltip: {
      enabled: false,
    },
  },
};

/**
 * Format số tiền theo định dạng Việt Nam
 */
function format(val: number): string {
  return val.toLocaleString("vi-VN") + " VND";
}
</script>
