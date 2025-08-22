<template>
  <div class="chart-container">
    <div class="chart-title">SLA ( pct )</div>
    <div class="chart-container-item">
      <canvas ref="chartCanvasSla" style="width: 100%;"></canvas>
    </div>
    <div class="chart-title">RTT ( ms )</div>
    <div class="chart-container-item">
      <canvas ref="chartCanvasRtt" style="width: 100%;"></canvas>
    </div>
  </div>
</template>
<script lang="ts" setup>
import {
  ref,
  watch,
  onMounted,
  onBeforeUnmount
} from 'vue'
import {
  TimeSeries,
  SmoothieChart
} from 'smoothie'
import {
  useResultinfoStore
} from '@/store/resultinfo';

const resultinfo = useResultinfoStore();

let seriesSla: TimeSeries;
let chartSla: SmoothieChart;
const chartCanvasSla = ref<HTMLCanvasElement | null>(null);

let seriesRtt: TimeSeries;
let chartRtt: SmoothieChart;
const chartCanvasRtt = ref<HTMLCanvasElement | null>(null);

let totalcount = 0;
let totalsuces = 0;
let slapercentage = 100;

let totaldelay = 0;
let rttaverageese = 0;

let updateTimerId: ReturnType<typeof setInterval> | null = null;
const clrUpdateTimer = () => {
  if (updateTimerId === null) {
    return;
  }
  clearInterval(updateTimerId);
  updateTimerId = null;
};
const setUpdateTimer = async () => {
  if (updateTimerId !== null) {
    return;
  }
  updateTimerId = setInterval(() => {
    const now = new Date().getTime()
    seriesSla.append(now, slapercentage);
    seriesRtt.append(now, rttaverageese);
  }, 500);
};

watch(
  () => resultinfo.resultInfo,
  (newVal, oldVal) => {
    if (!newVal?.pingresultInfo?.pingresult) return;
    const { success, delay } = newVal.pingresultInfo.pingresult;
    totalcount++;
    if (success) {
      totalsuces++;
    }
    totaldelay += delay;
    slapercentage = Number(((totalsuces * 100) / totalcount).toFixed(2));
    rttaverageese = Number((totaldelay / totalcount).toFixed(2));
  },
  {
    deep: false
  }
);

let roSla: ResizeObserver | null = null;
const fitCanvasSlaToContainer = () => {
  const canvasSla = chartCanvasSla.value
  if (canvasSla && canvasSla.parentElement) {
    const { clientWidth, clientHeight } = canvasSla.parentElement
    canvasSla.width = clientWidth
    canvasSla.height = clientHeight
  }
}
let roRtt: ResizeObserver | null = null;
const fitCanvasRttToContainer = () => {
  const canvasRtt = chartCanvasRtt.value
  if (canvasRtt && canvasRtt.parentElement) {
    const { clientWidth, clientHeight } = canvasRtt.parentElement
    canvasRtt.width = clientWidth
    canvasRtt.height = clientHeight
  }
}
const setResizeObserver = () => {
  if (!roSla) {
    roSla = new ResizeObserver(() => {
      fitCanvasSlaToContainer()
      ;(chartSla as any).resize?.()
    })
    if (chartCanvasSla.value?.parentElement) {
      roSla.observe(chartCanvasSla.value.parentElement)
    }
  }
  if (!roRtt) {
    roRtt = new ResizeObserver(() => {
      fitCanvasRttToContainer()
      ;(chartRtt as any).resize?.()
    })
    if (chartCanvasRtt.value?.parentElement) {
      roRtt.observe(chartCanvasRtt.value.parentElement)
    }
  }
}
const clrResizeObserver = () => {
  if (roSla) {
    roSla.disconnect();
    roSla = null;
  }
  if (roRtt) {
    roRtt.disconnect();
    roRtt = null;
  }
}

const elColorPrimary = getComputedStyle(document.documentElement).getPropertyValue('--el-color-primary').trim();
const elColorSuccess = getComputedStyle(document.documentElement).getPropertyValue('--el-color-success').trim();

const elementColor = getComputedStyle(document.documentElement).getPropertyValue('--element-color').trim();
const elementBgColor = getComputedStyle(document.documentElement).getPropertyValue('--element-bg-color').trim();
const elementBdColor = getComputedStyle(document.documentElement).getPropertyValue('--element-bd-color').trim();

const startCanvas = () => {
  chartSla = new SmoothieChart({
    millisPerPixel: 50,
    grid: {
      strokeStyle: elementBdColor,
      lineWidth: 1,
      millisPerLine: 1000,
      verticalSections: 4,
      fillStyle: elementBgColor
    },
    labels: {
      fontSize: 14,
      precision: 0,
      fillStyle: elementColor
    }
  });
  seriesSla = new TimeSeries();
  chartSla.addTimeSeries(seriesSla, {
    strokeStyle: elColorPrimary,
    lineWidth: 4
  });
  if (chartCanvasSla.value) {
    chartSla.streamTo(chartCanvasSla.value, 500);
  }

  chartRtt = new SmoothieChart({
    millisPerPixel: 50,
    grid: {
      strokeStyle: elementBdColor,
      lineWidth: 1,
      millisPerLine: 1000,
      verticalSections: 4,
      fillStyle: elementBgColor
    },
    labels: {
      fontSize: 14,
      precision: 0,
      fillStyle: elementColor
    }
  });
  seriesRtt = new TimeSeries();
  chartRtt.addTimeSeries(seriesRtt, {
    strokeStyle: elColorSuccess,
    lineWidth: 4
  });
  if (chartCanvasRtt.value) {
    chartRtt.streamTo(chartCanvasRtt.value, 500);
  }
};


onMounted(() => {
  startCanvas();
  setUpdateTimer();
  setResizeObserver();
})

onBeforeUnmount(() => {
  clrUpdateTimer();
  clrResizeObserver();
})
</script>

<style scoped>
.chart-container {
  box-sizing: border-box;
  width: 100%;
  height: 100%;
}
.chart-container-item {
  box-sizing: border-box;
  width: 100%;
  height: 60px;
}
.chart-title {
  font-size: 12px;
  font-weight: normal;
  margin-top: 4px;
  color: var(--element-color);
}
</style>
